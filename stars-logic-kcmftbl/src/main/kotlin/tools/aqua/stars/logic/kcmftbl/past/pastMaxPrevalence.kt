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

/**
 * CMFTBL implementation of the 'maxPrevalence' operator i.e. "In all past ticks in the interval phi
 * holds for at most ([percentage]*100)% of the ticks in the interval".
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
> pastMaxPrevalence(
    tickData: T,
    percentage: Double,
    interval: Interval<D>? = null,
    phi: (T) -> Boolean,
): Boolean =
    pastMinPrevalence(
        tickData = tickData,
        percentage = 1 - percentage,
        interval = interval,
        phi = { td -> !phi(td) },
    )
