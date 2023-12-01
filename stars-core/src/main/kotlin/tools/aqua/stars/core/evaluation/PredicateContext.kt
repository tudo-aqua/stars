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

import kotlin.reflect.cast
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * Predicate context.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @property segment The current [SegmentType].
 */
class PredicateContext<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(val segment: S) {

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

  /** All tick IDs of the current context state. */
  @Suppress("unused")
  val tIDs: List<Double>
    get() = segment.tickIDs

  private val nullaryPredicateCache: MutableMap<NullaryPredicate<E, T, S>, List<Double>> =
      mutableMapOf()
  private val tp1holdsCache:
      MutableMap<Pair<UnaryPredicate<*, E, T, S>, Pair<Double, Int>>, Boolean> =
      mutableMapOf()
  private val tp2holdsCache:
      MutableMap<Pair<BinaryPredicate<*, *, E, T, S>, Triple<Double, Int, Int>>, Boolean> =
      mutableMapOf()

  /** Evaluates [NullaryPredicate] on this [PredicateContext]. */
  fun evaluate(p: NullaryPredicate<E, T, S>): List<Double> {
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
   * @param tid The tick identifier.
   * @param vid1 Value 1.
   */
  fun <E1 : E> holds(p: UnaryPredicate<E1, E, T, S>, tid: Double, vid1: Int): Boolean =
      tp1holdsCache.getOrPut(p to (tid to vid1)) {
        val tick = segment.ticks[tid]
        val actor = tick?.run { entities.firstOrNull { it.id == vid1 } }

        tick != null && p.kClass.isInstance(actor) && p.eval(this, p.kClass.cast(actor))
      }

  /**
   * Evaluates whether [BinaryPredicate] [p] hold on current [PredicateContext].
   *
   * @param E1 [EntityType]
   * 1.
   * @param E2 [EntityType]
   * 2.
   * @param p The predicate.
   * @param tid The tick identifier.
   * @param vid1 Value 1.
   * @param vid2 Value 2.
   */
  fun <E1 : E, E2 : E> holds(
      p: BinaryPredicate<E1, E2, E, T, S>,
      tid: Double,
      vid1: Int,
      vid2: Int
  ): Boolean =
      tp2holdsCache.getOrPut(p to (Triple(tid, vid1, vid2))) {
        val tick = segment.ticks[tid]
        val actor1 = tick?.run { entities.firstOrNull { it.id == vid1 } }
        val actor2 = tick?.run { entities.firstOrNull { it.id == vid2 } }

        vid1 != vid2 &&
            tick != null &&
            p.kClasses.first.isInstance(actor1) &&
            p.kClasses.second.isInstance(actor2) &&
            p.eval(this, p.kClasses.first.cast(actor1), p.kClasses.second.cast(actor2))
      }
}
