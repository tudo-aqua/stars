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

package tools.aqua.stars.core.tsc.builder

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.edge.TSCAlwaysEdge
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.node.TSCBoundedNode
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * Class to assist in creating objects in the dsl. always contains one edge and the node that
 * belongs to that edge (edge.destination = node)
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param label name of the edge.
 * @param valueFunction Value function predicate of the node.
 * @param monitorFunction Monitor function predicate of the node.
 * @property projectionIDs Projection identifier of the node.
 * @property bounds Bounds of the node, only relevant for bounded nodes.
 * @property condition Condition predicate of the edge.
 */
class TSCBuilder<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    private val label: String = "",
    private val valueFunction: (PredicateContext<E, T, S>) -> Any = {},
    private val monitorFunction: (PredicateContext<E, T, S>) -> Boolean = { true },
    var projectionIDs: Map<Any, Boolean> = mapOf(),
    var bounds: Pair<Int, Int> = Pair(0, 0),
    var condition: ((PredicateContext<E, T, S>) -> Boolean)? = null,
) {

  /** All edges of the node. */
  private var edges: MutableList<TSCEdge<E, T, S>> = mutableListOf()

  /** Creates an Edge with a BoundedNode. Only function where [bounds] is relevant. */
  fun buildBounded(): TSCEdge<E, T, S> {
    val node = TSCBoundedNode(valueFunction, monitorFunction, projectionIDs, bounds, edges.toList())
    return condition?.let { cond -> TSCEdge(label, cond, node) } ?: TSCAlwaysEdge(label, node)
  }

  /**
   * Adds the given [edge] to [edges]. This will become the edges of the node that will be created
   * off of this object.
   *
   * @param edge [TSCEdge] to be added.
   */
  fun addEdge(edge: TSCEdge<E, T, S>) {
    edges.add(edge)
  }

  /** Returns the amount of elements in [edges]. */
  fun edgesCount(): Int = edges.size
}
