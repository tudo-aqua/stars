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

/**
 * CMFTBL implementation of the 'release' operator i.e. "In all future ticks in the interval phi 2
 * holds, at least until (and including) phi1 holds, or forever".
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
> release(
    tickData: T,
    interval: Interval<D>? = null,
    phi1: (T) -> Boolean,
    phi2: (T) -> Boolean,
): Boolean =
    !until(
        tickData = tickData,
        interval = interval,
        phi1 = { td -> !phi1(td) },
        phi2 = { td -> !phi2(td) },
    )
