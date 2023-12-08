/*
 * Copyright 2023 The STARS Project Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package tools.aqua.stars.importer.carla

import java.io.File
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import tools.aqua.stars.core.DEFAULT_MIN_SEGMENT_TICK_COUNT
import tools.aqua.stars.core.DEFAULT_NUM_SLICE_THREADS
import tools.aqua.stars.core.DEFAULT_SEGMENT_PREFETCH_SIZE
import tools.aqua.stars.core.DEFAULT_SIMULATION_RUN_PREFETCH_SIZE
import tools.aqua.stars.core.evaluation.EvaluationState
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.importer.carla.dataclasses.*

/**
 * Data loader for Carla experiments.
 *
 * @property useEveryVehicleAsEgo Whether the [Segment]s should be enriched by considering every
 * vehicle as the "ego" vehicle. This will multiply the size of the resulting sequence of [Segment]s
 * by the number of vehicles.
 * @property orderFilesBySeed Whether the dynamic data files should be sorted by their seeds instead
 * of the map.
 * @property minSegmentTickCount The amount of ticks there should be at minimum to generate a
 * [Segment] .
 * @param simulationRunPrefetchSize Size of the simulation run prefetch buffer.
 * @param segmentPrefetchSize Size of the segment prefetch buffer.
 * @property numSliceThreads Number of the segment slice threads.
 */
