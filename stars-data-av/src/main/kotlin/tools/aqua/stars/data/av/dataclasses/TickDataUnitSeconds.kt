/*
 * Copyright 2023-2025 The STARS Project Authors
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

import tools.aqua.stars.core.types.TickUnit

/**
 * Implementation of the [TickUnit] interface for 'seconds' units.
 *
 * @property tickSeconds Current tick value in seconds.
 */
class TickDataUnitSeconds(val tickSeconds: Double) :
    TickUnit<TickDataUnitSeconds, TickDataDifferenceSeconds> {
  override fun plus(other: TickDataDifferenceSeconds): TickDataUnitSeconds =
      TickDataUnitSeconds(this.tickSeconds + other.differenceSeconds)

  override fun minus(other: TickDataDifferenceSeconds): TickDataUnitSeconds =
      TickDataUnitSeconds(this.tickSeconds - other.differenceSeconds)

  override fun minus(other: TickDataUnitSeconds): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(this.tickSeconds - other.tickSeconds)

  override fun compareTo(other: TickDataUnitSeconds): Int =
      this.tickSeconds.compareTo(other.tickSeconds)

  override fun toString(): String = "${this.tickSeconds}s"

  override fun equals(other: Any?): Boolean =
      if (other is TickDataUnitSeconds) this.tickSeconds == other.tickSeconds
      else super.equals(other)

  override fun hashCode(): Int = this.tickSeconds.hashCode()
}
