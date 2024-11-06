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

import kotlin.reflect.cast
import tools.aqua.stars.core.types.*

/**
 * Predicate context which holds the evaluations of predicates for each evaluated entity.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property segment The current [SegmentType].
 */
class PredicateContext<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(val segment: S) {

  /** Identifier of the primary entity. */
  var primaryEntityId: Int = segment.primaryEntityId

  /** Cache for all entity IDs. */
  private val entityIdsCache = mutableListOf<Int>()

  /** All entity IDs of the current context state. */
  @Suppress("unused")
  val entityIds: List<Int>
    get() {
      if (entityIdsCache.isEmpty()) {
        entityIdsCache.addAll(
            segment.tickData.flatMap { tickData -> tickData.entities.map { it.id } }.distinct())
      }
      return entityIdsCache
    }

  /** Holds the evaluations of all previously calculated [NullaryPredicate]s. */
  private val nullaryPredicateCache: MutableMap<Pair<NullaryPredicate<E, T, S, U, D>, U>, Boolean> =
      mutableMapOf()
  /** Holds the evaluations of all previously calculated [UnaryPredicate]s. */
  private val unaryPredicateCache:
      MutableMap<Pair<UnaryPredicate<*, E, T, S, U, D>, Pair<U, Int>>, Boolean> =
      mutableMapOf()
  /** Holds the evaluations of all previously calculated [BinaryPredicate]s. */
  private val binaryPredicateCache:
      MutableMap<Pair<BinaryPredicate<*, *, E, T, S, U, D>, Triple<U, Int, Int>>, Boolean> =
      mutableMapOf()

  // TODO: Check if the caches are still "useful" and actually used.

  /**
   * Evaluates whether [NullaryPredicate] [predicate] holds for current [PredicateContext] at [tick]
   * .
   *
   * @param predicate The [NullaryPredicate] that is to be evaluated.
   * @param tick The [TickUnit] at which the [predicate] is evaluated.
   * @return Whether the [predicate] holds at the given [tick].
   */
  fun holds(predicate: NullaryPredicate<E, T, S, U, D>, tick: U): Boolean =
      nullaryPredicateCache.getOrPut(predicate to tick) {
        val currentTick = segment.ticks[tick]

        currentTick != null && predicate.eval(this)
      }

  /**
   * Evaluates whether [UnaryPredicate] [predicate] holds for current [PredicateContext], at [tick]
   * and for [entityId].
   *
   * @param E1 [EntityType].
   * @param predicate The predicate that is evaluated.
   * @param tick The [TickUnit] at which the [predicate] is evaluated.
   * @param entityId The ID of the [EntityType] for which the [predicate] is evaluated.
   * @return Whether the [predicate] holds at the given [tick] for the given [entityId].
   */
  @Suppress("UNCHECKED_CAST")
  fun <E1 : E> holds(
      predicate: UnaryPredicate<E1, E, T, S, U, D>,
      tick: U,
      entityId: Int
  ): Boolean =
      unaryPredicateCache.getOrPut(predicate to (tick to entityId)) {
        val currentTick = segment.ticks[tick]
        val entity = currentTick?.getEntityById(entityId)

        currentTick != null &&
            predicate.kClass.isInstance(entity) &&
            predicate.eval(this, entity as E1)
      }

  /**
   * Evaluates whether [UnaryPredicate] [predicate] holds for current [PredicateContext], at [tick]
   * and for both [entityId1] and [entityId2].
   *
   * @param E1 [EntityType].
   * @param E2 [EntityType].
   * @param predicate The predicate that is evaluated.
   * @param tick The [TickUnit] at which the [predicate] is evaluated.
   * @param entityId1 The first ID of the [EntityType] for which the [predicate] is evaluated.
   * @param entityId2 The second ID of the [EntityType] for which the [predicate] is evaluated.
   * @return Whether the [predicate] holds at the given [tick] for both [entityId1] and [entityId2].
   */
  fun <E1 : E, E2 : E> holds(
      predicate: BinaryPredicate<E1, E2, E, T, S, U, D>,
      tick: U,
      entityId1: Int,
      entityId2: Int
  ): Boolean =
      binaryPredicateCache.getOrPut(predicate to (Triple(tick, entityId1, entityId2))) {
        val currentTick = segment.ticks[tick]
        val entity1 = currentTick?.getEntityById(entityId1)
        val entity2 = currentTick?.getEntityById(entityId2)

        entityId1 != entityId2 &&
            currentTick != null &&
            predicate.kClasses.first.isInstance(entity1) &&
            predicate.kClasses.second.isInstance(entity2) &&
            predicate.eval(
                this,
                predicate.kClasses.first.cast(entity1),
                predicate.kClasses.second.cast(entity2))
      }
}
