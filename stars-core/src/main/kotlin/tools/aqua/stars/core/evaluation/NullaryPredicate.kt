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

package tools.aqua.stars.core.evaluation

import tools.aqua.stars.core.types.*

/**
 * Nullary predicate.
 *
 * @param E [EntityDataType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param name The name of the predicate.
 * @property eval The evaluation function on the context.
 */
class NullaryPredicate<
    E : EntityDataType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    name: String,
    val eval: (T) -> Boolean,
) : AbstractPredicate<E, T, U, D>(name = name) {

  /**
   * Checks if this predicate holds (i.e., is true) in the given context and tick identifier.
   *
   * @param tick The current tick that is beeing evaluated.
   * @param tickUnit The time stamp to evaluate this predicate for.
   * @return Whether the predicate holds in the given context and at the given [tick].
   */
  fun holds(
      tick: T,
      tickUnit: U,
  ): Boolean = TODO("Search for tickUnit in tick")
    //ctx.firstOrNull { it.currentTickUnit == tick }?.let { holds(it) } ?: false

  /**
   * Checks if this predicate holds (i.e., is true) in the given context and tick identifier.
   *
   * @param tick The tick to evaluate this predicate for.
   * @return Whether the predicate holds in the given context and at the given [tick].
   */
  fun holds(tick: T): Boolean = this.eval(tick)

  /** Creates a nullary tick predicate. */
  companion object {
    /**
     * Creates a nullary tick predicate.
     *
     * @param E [EntityDataType].
     * @param T [TickDataType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param name The name of the predicate.
     * @param eval The evaluation function on the [List] of [TickDataType]s.
     * @return The created [NullaryPredicate] with the given [eval] function.
     */
    fun <
        E : EntityDataType<E, T, U, D>,
        T : TickDataType<E, T, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> predicate(
        name: String,
        eval: (T) -> Boolean
    ): NullaryPredicate<E, T, U, D> = NullaryPredicate(name, eval)
  }
}
