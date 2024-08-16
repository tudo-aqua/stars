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
import java.nio.file.Path
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.DEFAULT_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.groundTruthFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder

fun saveAsJSONFile(filePathWithExtension: String, jsonContent: String) {
  File(filePathWithExtension).apply {
    createNewFile()
    writeText(jsonContent)
  }
}

fun saveAsJSONFile(serializableResult: SerializableResult) {
  val resultFolder =
      "${serializedResultsFolder}/${applicationStartTimeString}/${serializableResult.source}"
          .also { File(it).mkdirs() }
  saveAsJSONFile(
      "$resultFolder/${serializableResult.identifier?:DEFAULT_SERIALIZED_RESULT_IDENTIFIER}.json",
      serializableResult.getJsonString())
}

fun saveAsJSONFile(
    serializableResultComparison: SerializableResultComparison,
    comparedToGroundTruth: Boolean
) {
  val resultFolder =
      "${comparedResultsFolder}/${applicationStartTimeString}/${if(comparedToGroundTruth){"/ground-truth"}else{"/last-evaluation"}}/${serializableResultComparison.source}"
          .also { File(it).mkdirs() }
  saveAsJSONFile(
      "$resultFolder/comparison_${serializableResultComparison.identifier}.json",
      serializableResultComparison.getJsonString())
}

fun getLatestSerializationResultPath(): Path? {
  val resultFolder = File(serializedResultsFolder)
  return resultFolder
      .listFiles()
      ?.filter { it.name != groundTruthFolder && it.name != applicationStartTimeString }
      ?.sortedByDescending { it.name }
      ?.firstOrNull()
      ?.toPath()
}

fun getGroundTruthSerializationResultPath(): Path? {
  val resultFolder = File(serializedResultsFolder)
  return resultFolder
      .listFiles()
      ?.filter { it.name == groundTruthFolder }
      ?.sortedByDescending { it.name }
      ?.firstOrNull()
      ?.toPath()
}

fun getSerializedResultFromFileSystem(
    rootFolderPath: Path,
    serializableResults: List<SerializableResult>
): List<SerializableResult> =
    serializableResults.map { getSerializedResultFromFileSystem(rootFolderPath, it) }

fun getSerializedResultFromFileSystem(
    rootFolderPath: Path,
    serializableResult: SerializableResult
): SerializableResult {
  val serializedResultFile =
      File(
          "${rootFolderPath}/${serializableResult.source}/${serializableResult.identifier?:DEFAULT_SERIALIZED_RESULT_IDENTIFIER}.json")
  check(serializedResultFile.exists())
  return SerializableResult.getJsonContentOfPath(serializedResultFile.toPath())
}
