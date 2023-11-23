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

import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * Nullary predicate.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @property eval The evaluation function on the [PredicateContext].
 */
class NullaryPredicate<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val eval: (PredicateContext<E, T, S>, T) -> Boolean,
) {

  /** Evaluates predicate on [PredicateContext]. */
  fun evaluate(ctx: PredicateContext<E, T, S>): List<Double> = ctx.evaluate(this)

  /**
   * Checks if this predicate holds (i.e. is true) in the given context and tick identifier.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param tickId The time stamp to evaluate this predicate in. default: first tick in context.
   */
  fun holds(ctx: PredicateContext<E, T, S>, tickId: Double): Boolean =
      evaluate(ctx).contains(tickId)

  companion object {
    /** Creates a nullary tick predicate. */
    fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> predicate(
        eval: (PredicateContext<E, T, S>, T) -> Boolean
    ): NullaryPredicate<E, T, S> = NullaryPredicate(eval)
  }
}
