/*
 * Copyright 2023-2024 The STARS Project Authors
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

import kotlin.reflect.cast
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * CMFTBL implementation of the next operator.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> next(
    tickData: T,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi: (T) -> Boolean
): Boolean {
  val segment = tickData.segment
  val nowIndex = segment.tickData.indexOf(tickData)
  if (segment.tickData.lastIndex < nowIndex + 1) return false
  val nextTick = segment.tickData[nowIndex + 1]

  return if (nextTick.currentTick in
      (tickData.currentTick + interval.first)..(tickData.currentTick + interval.second))
      phi(nextTick)
  else false
}

/**
 * CMFTBL implementation of the next operator.
 *
 * @param E1 [EntityType]
 * 1.
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param entity Current [EntityType] of which the tickData gets retrieved.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <E1 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> next(
    entity: E1,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi: (E1) -> Boolean
): Boolean =
    next(
        entity.tickData,
        interval,
        phi = { td ->
          val nextEntity = td.entity(entity.id)
          if (entity::class.isInstance(nextEntity)) phi(entity::class.cast(nextEntity)) else false
        })

/**
 * CMFTBL implementation of the next operator for two entities.
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
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S>,
    T : TickDataType<E, T, S>,
    S : SegmentType<E, T, S>> next(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi: (E1, E2) -> Boolean
): Boolean {
  require(entity1.tickData == entity2.tickData) {
    "the two entities provided as argument are not from same tick"
  }
  return next(
      entity1.tickData,
      interval,
      phi = { td ->
        val futureEntity1 = td.entity(entity1.id)
        val futureEntity2 = td.entity(entity2.id)
        if (entity1::class.isInstance(futureEntity1) && entity2::class.isInstance(futureEntity2))
            phi(entity1::class.cast(futureEntity1), entity2::class.cast(futureEntity2))
        else false
      })
}
