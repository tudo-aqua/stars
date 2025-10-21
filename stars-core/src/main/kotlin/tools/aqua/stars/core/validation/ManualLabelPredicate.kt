/*
 * Copyright 2025 The STARS Project Authors
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

import tools.aqua.stars.core.evaluation.Predicate
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit

/**
 * The DSL builder for manual label predicates.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property predicate The abstract predicate to be tested.
 */
class ManualLabelPredicate<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(val predicate: Predicate<E, T, U, D>) {
  internal val manualLabelIntervals = mutableListOf<ManualLabelInterval<U, D>>()

  /** The name of the current predicate, derived from the associated abstract predicate. */
  val name: String = predicate.name

  /**
   * Defines a manual label interval for the current predicate. The interval is specified between
   * two tick units and will be added to the list of manually defined label intervals.
   *
   * @param fromTickUnit The starting tick unit of the interval.
   * @param toTickUnit The ending tick unit of the interval. Must be greater than or equal to
   *   [fromTickUnit].
   * @throws IllegalArgumentException If [toTickUnit] is less than [fromTickUnit].
   */
  fun interval(fromTickUnit: U, toTickUnit: U) {
    require(toTickUnit >= fromTickUnit) { "Invalid interval [$fromTickUnit, $toTickUnit]" }
    manualLabelIntervals += ManualLabelInterval(fromTickUnit, toTickUnit)
  }
}
