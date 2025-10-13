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
 * Binary predicate.
 *
 * @param E1 [EntityType].
 * @param E2 [EntityType].
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param name The name of the predicate.
 * @property kClasses The [KClass]es of the [EntityType]s that are evaluated by this predicate.
 * @property eval The evaluation function on the context.
 */
class BinaryPredicate<
    E1 : E,
    E2 : E,
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    name: String,
    val kClasses: Pair<KClass<E1>, KClass<E2>>,
    val eval: (T, E1, E2) -> Boolean,
) : AbstractPredicate<E, T, U, D>(name) {

  /**
   * Checks if this predicate holds (i.e., is true) in the given context.
   *
   * @param tick The current tick that is being evaluated.
   * @param tickUnit The time stamp to evaluate this predicate for.
   * @param entity1 The first entity to evaluate this predicate for.
   * @param entity2 The second entity to evaluate this predicate for.
   * @return Whether the predicate holds in the given context at the given [tick] for the given
   *   [entity1] and [entity2]. Returns false if the [tick] is not in the context.
   */
  fun holds(tick: T, tickUnit: U, entity1: E1, entity2: E2): Boolean =
      TODO("Search for tickUnit in tick")

  /**
   * Checks if this predicate holds (i.e., is true) in the given context.
   *
   * @param tick The current tick that is being evaluated.
   * @return Whether the predicate holds in the given context at the given [tick]. Returns false if
   *   the [tick] is not in the context.
   */
  fun holds(tick: T): Boolean = TODO("Search for tickUnit in tick")

  // ctx.firstOrNull { it.currentTickUnit == tick }?.let { holds(it, entity1, entity2) } ?: false

  /**
   * Checks if this predicate holds (i.e., is true) in the given context.
   *
   * @param tick The tick to evaluate this predicate for.
   * @param entity1 The first entity to evaluate this predicate for.
   * @param entity2 The second entity to evaluate this predicate for.
   * @return Whether the predicate holds for the current tick for the given [entity1] and [entity2].
   */
  @Suppress("UNCHECKED_CAST")
  fun holds(tick: T, entity1: E1 = tick.ego as E1, entity2: E2): Boolean =
      entity1 != entity2 &&
          this.kClasses.first.isInstance(entity1) &&
          this.kClasses.second.isInstance(entity2) &&
          this.eval(tick, this.kClasses.first.cast(entity1), this.kClasses.second.cast(entity2))

  /** Creates a binary tick predicate in this context. */
  companion object {
    /**
     * Creates a binary tick predicate in this context.
     *
     * @param E1 [EntityType].
     * @param E2 [EntityType].
     * @param E [EntityType].
     * @param T [TickDataType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param name The name of the predicate.
     * @param kClasses The [KClass]es of the [EntityType]s that are evaluated by this predicate.
     * @param eval The evaluation function on the [List] of [TickDataType]s.
     * @return The created [UnaryPredicate] with the given [eval] function and the [KClass]es of the
     *   entities for which the predicate should be evaluated.
     */
    fun <
        E1 : E,
        E2 : E,
        E : EntityType<E, T, U, D>,
        T : TickDataType<E, T, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>,
    > predicate(
        name: String,
        kClasses: Pair<KClass<E1>, KClass<E2>>,
        eval: (T, E1, E2) -> Boolean,
    ): BinaryPredicate<E1, E2, E, T, U, D> = BinaryPredicate(name, kClasses, eval)
  }
}
