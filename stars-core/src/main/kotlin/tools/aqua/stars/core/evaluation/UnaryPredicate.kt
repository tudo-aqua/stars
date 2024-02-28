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
 * Unary predicate.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property eval The evaluation function on the [PredicateContext].
 * @property kClass The actor.
 */
class UnaryPredicate<
    E1 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val eval: (PredicateContext<E, T, S, U, D>, E1) -> Boolean,
    val kClass: KClass<E1>
) {
  /**
   * Check if this predicate holds (i.e. is true) in the given context.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param tick The tick to evaluate this predicate in. default: first tick in context.
   * @param entityId The ID of the entity to evaluate this predicate for. default: ego vehicle.
   */
  fun holds(
      ctx: PredicateContext<E, T, S, U, D>,
      tick: U = ctx.segment.ticks.keys.first(),
      entityId: Int = ctx.primaryEntityId
  ): Boolean = ctx.holds(this, tick, entityId)

  /**
   * Check if this predicate holds (i.e. is true) in the given context.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param entity The entity to evaluate this predicate for.
   */
  fun holds(ctx: PredicateContext<E, T, S, U, D>, entity: E): Boolean =
      holds(ctx, entity.tickData.currentTick, entity.id)

  /**
   * Check if this predicate holds (i.e. is true) in the given context.
   *
   * @param ctx The context this predicate is evaluated in.
   */
  fun holds(ctx: PredicateContext<E, T, S, U, D>): Boolean =
      holds(ctx, ctx.segment.ticks.keys.first(), ctx.primaryEntityId)

  companion object {
    /** Creates a unary tick predicate. */
    fun <
        E1 : E,
        E : EntityType<E, T, S, U, D>,
        T : TickDataType<E, T, S, U, D>,
        S : SegmentType<E, T, S, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> predicate(
        klass: KClass<E1>,
        eval: (PredicateContext<E, T, S, U, D>, E1) -> Boolean
    ): UnaryPredicate<E1, E, T, S, U, D> = UnaryPredicate(eval, klass)
  }
}
