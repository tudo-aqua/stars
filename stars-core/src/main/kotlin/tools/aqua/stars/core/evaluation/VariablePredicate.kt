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
 * Variable predicate. Can only be used as sub-predicate inside other [Predicate]s.
 *
 * @param T [TickDataType].
 * @param K The type of the variable parameter.
 * @property name The name of the predicate.
 * @property eval The evaluation function on the [TickDataType].
 */
data class VariablePredicate<
    T : TickDataType<*, T, *, *>,
    K,
>(
    val name: String,
    val eval: (T, K) -> Boolean,
) {

  /**
   * Checks if this variable predicate holds (i.e., is true) at the given [tick] and parameter [other].
   *
   * @param tick The tick to evaluate this predicate for.
   * @param other The variable parameter to evaluate this predicate with.
   * @return Whether the predicate holds at the given [tick] and parameter [other].
   */
  fun holds(tick: T, other: K): Boolean = this.eval(tick, other)

  /** Companion object containing utility methods for working with [VariablePredicate]. */
  companion object {
    /**
     * Creates a [VariablePredicate].
     *
     * @param T [TickDataType].
     * @param K The type of the variable parameter.
     * @param name The name of the predicate.
     * @param eval The evaluation function on the [TickDataType] with a variable parameter of type [K].
     * @return The created [VariablePredicate] with the given [eval] function.
     */
    fun <
        T : TickDataType<*, T, *, *>,
        K,
    > predicate(name: String, eval: (T, K) -> Boolean): VariablePredicate<T, K> =
        VariablePredicate(name, eval)
  }
}
