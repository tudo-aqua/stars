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

@file:Suppress("unused")

package tools.aqua.stars.logic.kcmftbl

import tools.aqua.stars.core.types.*

/**
 * CMFTBL implementation of the 'maxPrevalence' operator i.e. "In all future ticks in the interval
 * phi holds for at most ([percentage]*100)% of the ticks in the interval".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param tickData Current [TickDataType].
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> maxPrevalence(
    tickData: T,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (T) -> Boolean
): Boolean = minPrevalence(tickData, 1 - percentage, interval, phi = { td -> !phi(td) })

/**
 * CMFTBL implementation of the 'maxPrevalence' operator for one entity i.e. "In all future ticks in
 * the interval phi holds for at most ([percentage]*100)% of the ticks in the interval".
 *
 * @param E1 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param entity Current [EntityType] of which the tickData gets retrieved.
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E1 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> maxPrevalence(
    entity: E1,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (E1) -> Boolean
): Boolean = minPrevalence(entity, 1 - percentage, interval, phi = { e -> !phi(e) })

/**
 * CMFTBL implementation of the 'maxPrevalence' operator for two entities i.e. "In all future ticks
 * in the interval phi holds for at most ([percentage]*100)% of the ticks in the interval".
 *
 * @param E1 [EntityType].
 * @param E2 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param entity1 First [EntityType].
 * @param entity2 Second [EntityType].
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> maxPrevalence(
    entity1: E1,
    entity2: E2,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (E1, E2) -> Boolean
): Boolean =
    minPrevalence(entity1, entity2, 1 - percentage, interval, phi = { e1, e2 -> !phi(e1, e2) })
