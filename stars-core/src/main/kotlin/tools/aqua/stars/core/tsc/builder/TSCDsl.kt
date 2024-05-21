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

@file:Suppress("unused")

package tools.aqua.stars.core.tsc.builder

import tools.aqua.stars.core.tsc.edge.TSCBoundedEdge
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.edge.TSCLeafEdge
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * Builds root node. Applies [buildSubtree] function to [TSCNode].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param buildSubtree The buildSubtree function. Must add exactly one edge.
 *
 * @return The [TSCNode] at the root level of the TSC.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> root(
    buildSubtree: TSCBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCNode<E, T, S, U, D> {
  val placeholderNode =
      TSCBuilder<E, T, S, U, D>()
          .apply {
            buildSubtree()
            this.bounds = edgesCount() to edgesCount()
          }
          .buildBounded()

  check(placeholderNode.destination.edges.size < 2) {
    "Too many elements to add - root can only host one."
  }
  check(placeholderNode.destination.edges.isNotEmpty()) {
    "Init must add exactly one element to root."
  }
  return placeholderNode.destination.edges[0].destination
}

/**
 * DSL function for an edge with BoundedNode.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label Name of the edge.
 * @param bounds Defines lower and upper limit of the BoundedNode.
 * @param buildSubtree The buildSubtree function.
 *
 * @return The [TSCEdge] with the given bounds.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBuilder<E, T, S, U, D>.bounded(
    label: String,
    bounds: Pair<Int, Int> = Pair(1, 1),
    buildSubtree: TSCBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
    TSCBuilder<E, T, S, U, D>(label)
        .apply {
          buildSubtree()
          this.bounds = bounds
        }
        .buildBounded()
        .also { this.addEdge(it) }

/**
 * DSL function for an edge with BoundedNode with the limits of (1,1).
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label name of the edge.
 * @param buildSubtree The buildSubtree function.
 *
 * @return The [TSCEdge] with the specific bounds (1,1).
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBuilder<E, T, S, U, D>.exclusive(
    label: String,
    buildSubtree: TSCBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> = this.bounded(label, 1 to 1) { buildSubtree() }

/**
 * DSL function for an edge with BoundedNode with the limits of (0,#Edges).
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label name of the edge.
 * @param init The init function.
 *
 * @return The [TSCEdge] with the specific bounds (0,1#Edges).
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBuilder<E, T, S, U, D>.optional(
    label: String,
    init: TSCBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
    TSCBuilder<E, T, S, U, D>(label)
        .apply {
          init()
          bounds = 0 to edgesCount()
        }
        .buildBounded()
        .also { this.addEdge(it) }

/**
 * DSL function for an edge with BoundedNode with the limits of (1,#Edges).
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label name of the edge.
 * @param buildSubtree The buildSubtree function.
 *
 * @return The [TSCEdge] with the specific bounds (1,#Edges).
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBuilder<E, T, S, U, D>.any(
    label: String,
    buildSubtree: TSCBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
    TSCBuilder<E, T, S, U, D>(label)
        .apply {
          buildSubtree()
          bounds = 1 to edgesCount()
        }
        .buildBounded()
        .also { this.addEdge(it) }

/**
 * DSL function for an edge with BoundedNode with the limits of (#Edges,#Edges).
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label name of the edge.
 * @param buildSubtree The buildSubtree function.
 *
 * @return The [TSCEdge] with the specific bounds (#Edges,#Edges).
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBuilder<E, T, S, U, D>.all(
    label: String,
    buildSubtree: TSCBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
    TSCBuilder<E, T, S, U, D>(label)
        .apply {
          buildSubtree()
          bounds = edgesCount() to edgesCount()
        }
        .buildBounded()
        .also { this.addEdge(it) }

/**
 * DSL function for an edge with LeafNode.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label name of the edge.
 * @param buildSubtree The buildSubtree function.
 *
 * @return The [TSCEdge] that is connected to a leaf node.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBuilder<E, T, S, U, D>.leaf(
    label: String,
    buildSubtree: TSCBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCLeafEdge<E, T, S, U, D> =
  TSCBuilder<E, T, S, U, D>(label)
    .apply {
      buildSubtree()
    }
    .buildLeaf()
    .also { this.addEdge(it) }

//fun <
//    E : EntityType<E, T, S, U, D>,
//    T : TickDataType<E, T, S, U, D>,
//    S : SegmentType<E, T, S, U, D>,
//    U : TickUnit<U, D>,
//    D : TickDifference<D>> TSCBuilder<E, T, S, U, D>.monitors(
//    buildSubtree: TSCMonitorBuilder<E, T, S, U, D>.() -> Unit = {}
//): TSCMonitorsEdge<E, T, S, U, D> =
//    TSCMonitorBuilder<E, T, S, U, D>(label)
//        .apply { buildSubtree() }
//        .buildMonitor()
//        .also { this.addEdge(it) }

/// **
// * DSL function for monitor function nodes.
// *
// * @param E [EntityType].
// * @param T [TickDataType].
// * @param S [SegmentType].
// * @param U [TickUnit].
// * @param D [TickDifference].
// * @param label name of the edge.
// * @param monitorFunction The monitor function.
// *
// * @return The [TSCEdge] that is connected to a monitor leaf node.
// */
// fun <
//    E : EntityType<E, T, S, U, D>,
//    T : TickDataType<E, T, S, U, D>,
//    S : SegmentType<E, T, S, U, D>,
//    U : TickUnit<U, D>,
//    D : TickDifference<D>> TSCBuilder<E, T, S, U, D>.monitor(
//    label: String,
//    monitorFunction: (PredicateContext<E, T, S, U, D>) -> Boolean
// ): TSCEdge<E, T, S, U, D> =
//    this.bounded(label, 0 to 0) {
//      this.condition = { _ -> true }
//      this.onlyMonitor = true
//      this.monitorFunction = monitorFunction
//    }
