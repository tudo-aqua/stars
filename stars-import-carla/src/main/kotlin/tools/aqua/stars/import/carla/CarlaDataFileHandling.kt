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

package tools.aqua.stars.import.carla

import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import tools.aqua.stars.data.av.Block
import tools.aqua.stars.data.av.Segment

// Create JsonBuilder with correct settings
private val jsonBuilder = Json {
  prettyPrint = true
  isLenient = true
  serializersModule = CarlaDataSerializerModule
}

fun main() {
  val mapFilePath =
      "D:\\aqua\\stars-main\\stars-experiments-data\\simulation_runs\\_Game_Carla_Maps_Town01\\static_data__Game_Carla_Maps_Town01.json"
  val dynamicFilePath =
      "D:\\aqua\\stars-main\\stars-experiments-data\\simulation_runs\\_Game_Carla_Maps_Town01\\dynamic_data__Game_Carla_Maps_Town01_seed2.json"
  val segments = loadSegments(Paths.get(mapFilePath), Paths.get(dynamicFilePath), true)
  val s = segments.toList()
  println("Done")
}

/**
 * Returns a [Sequence] of [Segment]s given a [List] of [CarlaSimulationRunsWrapper]s. Each
 * [CarlaSimulationRunsWrapper] contains the information about the used map data and the dynamic
 * data, each as [Path]s.
 */
fun loadSegments(
    simulationRunsWrappers: List<CarlaSimulationRunsWrapper>,
    useEveryVehicleAsEgo: Boolean = false
): Sequence<Segment> {
  // Load Blocks and save in SimulationRunsWrapper
  simulationRunsWrappers.forEach { it.blocks = loadBlocks(it.mapDataFile).toList() }

  // Load InputStreams for dynamic data files
  simulationRunsWrappers.forEach {
    it.dynamicDataInputStreamsList =
        it.dynamicDataFiles.map { getInputStreamsFromFile(it) }.flatten()
  }

  // Check that every SimulationRunsWrapper has dynamic data files loaded
  simulationRunsWrappers.forEach {
    check(it.dynamicDataInputStreamsArrayDeque.any()) {
      "The SimulationRunsWrapper with map data file '${it.mapDataFile}' has no loaded dynamic files. Dynamic file " +
          "paths: ${it.dynamicDataFiles.map { dynamicDataFilePath ->  dynamicDataFilePath.toUri() }}"
    }
  }

  /** Holds the [ArrayDeque] of [CarlaSimulationRunsWrapper] from the parameters */
  val simulationRunsWrappersDeque = ArrayDeque(simulationRunsWrappers)
  /** Holds the [Segment]s that still need to be calculated for the [Sequence] of [Segment]s */
  val segmentBuffer = ArrayDeque<Segment>()

  return generateSequence {
    // There currently is another Segment that was already calculated
    if (segmentBuffer.size > 0) {
      return@generateSequence segmentBuffer.removeFirst()
    }
    // No calculated Segment is left. Calculate new Segments
    if (simulationRunsWrappersDeque.size > 0) {
      /** Holds the current [CarlaSimulationRunsWrapper] */
      val simulationRunsWrapper = simulationRunsWrappersDeque.removeFirst()
      /** Holds the current [InputStream] of the next dynamic data file to be calculated */
      val dataInputStream = simulationRunsWrapper.dynamicDataInputStreamsArrayDeque.removeFirst()
      /** Holds the current simulationRun object */
      val simulationRun = jsonBuilder.decodeFromStream<List<JsonTickData>>(dataInputStream.first)

      // Calculate Blocks for current file and add each Segment to the Sequence
      segmentBuffer.addAll(
          sliceRunIntoSegments(
              simulationRunsWrapper.blocks,
              simulationRun,
              useEveryVehicleAsEgo,
              dataInputStream.second.fileName.toString()))
      return@generateSequence segmentBuffer.removeFirst()
    }
    // If there are no Segments nor Files to process, return null to indicate the end of the
    // Sequence
    return@generateSequence null
  }
}

/**
 * Return a [Sequence] of [Segment]s given a path to a [mapDataFile] in combination with a
 * [dynamicDataFile] path. [useEveryVehicleAsEgo] lets you decide whether to use every vehicle to be
 * used as the ego vehicle. This will multiply the size of the resulting sequence of [Segment]s by
 * the number of vehicles.
 */
fun loadSegments(
    mapDataFile: Path,
    dynamicDataFile: Path,
    useEveryVehicleAsEgo: Boolean = false
): Sequence<Segment> {
  // Call actual implementation of loadSegments with correct data structure
  return loadSegments(
      listOf(CarlaSimulationRunsWrapper(mapDataFile, listOf(dynamicDataFile))),
      useEveryVehicleAsEgo)
}

