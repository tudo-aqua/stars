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
import java.util.zip.ZipFile
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import tools.aqua.stars.data.av.Block
import tools.aqua.stars.data.av.Segment

/**
 * Returns a [Sequence] of [Segment]s given a [List] of [CarlaSimulationRunsWrapper]s. Each
 * [CarlaSimulationRunsWrapper] contains the information about the used map data and the dynamic
 * data, each as [Path]s.
 */
fun loadSegments(
    simulationRunsWrappers: List<CarlaSimulationRunsWrapper>,
    useEveryVehicleAsEgo: Boolean = false,
    minSegmentTickCount: Int = 10
): Sequence<Segment> {
  // Load Blocks and save in SimulationRunsWrapper
  simulationRunsWrappers.forEach { it.blocks = loadBlocks(it.mapDataFile).toList() }

  // Check that every SimulationRunsWrapper has dynamic data files loaded
  simulationRunsWrappers.forEach {
    check(it.dynamicDataFiles.any()) {
      "The SimulationRunsWrapper with map data file '${it.mapDataFile}' has no dynamic files. Dynamic file " +
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
      val simulationRunsWrapper = simulationRunsWrappersDeque.first()
      // Remove current simulationRunsWrapper only if there is no dynamic data file left to analyze
      if (simulationRunsWrapper.dynamicDataFilesArrayDeque.size == 1) {
        simulationRunsWrappersDeque.removeFirst()
      }
      /** Holds the current [InputStream] of the next dynamic data file to be calculated */
      val currentDynamicDataPath = simulationRunsWrapper.dynamicDataFilesArrayDeque.removeFirst()

      println("Reading simulation run file: ${currentDynamicDataPath.toUri()}")

      /** Holds the current simulationRun object */
      val simulationRun = getJsonContentOfPath<List<JsonTickData>>(currentDynamicDataPath)

      // Calculate Blocks for current file and add each Segment to the Sequence
      segmentBuffer.addAll(
          sliceRunIntoSegments(
              simulationRunsWrapper.blocks,
              simulationRun,
              useEveryVehicleAsEgo,
              currentDynamicDataPath.fileName.toString(),
              minSegmentTickCount))
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
  /** Holds the decoded list of [JsonBlock]s from the specified [mapDataFile] */
  val jsonBlocks = getJsonContentOfPath<List<JsonBlock>>(mapDataFile)

  return calculateStaticBlocks(jsonBlocks, mapDataFile.fileName.toString()).asSequence()
}

/**
 * Returns the parsed Json content for the given [inputFilePath]. Currently supported file
 * extensions: ".json", ".zip". The generic parameter [T] specifies the class to which the content
 * should be parsed to
 */
inline fun <reified T> getJsonContentOfPath(inputFilePath: Path): T {
  // Create JsonBuilder with correct settings
  val jsonBuilder = Json {
    prettyPrint = true
    isLenient = true
    serializersModule = CarlaDataSerializerModule
  }

  // Check if inputFilePath exists
  check(inputFilePath.exists()) { "The given file path does not exist: ${inputFilePath.toUri()}" }

  // Check whether the given inputFilePath is a directory
  check(!inputFilePath.isDirectory()) {
    "Cannot get InputStream for directory. Path: $inputFilePath"
  }

  // If ".json"-file: Just return InputStream of file
  if (inputFilePath.extension == "json") {
    return jsonBuilder.decodeFromStream<T>(inputFilePath.inputStream())
  }

  // if ".zip"-file: Extract single archived file
  if (inputFilePath.extension == "zip") {
    // https://stackoverflow.com/a/46644254
    ZipFile(File(inputFilePath.toUri())).use { zip ->
      zip.entries().asSequence().forEach { entry ->
        // Add InputStream to inputStreamBuffer
        return jsonBuilder.decodeFromStream<T>(zip.getInputStream(entry))
      }
    }
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
  /**
   * Holds a [ArrayDeque] of [Pair]s of an [InputStream] and the related [Path]. Is only available
   * after initializing [dynamicDataInputStreamsList]
   */
  val dynamicDataFilesArrayDeque = ArrayDeque(dynamicDataFiles)
}
