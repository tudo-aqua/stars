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

import kotlin.reflect.cast
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * CMFTBL implementation of the since operator.
 *
 * @param E [EntityType]
 * @param T [TickDataType]
 * @param S [SegmentType]
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi1 Predicate 1.
 * @param phi2 Predicate 2.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> since(
    tickData: T,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (T) -> Boolean,
    phi2: (T) -> Boolean
): Boolean {

  val segment = tickData.segment
  val now = tickData.currentTick
  val nowIndex = segment.tickData.indexOf(tickData)

  for (searchIndex in nowIndex downTo 0) {
    val searchTickData = segment.tickData[searchIndex]

    if (phi2(searchTickData) &&
        searchTickData.currentTick in now - interval.second..now - interval.first)
        return true
    else if (!phi1(searchTickData)) return false
  }
  return false
}

/**
 * CMFTBL implementation of the since operator.
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
fun <E1 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> since(
    entity: E1,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (E1) -> Boolean,
    phi2: (E1) -> Boolean
): Boolean =
    since(
        entity.tickData,
        interval,
        phi1 = { td ->
          val pastEntity = td.entity(entity.id)
          if (entity::class.isInstance(pastEntity)) phi1(entity::class.cast(pastEntity)) else false
        },
        phi2 = { td ->
          val pastEntity = td.entity(entity.id)
          if (entity::class.isInstance(pastEntity)) phi2(entity::class.cast(pastEntity)) else false
        })

/**
 * CMFTBL implementation of the since operator for two entities.
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
    S : SegmentType<E, T, S>> since(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (E1, E2) -> Boolean,
    phi2: (E1, E2) -> Boolean
): Boolean {

  require(entity1.tickData == entity2.tickData) {
    "the two entities provided as argument are not from same tick"
  }
  return since(
      entity1.tickData,
      interval,
      phi1 = { td ->
        val pastEntity1 = td.entity(entity1.id)
        val pastEntity2 = td.entity(entity2.id)
        if (entity1::class.isInstance(pastEntity1) && entity2::class.isInstance(pastEntity2))
            phi1(entity1::class.cast(pastEntity1), entity2::class.cast(pastEntity2))
        else false
      },
      phi2 = { td ->
        val pastEntity1 = td.entity(entity1.id)
        val pastEntity2 = td.entity(entity2.id)
        if (entity1::class.isInstance(pastEntity1) && entity2::class.isInstance(pastEntity2))
            phi2(entity1::class.cast(pastEntity1), entity2::class.cast(pastEntity2))
        else false
      })
}