class CarlaDataLoader(
    private val useEveryVehicleAsEgo: Boolean = false,
    private val orderFilesBySeed: Boolean = false,
    private val minSegmentTickCount: Int = DEFAULT_MIN_SEGMENT_TICK_COUNT,
    simulationRunPrefetchSize: Int = DEFAULT_SIMULATION_RUN_PREFETCH_SIZE,
    segmentPrefetchSize: Int = DEFAULT_SEGMENT_PREFETCH_SIZE,
    private val numSliceThreads: Int = DEFAULT_NUM_SLICE_THREADS,
) {

  /** Carla data serializer module. */
  private val carlaDataSerializerModule: SerializersModule = SerializersModule {
    polymorphic(JsonActor::class) {
      subclass(JsonVehicle::class)
      subclass(JsonTrafficLight::class)
      subclass(JsonTrafficSign::class)
      subclass(JsonPedestrian::class)
    }
  }

  /** Coroutine scope for simulation file IO. */
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

  /** Channel for loaded simulation runs. */
  private val simulationRunChannel =
      Channel<Triple<CarlaSimulationRunsWrapper, Path, List<JsonTickData>>?>(
          capacity = simulationRunPrefetchSize)

  /** Channel for sliced segments. */
  private val segmentChannel = Channel<Segment?>(capacity = segmentPrefetchSize)

  init {
    EvaluationState.simulationRunsBufferSize.set(simulationRunPrefetchSize)
    EvaluationState.segmentsBufferSize.set(segmentPrefetchSize)
  }

  /**
   * Return a [Sequence] of [Segment]s given a path to a [mapDataFile] in combination with a
   * [dynamicDataFile] path.
   *
   * @param mapDataFile The [Path] to map data file containing all static information.
   * @param dynamicDataFile The [Path] to the data file which contains the timed state data for the
   * simulation.
   * @return A [Sequence] of [Segment]s based on the given [mapDataFile] and [dynamicDataFile]
   */
  fun loadSegments(
      mapDataFile: Path,
      dynamicDataFile: Path,
  ): Sequence<Segment> =
      loadSegments(listOf(CarlaSimulationRunsWrapper(mapDataFile, listOf(dynamicDataFile))))

  /**
   * Load [Segment]s for one specific map. The map data comes from the file [mapDataFile] and the
   * dynamic data from multiple files [dynamicDataFiles].
   *
   * @param mapDataFile The [Path] to map data file containing all static information.
   * @param dynamicDataFiles A [List] of [Path]s to the data files which contain the timed state
   * data for the simulation.
   * @return A [Sequence] of [Segment]s based on the given [mapDataFile] and [dynamicDataFiles]
   */
  fun loadSegments(mapDataFile: Path, dynamicDataFiles: List<Path>): Sequence<Segment> =
      loadSegments(listOf(CarlaSimulationRunsWrapper(mapDataFile, dynamicDataFiles)))

  /**
   * Load [Segment]s based on a [Map]. The [Map] needs to have the following structure. Map<[Path],
   * List[Path]> with the semantics: Map<MapDataPath, List<DynamicDataPath>>. As each dynamic data
   * is linked to a map data, they are linked in the [Map].
   *
   * @param mapToDynamicDataFiles Maps the [Path] of the static file to a [List] of [Path]s of
   * dynamic files related to the static file.
   * @return A [Sequence] of [Segment]s based on the given 'mapDataFile' and 'dynamicDataFiles'.
   */
  fun loadSegments(mapToDynamicDataFiles: Map<Path, List<Path>>): Sequence<Segment> =
      loadSegments(mapToDynamicDataFiles.map { CarlaSimulationRunsWrapper(it.key, it.value) })

  /**
   * Returns a [Sequence] of [Segment]s given a [List] of [CarlaSimulationRunsWrapper]s. Each
   * [CarlaSimulationRunsWrapper] contains the information about the used map data and the dynamic
   * data, each as [Path]s.
   *
   * @param simulationRunsWrappers The [List] of [CarlaSimulationRunsWrapper]s that wrap the map
   * data to its dynamic data.
   * @return A [Sequence] of [Segment]s based on the given [simulationRunsWrappers].
   */
  fun loadSegments(
      simulationRunsWrappers: List<CarlaSimulationRunsWrapper>,
  ): Sequence<Segment> {
    val simulationRunsWrapperList = loadBlocks(simulationRunsWrappers)

    EvaluationState.totalSimulationBlocks.set(simulationRunsWrapperList.size)

    scope.launch { startLoadJob(simulationRunsWrapperList) }

    return generateSequence {
      runBlocking {
        segmentChannel.receive().also { EvaluationState.segmentsBuffer.decrementAndGet() }
      }
    }
  }

  private fun loadBlocks(
      simulationRunsWrappers: List<CarlaSimulationRunsWrapper>
  ): List<CarlaSimulationRunsWrapper> {
    var simRuns = simulationRunsWrappers

    // Check that every SimulationRunsWrapper has dynamic data files loaded
    simRuns.forEach {
      check(it.dynamicDataFiles.any()) {
        "The SimulationRunsWrapper with map data file '${it.mapDataFile}' has no dynamic files. Dynamic file " +
            "paths: ${it.dynamicDataFiles.map { dynamicDataFilePath ->  dynamicDataFilePath.toUri() }}"
      }
    }

    // If orderFilesBySeed is set, order the dynamic data files by seed instead of grouped by map
    // name
    if (orderFilesBySeed) {
      // Create a single CarlaSimulationRunsWrapper for each dynamic file
      simRuns =
          simRuns
              .flatMap { wrapper ->
                wrapper.dynamicDataFiles.map {
                  CarlaSimulationRunsWrapper(wrapper.mapDataFile, listOf(it))
                }
              }
              .sortedBy {
                // Sort the list by the seed name in the dynamic file name
                getSeed(it.dynamicDataFiles.first().fileName.toString())
              }
    }

    // Load Blocks and save in SimulationRunsWrapper
    simRuns.forEach {
      it.blocks =
          calculateStaticBlocks(
                  getJsonContentOfPath<List<JsonBlock>>(it.mapDataFile),
                  it.mapDataFile.fileName.toString())
              .toList()
    }

    return simRuns
  }

  private suspend fun startLoadJob(
      simulationRunsWrapperList: List<CarlaSimulationRunsWrapper>
  ): Unit = coroutineScope {
    val sliceJobs = mutableListOf<Deferred<Unit>>()
    repeat(numSliceThreads) { sliceJobs.add(scope.async { startSliceJob() }) }

    // Iterate all maps
    simulationRunsWrapperList.forEach { simRunWrapper ->
      // Iterate all seeds in current map
      simRunWrapper.dynamicDataFiles.forEach { dynamicDataPath ->
        // println("Reading simulation run file: ${dynamicDataPath.toUri()}")
        simulationRunChannel.send(
            Triple(
                simRunWrapper,
                dynamicDataPath,
                getJsonContentOfPath<List<JsonTickData>>(dynamicDataPath)))
        EvaluationState.simulationRunsBuffer.incrementAndGet()
      }
      EvaluationState.readSimulationRuns.incrementAndGet()
    }
    // If there are no more Files to process, close channel
    simulationRunChannel.close()

    // Await finishing of slice jobs
    sliceJobs.awaitAll()

    // Close sequence by sending null
    segmentChannel.send(null)
    EvaluationState.segmentsBuffer.incrementAndGet()

    // Close segment channel
    segmentChannel.close()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private suspend fun startSliceJob(): Unit = coroutineScope {
    while (!simulationRunChannel.isClosedForReceive) {
      val rec = simulationRunChannel.receiveCatching()

      val sim = rec.getOrNull()
      if (!rec.isSuccess || sim == null)
          break // If there are no Segments nor Files to process, return null to end sequence

      EvaluationState.simulationRunsBuffer.decrementAndGet()

      val segments =
          sliceRunIntoSegments(
              sim.first.blocks,
              sim.third,
              useEveryVehicleAsEgo,
              sim.second.fileName.toString(),
              minSegmentTickCount)
      EvaluationState.slicedSimulationRuns.incrementAndGet()

      segments.forEach { t ->
        segmentChannel.send(t)
        EvaluationState.segmentsBuffer.incrementAndGet()
      }
    }
  }

  /**
   * Returns the parsed Json content for the given [file]. Currently supported file extensions:
   * ".json", ".zip". The generic parameter [T] specifies the class to which the content should be
   * parsed to.
   *
   * @return The parsed JSON content from the given [Path].
   */
  @OptIn(ExperimentalSerializationApi::class)
  private inline fun <reified T> getJsonContentOfPath(file: Path): T {
    // Create JsonBuilder with correct settings
    val jsonBuilder = Json {
      prettyPrint = true
      isLenient = true
      serializersModule = carlaDataSerializerModule
    }

    // Check if inputFilePath exists
    check(file.exists()) { "The given file path does not exist: ${file.toUri()}" }

    // Check whether the given inputFilePath is a directory
    check(!file.isDirectory()) { "Cannot get InputStream for directory. Path: $file" }

    // If ".json"-file: Just return InputStream of file
    if (file.extension == "json") {
      return jsonBuilder.decodeFromStream<T>(file.inputStream())
    }

    // if ".zip"-file: Extract single archived file
    if (file.extension == "zip") {
      // https://stackoverflow.com/a/46644254
      ZipFile(File(file.toUri())).use { zip ->
        zip.entries().asSequence().forEach { entry ->
          // Add InputStream to inputStreamBuffer
          return jsonBuilder.decodeFromStream<T>(zip.getInputStream(entry))
        }
      }
    }

    // If none of the supported file extensions is present, throw an Exception
    error("Unexpected file extension: ${file.extension}. Supported extensions: '.json', '.zip'")
  }
}
