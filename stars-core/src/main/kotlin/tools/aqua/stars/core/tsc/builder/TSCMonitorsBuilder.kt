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

package tools.aqua.stars.core.tsc.builder

import tools.aqua.stars.core.evaluation.Predicate
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating monitors nodes in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 */
open class TSCMonitorsBuilder<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
> : TSCBuilder<E, T, U, D>() {

  /** Creates the monitors map. */
  fun build(): Map<String, Predicate<E, T, U, D>> = monitorMap

  /**
   * DSL function for a monitor. Creates a [Predicate] internally with the given [condition] and
   * [label] name.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param label Name of the edge.
   * @param condition The monitor condition.
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCMonitorsBuilder<E, T, U, D>.monitor(
      label: String,
      condition: (T) -> Boolean,
  ) {
    check(!monitorMap.containsKey(label)) { "Monitor $label already exists" }
    monitorMap[label] = Predicate(name = label, eval = condition)
  }

  /**
   * DSL function for a monitor.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param label Optional name of the edge. If empty, the name of the [predicate] is used.
   * @param predicate The monitor condition [Predicate].
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCMonitorsBuilder<E, T, U, D>.monitor(
      label: String = "",
      predicate: Predicate<E, T, U, D>,
  ) {
    val monitorLabel = label.ifEmpty { predicate.name }
    check(!monitorMap.containsKey(monitorLabel)) { "Monitor $monitorLabel already exists" }
    monitorMap[monitorLabel] = predicate
  }
}
