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

package tools.aqua.stars.logic.kcmftbl

import kotlin.reflect.cast
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * CMFTBL implementation of the until operator.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi1 Predicate 1.
 * @param phi2 Predicate 2.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> until(
    tickData: T,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (T) -> Boolean,
    phi2: (T) -> Boolean
): Boolean {

  val segment = tickData.segment
  val now = tickData.currentTick
  val nowIndex = segment.tickData.indexOf(tickData)
  val range: IntRange = nowIndex..segment.tickData.lastIndex
  for (searchIndex in range) {
    val searchTickData = segment.tickData[searchIndex]

    if (phi2(searchTickData) &&
        searchTickData.currentTick in now + interval.first..now + interval.second)
        return true
    else if (!phi1(searchTickData)) return false
  }
  return false
}

/**
 * CMFTBL implementation of the until operator.
 *
 * @param E1 [EntityType]
 * 1.
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param entity Current [EntityType] of which the tickData gets retrieved.
 * @param interval Observation interval.
 * @param phi1 Predicate 1.
 * @param phi2 Predicate 2.
 */
fun <E1 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> until(
    entity: E1,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (E1) -> Boolean,
    phi2: (E1) -> Boolean
): Boolean =
    until(
        entity.tickData,
        interval,
        phi1 = { td ->
          val futureEntity = td.entity(entity.id)
          if (entity::class.isInstance(futureEntity)) phi1(entity::class.cast(futureEntity))
          else false
        },
        phi2 = { td ->
          val futureEntity = td.entity(entity.id)
          if (entity::class.isInstance(futureEntity)) phi2(entity::class.cast(futureEntity))
          else false
        })

/**
 * CMFTBL implementation of the until operator for two entities.
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
 * @param phi1 Predicate 1.
 * @param phi2 Predicate 2.
 */
@Suppress("DuplicatedCode")
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S>,
    T : TickDataType<E, T, S>,
    S : SegmentType<E, T, S>> until(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (E1, E2) -> Boolean,
    phi2: (E1, E2) -> Boolean
): Boolean {

  require(entity1.tickData == entity2.tickData) {
    "the two entities provided as argument are not from same tick"
  }
  return until(
      entity1.tickData,
      interval,
      phi1 = { td ->
        val futureEntity1 = td.entity(entity1.id)
        val futureEntity2 = td.entity(entity2.id)
        if (entity1::class.isInstance(futureEntity1) && entity2::class.isInstance(futureEntity2))
            phi1(entity1::class.cast(futureEntity1), entity2::class.cast(futureEntity2))
        else false
      },
      phi2 = { td ->
        val futureEntity1 = td.entity(entity1.id)
        val futureEntity2 = td.entity(entity2.id)
        if (entity1::class.isInstance(futureEntity1) && entity2::class.isInstance(futureEntity2))
            phi2(entity1::class.cast(futureEntity1), entity2::class.cast(futureEntity2))
        else false
      })
}
