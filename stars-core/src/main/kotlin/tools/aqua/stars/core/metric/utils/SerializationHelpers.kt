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
import java.nio.file.Path
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder

fun saveAsJsonFile(filePathWithExtension: String, jsonContent: String): File {
  var filePath = filePathWithExtension
  if (File(filePathWithExtension).extension == "") {
    filePath += ".json"
  }
  val file = File(filePath)
  check(!file.exists()) { "The file already exists! File at path: '$file'" }
  file.apply {
    parentFile.mkdirs()
    createNewFile()
    writeText(jsonContent)
  }
  return file
}

fun saveAsJsonFile(serializableResult: SerializableResult): File {
  val resultingPath =
      "${serializedResultsFolder}/${applicationStartTimeString}/${serializableResult.source}/${serializableResult.identifier}.json"
  saveAsJsonFile(resultingPath, serializableResult.getJsonString())
  return File(resultingPath)
}

fun saveAsJsonFile(
    serializableResultComparison: SerializableResultComparison,
    comparedToGroundTruth: Boolean
): Path {
  val resultingPath =
      "${comparedResultsFolder}/" +
          "${applicationStartTimeString}/" +
          "${if(comparedToGroundTruth){"/ground-truth"}else{"/latest-evaluation"}}/" +
          "${serializableResultComparison.source}/" +
          "[${serializableResultComparison.verdict.shortString}]_comparison_${serializableResultComparison.identifier}.json"
  saveAsJsonFile(resultingPath, serializableResultComparison.getJsonString())
  return File(resultingPath).toPath()
}

fun getSourcesOfLatestSerializationResults(): List<String>? =
    getSourcesOfDirectory(getLatestSerializationResultDirectory())

fun getSourcesOfGroundTruthSerializationResults(): List<String>? =
    getSourcesOfDirectory(getGroundTruthSerializationResultDirectory())

private fun getSourcesOfDirectory(directory: File?): List<String>? =
    directory?.listFiles()?.filter { it.isDirectory }?.map { it.name }

fun getSerializedResultsFromSourceFolder(
    rootFolderPath: File?,
    serializableResult: SerializableResult
): List<SerializableResult> =
    getSerializedResultsFromSourceFolder(File("${rootFolderPath}/${serializableResult.source}"))

fun getSerializedResultsFromSourceFolder(folderPath: File?): List<SerializableResult> =
    folderPath?.listFiles()?.map { SerializableResult.getJsonContentOfDirectory(it) } ?: emptyList()

/**
 * @param root The root folder to search for serialized results, i.e. "/ground-truth/".
 * @return A map of **sources** and their respective serialized results.
 */
fun getSerializedResults(root: File?): Map<String, List<SerializableResult>> =
    root?.listFiles()?.associate { sourceDir ->
      sourceDir.name to
          sourceDir.listFiles().map { SerializableResult.getJsonContentOfDirectory(it) }
    } ?: emptyMap()

fun getGroundTruthSerializationResultDirectory(): File? =
    File(serializedResultsFolder).listFiles()?.firstOrNull {
      it.name == GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER
    }

fun getLatestSerializationResultDirectory(): File? =
    File(serializedResultsFolder)
        .listFiles()
        ?.filter {
          it.name != GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER &&
              it.name != applicationStartTimeString
        }
        ?.maxByOrNull { it.name }

private var groundTruthCache: Map<String, List<SerializableResult>>? = null
val groundTruth: Map<String, List<SerializableResult>>
  get() =
      groundTruthCache
          ?: getSerializedResults(getGroundTruthSerializationResultDirectory()).also {
            groundTruthCache = it
          }

private var latestResultsCache: Map<String, List<SerializableResult>>? = null
val latestResults: Map<String, List<SerializableResult>>
  get() =
      latestResultsCache
          ?: getSerializedResults(getLatestSerializationResultDirectory()).also {
            latestResultsCache = it
          }
