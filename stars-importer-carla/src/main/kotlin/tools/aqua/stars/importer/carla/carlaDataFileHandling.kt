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
import kotlin.io.path.nameWithoutExtension
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
 * Returns a [Sequence] of [TickSequence]s given a [List] of [CarlaSimulationRunsWrapper]s. Each
 * [CarlaSimulationRunsWrapper] contains the information about the used map data and the dynamic
 * data, each as [Path]s.
 *
 * @param simulationRunsWrappers The [List] of [CarlaSimulationRunsWrapper]s that wrap the map data
 *   to its dynamic data.
 * @param bufferSize The size of the buffer for each [TickSequence]. This is the window of ticks
 *   that gets supplied to the TSCEvaluation.
 * @param orderFilesBySeed Whether the dynamic data files should be sorted by their seeds instead of
 *   the map.
 * @return A [Sequence] of [TickSequence]s based on the given [simulationRunsWrappers].
 */
fun loadTicks(
    simulationRunsWrappers: List<CarlaSimulationRunsWrapper>,
    bufferSize: Int = 100,
    orderFilesBySeed: Boolean = false
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

  val simulationRunsWrapperIterator = simulationRunsWrappers.iterator()
  var currentSimulationRunsWrapper = simulationRunsWrapperIterator.next()
  var currentDynamicDataPathIterator = currentSimulationRunsWrapper.dynamicDataFiles.iterator()

  return generateSequence {
    if (!currentDynamicDataPathIterator.hasNext() && simulationRunsWrapperIterator.hasNext()) {
      currentSimulationRunsWrapper = simulationRunsWrapperIterator.next()
      currentDynamicDataPathIterator = currentSimulationRunsWrapper.dynamicDataFiles.iterator()
    }

    if (currentDynamicDataPathIterator.hasNext()) {
      val currentDynamicDataPath = currentDynamicDataPathIterator.next()
      println("Reading simulation run file: ${currentDynamicDataPath.toUri()}")

      // Holds the current simulationRun object
      val simulationRun = getJsonContentOfPath<List<JsonTickData>>(currentDynamicDataPath)

      // Calculate TickData objects from JSON
      val ticks =
          convertTickData(
              world = currentSimulationRunsWrapper.world,
              jsonSimulationRun = simulationRun,
              simulationRunId = currentDynamicDataPath.nameWithoutExtension)

      val iterator = ticks.iterator()
      return@generateSequence TickSequence(bufferSize) {
        if (iterator.hasNext()) iterator.next() else null
      }
    }

    // No more dynamic data files left to process. Terminate the sequence.
    return@generateSequence null
  }
}

/**
 * Returns a [Sequence] of [TickSequence]s given a path to a [mapDataFile] in combination with a
 * [dynamicDataFile] path.
 *
 * @param mapDataFile The [Path] to map data file containing all static information.
 * @param dynamicDataFile The [Path] to the data file which contains the timed state data for the
 *   simulation.
 * @param bufferSize The size of the buffer for each [TickSequence]. This is the window of ticks
 *   that gets supplied to the TSCEvaluation.
 * @param orderFilesBySeed Whether the dynamic data files should be sorted by their seeds instead of
 *   the map.
 * @return A [Sequence] of [TickSequence]s based on the given [mapDataFile] and [dynamicDataFile].
 */
fun loadTicks(
    mapDataFile: Path,
    dynamicDataFile: Path,
    bufferSize: Int = 100,
    orderFilesBySeed: Boolean = false
): Sequence<TickSequence<TickData>> =
    loadTicks(
        simulationRunsWrappers =
            listOf(CarlaSimulationRunsWrapper(mapDataFile, listOf(dynamicDataFile))),
        bufferSize = bufferSize,
        orderFilesBySeed = orderFilesBySeed)

/**
 * Returns a [Sequence] of [TickSequence]s given a path to a [mapDataFile] in combination with a
 * [List] of [dynamicDataFiles] paths.
 *
 * @param mapDataFile The [Path] to map data file containing all static information.
 * @param dynamicDataFiles The [List] of [Path]s to the data files which contain the timed state
 *   data for the simulation.
 * @param bufferSize The size of the buffer for each [TickSequence]. This is the window of ticks
 *   that gets supplied to the TSCEvaluation.
 * @param orderFilesBySeed Whether the dynamic data files should be sorted by their seeds instead of
 *   the map.
 * @return A [Sequence] of [TickSequence]s based on the given [mapDataFile] and [dynamicDataFiles].
 */
fun loadTicks(
    mapDataFile: Path,
    dynamicDataFiles: List<Path>,
    bufferSize: Int = 100,
    orderFilesBySeed: Boolean = false
): Sequence<TickSequence<TickData>> =
    loadTicks(
        simulationRunsWrappers = listOf(CarlaSimulationRunsWrapper(mapDataFile, dynamicDataFiles)),
        bufferSize = bufferSize,
        orderFilesBySeed = orderFilesBySeed)

/**
 * Returns a [Sequence] of [TickSequence]s given a map of static data to dynamic data files.
 *
 * @param mapToDynamicDataFiles The [World] of static data to dynamic data.
 * @param bufferSize The size of the buffer for each [TickSequence]. This is the window of ticks
 *   that gets supplied to the TSCEvaluation.
 * @param orderFilesBySeed Whether the dynamic data files should be sorted by their seeds instead of
 *   the map.
 * @return A [Sequence] of [TickSequence]s based on the given [World] of static data to dynamic
 *   data.
 */
fun loadTicks(
    mapToDynamicDataFiles: kotlin.collections.Map<Path, List<Path>>,
    bufferSize: Int = 100,
    orderFilesBySeed: Boolean = false
): Sequence<TickSequence<TickData>> =
    loadTicks(
        simulationRunsWrappers =
            mapToDynamicDataFiles.map { CarlaSimulationRunsWrapper(it.key, it.value) },
        bufferSize = bufferSize,
        orderFilesBySeed = orderFilesBySeed)
