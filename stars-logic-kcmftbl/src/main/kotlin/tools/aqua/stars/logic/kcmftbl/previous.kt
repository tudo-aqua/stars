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
 * CMFTBL implementation of the previous operator.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> previous(
    tickData: T,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi: (T) -> Boolean
): Boolean {
  val segment = tickData.segment
  val nowIndex = segment.tickData.indexOf(tickData)
  if (nowIndex - 1 < 0) return false
  val previousTick = segment.tickData[nowIndex - 1]

  return if (previousTick.currentTick in
      (tickData.currentTick - interval.second)..(tickData.currentTick - interval.first))
      phi(previousTick)
  else false
}

/**
 * CMFTBL implementation of the previous operator.
 *
 * @param E1 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param entity Current [EntityType] of which the tickData gets retrieved.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <E1 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> previous(
    entity: E1,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi: (entity: E1) -> Boolean
): Boolean =
    previous(
        entity.tickData,
        interval,
        phi = { td ->
          val previousEntity = td.entity(entity.id)
          if (entity::class.isInstance(previousEntity)) phi(entity::class.cast(previousEntity))
          else false
        })

/**
 * CMFTBL implementation of the previous operator for two entities.
 *
 * @param E1 [EntityType].
 * @param E2 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param entity1 Current [EntityType]
 * @param entity2 Current [EntityType]
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S>,
    T : TickDataType<E, T, S>,
    S : SegmentType<E, T, S>> previous(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    phi: (E1, E2) -> Boolean
): Boolean {
  require(entity1.tickData == entity2.tickData) {
    "the two entities provided as argument are not from same tick"
  }
  return previous(
      entity1.tickData,
      interval,
      phi = { td ->
        val previousEntity1 = td.entity(entity1.id)
        val previousEntity2 = td.entity(entity2.id)
        if (entity1::class.isInstance(previousEntity1) &&
            entity2::class.isInstance(previousEntity2))
            phi(entity1::class.cast(previousEntity1), entity2::class.cast(previousEntity2))
        else false
      })
}
