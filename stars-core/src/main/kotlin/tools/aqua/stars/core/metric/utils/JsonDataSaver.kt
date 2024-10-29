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
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.serialization.extensions.getJsonString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.PREVIOUS_EVALUATION_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.baselineDirectory
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder

/**
 * Extension function for [String] to save it as a Json file at the [filePathWithExtension].
 * Warning: Overrides existing files.
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
 * @param comparedToBaseline Sets whether the [SerializableResultComparison] were created by the
 *   comparison to the baseline result set, or to the previous evaluation run. This alters the
 *   resulting folder.
 * @return The created [File].
 */
fun SerializableResultComparison.saveAsJsonFile(comparedToBaseline: Boolean): File {
  val resultingPath =
      "$comparedResultsFolder/$applicationStartTimeString/${
            if(comparedToBaseline){"/${baselineDirectory.replace('/', '-')}"}
            else{"/$PREVIOUS_EVALUATION_SERIALIZED_RESULT_IDENTIFIER"}
          }/${source}/[${verdict.shortString}]_comparison_${identifier}.json"
  getJsonString().saveAsJsonFile(resultingPath)
  return File(resultingPath)
}

/**
 * Extension function for [SerializableResultComparison]s to save them as Json files.
 *
 * @param comparedToBaseline Sets whether the [SerializableResultComparison]s were created by the
 *   comparison to the baseline result set, or to the previous evaluation run. This alters the
 *   resulting folder.
 * @return The created [File]s.
 * @see SerializableResultComparison.saveAsJsonFile
 */
fun List<SerializableResultComparison>.saveAsJsonFiles(comparedToBaseline: Boolean): List<File> =
    map {
      it.saveAsJsonFile(comparedToBaseline)
    }
