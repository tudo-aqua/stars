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

@file:Suppress("DuplicatedCode")

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
    D : TickDifference<D>,
> minPrevalence(
    tickData: T,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (T) -> Boolean,
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

        if (
            interval != null &&
                (currentTickData.currentTick < now + interval.first ||
                    currentTickData.currentTick >= now + interval.second)
        )
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
    D : TickDifference<D>,
> minPrevalence(
    entity: E1,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (E1) -> Boolean,
): Boolean =
    minPrevalence(
        tickData = entity.tickData,
        percentage = percentage,
        interval = interval,
        phi = { td -> td.getEntityById(entity.id)?.let { phi(it as E1) } == true },
    )

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
    D : TickDifference<D>,
> minPrevalence(
    entity1: E1,
    entity2: E2,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (E1, E2) -> Boolean,
): Boolean {
  checkTick(entity1, entity2)
  return minPrevalence(
      tickData = entity1.tickData,
      percentage = percentage,
      interval = interval,
      phi = { td ->
        val futureEntity1 = td.getEntityById(entity1.id)
        val futureEntity2 = td.getEntityById(entity2.id)
        if (futureEntity1 == null || futureEntity2 == null) false
        else phi(futureEntity1 as E1, futureEntity2 as E2)
      },
  )
}
