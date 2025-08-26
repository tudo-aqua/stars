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

package tools.aqua.stars.logic.kcmftbl

import tools.aqua.stars.core.types.*

/**
 * CMFTBL implementation of the 'back-to' operator i.e. "In all previous ticks in the interval phi 2
 * held, at least since (and including) phi1 held, or forever".
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param tickData Current [TickDataType].
 * @param interval Observation interval.
 * @param phi1 First predicate.
 * @param phi2 Second predicate.
 */
fun <
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> backTo(
    tickData: T,
    interval: Pair<D, D>? = null,
    phi1: (T) -> Boolean,
    phi2: (T) -> Boolean
): Boolean =
    !since(
        tickData = tickData,
        interval = interval,
        phi1 = { td -> !phi1(td) },
        phi2 = { td -> !phi2(td) })

/// **
// * CMFTBL implementation of the 'back-to' operator i.e. "In all previous ticks in the interval phi
// 2
// * held, at least since (and including) phi1 held, or forever".
// *
// * @param E1 [EntityType].
// * @param E [EntityType].
// * @param T [TickDataType].
// * @param U [TickUnit].
// * @param D [TickDifference].
// * @param entity Current [EntityType] of which the tickData gets retrieved.
// * @param interval Observation interval.
// * @param phi1 First predicate.
// * @param phi2 Second predicate.
// */
// @Suppress("UNCHECKED_CAST")
// fun <
//    E1 : E,
//    E : EntityType<E, T, U, D>,
//    T : TickDataType<E, T, U, D>,
//    U : TickUnit<U, D>,
//    D : TickDifference<D>> backTo(
//    entity: E1,
//    interval: Pair<D, D>? = null,
//    phi1: (E1) -> Boolean,
//    phi2: (E1) -> Boolean
// ): Boolean =
//    !since(
//        entity.tickData,
//        interval,
//        phi1 = { td -> td.getEntityById(entity.id)?.let { !phi1(it as E1) } != false },
//        phi2 = { td -> td.getEntityById(entity.id)?.let { !phi2(it as E1) } != false })

/// **
// * CMFTBL implementation of the 'back-to' operator i.e. "In all previous ticks in the interval phi
// 2
// * held, at least since (and including) phi1 held, or forever".
// *
// * @param E1 [EntityType].
// * @param E2 [EntityType].
// * @param E [EntityType].
// * @param T [TickDataType].
// * @param U [TickUnit].
// * @param D [TickDifference].
// * @param entity1 First [EntityType].
// * @param entity2 Second [EntityType].
// * @param interval Observation interval.
// * @param phi1 First predicate.
// * @param phi2 Second predicate.
// */
// @Suppress("UNCHECKED_CAST")
// fun <
//    E1 : E,
//    E2 : E,
//    E : EntityType<E, T, U, D>,
//    T : TickDataType<E, T, U, D>,
//    U : TickUnit<U, D>,
//    D : TickDifference<D>> backTo(
//    entity1: E1,
//    entity2: E2,
//    interval: Pair<D, D>? = null,
//    phi1: (E1, E2) -> Boolean,
//    phi2: (E1, E2) -> Boolean
// ): Boolean {
//  checkTick(entity1, entity2)
//  return !since(
//      entity1.tickData,
//      interval,
//      phi1 = { td ->
//        !phi1(
//            (td.getEntityById(entity1.id) ?: return@since true) as E1,
//            (td.getEntityById(entity2.id) ?: return@since true) as E2)
//      },
//      phi2 = { td ->
//        !phi2(
//            (td.getEntityById(entity1.id) ?: return@since true) as E1,
//            (td.getEntityById(entity2.id) ?: return@since true) as E2)
//      })
// }
