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
import tools.aqua.stars.logic.kcmftbl.Interval.Companion.isNotIn

/**
 * CMFTBL implementation of the 'next' operator i.e. "In the next tick phi holds and the tick is in
 * the interval".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
> next(tickData: T, interval: Interval<D>? = null, phi: (T) -> Boolean): Boolean {
  // There needs to be a next tick
  val nextTick = tickData.nextTick ?: return false

  // The next tick has to be in the interval
  val pos = nextTick.currentTickUnit - tickData.currentTickUnit
  if (pos.isNotIn(interval)) return false

  return phi(nextTick)
}
