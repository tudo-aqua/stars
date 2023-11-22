/*
 * Copyright 2023 The STARS Project Authors
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

import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * CMFTBL implementation of the maxPrevalence operator.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param tickData Current [TickDataType].
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> maxPrevalence(
    tickData: T,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    phi: (T) -> Boolean
): Boolean = minPrevalence(tickData, 1 - percentage, interval, phi = { td -> !phi(td) })

/**
 * CMFTBL implementation of the maxPrevalence operator.
 *
 * @param E1 [EntityType]
 * 1.
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param entity Current [EntityType] of which the tickData gets retrieved.
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E1 : E,
    E : EntityType<E, T, S>,
    T : TickDataType<E, T, S>,
    S : SegmentType<E, T, S>> maxPrevalence(
    entity: E1,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    phi: (E1) -> Boolean
): Boolean = minPrevalence(entity, 1 - percentage, interval, phi = { a -> !phi(a) })

/**
 * CMFTBL implementation of the maxPrevalence operator for two entities.
 *
 * @param E1 [EntityType]
 * 1.
 * @param E2 [EntityType]
 * 2.
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param entity1 Current [EntityType]
 * 1.
 * @param entity2 Current [EntityType]
 * 2.
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S>,
    T : TickDataType<E, T, S>,
    S : SegmentType<E, T, S>> maxPrevalence(
    entity1: E1,
    entity2: E2,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    phi: (E1, E2) -> Boolean
): Boolean =
    minPrevalence(entity1, entity2, 1 - percentage, interval, phi = { a1, a2 -> !phi(a1, a2) })
