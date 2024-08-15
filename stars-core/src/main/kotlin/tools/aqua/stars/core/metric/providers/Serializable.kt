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
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonResult
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.DEFAULT_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.saveAsJSONFile

interface Serializable {
  fun getSerializableResults(): SerializableResult

  fun compareResults(otherResult: SerializableResult): SerializableResultComparisonResult {
    val serializedResult = getSerializableResults()
    if (serializedResult.javaClass.name != otherResult.javaClass.name) {
      throw RuntimeException("These results cannot be compared")
    }
    return SerializableResultComparisonResult(
        areEqual = serializedResult == otherResult,
        identifier = serializedResult.identifier ?: DEFAULT_SERIALIZED_RESULT_IDENTIFIER,
        source = serializedResult.source,
        oldValue = otherResult.value.toString(),
        newValue = serializedResult.value.toString())
  }

  fun writeSerializedResults() {
    saveAsJSONFile(getSerializableResults())
  }

  fun compareAllResults(path: Path): Boolean {
    return true
  }
}
