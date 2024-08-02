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

package tools.aqua.stars.core.evaluation

import kotlin.reflect.KClass
import tools.aqua.stars.core.types.*

/**
 * Binary predicate.
 *
 * @param E1 [EntityType].
 * @param E2 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property kClasses The [KClass]es of the [EntityType]s that are evaluated by this predicate.
 * @property eval The evaluation function on the [PredicateContext].
 */
class BinaryPredicate<
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val kClasses: Pair<KClass<E1>, KClass<E2>>,
    val eval: (PredicateContext<E, T, S, U, D>, E1, E2) -> Boolean,
) {

  /**
   * Checks if this predicate holds (i.e. is true) in the given context.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param tick (Default: first tick in context) The time stamp to evaluate this predicate in.
   * @param entityId1 (Default: Primary entity) The ID of the first entity to evaluate this
   *   predicate for.
   * @param entityId2 The ID of the second entity to evaluate this predicate for.
   * @return Whether the predicate holds in the given [PredicateContext] at the given [tick] for the
   *   given [entityId1] and [entityId2].
   */
  fun holds(
      ctx: PredicateContext<E, T, S, U, D>,
      tick: U = ctx.segment.ticks.keys.first(),
      entityId1: Int = ctx.primaryEntityId,
      entityId2: Int
  ): Boolean = ctx.holds(this, tick, entityId1, entityId2)

  /**
   * Checks if this predicate holds (i.e. is true) in the given context on current tick.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param entity1 The first entity to evaluate this predicate for.
   * @param entity2 The second entity to evaluate this predicate for.
   * @return Whether the predicate holds in the given [PredicateContext] for the given [entity1] and
   *   [entity2].
   */
  fun holds(ctx: PredicateContext<E, T, S, U, D>, entity1: E1, entity2: E2): Boolean =
      holds(
          ctx,
          entity1.tickData.currentTick.apply {
            if (this != entity2.tickData.currentTick) error("ticks don't match")
          },
          entity1.id,
          entity2.id)

  companion object {
    /**
     * Creates a binary tick predicate in this context.
     *
     * @param E1 [EntityType].
     * @param E2 [EntityType].
     * @param E [EntityType].
     * @param T [TickDataType].
     * @param S [SegmentType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param kClasses The [KClass]es of the [EntityType]s that are evaluated by this predicate.
     * @param eval The evaluation function on the [PredicateContext].
     * @return The created [UnaryPredicate] with the given [eval] function and the [KClass]es of the
     *   entities for which the predicate should be evaluated.
     */
    fun <
        E1 : E,
        E2 : E,
        E : EntityType<E, T, S, U, D>,
        T : TickDataType<E, T, S, U, D>,
        S : SegmentType<E, T, S, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> predicate(
        kClasses: Pair<KClass<E1>, KClass<E2>>,
        eval: (PredicateContext<E, T, S, U, D>, E1, E2) -> Boolean,
    ): BinaryPredicate<E1, E2, E, T, S, U, D> = BinaryPredicate(kClasses, eval)
  }
}
