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
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * Unary predicate.
 *
 * @param E1 [EntityType]
 * 1.
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @property eval The evaluation function on the [PredicateContext].
 * @property kClass The actor.
 */
class UnaryPredicate<
    E1 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val eval: (PredicateContext<E, T, S>, E1) -> Boolean,
    val kClass: KClass<E1>
) {
  /**
   * Check if this predicate holds (i.e. is true) in the given context.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param tickId The time stamp to evaluate this predicate in. default: first tick in context.
   * @param entityId The ID of the entity to evaluate this predicate for. default: ego vehicle.
   */
  fun holds(
      ctx: PredicateContext<E, T, S>,
      tickId: Double = ctx.segment.firstTickId,
      entityId: Int = ctx.primaryEntityId
  ): Boolean = ctx.holds(this, tickId, entityId)

  /**
   * Check if this predicate holds (i.e. is true) in the given context.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param entity The entity to evaluate this predicate for.
   */
  fun holds(ctx: PredicateContext<E, T, S>, entity: E): Boolean =
      holds(ctx, entity.tickData.currentTick, entity.id)

  /**
   * Check if this predicate holds (i.e. is true) in the given context.
   *
   * @param ctx The context this predicate is evaluated in.
   */
  fun holds(ctx: PredicateContext<E, T, S>): Boolean =
      holds(ctx, ctx.segment.firstTickId, ctx.primaryEntityId)

  companion object {
    /** Creates a unary tick predicate. */
    fun <
        E1 : E,
        E : EntityType<E, T, S>,
        T : TickDataType<E, T, S>,
        S : SegmentType<E, T, S>> predicate(
        klass: KClass<E1>,
        eval: (PredicateContext<E, T, S>, E1) -> Boolean
    ): UnaryPredicate<E1, E, T, S> = UnaryPredicate(eval, klass)
  }
}
