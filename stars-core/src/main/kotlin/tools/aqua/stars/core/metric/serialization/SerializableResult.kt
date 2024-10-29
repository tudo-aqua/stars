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
import tools.aqua.stars.core.metric.serialization.extensions.getJsonString

/**
 * This interface defines the base structure for all [SerializableResult]s that are produced by
 * implementing classes of the [Serializable] interface.
 */
@Serializable
sealed class SerializableResult {
  /** The identifier of this specific result. */
  abstract val identifier: String
  /** The source (i.e. the metric) which produced this result. */
  abstract val source: String
  /** The value that should be serialized. */
  abstract val value: Any

  abstract override fun equals(other: Any?): Boolean

  abstract override fun hashCode(): Int

  override fun toString(): String = getJsonString()

  /**
   * Compares this [SerializableResult] to the given [otherResult] and produces a possible
   * [SerializableResultComparison].
   *
   * No [SerializableResultComparison] is created when either:
   * 1. The [SerializableResult]s have different implementation (i.e. have incomparable [value]s.
   * 2. The [source] and [identifier] do not match.
   *
   * @param otherResult The [SerializableResult] to which this [SerializableResult] is compared
   *   with.
   * @return The [SerializableResultComparison] when the [SerializableResult]s are comparable.
   *   Otherwise, null.
   */
  fun compareTo(otherResult: SerializableResult): SerializableResultComparison? {
    if (javaClass.name != otherResult.javaClass.name) {
      return null
    }
    if (source != otherResult.source || identifier != otherResult.identifier) {
      return null
    }
    return SerializableResultComparison(
        verdict = if (this == otherResult) EQUAL_RESULTS else NOT_EQUAL_RESULTS,
        identifier = identifier,
        source = source,
        oldValue = otherResult.value.toString(),
        newValue = value.toString())
  }
}
