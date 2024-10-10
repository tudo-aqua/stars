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

/**
 * Enumeration that holds all verdicts for [SerializableResultComparison]s.
 *
 * @property shortString The abbreviation for each verdict to be used as a prefix for
 *   [SerializableResultComparison] files.
 */
enum class SerializableResultComparisonVerdict(val shortString: String) {
  /** Result file with the same identifier has been found and the results are equal. */
  EQUAL_RESULTS("EQ"),

  /** Result file with the same identifier has been found but the results are not equal. */
  NOT_EQUAL_RESULTS("NEQ"),

  /** Source found in the compared results is missing. */
  MISSING_METRIC_SOURCE("MIS"),

  /** No matching source has been found in the compared results. */
  NEW_METRIC_SOURCE("NEW"),

  /** Identifier found in the compared results is missing. */
  MISSING_IDENTIFIER("MIS"),

  /** No matching identifier has been found in the compared results. */
  NEW_IDENTIFIER("NEW")
}
