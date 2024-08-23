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

package tools.aqua.stars.core.metric.serialization

import kotlinx.serialization.Serializable
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict.*

/** This class defines the structure for all comparisons between two [SerializableResult]s. */
@Serializable
data class SerializableResultComparison(
    /** The [SerializableResultComparisonVerdict] of this comparison. */
    val verdict: SerializableResultComparisonVerdict,
    /** The source from which the compared [SerializableResult]s came from. */
    val source: String,
    /** The source from which the compared [SerializableResult]s came from. */
    val identifier: String,
    /** The value of the old [SerializableResult]. */
    val oldValue: String,
    /** The value of the [SerializableResult] that was produced during this evaluation. */
    val newValue: String
) {
  companion object {
    /**
     * Returns whether all [SerializableResultComparison]s in this list have a verdict of
     * [EQUAL_RESULTS], [NEW_METRIC_SOURCE] or [NEW_IDENTIFIER], i.e. there was no miss or mismatch
     * against compared data.
     */
    fun List<SerializableResultComparison>.noMismatch(): Boolean = all {
      it.verdict in listOf(EQUAL_RESULTS, NEW_METRIC_SOURCE, NEW_IDENTIFIER)
    }
  }
}
