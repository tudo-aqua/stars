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
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.DEFAULT_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString

fun saveAsJSONFile(serializableResult: SerializableResult) {
  val resultFolder = getAndCreateJSONFolder(source = serializableResult.source)
  File("$resultFolder/${serializableResult.identifier?:DEFAULT_SERIALIZED_RESULT_IDENTIFIER}.json")
      .apply {
        createNewFile()
        writeText(serializableResult.getJsonString())
      }
}

private fun getAndCreateJSONFolder(source: String): String =
    "${ApplicationConstantsHolder.SERIALIZED_RESULTS_FOLDER}/${applicationStartTimeString}/$source"
        .also { File(it).mkdirs() }

fun getLatestSerializationResultPath(): Path? {
  val resultFolder = File(ApplicationConstantsHolder.SERIALIZED_RESULTS_FOLDER)
  return resultFolder
      .listFiles()
      ?.filter {
        it.name != GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER &&
            it.name != applicationStartTimeString
      }
      ?.sortedByDescending { it.name }
      ?.firstOrNull()
      ?.toPath()
}

fun getGroundTruthSerializationResultPath(): Path? {
  val resultFolder = File(ApplicationConstantsHolder.SERIALIZED_RESULTS_FOLDER)
  return resultFolder
      .listFiles()
      ?.filter { it.name == GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER }
      ?.sortedByDescending { it.name }
      ?.firstOrNull()
      ?.toPath()
}

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
