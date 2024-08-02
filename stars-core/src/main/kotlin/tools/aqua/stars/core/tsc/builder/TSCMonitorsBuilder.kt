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

package tools.aqua.stars.core.tsc.builder

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating monitors nodes in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 */
open class TSCMonitorsBuilder<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> : TSCBuilder<E, T, S, U, D>() {

  /** Creates the monitors map. */
  fun build(): Map<String, (PredicateContext<E, T, S, U, D>) -> Boolean> = monitorMap

  /**
   * DSL function for a monitor.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param S [SegmentType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param label Name of the edge.
   * @param condition The monitor condition.
   */
  fun <
      E : EntityType<E, T, S, U, D>,
      T : TickDataType<E, T, S, U, D>,
      S : SegmentType<E, T, S, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>> TSCMonitorsBuilder<E, T, S, U, D>.monitor(
      label: String,
      condition: (PredicateContext<E, T, S, U, D>) -> Boolean
  ) {
    check(!monitorMap.containsKey(label)) { "Monitor $label already exists" }
    monitorMap[label] = condition
  }
}
