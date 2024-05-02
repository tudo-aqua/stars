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
import tools.aqua.stars.core.tsc.edge.TSCAlwaysEdge
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.node.TSCBoundedNode
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating objects in the dsl. always contains one edge and the node that
 * belongs to that edge (edge.destination = node)
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property label name of the edge.
 * @property valueFunction Value function predicate of the node.
 * @property monitorFunction Monitor function predicate of the node.
 * @property projectionIDs Projection identifier of the node.
 * @property bounds Bounds of the node, only relevant for bounded nodes.
 * @property condition Condition predicate of the edge.
 * @property onlyMonitor Flag to indicate if this node is only a monitor.
 */
class TSCBuilder<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val label: String = "",
    var valueFunction: (PredicateContext<E, T, S, U, D>) -> Any = {},
    var monitorFunction: (PredicateContext<E, T, S, U, D>) -> Boolean = { true },
    var projectionIDs: Map<Any, Boolean> = mapOf(),
    var bounds: Pair<Int, Int> = Pair(0, 0),
    var condition: ((PredicateContext<E, T, S, U, D>) -> Boolean)? = null,
    var onlyMonitor: Boolean = false
) {

  /** Holds all edges of the node. */
  private val edges: MutableList<TSCEdge<E, T, S, U, D>> = mutableListOf()

  /**
   * Creates a [TSCEdge] with a [TSCBoundedNode]. Only functions where [bounds] is relevant.
   *
   * @return The created [TSCEdge].
   */
  fun buildBounded(): TSCEdge<E, T, S, U, D> {
    val node =
        TSCBoundedNode(
            valueFunction, monitorFunction, projectionIDs, bounds, edges.toList(), onlyMonitor)
    return condition?.let { cond -> TSCEdge(label, cond, node) } ?: TSCAlwaysEdge(label, node)
  }

  /**
   * Adds the given [edge] to [edges]. This will become the edges of the node that will be created
   * off of this object.
   *
   * @param edge [TSCEdge] to be added.
   */
  fun addEdge(edge: TSCEdge<E, T, S, U, D>) {
    edges.add(edge)
  }

  /**
   * Returns the amount of elements in [edges].
   *
   * @return The amount of [TSCEdge]s.
   */
  fun edgesCount(): Int = edges.size
}
