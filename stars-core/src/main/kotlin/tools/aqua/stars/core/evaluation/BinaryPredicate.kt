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
 * Binary predicate.
 *
 * @param E1 [EntityType]
 * 1.
 * @param E2 [EntityType]
 * 2.
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @property eval The evaluation function on the [PredicateContext].
 * @property kClasses The actors.
 */
class BinaryPredicate<
    E1 : E, E2 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val eval: (PredicateContext<E, T, S>, E1, E2) -> Boolean,
    val kClasses: Pair<KClass<E1>, KClass<E2>>
) {

  /**
   * Checks if this predicate holds (i.e. is true) in the given context.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param tickId The time stamp to evaluate this predicate in. default: first tick in context.
   * @param actor1 The ID of the first actor to evaluate this predicate for. default: ego vehicle.
   * @param actor2 The ID of the second actor to evaluate this predicate for.
   */
  fun holds(
      ctx: PredicateContext<E, T, S>,
      tickId: Double = ctx.segment.firstTickId,
      actor1: Int = ctx.primaryEntityId,
      actor2: Int
  ): Boolean = ctx.holds(this, tickId, actor1, actor2)

  /**
   * Checks if this predicate holds (i.e. is true) in the given context on current tick.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param actor1 The ID of the first actor to evaluate this predicate for. default: ego vehicle.
   * @param actor2 The ID of the second actor to evaluate this predicate for.
   */
  fun holds(ctx: PredicateContext<E, T, S>, actor1: E1, actor2: E2): Boolean =
      holds(
          ctx,
          actor1.tickData.currentTick.apply {
            if (this != actor2.tickData.currentTick) error("ticks don't match")
          },
          actor1.id,
          actor2.id)

  companion object {
    /** Creates a binary tick predicate in this context. */
    fun <
        E1 : E,
        E2 : E,
        E : EntityType<E, T, S>,
        T : TickDataType<E, T, S>,
        S : SegmentType<E, T, S>> predicate(
        kClasses: Pair<KClass<E1>, KClass<E2>>,
        eval: (PredicateContext<E, T, S>, E1, E2) -> Boolean
    ): BinaryPredicate<E1, E2, E, T, S> = BinaryPredicate(eval, kClasses)
  }
}
