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

import java.io.File as File
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder

/**
 * Extension function for [String] to save it as a Json file at the [filePathWithExtension].
 *
 * @param filePathWithExtension The name of the file into which the calling [String] should be
 *   saved.
 * @return The created [File].
 * @throws IllegalArgumentException When there is already a file at [filePathWithExtension].
 */
fun String.saveAsJsonFile(filePathWithExtension: String): File {
  var filePath = filePathWithExtension
  if (File(filePathWithExtension).extension.isBlank()) {
    filePath += ".json"
  }
  val file = File(filePath)
  require(!file.exists()) { "The file already exists! File at path: '$file'" }
  file.apply {
    parentFile.mkdirs()
    createNewFile()
    writeText(this@saveAsJsonFile)
  }
  return file
}

/**
 * Extension function for [SerializableResult] to save it as a Json file. This function builds the
 * resulting file path by the values in the calling [SerializableResult] and passes it to
 * [saveAsJsonFile] for [String]s.
 *
 * @return The created [File].
 */
fun SerializableResult.saveAsJsonFile(): File {
  val resultingPath =
      "$serializedResultsFolder/$applicationStartTimeString/$source/$identifier.json"
  getJsonString().saveAsJsonFile(resultingPath)
  return File(resultingPath)
}

/**
 * Extension function for [SerializableResultComparison] to save it as a Json file. This function
 * builds the resulting file path by the values in the calling [SerializableResultComparison] and
 * passes it to [saveAsJsonFile] for [String]s.
 *
 * @param comparedToGroundTruth Sets whether the [SerializableResultComparison] were created by the
 *   comparison to ground the ground truth result set, or to the latest evaluation run. This alters
 *   the resulting folder.
 * @return The created [File].
 */
fun SerializableResultComparison.saveAsJsonFile(comparedToGroundTruth: Boolean): File {
  val resultingPath =
      "${comparedResultsFolder}/" +
          "${applicationStartTimeString}/" +
          "${if(comparedToGroundTruth){"/ground-truth"}else{"/latest-evaluation"}}/" +
          "${source}/" +
          "[${verdict.shortString}]_comparison_${identifier}.json"
  getJsonString().saveAsJsonFile(resultingPath)
  return File(resultingPath)
}

/**
 * Returns a [Map] of [String] to [List] of [SerializableResult] with the following structure:
 * Map(source, List(SerializableResult)), where `source` equals the found
 * [SerializableResult.source]s in the given [root] directory. To each source, all deserialized
 * [SerializableResult]s are stored in the [List].
 *
 * @param root The root folder to search for serialized results, i.e. "/ground-truth/".
 * @return A map of [SerializableResult.source]s and their respective serialized results.
 */
fun getSerializedResults(root: File?): Map<String, List<SerializableResult>> =
    root?.listFiles()?.associate { sourceDir ->
      sourceDir.name to
          sourceDir.listFiles().map { SerializableResult.getJsonContentOfDirectory(it) }
    } ?: emptyMap()

/**
 * Returns the [File] pointing to the root directory of the ground truth result data set. When no
 * such directory was found, `null` is returned.
 *
 * @return A [File] pointing to the root directory of the ground truth result data set. Otherwise,
 *   null.
 */
fun getGroundTruthSerializationResultDirectory(): File? =
    File(serializedResultsFolder).listFiles()?.firstOrNull {
      it.name == GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER
    }

/**
 * Returns the [File] pointing to the root directory of the latest evaluation result directory. When
 * no such directory was found, `null` is returned.
 *
 * @return A [File] pointing to the root directory of the latest evaluation result directory.
 *   Otherwise, null.
 */
fun getLatestSerializationResultDirectory(): File? =
    File(serializedResultsFolder)
        .listFiles()
        ?.filter {
          it.name != GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER &&
              it.name != applicationStartTimeString
        }
        ?.maxByOrNull { it.name }

/**
 * Holds the [Map] of all ground-truth sources with their deserialized [SerializableResult]s, or
 * null when the ground truth was not demanded.
 */
private var groundTruthCache: Map<String, List<SerializableResult>>? = null

/** Holds the [Map] of all ground-truth sources with their deserialized [SerializableResult]s. */
val groundTruth: Map<String, List<SerializableResult>>
  get() =
      groundTruthCache
          ?: getSerializedResults(getGroundTruthSerializationResultDirectory()).also {
            groundTruthCache = it
          }

/**
 * Holds the [Map] of all latest evaluation result sources with their deserialized
 * [SerializableResult]s, or null when the latest evaluations results were not demanded.
 */
private var latestResultsCache: Map<String, List<SerializableResult>>? = null

/**
 * Holds the [Map] of all latest evaluation result sources with their deserialized
 * [SerializableResult]s.
 */
val latestResults: Map<String, List<SerializableResult>>
  get() =
      latestResultsCache
          ?: getSerializedResults(getLatestSerializationResultDirectory()).also {
            latestResultsCache = it
          }
