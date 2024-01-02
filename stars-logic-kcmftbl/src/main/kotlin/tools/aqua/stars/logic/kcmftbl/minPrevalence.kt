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

import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * CMFTBL implementation of the minPrevalence operator.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param tickData Current [TickDataType].
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> minPrevalence(
    tickData: T,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    phi: (T) -> Boolean
): Boolean {
  val segment = tickData.segment
  val now = tickData.currentTick
  val nowIndex = segment.tickData.indexOf(tickData)

  val tickDataLength = segment.tickData.takeLast(segment.tickData.size - nowIndex).size

  val trueCount =
      (nowIndex..segment.tickData.lastIndex)
          .map { currentIndex ->
            val currentTickData = segment.tickData[currentIndex]
            if (currentTickData.currentTick < (now + interval.first) ||
                currentTickData.currentTick > (now + interval.second))
                true
            else phi(currentTickData)
          }
          .count { it }
  return trueCount >= tickDataLength * percentage
}

/**
 * CMFTBL implementation of the minPrevalence operator.
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
@Suppress("UNCHECKED_CAST")
fun <
    E1 : E,
    E : EntityType<E, T, S>,
    T : TickDataType<E, T, S>,
    S : SegmentType<E, T, S>> minPrevalence(
    entity: E1,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    phi: (E1) -> Boolean
): Boolean {
  val segment = entity.tickData.segment
  val now = entity.tickData.currentTick
  val nowIndex = segment.tickData.indexOf(entity.tickData)

  val tickDataLength = segment.tickData.takeLast(segment.tickData.size - nowIndex).size

  val trueCount =
      (nowIndex..segment.tickData.lastIndex)
          .map { currentIndex ->
            val currentTickData = segment.tickData[currentIndex]
            if (currentTickData.currentTick < (now + interval.first) ||
                currentTickData.currentTick > (now + interval.second))
                true
            else
                currentTickData.entities
                    .firstOrNull { it.id == entity.id }
                    ?.let { ac -> phi(ac as E1) }
                    ?: false
          }
          .count { it }
  return trueCount >= tickDataLength * percentage
}

/**
 * CMFTBL implementation of the minPrevalence operator for two entities.
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
@Suppress("UNCHECKED_CAST")
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S>,
    T : TickDataType<E, T, S>,
    S : SegmentType<E, T, S>> minPrevalence(
    entity1: E1,
    entity2: E2,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    phi: (E1, E2) -> Boolean
): Boolean {
  require(entity1.tickData == entity2.tickData) {
    "the two actors provided as argument are not from same tick"
  }

  val segment = entity1.tickData.segment
  val now = entity1.tickData.currentTick
  val nowIndex = segment.tickData.indexOf(entity1.tickData)

  val tickDataLength = segment.tickData.takeLast(segment.tickData.size - nowIndex).size

  val trueCount =
      (nowIndex..segment.tickData.lastIndex)
          .map { currentIndex ->
            val currentTickData = segment.tickData[currentIndex]
            if (currentTickData.currentTick < (now + interval.first) ||
                currentTickData.currentTick > (now + interval.second))
                true
            else
                currentTickData.entities
                    .firstOrNull { it.id == entity1.id }
                    ?.let { nextEntity1 ->
                      currentTickData.entities
                          .firstOrNull { it.id == entity2.id }
                          ?.let { nextEntity2 -> phi(nextEntity1 as E1, nextEntity2 as E2) }
                    }
                    ?: false
          }
          .count { it }
  return trueCount >= tickDataLength * percentage
}
