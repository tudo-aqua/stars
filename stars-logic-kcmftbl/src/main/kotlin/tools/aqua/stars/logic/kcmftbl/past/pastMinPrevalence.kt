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

package tools.aqua.stars.logic.kcmftbl.past

import tools.aqua.stars.core.types.*
import tools.aqua.stars.logic.kcmftbl.Interval
import tools.aqua.stars.logic.kcmftbl.Interval.Companion.isAfter
import tools.aqua.stars.logic.kcmftbl.Interval.Companion.isBefore

/**
 * CMFTBL implementation of the 'pastMinPrevalence' operator i.e. "In all past ticks in the interval
 * phi holds for at least ([percentage]*100)% of the ticks in the interval".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param tickData Current [TickDataType].
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
> pastMinPrevalence(
    tickData: T,
    percentage: Double,
    interval: Interval<D>? = null,
    phi: (T) -> Boolean,
): Boolean {
  require(percentage in 0.0..1.0) { "The percentage must be within [0.0, 1.0]" }

  val now = tickData.currentTickUnit
  var tickCount = 0
  var trueCount = 0

  for (tick in tickData.backward()) {
    val pos = now - tick.currentTickUnit

    // Check if the current tick is before the start of the interval
    if (pos.isBefore(interval)) continue

    // Check if the current tick is after the end of the interval
    if (pos.isAfter(interval)) break

    // Count the tick if the predicate holds true
    if (phi(tick)) trueCount++

    tickCount++
  }

  return trueCount >= tickCount * percentage
}
