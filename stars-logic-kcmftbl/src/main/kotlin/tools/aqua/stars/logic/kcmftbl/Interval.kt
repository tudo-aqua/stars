/*
 * Copyright 2025-2026 The STARS Project Authors
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

package tools.aqua.stars.logic.kcmftbl

import tools.aqua.stars.core.types.TickDifference

/**
 * Interval class representing a time interval with a start and end [TickDifference].
 *
 * @param D [TickDifference].
 * @property start The start of the interval (inclusive).
 * @property end The end of the interval (inclusive).
 */
data class Interval<D : TickDifference<D>>(
    val start: D,
    val end: D,
) {

  init {
    require(start <= start + start) { "The first value of the interval must be non-negative." }
    require(start < end) {
      "The second value of the interval must be greater than the first value."
    }
  }

  /** Secondary constructor to create an [Interval] from a [Pair] of [TickDifference]s. */
  constructor(interval: Pair<D, D>) : this(interval.first, interval.second)

  /** Companion object for [Interval] utility functions. */
  companion object {
    /**
     * Checks if the current tick is before the [interval].
     *
     * @param D [TickDifference].
     * @param interval The interval to check against.
     * @return True if the current tick is before the interval start, false otherwise. False if the
     *   interval is null.
     */
    fun <D : TickDifference<D>> D.isBefore(interval: Interval<D>?): Boolean =
        interval != null && this < interval.start

    /**
     * Checks if the current tick is after the [interval].
     *
     * @param D [TickDifference].
     * @param interval The interval to check against.
     * @return True if the current tick is after the interval end, false otherwise. False if the
     *   interval is null.
     */
    fun <D : TickDifference<D>> D.isAfter(interval: Interval<D>?): Boolean =
        interval != null && this > interval.end

    /**
     * Checks if the current tick is in the [interval].
     *
     * @param D [TickDifference].
     * @param interval The interval to check against.
     * @return True if the current tick is in the interval, false otherwise. False if the interval
     *   is null.
     */
    fun <D : TickDifference<D>> D.isIn(interval: Interval<D>?): Boolean =
        interval != null && !isBefore(interval) && !isAfter(interval)

    /**
     * Checks if the current tick is not in the [interval].
     *
     * @param D [TickDifference].
     * @param interval The interval to check against.
     * @return True if the current tick is not in the interval, false otherwise. False if the
     *   interval is null.
     */
    fun <D : TickDifference<D>> D.isNotIn(interval: Interval<D>?): Boolean =
        isBefore(interval) || isAfter(interval)

    /** Extension function to create an [Interval] using the range operator. */
    operator fun <D : TickDifference<D>> D.rangeTo(other: D): Interval<D> = Interval(this, other)
  }
}
