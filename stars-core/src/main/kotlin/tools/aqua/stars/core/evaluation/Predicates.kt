/*
 * Copyright 2023 The STARS Project Authors
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

// region nullary predicate

class NullaryPredicate<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val eval: (PredicateContext<E, T, S>, T) -> Boolean,
) {
  fun evaluate(ctx: PredicateContext<E, T, S>) = ctx.evaluate(this)
  fun holds(ctx: PredicateContext<E, T, S>, tid: Double) = evaluate(ctx).contains(tid)
}

/** Creates a nullary tick predicate */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> predicate(
    eval: (PredicateContext<E, T, S>, T) -> Boolean
) = NullaryPredicate(eval)

// endregion

// region unary predicate

class UnaryPredicate<
    E1 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val eval: (PredicateContext<E, T, S>, E1) -> Boolean,
    val klass: KClass<E1>
) {
  /**
   * Check if this predicate holds (i.e. is true) in the given context
   *
   * @param ctx The context this predicate is evaluated in
   * @param tickId The time stamp to evaluate this predicate in. default: first tick in context
   * @param actorId The ID of the actor to evaluate this predicate for. default: ego vehicle
   */
  fun holds(
      ctx: PredicateContext<E, T, S>,
      tickId: Double = ctx.segment.firstTickId,
      actorId: Int = ctx.egoID
  ) = ctx.holds(this, tickId, actorId)
  fun holds(ctx: PredicateContext<E, T, S>, entity: E) =
      holds(ctx, entity.tickData.currentTick, entity.id)
  fun holds(ctx: PredicateContext<E, T, S>) = holds(ctx, ctx.segment.firstTickId, ctx.egoID)
}

/** Creates a unary tick predicate */
fun <
    E1 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> predicate(
    klass: KClass<E1>,
    eval: (PredicateContext<E, T, S>, E1) -> Boolean
): UnaryPredicate<E1, E, T, S> = UnaryPredicate(eval, klass)

// endregion

// region binary predicate

class BinaryPredicate<
    E1 : E, E2 : E, E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val eval: (PredicateContext<E, T, S>, E1, E2) -> Boolean,
    val klass: Pair<KClass<E1>, KClass<E2>>
) {

  /**
   * Check if this predicate holds (i.e. is true) in the given context
   *
   * @param ctx The context this predicate is evaluated in
   * @param tickId The time stamp to evaluate this predicate in. default: first tick in context
   * @param actor1Id The ID of the first actor to evaluate this predicate for. default: ego vehicle
   * @param actor2Id The ID of the second actor to evaluate this predicate for.
   */
  fun holds(
      ctx: PredicateContext<E, T, S>,
      tickId: Double = ctx.segment.firstTickId,
      actor1Id: Int = ctx.egoID,
      actor2Id: Int
  ) = ctx.holds(this, tickId, actor1Id, actor2Id)
  fun holds(ctx: PredicateContext<E, T, S>, actor1: E1, actor2: E2) =
      holds(
          ctx,
          actor1.tickData.currentTick.apply {
            if (this != actor2.tickData.currentTick) error("ticks don't match")
          },
          actor1.id,
          actor2.id)
}

/** Creates a binary tick predicate in this context */
fun <
    E1 : E,
    E2 : E,
    E : EntityType<E, T, S>,
    T : TickDataType<E, T, S>,
    S : SegmentType<E, T, S>> predicate(
    klasses: Pair<KClass<E1>, KClass<E2>>,
    eval: (PredicateContext<E, T, S>, E1, E2) -> Boolean
) = BinaryPredicate(eval, klasses)

// endregion
