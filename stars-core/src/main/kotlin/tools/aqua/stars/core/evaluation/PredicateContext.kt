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
 * Predicate context.
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

  private val nullaryPredicateCache: MutableMap<NullaryPredicate<E, T, S, U, D>, List<U>> =
      mutableMapOf()
  private val tp1holdsCache:
      MutableMap<Pair<UnaryPredicate<*, E, T, S, U, D>, Pair<U, Int>>, Boolean> =
      mutableMapOf()
  private val tp2holdsCache:
      MutableMap<Pair<BinaryPredicate<*, *, E, T, S, U, D>, Triple<U, Int, Int>>, Boolean> =
      mutableMapOf()

  /** Evaluates [NullaryPredicate] on this [PredicateContext]. */
  fun evaluate(p: NullaryPredicate<E, T, S, U, D>): List<U> {
    var evaluation = nullaryPredicateCache[p]

    if (evaluation == null) {
      evaluation = segment.tickData.filter { p.eval(this, it) }.map { it.currentTick }
      nullaryPredicateCache += p to evaluation
    }

    return evaluation
  }

  /**
   * Evaluates whether [UnaryPredicate] [p] hold on current [PredicateContext].
   *
   * @param E1 [EntityType].
   * @param p The predicate.
   * @param tick The tick.
   * @param vid1 Value 1.
   */
  fun <E1 : E> holds(p: UnaryPredicate<E1, E, T, S, U, D>, tick: U, vid1: Int): Boolean =
      tp1holdsCache.getOrPut(p to (tick to vid1)) {
        val currentTick = segment.ticks[tick]
        val entity = currentTick?.getEntityById(vid1)

        currentTick != null && p.kClass.isInstance(entity) && p.eval(this, p.kClass.cast(entity))
      }

  /**
   * Evaluates whether [BinaryPredicate] [p] hold on current [PredicateContext].
   *
   * @param E1 [EntityType].
   * @param E2 [EntityType].
   * @param p The predicate.
   * @param tick The tick.
   * @param vid1 Value 1.
   * @param vid2 Value 2.
   */
  fun <E1 : E, E2 : E> holds(
      p: BinaryPredicate<E1, E2, E, T, S, U, D>,
      tick: U,
      vid1: Int,
      vid2: Int
  ): Boolean =
      tp2holdsCache.getOrPut(p to (Triple(tick, vid1, vid2))) {
        val currentTick = segment.ticks[tick]
        val entity1 = currentTick?.getEntityById(vid1)
        val entity2 = currentTick?.getEntityById(vid2)

        vid1 != vid2 &&
            currentTick != null &&
            p.kClasses.first.isInstance(entity1) &&
            p.kClasses.second.isInstance(entity2) &&
            p.eval(this, p.kClasses.first.cast(entity1), p.kClasses.second.cast(entity2))
      }
}
