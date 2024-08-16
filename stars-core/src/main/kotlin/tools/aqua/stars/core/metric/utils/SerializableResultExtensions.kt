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

import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict.NO_MATCHING_RESULT
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.DEFAULT_SERIALIZED_RESULT_IDENTIFIER

fun List<SerializableResult>.compareTo(
    otherResult: SerializableResult
): SerializableResultComparison? = this.firstNotNullOfOrNull { it.compareTo(otherResult) }

fun List<SerializableResult>.compareTo(
    otherResults: List<SerializableResult>
): List<SerializableResultComparison> {
  val comparedThisToOther: List<SerializableResultComparison> =
      this.map { thisResult ->
        otherResults.firstNotNullOfOrNull { otherResult -> thisResult.compareTo(otherResult) }
            ?: SerializableResultComparison(
                verdict = NO_MATCHING_RESULT,
                source = thisResult.source,
                identifier = thisResult.identifier ?: DEFAULT_SERIALIZED_RESULT_IDENTIFIER,
                newValue = thisResult.value.toString(),
                oldValue = "None")
      }
  val comparedOtherToThis =
      otherResults.map { otherResult ->
        this.firstNotNullOfOrNull { thisResult -> thisResult.compareTo(otherResult) }
            ?: SerializableResultComparison(
                verdict = NO_MATCHING_RESULT,
                source = otherResult.source,
                identifier = otherResult.identifier ?: DEFAULT_SERIALIZED_RESULT_IDENTIFIER,
                newValue = otherResult.value.toString(),
                oldValue = "None")
      }
  return (comparedThisToOther + comparedOtherToThis).distinct()
}
