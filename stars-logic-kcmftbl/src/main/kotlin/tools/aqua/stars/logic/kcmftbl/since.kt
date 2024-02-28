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
 * CMFTBL implementation of the since operator i.e. "If phi 2 held at any timestamp in the past in
 * the interval, then phi 1 must hold for all timestamps since then until the end of the interval".
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

  val segment = tickData.segment
  val now = tickData.currentTick
  val nowIndex = segment.tickData.indexOf(tickData)

  for (searchIndex in nowIndex downTo 0) {
    val searchTickData = segment.tickData[searchIndex]

    if (interval != null) {
      // Interval not reached yet, continue iteration
      if (searchTickData.currentTick > now - interval.second) continue

      // Interval left, no phi2 held
      if (searchTickData.currentTick < now - interval.first) return false
    }

    // In Interval, check that phi 1 holds until phi2 has been reached
    if (!phi1(searchTickData)) return false

    // Phi2 holds, return true
    if (phi2(searchTickData)) return true
  }
  return false
}

/**
 * CMFTBL implementation of the since operator i.e. "If phi 2 held at any timestamp in the past in
 * the interval, then phi 1 must hold for all timestamps since then until the end of the interval".
 *
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
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> since(
    entity: E,
    interval: Pair<D, D>? = null,
    phi1: (E) -> Boolean,
    phi2: (E) -> Boolean
): Boolean =
    since(
        entity.tickData,
        interval,
        phi1 = { td -> td.getEntityById(entity.id)?.let { phi1(it) } ?: false },
        phi2 = { td -> td.getEntityById(entity.id)?.let { phi2(it) } ?: false })

/**
 * CMFTBL implementation of the since operator for two entities i.e. "If phi 2 held at any timestamp
 * in the past in the interval, then phi 1 must hold for all timestamps since then until the end of
 * the interval".
 *
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
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> since(
    entity1: E,
    entity2: E,
    interval: Pair<D, D>? = null,
    phi1: (E, E) -> Boolean,
    phi2: (E, E) -> Boolean
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
        if (pastEntity1 == null || pastEntity2 == null) false else phi1(pastEntity1, pastEntity2)
      },
      phi2 = { td ->
        val pastEntity1 = td.getEntityById(entity1.id)
        val pastEntity2 = td.getEntityById(entity2.id)
        if (pastEntity1 == null || pastEntity2 == null) false else phi2(pastEntity1, pastEntity2)
      })
}
