/*
 * Copyright 2023-2026 The STARS Project Authors
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
 * Predicate.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property name The name of the predicate.
 * @property eval The evaluation function on the context.
 */
data class Predicate<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    val name: String,
    val eval: (T) -> Boolean,
) {

  /**
   * Checks if this predicate holds (i.e., is true) in the given context and tick identifier.
   *
   * @param tick The tick to evaluate this predicate for.
   * @return Whether the predicate holds in the given context and at the given [tick].
   */
  fun holds(tick: T): Boolean = this.eval(tick)

  /** Companion object containing utility methods for working with [Predicate]. */
  companion object {
    /**
     * Creates a Predicate.
     *
     * @param E [EntityType].
     * @param T [TickDataType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param name The name of the predicate.
     * @param eval The evaluation function on the [List] of [TickDataType]s.
     * @return The created [Predicate] with the given [eval] function.
     */
    fun <
        E : EntityType<E, T, U, D>,
        T : TickDataType<E, T, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>,
    > predicate(name: String, eval: (T) -> Boolean): Predicate<E, T, U, D> = Predicate(name, eval)
  }
}
