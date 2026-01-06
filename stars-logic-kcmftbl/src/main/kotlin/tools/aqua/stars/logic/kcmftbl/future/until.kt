/*
 * Copyright 2023-2026 The STARS Project Authors
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

package tools.aqua.stars.logic.kcmftbl.future

import tools.aqua.stars.core.types.*
import tools.aqua.stars.logic.kcmftbl.Interval
import tools.aqua.stars.logic.kcmftbl.Interval.Companion.isAfter
import tools.aqua.stars.logic.kcmftbl.Interval.Companion.isBefore

/**
 * CMFTBL implementation of the 'until' operator i.e. "In all future ticks in the interval phi 1
 * holds, at least until phi 2 holds".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi1 First predicate.
 * @param phi2 Second predicate.
 */
fun <
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
> until(
    tickData: T,
    interval: Interval<D>? = null,
    phi1: (T) -> Boolean,
    phi2: (T) -> Boolean,
): Boolean {
  val now = tickData.currentTickUnit
  for (tick in tickData.forward()) {
    val pos = tick.currentTickUnit - now

    // Interval not reached yet, phi1 must hold
    if (pos.isBefore(interval)) if (phi1(tick)) continue else return false

    // Interval left, but phi2 did not hold
    if (pos.isAfter(interval)) return false

    // In interval: if phi2 holds, return true
    if (phi2(tick)) return true

    // In interval: phi2 did not hold, phi1 must hold
    if (!phi1(tick)) return false
  }

  return false
}
