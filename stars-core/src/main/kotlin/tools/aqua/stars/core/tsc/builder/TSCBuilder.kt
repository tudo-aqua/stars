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
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating objects in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 */
@TSCBuilderMarker
sealed class TSCBuilder<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
> {

  /** Holds all [TSCEdge]s of the [TSCNode]. */
  protected val edges: MutableList<TSCEdge<E, T, U, D>> = mutableListOf()

  /** Holds all monitors of the [TSCNode] as [Predicate]s. */
  protected val monitorMap: MutableMap<String, Predicate<T>> = mutableMapOf()

  /** Holds the optional monitors [TSCEdge] as [Predicate]s. */
  protected var monitors: Map<String, Predicate<T>>? = null
    set(value) {
      check(monitors == null) { "Monitors node already set." }
      field = value
    }

  /** Condition predicate of the [TSCEdge]. (Default: [CONST_TRUE]) */
  protected var condition: Predicate<T> = Predicate(name = "CONST_TRUE", eval = CONST_TRUE)
    set(value) {
      check(!isConditionSet) { "Condition already set." }
      isConditionSet = true
      field = value
    }

  private var isConditionSet = false

  /** Value function predicate of the [TSCNode]. (Default: empty) */
  protected var valueFunction: ((T) -> Any) = { _ -> }
    set(value) {
      check(!isValueFunctionSet) { "Value function already set." }
      isValueFunctionSet = true
      field = value
    }

  private var isValueFunctionSet = false

  /**
   * Adds the given [edge] to [edges]. This will become the [TSCEdge]s of the [TSCNode] that will be
   * created off of this object.
   *
   * @param edge [TSCEdge] to be added.
   */
  fun addEdge(edge: TSCEdge<E, T, U, D>) {
    check(edges.none { it.destination.label == edge.destination.label }) {
      "Edge to node with label ${edge.destination.label} already exists in this scope."
    }
    edges.add(edge)
  }

  /**
   * Returns the amount of elements in [edges].
   *
   * @return The amount of [TSCEdge]s.
   */
  fun edgesCount(): Int = edges.size
}
