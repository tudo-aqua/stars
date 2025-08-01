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

import kotlin.reflect.KClass
import kotlin.reflect.cast
import tools.aqua.stars.core.types.*

/**
 * Unary predicate.
 *
 * @param E1 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param name The name of the predicate.
 * @property kClass The [KClass] of the [EntityType] that is evaluated by this predicate.
 * @property eval The evaluation function on the context.
 */
class UnaryPredicate<
    E1 : E,
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    name: String,
    val kClass: KClass<E1>,
    val eval: (T, E1) -> Boolean,
) : AbstractPredicate<E, T, U, D>(name = name) {

  /**
   * Check if this predicate holds (i.e., is true) in the given context.
   *
   * @param tick The current tick that is beeing evaluated.
   *    * @param tickUnit The time stamp to evaluate this predicate for.
   * @param entity The entity to evaluate this predicate for.
   * @return Whether the predicate holds in the given context at the given [tick] for the
   *   given [entity].
   */
  fun holds(
    tick: T,
    tickUnit: U,
    entity: E1
  ): Boolean = TODO("Search for tickUnit in tick")
      //ctx.firstOrNull { it.currentTickUnit == tick }?.let { holds(it, entity) } ?: false

  /**
   * Check if this predicate holds (i.e., is true) in the given context.
   *
   * @param tick The tick to evaluate this predicate for.
   * @param entity The entity to evaluate this predicate for.
   * @return Whether the predicate holds in the given context at the given [tick] for the
   *   given [entity].
   */
  fun holds(
    tick: T,
    entity: E1
  ): Boolean  =
        this.kClass.isInstance(entity) &&
        this.eval(tick, this.kClass.cast(entity))

  /** Creates a unary tick predicate. * */
  companion object {
    /**
     * Creates a unary tick predicate.
     *
     * @param E1 [EntityType].
     * @param E [EntityType].
     * @param T [TickDataType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param name The name of the predicate.
     * @param kClass The [KClass] of the [EntityType] that is evaluated by this predicate.
     * @param eval The evaluation function on the [List] of [TickDataType]s.
     * @return The created [UnaryPredicate] with the given [eval] function and the [KClass] of the
     *   entity for which the predicate should be evaluated.
     */
    fun <
        E1 : E,
        E : EntityType<E, T, U, D>,
        T : TickDataType<E, T, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> predicate(
        name: String,
        kClass: KClass<E1>,
        eval: (T, E1) -> Boolean,
    ): UnaryPredicate<E1, E, T, U, D> = UnaryPredicate(name, kClass, eval)
  }
}
