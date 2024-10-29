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

package tools.aqua.stars.data.av.dataclasses

import tools.aqua.stars.core.types.TickDifference

/**
 * Implementation of the [TickDifference] interface for 'milliseconds' units.
 *
 * @property differenceMillis Difference in milliseconds.
 */
data class TickDataDifferenceMilliseconds(val differenceMillis: Long) :
    TickDifference<TickDataDifferenceMilliseconds> {
  override fun compareTo(other: TickDataDifferenceMilliseconds): Int =
      this.differenceMillis.compareTo(other.differenceMillis)

  override fun plus(other: TickDataDifferenceMilliseconds): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(this.differenceMillis + other.differenceMillis)

  override fun minus(other: TickDataDifferenceMilliseconds): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(this.differenceMillis - other.differenceMillis)

  override fun serialize(): String = this.differenceMillis.toString()

  override fun deserialize(str: String): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(str.toLong())

  override fun toString(): String =
      "TickDataDifferenceMilliseconds(difference: ${this.differenceMillis})"

  override fun equals(other: Any?): Boolean =
      if (other is TickDataDifferenceMilliseconds) this.differenceMillis == other.differenceMillis
      else super.equals(other)

  override fun hashCode(): Int = this.differenceMillis.hashCode()
}
