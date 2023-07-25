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
 * Return an array deque of [Segment]s given a path to a [mapDataFile] in combination with a
 * [dynamicDataFile] path. [useEveryVehicleAsEgo] lets you decide whether to use every vehicle to be
 * used as the ego vehicle. This will multiply the size of the resulting sequence of [Segment]s by
 * the number of vehicles.
 */
fun loadSegments(
    mapDataFile: Path,
    dynamicDataFile: Path,
    useEveryVehicleAsEgo: Boolean = false
): Sequence<Segment> {
  /** Holds the converted [Block]s based on the [mapDataFile] path */
  val blocks = loadBlocks(mapDataFile).toList()

  /** Holds the [InputStream]s for the [dynamicDataFile] */
  val dynamicDataFileInputStreams = getInputStreamsFromFile(dynamicDataFile)

  check(dynamicDataFileInputStreams.any()) {
    "There are no readable files at path: $dynamicDataFile"
  }

  val segmentBuffer = ArrayDeque<Segment>()

  return generateSequence {
    if (segmentBuffer.size > 0) {
      return@generateSequence segmentBuffer.removeFirst()
    }
    if (dynamicDataFileInputStreams.size > 0) {
      /** Holds the current [InputStream] and [Path] pair of the current dynamic data file */
      val dataInputStream = dynamicDataFileInputStreams.removeFirst()
      /** Holds the current simulationRun object */
      val simulationRun = jsonBuilder.decodeFromStream<List<JsonTickData>>(dataInputStream.first)

      // Calculate Blocks for current file and add each Segment to the Sequence
      segmentBuffer.addAll(
          sliceRunIntoSegments(
              blocks,
              simulationRun,
              useEveryVehicleAsEgo,
              dataInputStream.second.fileName.toString()))
      return@generateSequence segmentBuffer.removeFirst()
    }
    // If there are no Segments nor Files to process, return null to indicate the end of the Sequence
    return@generateSequence null
  }
}

fun loadSegments(
    mapDataFile: Path,
    dynamicDataFiles: List<Path>,
    useEveryVehicleAsEgo: Boolean = false
): Sequence<Segment> {
  /** Holds the converted [Block]s based on the [mapDataFile] path */
  val blocks = loadBlocks(mapDataFile)
  return sequenceOf()
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
