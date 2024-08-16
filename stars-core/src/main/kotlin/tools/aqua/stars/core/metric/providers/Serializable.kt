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

package tools.aqua.stars.core.metric.providers

import java.nio.file.Path
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.utils.*

interface Serializable {
  fun getSerializableResults(): List<SerializableResult>

  fun compareToLastResults(): List<SerializableResultComparison> =
      compareTo(getSerializedResultsFromFolder(getLatestSerializationResultPath()))

  fun compareToGroundTruthResults(): List<SerializableResultComparison> =
      compareTo(getSerializedResultsFromFolder(getGroundTruthSerializationResultPath()))

  fun compareTo(otherResults: List<SerializableResult>): List<SerializableResultComparison> =
      getSerializableResults().compareTo(otherResults)

  fun compareTo(otherResult: SerializableResult): SerializableResultComparison? =
      getSerializableResults().compareTo(otherResult)

  fun compareResults(resultFolderPath: Path): List<SerializableResultComparison> =
      getSerializableResults().compareTo(getSerializedResultsFromFolder(resultFolderPath))

  fun getJsonStrings(): List<String> {
    return getSerializableResults().map { it.getJsonString() }
  }

  fun writeSerializedResults() {
    getSerializableResults().forEach { saveAsJsonFile(it) }
  }
}