/**
 * Load [Segment]s for one specific map. The map data comes from the file [mapDataFile] and the
 * dynamic data from multiple files [dynamicDataFiles].
 */
fun loadSegments(
    mapDataFile: Path,
    dynamicDataFiles: List<Path>,
    useEveryVehicleAsEgo: Boolean = false
): Sequence<Segment> {
  // Call actual implementation of loadSegments with correct data structure
  return loadSegments(
      listOf(CarlaSimulationRunsWrapper(mapDataFile, dynamicDataFiles)), useEveryVehicleAsEgo)
}

/**
 * Load [Segment]s based on a [Map]. The [Map] needs to have the following structure. Map<[Path],
 * List[Path]> with the semantics: Map<MapDataPath, List<DynamicDataPath>>. As each dynamic data is
 * linked to a map data, they are linked in the [Map].
 */
fun loadSegments(
    mapToDynamicDataFiles: Map<Path, List<Path>>,
    useEveryVehicleAsEgo: Boolean = false
): Sequence<Segment> {
  /** Holds the [List] of [CarlaSimulationRunsWrapper]s */
  val listOfCarlaSimulationRunsWrappers =
      mapToDynamicDataFiles.map { CarlaSimulationRunsWrapper(it.key, it.value) }

  // Call actual implementation of loadSegments with correct data structure
  return loadSegments(listOfCarlaSimulationRunsWrappers, useEveryVehicleAsEgo)
}

/** Return a [Sequence] of [Block]s from a given [mapDataFile] path. */
fun loadBlocks(mapDataFile: Path): Sequence<Block> {
  /** Holds the InputStreams for the mapDataFile */
  val mapDataInputStreams = getInputStreamsFromFile(mapDataFile)

  check(mapDataInputStreams.count() == 1) {
    "Only one file is allowed for map data. Found ${mapDataInputStreams.count()} file under path: $mapDataFile"
  }

  /** Holds the single InputStream for the mapDataFile */
  val mapDataInputStream = mapDataInputStreams.first()

  /** Holds the decoded list of [JsonBlock]s from the specified [mapDataFile] */
  val jsonBlocks = jsonBuilder.decodeFromStream<List<JsonBlock>>(mapDataInputStream.first)

  return calculateStaticBlocks(jsonBlocks, mapDataFile.fileName.toString()).asSequence()
}

/**
 * Returns an [InputStream] for the given [inputFilePath]. Currently supported file extensions:
 * ".json", ".zip".
 */
fun getInputStreamsFromFile(inputFilePath: Path): ArrayDeque<Pair<InputStream, Path>> {
  val inputStreamBuffer = ArrayDeque<Pair<InputStream, Path>>()
  // Check whether the given inputFilePath is a directory
  if (inputFilePath.isDirectory()) {
    error("Cannot get InputStream for directory. Path: $inputFilePath")
  }

  // If ".json"-file: Just return InputStream of file
  if (inputFilePath.extension == "json") {
    inputStreamBuffer.add(inputFilePath.inputStream() to inputFilePath)
    return inputStreamBuffer
  }

  // if ".zip"-file: Extract single archived file
  if (inputFilePath.extension == "zip") {
    // https://stackoverflow.com/a/46644254
    ZipFile(File(inputFilePath.toUri())).use { zip ->
      zip.entries().asSequence().forEach { entry ->
        // Add InputStream to inputStreamBuffer
        inputStreamBuffer.add(zip.getInputStream(entry) to inputFilePath)
      }
    }
    return inputStreamBuffer
  }

  // If none of the supported file extensions is present, throw an Exception
  error(
      "Unexpected file extension: ${inputFilePath.extension}. Supported extensions: '.json', '.zip'")
}

/**
 * Contains the information for all simulation runs for one specific map. Each
 * [CarlaSimulationRunsWrapper] contains a [Path] to the [mapDataFile] and a list of [Path]s for the
 * [dynamicDataFiles]. It also holds properties for calculated [Block]s and the [InputStream]s for
 * each [Path] of [dynamicDataFiles].
 */
data class CarlaSimulationRunsWrapper(val mapDataFile: Path, val dynamicDataFiles: List<Path>) {
  /** Holds the [List] of [Block]s after calling [loadBlocks]. */
  var blocks: List<Block> = listOf()
  /** Holds a [List] of [Pair]s of an [InputStream] and the related [Path] */
  var dynamicDataInputStreamsList: List<Pair<InputStream, Path>> = listOf()
  /**
   * Holds a [ArrayDeque] of [Pair]s of an [InputStream] and the related [Path]. Is only available
   * after initializing [dynamicDataInputStreamsList]
   */
  val dynamicDataInputStreamsArrayDeque: ArrayDeque<Pair<InputStream, Path>>
    get() = ArrayDeque(dynamicDataInputStreamsList)
}
