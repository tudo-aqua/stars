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
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating objects in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 */
@TSCBuilderMarker
sealed class TSCBuilder<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> {

  /** Holds all edges of the node. */
  protected val edges: MutableList<TSCEdge<E, T, S, U, D>> = mutableListOf()

  /** Holds all monitors of the node. */
  protected val monitorMap: MutableMap<String, (PredicateContext<E, T, S, U, D>) -> Boolean> =
      mutableMapOf()

  /** Holds the optional projections. */
  protected var projections: Map<String, Boolean>? = null
    set(value) {
      check(projections == null) { "Projections node already set." }
      field = value
    }

  /** Holds the optional monitors edge. */
  protected var monitors: Map<String, (PredicateContext<E, T, S, U, D>) -> Boolean>? = null
    set(value) {
      check(monitors == null) { "Monitors node already set." }
      field = value
    }

  /** Condition predicate of the edge. (Default: [CONST_TRUE]) */
  protected var condition: ((PredicateContext<E, T, S, U, D>) -> Boolean)? = CONST_TRUE
    set(value) {
      check(!isConditionSet) { "Condition already set." }
      isConditionSet = true
      field = value
    }

  private var isConditionSet = false

  /** Value function predicate of the node. (Default: empty) */
  protected var valueFunction: ((PredicateContext<E, T, S, U, D>) -> Any) = { _ -> }
    set(value) {
      check(!isValueFunctionSet) { "Value function already set." }
      isValueFunctionSet = true
      field = value
    }

  private var isValueFunctionSet = false

  /**
   * Adds the given [edge] to [edges]. This will become the edges of the node that will be created
   * off of this object.
   *
   * @param edge [TSCEdge] to be added.
   */
  fun addEdge(edge: TSCEdge<E, T, S, U, D>) {
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
