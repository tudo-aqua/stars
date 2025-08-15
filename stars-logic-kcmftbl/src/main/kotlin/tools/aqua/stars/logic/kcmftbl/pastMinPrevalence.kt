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
 * CMFTBL implementation of the 'pastMinPrevalence' operator i.e. "In all past ticks in the interval
 * phi holds for at least ([percentage]*100)% of the ticks in the interval".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param tickData Current [TickDataType].
 * @param percentage Threshold value.
 * @param interval Observation interval.
 * @param phi Predicate.
 */
fun <
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> pastMinPrevalence(
    tickData: T,
    percentage: Double,
    interval: Pair<D, D>? = null,
    phi: (T) -> Boolean
): Boolean {
  checkInterval(interval)
  checkPercentage(percentage)

  val now = tickData.currentTickUnit
  var tickCount = 0
  var trueCount = 0

  for (tick in tickData.backward()) {
    // Check if the current tick is before the start of the interval
    if (interval != null && tick.currentTickUnit > now - interval.first) continue

    // Check if the current tick is after the end of the interval
    if (interval != null && tick.currentTickUnit < now - interval.second) break

    // Count the tick if the predicate holds true
    if (phi(tick)) trueCount++

    tickCount++
  }

  return trueCount >= tickCount * percentage
}

/// **
// * CMFTBL implementation of the 'minPrevalence' operator for one entity i.e. "In all past ticks in
// * the interval phi holds for at least ([percentage]*100)% of the ticks in the interval".
// *
// * @param E1 [EntityType].
// * @param E [EntityType].
// * @param T [TickDataType].
// * @param U [TickUnit].
// * @param D [TickDifference].
// * @param entity Current [EntityType] of which the tickData gets retrieved.
// * @param percentage Threshold value.
// * @param interval Observation interval.
// * @param phi Predicate.
// */
// @Suppress("UNCHECKED_CAST")
// fun <
//    E1 : E,
//    E : EntityType<E, T, U, D>,
//    T : TickDataType<E, T, U, D>,
//    U : TickUnit<U, D>,
//    D : TickDifference<D>> pastMinPrevalence(
//    entity: E1,
//    percentage: Double,
//    interval: Pair<D, D>? = null,
//    phi: (E1) -> Boolean
// ): Boolean =
//    pastMinPrevalence(
//        tickData = entity.tickData,
//        percentage = percentage,
//        interval = interval,
//        phi = { td -> td.getEntityById(entity.id)?.let { phi(it as E1) } == true })

/// **
// * CMFTBL implementation of the 'minPrevalence' operator for two entities i.e. "In all past ticks
// in
// * the interval phi holds for at least ([percentage]*100)% of the ticks in the interval".
// *
// * @param E1 [EntityType].
// * @param E2 [EntityType].
// * @param E [EntityType].
// * @param T [TickDataType].
// * @param U [TickUnit].
// * @param D [TickDifference].
// * @param entity1 First [EntityType].
// * @param entity2 Second [EntityType].
// * @param percentage Threshold value.
// * @param interval Observation interval.
// * @param phi Predicate.
// */
// @Suppress("UNCHECKED_CAST", "DuplicatedCode")
// fun <
//    E1 : E,
//    E2 : E,
//    E : EntityType<E, T, U, D>,
//    T : TickDataType<E, T, U, D>,
//    U : TickUnit<U, D>,
//    D : TickDifference<D>> pastMinPrevalence(
//    entity1: E1,
//    entity2: E2,
//    percentage: Double,
//    interval: Pair<D, D>? = null,
//    phi: (E1, E2) -> Boolean
// ): Boolean {
//  require(entity1.tickData == entity2.tickData) {
//    "the two entities provided as argument are not from same tick"
//  }
//  return pastMinPrevalence(
//      tickData = entity1.tickData,
//      percentage = percentage,
//      interval = interval,
//      phi = { td ->
//        val pastEntity1 = td.getEntityById(entity1.id)
//        val pastEntity2 = td.getEntityById(entity2.id)
//        if (pastEntity1 == null || pastEntity2 == null) false
//        else phi(pastEntity1 as E1, pastEntity2 as E2)
//      })
// }
