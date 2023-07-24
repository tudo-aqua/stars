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

class PredicateContext<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(val segment: S) {

  var egoID = segment.egoVehicleId // TODO: rename to primaryEntityId

  /** cache for all entity IDs */
  private val entityIdsCache = mutableListOf<Int>()

  /** all entity IDs of the current context state */
  val entityIds: List<Int>
    get() {
      if (entityIdsCache.isEmpty()) {
        entityIdsCache.addAll(
            segment.tickData.flatMap { tickData -> tickData.entities.map { it.id } }.distinct())
      }
      return entityIdsCache
    }

  /** all tick IDs of the current context state */
  val tids
    get() = segment.tickIDs

  // region evaluation for TickPredicateZero

  private val nullaryPredicateCache: MutableMap<NullaryPredicate<E, T, S>, List<Double>> =
      mutableMapOf()

  // TODO: NullaryPredicate should have `holds` method like the others; with timestamp?

  fun evaluate(p: NullaryPredicate<E, T, S>): List<Double> {
    var evaluation = nullaryPredicateCache[p]
    return if (evaluation == null) {
      evaluation = segment.tickData.filter { p.eval(this, it) }.map { it.currentTick }
      nullaryPredicateCache += p to evaluation
      evaluation
    } else {
      evaluation
    }
  }

  // endregion

  // region evaluation for TickPredicateOne

  private val tp1holdsCache:
      MutableMap<Pair<UnaryPredicate<*, E, T, S>, Pair<Double, Int>>, Boolean> =
      mutableMapOf()

  fun <E1 : E> holds(p: UnaryPredicate<E1, E, T, S>, tid: Double, vid1: Int): Boolean =
      tp1holdsCache.getOrPut(p to (tid to vid1)) {
        val tick = segment.ticks[tid]
        val actor = tick?.entities?.firstOrNull { it.id == vid1 }
        if (tick != null && p.klass.isInstance(actor)) {
          p.eval(this, p.klass.cast(actor))
        } else {
          false
        }
      }

  // endregion

  // region evaluation for region TickPredicateTwo

  private val tp2holdsCache:
      MutableMap<Pair<BinaryPredicate<*, *, E, T, S>, Triple<Double, Int, Int>>, Boolean> =
      mutableMapOf()

  fun <E1 : E, E2 : E> holds(
      p: BinaryPredicate<E1, E2, E, T, S>,
      tid: Double,
      vid1: Int,
      vid2: Int
  ): Boolean =
      tp2holdsCache.getOrPut(p to (Triple(tid, vid1, vid2))) {
        val tick = segment.ticks[tid]
        val actor1 = tick?.entities?.firstOrNull { it.id == vid1 }
        val actor2 = tick?.entities?.firstOrNull { it.id == vid2 }
        if (vid1 == vid2) {
          false
        } else if (tick != null &&
            p.klass.first.isInstance(actor1) &&
            p.klass.second.isInstance(actor2)) {
          p.eval(this, p.klass.first.cast(actor1), p.klass.second.cast(actor2))
        } else {
          false
        }
      }

  // endregion

}
