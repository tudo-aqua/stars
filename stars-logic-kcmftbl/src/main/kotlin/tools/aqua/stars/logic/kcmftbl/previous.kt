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
 * CMFTBL implementation of the 'previous' operator i.e. "In the previous tick phi holds and the
 * tick is in the interval".
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
  checkInterval(interval)

  val segment = tickData.segment
  val nowIndex = segment.tickData.indexOf(tickData)

  // There needs to be a previous tick
  if (nowIndex == 0) return false
  val previousTick = segment.tickData[nowIndex - 1]

  if (interval != null &&
      (previousTick.currentTick <= tickData.currentTick - interval.second ||
          previousTick.currentTick > tickData.currentTick - interval.first))
      return false

  return phi(previousTick)
}

/**
 * CMFTBL implementation of the 'previous' operator for one entity i.e. "In the previous tick phi
 * holds and the tick is in the interval".
 *
 * @param E1 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param entity Current [EntityType] of which the tickData gets retrieved.
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
    D : TickDifference<D>> previous(
    entity: E1,
    interval: Pair<D, D>? = null,
    phi: (E1) -> Boolean
): Boolean =
    previous(
        entity.tickData,
        interval,
        phi = { td -> td.getEntityById(entity.id)?.let { phi(it as E1) } ?: false })

/**
 * CMFTBL implementation of the 'previous' operator for two entities i.e. "In the previous tick phi
 * holds and the tick is in the interval".
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
 * @param interval Observation interval.
 * @param phi Predicate.
 */
@Suppress("UNCHECKED_CAST")
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> previous(
    entity1: E1,
    entity2: E2,
    interval: Pair<D, D>? = null,
    phi: (E1, E2) -> Boolean
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
        else phi(previousEntity1 as E1, previousEntity2 as E2)
      })
}
