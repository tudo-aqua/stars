/*
 * Copyright 2024 The STARS Project Authors
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

package tools.aqua.stars.core.metric.utils

import java.io.File
import kotlinx.serialization.json.Json
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.baselineDirectory
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder

/**
 * Holds the [Map] of all baseline sources with their deserialized [SerializableResult]s, or null
 * when the baseline was not demanded.
 */
private var baselineCache: Map<String, List<SerializableResult>>? = null

/** Holds the [Map] of all baseline sources with their deserialized [SerializableResult]s. */
val baseline: Map<String, List<SerializableResult>>
  get() =
      baselineCache
          ?: getSerializedResults(getBaselineSerializationResultDirectory()).also {
            baselineCache = it
          }

/**
 * Returns the [File] pointing to the root directory of the baseline result data set. When no such
 * directory was found, `null` is returned.
 *
 * @return A [File] pointing to the root directory of the baseline result data set. Otherwise, null.
 */
fun getBaselineSerializationResultDirectory(): File? =
    File(serializedResultsFolder).listFiles()?.firstOrNull { it.name == baselineDirectory }

// endregion

// region previous Results
/**
 * Holds the [Map] of all latest evaluation result sources with their deserialized
 * [SerializableResult]s, or null when the latest evaluations results were not demanded.
 */
private var previousResultsCache: Map<String, List<SerializableResult>>? = null

/**
 * Holds the [Map] of all latest evaluation result sources with their deserialized
 * [SerializableResult]s.
 */
val previousResults: Map<String, List<SerializableResult>>
  get() =
      previousResultsCache
          ?: getSerializedResults(getPreviousSerializationResultDirectory()).also {
            previousResultsCache = it
          }

/**
 * Returns the [File] pointing to the root directory of the previous evaluation result directory.
 * When no such directory was found, `null` is returned.
 *
 * @return A [File] pointing to the root directory of the previous evaluation result directory.
 *   Otherwise, null.
 */
fun getPreviousSerializationResultDirectory(): File? =
    File(serializedResultsFolder)
        .listFiles()
        ?.filter { it.name != baselineDirectory && it.name != applicationStartTimeString }
        ?.maxByOrNull { it.name }

/**
 * Returns a deserialized [SerializableResult] for the given [file].
 *
 * @param file The [File] from which the [SerializableResult] should be deserialized from.
 * @return The deserialized [SerializableResult] from the given [file].
 */
fun getJsonContentOfFile(file: File): SerializableResult {
  // Check if inputFilePath exists
  check(file.exists()) { "The given file path does not exist: ${file.path}" }

  // Check whether the given inputFilePath is a directory
  check(!file.isDirectory()) { "Cannot get InputStream for directory. Path: $file" }

  // If ".json"-file: Just return InputStream of file
  if (file.extension == "json") {
    return getJsonContentFromString(file.readText())
  }

  // If none of the supported file extensions is present, throw an Exception
  error("Unexpected file extension: ${file.extension}. Supported extensions: '.json'")
}

/**
 * Returns a deserialized [SerializableResult] for the given [String].
 *
 * @param content The [String] content from which the [SerializableResult] should be deserialized
 *   from.
 * @return The deserialized [SerializableResult] from the given [String].
 */
fun getJsonContentFromString(content: String): SerializableResult =
    Json.decodeFromString<SerializableResult>(content)

/**
 * Returns a [Map] of [String] to [List] of [SerializableResult] with the following structure:
 * Map(source, List(SerializableResult)), where `source` equals the found
 * [SerializableResult.source]s in the given [root] directory. To each source, all deserialized
 * [SerializableResult]s are stored in the [List].
 *
 * @param root The root folder to search for serialized results, i.e. "/baseline/".
 * @return A map of [SerializableResult.source]s and their respective serialized results.
 */
fun getSerializedResults(root: File?): Map<String, List<SerializableResult>> =
    root?.listFiles()?.associate { sourceDir ->
      sourceDir.name to (sourceDir.listFiles()?.map { getJsonContentOfFile(it) } ?: emptyList())
    } ?: emptyMap()
