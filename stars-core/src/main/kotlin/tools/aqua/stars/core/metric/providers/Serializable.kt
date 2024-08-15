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
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.DEFAULT_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.getSerializedResultFromFileSystem
import tools.aqua.stars.core.metric.utils.saveAsJSONFile

interface Serializable {
  fun getSerializableResults(): List<SerializableResult>

  fun compareTo(otherResult: SerializableResult): SerializableResultComparison {
    val serializedResults = getSerializableResults()
    serializedResults.forEach { serializedResult ->
      if (serializedResult.javaClass.name != otherResult.javaClass.name) {
        return@forEach
      }
      if (serializedResult.source != otherResult.source ||
          serializedResult.identifier != otherResult.identifier) {
        return@forEach
      }
      return SerializableResultComparison(
          areEqual = serializedResult == otherResult,
          identifier = serializedResult.identifier ?: DEFAULT_SERIALIZED_RESULT_IDENTIFIER,
          source = serializedResult.source,
          oldValue = otherResult.value.toString(),
          newValue = serializedResult.value.toString())
    }
    throw IllegalArgumentException("There were no results that were comparable.")
  }

  fun writeSerializedResults() {
    getSerializableResults().forEach { saveAsJSONFile(it) }
  }

  fun compareResults(resultFolderPath: Path): List<SerializableResultComparison> =
      getSerializableResults().map {
        val deserializedResult = getSerializedResultFromFileSystem(resultFolderPath, it)
        compareTo(deserializedResult)
      }
}
