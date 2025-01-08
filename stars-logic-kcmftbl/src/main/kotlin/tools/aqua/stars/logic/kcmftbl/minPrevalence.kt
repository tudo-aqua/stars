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
 * CMFTBL implementation of the 'minPrevalence' operator i.e. "In all future ticks in the interval
 * phi holds for at least ([percentage]*100)% of the ticks in the interval".
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
    D : TickDifference<D>> minPrevalence(
    tickData: T,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (T) -> Boolean
): Boolean {
  checkInterval(interval)
  checkPercentage(percentage)

  val segment = tickData.segment
  val now = tickData.currentTick
  val nowIndex = segment.tickData.indexOf(tickData)

  var tickCount = 0
  val trueCount =
      (nowIndex..segment.tickData.lastIndex).count { currentIndex ->
        val currentTickData = segment.tickData[currentIndex]

        if (interval != null &&
            (currentTickData.currentTick < now + interval.first ||
                currentTickData.currentTick >= now + interval.second))
            return@count false

        tickCount++
        phi(currentTickData)
      }

  return trueCount >= tickCount * percentage
}

/**
 * CMFTBL implementation of the 'minPrevalence' operator for one entity i.e. "In all future ticks in
 * the interval phi holds for at least ([percentage]*100)% of the ticks in the interval".
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
@Suppress("UNCHECKED_CAST")
fun <
    E1 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> minPrevalence(
    entity: E1,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (E1) -> Boolean
): Boolean {
  val segment = entity.tickData.segment
  val now = entity.tickData.currentTick
  val nowIndex = segment.tickData.indexOf(entity.tickData)

  var tickCount = 0
  val trueCount =
      (nowIndex..segment.tickData.lastIndex).count { currentIndex ->
        val currentTickData = segment.tickData[currentIndex]

        if (interval != null &&
            currentTickData.currentTick !in now + interval.first..now + interval.second)
            return@count false

        tickCount++

        val currentEntity = currentTickData.getEntityById(entity.id) ?: return@count false
        phi(currentEntity as E1)
      }

  return trueCount >= tickCount * percentage
}

/**
 * CMFTBL implementation of the 'minPrevalence' operator for two entities i.e. "In all future ticks
 * in the interval phi holds for at least ([percentage]*100)% of the ticks in the interval".
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
@Suppress("UNCHECKED_CAST", "DuplicatedCode")
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> minPrevalence(
    entity1: E1,
    entity2: E2,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (E1, E2) -> Boolean
): Boolean {
  require(entity1.tickData == entity2.tickData) {
    "the two actors provided as argument are not from same tick"
  }

  val segment = entity1.tickData.segment
  val now = entity1.tickData.currentTick
  val nowIndex = segment.tickData.indexOf(entity1.tickData)

  var tickCount = 0
  val trueCount =
      (nowIndex..segment.tickData.lastIndex).count { currentIndex ->
        val currentTickData = segment.tickData[currentIndex]
        if (interval != null &&
            currentTickData.currentTick !in now + interval.first..now + interval.second)
            return@count false

        tickCount++

        val firstEntity = currentTickData.getEntityById(entity1.id) ?: return@count false
        val secondEntity = currentTickData.getEntityById(entity2.id) ?: return@count false
        phi(firstEntity as E1, secondEntity as E2)
      }

  return trueCount >= tickCount * percentage
}
