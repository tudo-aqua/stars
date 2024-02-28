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

import tools.aqua.stars.core.types.*

/**
 * CMFTBL implementation of the previous operator i.e. "In the previous timeframe phi holds and the
 * timestamp is in the interval".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> previous(
    tickData: T,
    interval: Pair<D, D>? = null,
    phi: (T) -> Boolean
): Boolean {
  val segment = tickData.segment
  val nowIndex = segment.tickData.indexOf(tickData)
  if (nowIndex - 1 < 0) return false
  val previousTick = segment.tickData[nowIndex - 1]

  return if (interval == null ||
      previousTick.currentTick in
          (tickData.currentTick - interval.second)..(tickData.currentTick - interval.first))
      phi(previousTick)
  else false
}

/**
 * CMFTBL implementation of the previous operator i.e. "In the previous timeframe phi holds and the
 * timestamp is in the interval".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param entity Current [EntityType] of which the tickData gets retrieved.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> previous(
    entity: E,
    interval: Pair<D, D>? = null,
    phi: (entity: E) -> Boolean
): Boolean =
    previous(
        entity.tickData,
        interval,
        phi = { td -> td.getEntityById(entity.id)?.let { phi(it) } ?: false })

/**
 * CMFTBL implementation of the previous operator for two entities i.e. "In the previous timeframe
 * phi holds and the timestamp is in the interval".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param entity1 First [EntityType].
 * @param entity2 Second [EntityType].
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> previous(
    entity1: E,
    entity2: E,
    interval: Pair<D, D>? = null,
    phi: (E, E) -> Boolean
): Boolean {
  require(entity1.tickData == entity2.tickData) {
    "The two entities provided as argument are not from same tick."
  }
  return previous(
      entity1.tickData,
      interval,
      phi = { td ->
        val previousEntity1 = td.getEntityById(entity1.id)
        val previousEntity2 = td.getEntityById(entity2.id)

        if (previousEntity1 == null || previousEntity2 == null) false
        else phi(previousEntity1, previousEntity2)
      })
}
