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

import tools.aqua.stars.core.types.*

/**
 * Nullary predicate.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property eval The evaluation function on the [PredicateContext].
 */
class NullaryPredicate<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val eval: (PredicateContext<E, T, S, U, D>) -> Boolean,
) {

  /**
   * Checks if this predicate holds (i.e. is true) in the given context and tick identifier.
   *
   * @param ctx The context this predicate is evaluated in.
   * @param tick (Default: First tick in context) The tick to evaluate this predicate in.
   * @return Whether the predicate holds in the given [PredicateContext] and at the given [tick].
   */
  fun holds(
      ctx: PredicateContext<E, T, S, U, D>,
      tick: U = ctx.segment.ticks.keys.first()
  ): Boolean = ctx.holds(this, tick)

  companion object {
    /**
     * Creates a nullary tick predicate.
     *
     * @param E [EntityType].
     * @param T [TickDataType].
     * @param S [SegmentType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param eval The evaluation function on the [PredicateContext].
     * @return The created [NullaryPredicate] with the given [eval] function.
     */
    fun <
        E : EntityType<E, T, S, U, D>,
        T : TickDataType<E, T, S, U, D>,
        S : SegmentType<E, T, S, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> predicate(
        eval: (PredicateContext<E, T, S, U, D>) -> Boolean
    ): NullaryPredicate<E, T, S, U, D> = NullaryPredicate(eval)
  }
}
