/*
 * Copyright 2023-2025 The STARS Project Authors
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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import tools.aqua.stars.core.evaluation.TickSequence
import tools.aqua.stars.data.av.dataclasses.TickData
import tools.aqua.stars.data.av.dataclasses.World
import tools.aqua.stars.importer.carla.dataclasses.*

/** Carla data serializer module. */
val carlaDataSerializerModule: SerializersModule = SerializersModule {
  polymorphic(JsonActor::class) {
    subclass(JsonVehicle::class)
    subclass(JsonTrafficLight::class)
    subclass(JsonTrafficSign::class)
    subclass(JsonPedestrian::class)
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
inline fun <reified T> getJsonContentOfPath(file: Path): T {
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

/**
 * Return a [World] object from a given [staticDataFile] path.
 *
 * @param staticDataFile The [Path] which points to the file that contains the static map data.
 * @return The loaded [World]s as a [Sequence] based on the given [staticDataFile] [Path].
 */
fun loadWorld(staticDataFile: Path): World =
    calculateWorld(getJsonContentOfPath<JsonWorld>(staticDataFile))

/**
 * Returns a [Sequence] of [TickSequence]s given a path to a [mapDataFile] in combination with a
 * [dynamicDataFile] path.
 *
 * @param mapDataFile The [Path] to map data file containing all static information.
 * @param dynamicDataFile The [Path] to the data file which contains the timed state data for the
 *   simulation.
 * @param bufferSize The size of the buffer for each [TickSequence]. This is the window of ticks
 *   that gets supplied to the TSCEvaluation.
 * @return A [Sequence] of [TickSequence]s based on the given [mapDataFile] and [dynamicDataFile].
 */
fun loadTicks(
    mapDataFile: Path,
    dynamicDataFile: Path,
    bufferSize: Int = 100,
): Sequence<TickSequence<TickData>> =
    loadTicks(
        simulationRunsWrappers =
            listOf(CarlaSimulationRunsWrapper(mapDataFile, listOf(dynamicDataFile))),
        bufferSize = bufferSize,
    )

/**
 * Returns a [Sequence] of [TickSequence]s given a path to a [mapDataFile] in combination with a
 * [List] of [dynamicDataFiles] paths.
 *
 * @param mapDataFile The [Path] to map data file containing all static information.
 * @param dynamicDataFiles The [List] of [Path]s to the data files which contain the timed state
 *   data for the simulation.
 * @param bufferSize The size of the buffer for each [TickSequence]. This is the window of ticks
 *   that gets supplied to the TSCEvaluation.
 * @return A [Sequence] of [TickSequence]s based on the given [mapDataFile] and [dynamicDataFiles].
 */
fun loadTicks(
    mapDataFile: Path,
    dynamicDataFiles: List<Path>,
    bufferSize: Int = 100,
): Sequence<TickSequence<TickData>> =
    loadTicks(
        simulationRunsWrappers = listOf(CarlaSimulationRunsWrapper(mapDataFile, dynamicDataFiles)),
        bufferSize = bufferSize,
    )

/**
 * Returns a [Sequence] of [TickSequence]s given a map of static data to dynamic data files.
 *
 * @param mapToDynamicDataFiles The [World] of static data to dynamic data.
 * @param bufferSize The size of the buffer for each [TickSequence]. This is the window of ticks
 *   that gets supplied to the TSCEvaluation.
 * @return A [Sequence] of [TickSequence]s based on the given [World] of static data to dynamic
 *   data.
 */
fun loadTicks(
    mapToDynamicDataFiles: Map<Path, List<Path>>,
    bufferSize: Int = 100,
): Sequence<TickSequence<TickData>> =
    loadTicks(
        simulationRunsWrappers =
            mapToDynamicDataFiles.map { CarlaSimulationRunsWrapper(it.key, it.value) },
        bufferSize = bufferSize,
    )

/**
 * Returns a [Sequence] of [TickSequence]s given a [List] of [CarlaSimulationRunsWrapper]s. Each
 * [CarlaSimulationRunsWrapper] contains the information about the used map data and the dynamic
 * data, each as [Path]s.
 *
 * @param simulationRunsWrappers The [List] of [CarlaSimulationRunsWrapper]s that wrap the map data
 *   to its dynamic data.
 * @param egoIds The optional [Set] of ids of the ego vehicles to take. Overrides the ego flag in
 *   the Json data.
 * @param useEveryVehicleAsEgo Whether the [TickData]s should be enriched by considering every
 *   vehicle as the ego vehicle. Overrides the [egoIds] parameter.
 * @param useFirstVehicleAsEgo Whether to treat the first vehicle that appears in all ticks as ego.
 *   Overrides the [egoIds] parameter.
 * @param bufferSize The size of the buffer for each [TickSequence]. This is the window of ticks
 *   that gets supplied to the TSCEvaluation.
 * @return A [Sequence] of [TickSequence]s based on the given [simulationRunsWrappers].
 */
fun loadTicks(
    simulationRunsWrappers: List<CarlaSimulationRunsWrapper>,
    egoIds: Set<Int> = emptySet(),
    useEveryVehicleAsEgo: Boolean = false,
    useFirstVehicleAsEgo: Boolean = false,
    bufferSize: Int = 100,
): Sequence<TickSequence<TickData>> {
  // Check that the simulationRunsWrappers are not empty
  check(simulationRunsWrappers.isNotEmpty()) {
    "The list of SimulationRunsWrapper is empty. Cannot load ticks without simulation runs."
  }

  // Check that every SimulationRunsWrapper has dynamic data files loaded
  check(simulationRunsWrappers.none { it.dynamicDataFiles.isEmpty() }) {
    "Some SimulationRunsWrappers do not have dynamic data files loaded. " +
        "Cannot load ticks without dynamic data files."
  }

  check(!useEveryVehicleAsEgo || !useFirstVehicleAsEgo) {
    "Parameters useEveryVehicleAsEgo and useFirstVehicleAsEgo may not be set to true simultaneously."
  }

  var buffer: MutableList<() -> TickSequence<TickData>> = mutableListOf()
  val simulationRunsWrapperIterator = simulationRunsWrappers.iterator()
  var currentSimulationRunsWrapper = simulationRunsWrapperIterator.next()
  var currentDynamicDataPathIterator = currentSimulationRunsWrapper.dynamicDataFiles.iterator()

  return generateSequence {
    // If there are buffered TickSequences, return the next one
    if (buffer.isNotEmpty()) {
      return@generateSequence buffer.removeFirst().invoke()
    }

    // If current dynamic data file is exhausted, move to the next SimulationRunsWrapper
    if (!currentDynamicDataPathIterator.hasNext() && simulationRunsWrapperIterator.hasNext()) {
      currentSimulationRunsWrapper = simulationRunsWrapperIterator.next()
      currentDynamicDataPathIterator = currentSimulationRunsWrapper.dynamicDataFiles.iterator()
    }

    // If there is a dynamic data file to process, load it and create TickSequences
    if (currentDynamicDataPathIterator.hasNext()) {
      buffer =
          loadTicksForRecording(
              world = currentSimulationRunsWrapper.world,
              currentDynamicDataPath = currentDynamicDataPathIterator.next(),
              egoIds = egoIds,
              useEveryVehicleAsEgo = useEveryVehicleAsEgo,
              useFirstVehicleAsEgo = useFirstVehicleAsEgo,
              bufferSize = bufferSize,
          )
      return@generateSequence buffer.removeFirst().invoke()
    }
    // No more dynamic data files left to process. Terminate the sequence.
    else return@generateSequence null
  }
}

/**
 * Determines the [egoIds] to use for the given recording based on [egoIds], [useEveryVehicleAsEgo],
 * and [useFirstVehicleAsEgo]. Then creates a list of lambdas that create [TickSequence]s for each
 * ego id.
 *
 * @param world The [World] object containing the static map data.
 * @param currentDynamicDataPath The [Path] to the dynamic data file for the current recording.
 * @param egoIds The optional [Set] of ids of the ego vehicles to take. Overrides the ego flag in
 *   the Json data.
 * @param useEveryVehicleAsEgo Whether the [TickData]s should be enriched by considering every
 *   vehicle as the ego vehicle. Overrides the [egoIds] parameter.
 * @param useFirstVehicleAsEgo Whether to treat the first vehicle that appears in all ticks as ego.
 *   Overrides the [egoIds] parameter.
 * @param bufferSize The size of the buffer for each [TickSequence].
 * @return A [MutableList] of lambdas that create [TickSequence]s for each ego id.
 */
private fun loadTicksForRecording(
    world: World,
    currentDynamicDataPath: Path,
    egoIds: Set<Int>,
    useEveryVehicleAsEgo: Boolean,
    useFirstVehicleAsEgo: Boolean,
    bufferSize: Int,
): MutableList<() -> TickSequence<TickData>> {
  println("Reading simulation run file: ${currentDynamicDataPath.toUri()}")

  // Holds the current simulationRun object
  val jsonSimulationRun = getJsonContentOfPath<List<JsonTickData>>(currentDynamicDataPath)

  // Copy requested set of ego ids to use
  var egoIdsToUse = egoIds

  // If every vehicle or the first vehicle as ego is requested, determine the ego ids accordingly
  if (useEveryVehicleAsEgo || useFirstVehicleAsEgo) {
    // Find all vehicles in the simulation run that exist in the first tick
    val jsonVehicles =
        jsonSimulationRun
            .first()
            .actorPositions
            .map { a -> a.actor }
            .filterIsInstance<JsonVehicle>()

    println("Found ${jsonVehicles.size} vehicles in the first tick of the simulation run.")

    val activeVehicleIds =
        jsonVehicles
            .filter { !(it.attributes["role_name"] ?: "").contains("parked") }
            .map { v -> v.id }
            .toSet()

    println("Found ${activeVehicleIds.size} active vehicles.")

    egoIdsToUse =
        if (useEveryVehicleAsEgo) activeVehicleIds
        else
            setOf(
                activeVehicleIds.first { v ->
                  jsonSimulationRun.all { it.actorPositions.map { a -> a.actor.id }.contains(v) }
                }
            )
  }

  return if (egoIdsToUse.isEmpty()) {
    mutableListOf(
        createTickLoader(
            world = world,
            jsonSimulationRun = jsonSimulationRun,
            tickDataSourcePath = currentDynamicDataPath,
            bufferSize = bufferSize,
            null,
        )
    )
  } else {
    egoIdsToUse
        .map { egoId ->
          createTickLoader(
              world = world,
              jsonSimulationRun = jsonSimulationRun,
              tickDataSourcePath = currentDynamicDataPath,
              bufferSize = bufferSize,
              egoId = egoId,
          )
        }
        .toMutableList()
  }
}

/**
 * Returns a lambda that creates a [TickSequence] of [TickData] from the given jsonSimulationRun.
 * This may be used to store the remaining runs to process in a buffer and yield the creation of the
 * [TickData] objects until invocation.
 *
 * @param world The [World] object containing the static map data.
 * @param jsonSimulationRun The [List] of [JsonTickData] representing the dynamic data of the
 *   simulation run.
 * @param tickDataSourcePath The [Path] to the source of the tick data.
 * @param bufferSize The size of the buffer for the [TickSequence].
 * @param egoId The optional id of the ego vehicle to consider in the [TickData]. If null, the
 *   tagged ego vehicle in the JSON data is used.
 * @return A lambda that creates a [TickSequence] of [TickData].
 */
private fun createTickLoader(
    world: World,
    jsonSimulationRun: List<JsonTickData>,
    tickDataSourcePath: Path,
    bufferSize: Int,
    egoId: Int?,
): () -> TickSequence<TickData> = {
  println(
      "Creating TickData sequence for tick data source: ${tickDataSourcePath.toUri()} ${ if(egoId != null) " with egoId: $egoId" else ""}"
  )

  // Calculate TickData objects from JSON
  val ticks =
      convertTickData(
          world = world,
          jsonSimulationRun = jsonSimulationRun,
          tickDataSourcePath = tickDataSourcePath,
          egoId = egoId,
      )

  val iterator = ticks.iterator()
  TickSequence(bufferSize) { if (iterator.hasNext()) iterator.next() else null }
}
