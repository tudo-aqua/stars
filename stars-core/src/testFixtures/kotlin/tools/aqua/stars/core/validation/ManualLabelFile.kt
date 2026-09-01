/*
 * Copyright 2025-2026 The STARS Project Authors
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

package tools.aqua.stars.core.validation

import kotlin.collections.plusAssign
import tools.aqua.stars.core.evaluation.Predicate
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit

/**
 * Represents a manually labeled file that stores predicates to be tested against sequences of ticks
 * which manually labeled results for the predicates.
 *
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property ticksToTest A sequence of tick data sequences that the predicates will be tested on.
 */
class ManualLabelFile<
    T : TickDataType<*, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(val ticksToTest: List<T>) {
  internal val predicatesToHold = mutableListOf<ManualLabelPredicate<T, U, D>>()
  internal val predicatesToNotHold = mutableListOf<ManualLabelPredicate<T, U, D>>()

  /**
   * Adds a manually labeled predicate to the list of predicates that are expected to hold true for
   * specified intervals of tick sequences within a [ManualLabelFile].
   *
   * @param predicate The abstract predicate to be evaluated against tick sequences.
   * @param manualLabelPredicateInvocation A lambda for configuring the manual label predicate,
   *   including defining the intervals where the predicate is expected to hold true.
   */
  fun predicateHolds(
      predicate: Predicate<T>,
      manualLabelPredicateInvocation: ManualLabelPredicate<T, U, D>.() -> Unit,
  ) {
    val manualLabelPredicate = ManualLabelPredicate(predicate)
    manualLabelPredicate.apply(manualLabelPredicateInvocation)
    predicatesToHold += manualLabelPredicate
  }

  /**
   * Adds a manually labeled predicate to the list of predicates that are expected *not* to hold
   * true for specified intervals of tick sequences within a [ManualLabelFile].
   *
   * @param predicate The abstract predicate to be evaluated against tick sequences.
   * @param manualLabelPredicateInvocation A lambda for configuring the manual label predicate,
   *   including defining the intervals where the predicate is expected not to hold true.
   */
  fun predicateDoesNotHold(
      predicate: Predicate<T>,
      manualLabelPredicateInvocation: ManualLabelPredicate<T, U, D>.() -> Unit,
  ) {
    val manualLabelPredicate = ManualLabelPredicate(predicate)
    manualLabelPredicate.apply(manualLabelPredicateInvocation)
    predicatesToNotHold += manualLabelPredicate
  }
}
