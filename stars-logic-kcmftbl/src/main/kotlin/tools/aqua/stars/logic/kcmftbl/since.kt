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
 * CMFTBL implementation of the 'since' operator i.e. "In all previous ticks in the interval phi 1
 * held, since phi 2 held".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi1 First predicate.
 * @param phi2 Second predicate.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> since(
    tickData: T,
    interval: Pair<D, D>? = null,
    phi1: (T) -> Boolean,
    phi2: (T) -> Boolean
): Boolean {
  checkInterval(interval)

  val segment = tickData.segment
  val now = tickData.currentTick
  val nowIndex = segment.tickData.indexOf(tickData)

  for (searchIndex in nowIndex downTo 0) {
    val searchTickData = segment.tickData[searchIndex]

    // Interval not reached yet, phi1 must hold
    if (interval != null && searchTickData.currentTick > now - interval.first)
        if (phi1(searchTickData)) continue else return false

    // Interval left, but phi2 did not hold
    if (interval != null && searchTickData.currentTick <= now - interval.second) return false

    // In interval: if phi2 holds, return true
    if (phi2(searchTickData)) return true

    // In interval: phi2 did not hold, phi1 must hold
    if (!phi1(searchTickData)) return false
  }
  return false
}

/**
 * CMFTBL implementation of the 'since' operator for one entity i.e. "In all previous ticks in the
 * interval phi 1 held, since phi 2 held".
 *
 * @param E1 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param entity Current [EntityType] of which the tickData gets retrieved.
 * @param interval Observation interval.
 * @param phi1 First predicate.
 * @param phi2 Second predicate.
 */
@Suppress("UNCHECKED_CAST")
fun <
    E1 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> since(
    entity: E1,
    interval: Pair<D, D>? = null,
    phi1: (E1) -> Boolean,
    phi2: (E1) -> Boolean
): Boolean =
    since(
        entity.tickData,
        interval,
        phi1 = { td -> td.getEntityById(entity.id)?.let { phi1(it as E1) } ?: false },
        phi2 = { td -> td.getEntityById(entity.id)?.let { phi2(it as E1) } ?: false })

/**
 * CMFTBL implementation of the 'since' operator for two entities i.e. "In all previous ticks in the
 * interval phi 1 held, since phi 2 held".
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
 * @param phi1 First predicate.
 * @param phi2 Second predicate.
 */
@Suppress("UNCHECKED_CAST")
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> since(
    entity1: E1,
    entity2: E2,
    interval: Pair<D, D>? = null,
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
        val pastEntity1 = td.getEntityById(entity1.id)
        val pastEntity2 = td.getEntityById(entity2.id)
        if (pastEntity1 == null || pastEntity2 == null) false
        else phi1(pastEntity1 as E1, pastEntity2 as E2)
      },
      phi2 = { td ->
        val pastEntity1 = td.getEntityById(entity1.id)
        val pastEntity2 = td.getEntityById(entity2.id)
        if (pastEntity1 == null || pastEntity2 == null) false
        else phi2(pastEntity1 as E1, pastEntity2 as E2)
      })
}
