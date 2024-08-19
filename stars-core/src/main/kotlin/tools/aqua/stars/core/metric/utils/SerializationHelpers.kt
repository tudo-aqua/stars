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
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.DEFAULT_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.groundTruthFolder
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
      "${serializedResultsFolder}/${applicationStartTimeString}/${serializableResult.source}/${serializableResult.identifier?:DEFAULT_SERIALIZED_RESULT_IDENTIFIER}.json"
  saveAsJsonFile(resultingPath, serializableResult.getJsonString())
  return File(resultingPath)
}

fun saveAsJsonFile(
    serializableResultComparison: SerializableResultComparison,
    comparedToGroundTruth: Boolean
): Path {
  val resultingPath =
      "${comparedResultsFolder}/${applicationStartTimeString}/${if(comparedToGroundTruth){"/ground-truth"}else{"/last-evaluation"}}/${serializableResultComparison.source}/comparison_${serializableResultComparison.identifier}.json"
  saveAsJsonFile(resultingPath, serializableResultComparison.getJsonString())
  return File(resultingPath).toPath()
}

fun getLatestSerializationResultPath(): File? {
  val resultFolder = File(serializedResultsFolder)
  return resultFolder
      .listFiles()
      ?.filter { it.name != groundTruthFolder && it.name != applicationStartTimeString }
      ?.sortedByDescending { it.name }
      ?.firstOrNull()
}

fun getSourcesOfLatestSerializationResults(): List<String>? =
    getSourcesOfDirectory(getLatestSerializationResultPath())

fun getSourcesOfGroundTruthSerializationResults(): List<String>? =
    getSourcesOfDirectory(getGroundTruthSerializationResultPath())

private fun getSourcesOfDirectory(directory: File?): List<String>? =
    directory?.listFiles()?.filter { it.isDirectory }?.map { it.name }

fun getGroundTruthSerializationResultPath(): File? {
  val resultFolder = File(serializedResultsFolder)
  return resultFolder
      .listFiles()
      ?.filter { it.name == groundTruthFolder }
      ?.sortedByDescending { it.name }
      ?.firstOrNull()
}

fun getSerializedResultsFromFolder(
    rootFolderPath: File?,
    serializableResult: SerializableResult
): List<SerializableResult> =
    getSerializedResultsFromFolder(File("${rootFolderPath}/${serializableResult.source}"))

fun getSerializedResultsFromFolder(folderPath: File?): List<SerializableResult> =
    folderPath?.listFiles()?.map { SerializableResult.getJsonContentOfDirectory(it) } ?: emptyList()
