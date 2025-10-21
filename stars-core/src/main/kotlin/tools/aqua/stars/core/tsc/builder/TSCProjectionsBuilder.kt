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

package tools.aqua.stars.core.tsc.builder

import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating monitors [TSCNode]s in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 */
open class TSCProjectionsBuilder<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
> : TSCBuilder<E, T, U, D>() {

  /** Holds all projections of the [TSCNode]. */
  private val projectionIDs: MutableMap<String, Boolean> = mutableMapOf()

  /** Creates the projections map. */
  fun build(): Map<String, Boolean> = projectionIDs

  /**
   * DSL function to build projections from a label without recursion.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param label Name of the projection.
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCProjectionsBuilder<E, T, U, D>.projection(label: String) {
    check(!projectionIDs.containsKey(label)) { "Projection $label already exists" }
    projectionIDs[label] = false
  }

  /**
   * DSL function to build projections from a label with recursion.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param label Name of the projection.
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCProjectionsBuilder<E, T, U, D>.projectionRecursive(label: String) {
    check(!projectionIDs.containsKey(label)) { "Projection $label already exists" }
    projectionIDs[label] = true
  }
}
