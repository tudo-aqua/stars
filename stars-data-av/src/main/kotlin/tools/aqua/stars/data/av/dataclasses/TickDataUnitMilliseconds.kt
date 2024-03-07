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

import tools.aqua.stars.core.types.TickUnit

/**
 * Json format containing tick data in milliseconds.
 *
 * @property tickMillis Current tick value in milliseconds.
 */
data class TickDataUnitMilliseconds(val tickMillis: Long) :
    TickUnit<TickDataUnitMilliseconds, TickDataDifferenceMilliseconds> {
  override fun compareTo(other: TickDataUnitMilliseconds): Int =
      tickMillis.compareTo(other.tickMillis)

  override fun minus(other: TickDataUnitMilliseconds): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(tickMillis - other.tickMillis)

  override fun minus(other: TickDataDifferenceMilliseconds): TickDataUnitMilliseconds =
      TickDataUnitMilliseconds(tickMillis - other.differenceMillis)

  override fun plus(other: TickDataDifferenceMilliseconds): TickDataUnitMilliseconds =
      TickDataUnitMilliseconds(tickMillis + other.differenceMillis)

  override fun toString(): String = "TickDataUnitMilliseconds(milliSeconds: $tickMillis)"

  override fun equals(other: Any?): Boolean =
      if (other is TickDataUnitMilliseconds) tickMillis == other.tickMillis else super.equals(other)

  override fun hashCode(): Int = tickMillis.hashCode()
}
