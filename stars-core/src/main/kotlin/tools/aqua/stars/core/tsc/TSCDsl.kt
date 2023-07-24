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

package tools.aqua.stars.core.tsc

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * class to assist in creating objects in the dsl. always contains one edge and the node that
 * belongs to that edge (edge.destination = node)
 * @param label name of the edge
 * @property valueFunction valueFunction of the node
 * @property monitorFunction monitorFunction of the node
 * @property projectionIDs projectionIDs of the node
 * @property bounds bounds of the node, only relevant for bounded nodes
 * @property edges contains all edges of the node
 * @property condition condition of the edge
 */
class TSCBuilder<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    var label: String = ""
) {
  var valueFunction: (PredicateContext<E, T, S>) -> Any = {}
  var monitorFunction: (PredicateContext<E, T, S>) -> Boolean = { true }
  var projectionIDs: Map<Any, Boolean> = mapOf()
  var bounds: Pair<Int, Int> = Pair(0, 0)
  private var edges: MutableList<TSCEdge<E, T, S>> = mutableListOf()

  var condition: ((PredicateContext<E, T, S>) -> Boolean)? = null

  /** creates an Edge with a BoundedNode, only method where bounds is relevant */
  fun buildBounded(): TSCEdge<E, T, S> {
    val node =
        TSCBoundedNode(valueFunction, monitorFunction, projectionIDs, bounds, *edges.toTypedArray())
    return condition?.let { cond -> TSCEdge(label, cond, node) } ?: TSCAlwaysEdge(label, node)
  }

  /**
   * adds the given edge to "edges" - this will become the edges of the node that will be created
   * off of this object
   * @param edge
   */
  fun addEdge(edge: TSCEdge<E, T, S>) {
    edges.add(edge)
  }

  /** returns the amount of elements in "edges" */
  fun edgesCount(): Int {
    return edges.size
  }
}

fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> root(
    init: TSCBuilder<E, T, S>.() -> Unit = {}
): TSCNode<E, T, S> {
  val placeholderNode =
      TSCBuilder<E, T, S>()
          .apply {
            init()
            this.bounds = edgesCount() to edgesCount()
          }
          .buildBounded()
  check(placeholderNode.destination.edges.size < 2) {
    "Too many elements to add - root can only host one."
  }
  check(placeholderNode.destination.edges.isNotEmpty()) {
    "init must add exactly one element to root"
  }
  return placeholderNode.destination.edges[0].destination
}

/**
 * DSL function for an edge with BoundedNode
 * @param label name of the edge
 * @param bounds defines lower and upper limit of the BoundedNode
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .bounded(
    label: String,
    bounds: Pair<Int, Int> = Pair(1, 1),
    init: TSCBuilder<E, T, S>.() -> Unit = {}
): TSCEdge<E, T, S> {
  return TSCBuilder<E, T, S>(label)
      .apply {
        init()
        this.bounds = bounds
      }
      .buildBounded()
      .also { this.addEdge(it) }
}

/**
 * DSL function for an edge with BoundedNode with the limits of (1,1)
 * @param label name of the edge
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .exclusive(label: String, init: TSCBuilder<E, T, S>.() -> Unit = {}): TSCEdge<E, T, S> {
  return this.bounded(label, 1 to 1) { init() }
}

/**
 * DSL function for an edge with BoundedNode with the limits of (0,#Edges)
 * @param label name of the edge
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .optional(label: String, init: TSCBuilder<E, T, S>.() -> Unit = {}): TSCEdge<E, T, S> {
  return TSCBuilder<E, T, S>(label)
      .apply {
        init()
        bounds = 0 to edgesCount()
      }
      .buildBounded()
      .also { addEdge(it) }
}

/**
 * DSL function for an edge with BoundedNode with the limits of (#Edges,#Edges)
 * @param label name of the edge
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .all(label: String, init: TSCBuilder<E, T, S>.() -> Unit = {}): TSCEdge<E, T, S> {
  return TSCBuilder<E, T, S>(label)
      .apply {
        init()
        this.bounds = edgesCount() to edgesCount()
      }
      .buildBounded()
      .also { addEdge(it) }
}

/**
 * DSL function for an edge with LeafNode
 * @param label name of the edge
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .leaf(label: String, init: TSCBuilder<E, T, S>.() -> Unit = {}): TSCEdge<E, T, S> {
  return TSCBuilder<E, T, S>(label)
      .apply {
        init()
        this.bounds = 0 to 0
      }
      .buildBounded()
      .also { this.addEdge(it) }
}
