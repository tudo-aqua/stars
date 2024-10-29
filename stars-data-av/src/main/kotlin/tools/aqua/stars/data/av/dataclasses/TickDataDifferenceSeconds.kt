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
 * Implementation of the [TickDifference] interface for 'seconds' units.
 *
 * @property differenceSeconds Difference in seconds.
 */
class TickDataDifferenceSeconds(val differenceSeconds: Double) :
    TickDifference<TickDataDifferenceSeconds> {
  override fun compareTo(other: TickDataDifferenceSeconds): Int =
      this.differenceSeconds.compareTo(other.differenceSeconds)

  override fun plus(other: TickDataDifferenceSeconds): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(this.differenceSeconds + other.differenceSeconds)

  override fun minus(other: TickDataDifferenceSeconds): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(this.differenceSeconds - other.differenceSeconds)

  override fun serialize(): String = this.differenceSeconds.toString()

  override fun deserialize(str: String): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(str.toDouble())

  override fun toString(): String =
      "TickDataDifferenceSeconds(difference: ${this.differenceSeconds})"

  override fun equals(other: Any?): Boolean =
      if (other is TickDataDifferenceSeconds) this.differenceSeconds == other.differenceSeconds
      else super.equals(other)

  override fun hashCode(): Int = this.differenceSeconds.hashCode()
}
