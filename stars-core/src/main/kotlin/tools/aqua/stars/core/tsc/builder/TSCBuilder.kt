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
import tools.aqua.stars.core.tsc.edge.TSCMonitorsEdge
import tools.aqua.stars.core.tsc.edge.TSCProjectionsEdge
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating objects in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param B [TSCEdge].
 * @property label Name of the edge.
 * @property bounds Bounds of the node, only relevant for bounded nodes.
 */
@TSCBuilderMarker
sealed class TSCBuilder<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
    B : TSCEdge<E, T, S, U, D>>(
    val label: String,
    var bounds: Pair<Int, Int>,
) {

  /** Holds all edges of the node. */
  protected val edges: MutableList<B> = mutableListOf()

  /** Holds the optional projections. */
  protected var projections: TSCProjectionsEdge<E, T, S, U, D>? = null
    set(value) {
      check(projections == null) { "Projections node already set." }
      field = value
    }

  /** Holds the optional monitors edge. */
  protected var monitors: TSCMonitorsEdge<E, T, S, U, D>? = null
    set(value) {
      check(monitors == null) { "Monitors node already set." }
      field = value
    }

  /** Condition predicate of the edge. (Default: [CONST_TRUE]) */
  protected var condition: ((PredicateContext<E, T, S, U, D>) -> Boolean)? = CONST_TRUE
    set(value) {
      check(!conditionSet) { "Condition already set." }
      conditionSet = true
      field = value
    }
  private var conditionSet = false

  /** Value function predicate of the node. (Default: empty) */
  protected var valueFunction: ((PredicateContext<E, T, S, U, D>) -> Any) = { _ -> }
    set(value) {
      check(!valueFunctionSet) { "Value function already set." }
      valueFunctionSet = true
      field = value
    }
  private var valueFunctionSet = false

  /** Projection identifier of the node. (Default: empty map) */
  protected val projectionIDs: MutableMap<Any, Boolean> = mutableMapOf()

  /** Builds the [TSCEdge]. */
  abstract fun build(): B

  /**
   * Adds the given [edge] to [edges]. This will become the edges of the node that will be created
   * off of this object.
   *
   * @param edge [TSCEdge] to be added.
   */
  fun addEdge(edge: B) {
    check(edges.none { it.label == edge.label }) {
      "Edge with label ${edge.label} already exists in this scope."
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
