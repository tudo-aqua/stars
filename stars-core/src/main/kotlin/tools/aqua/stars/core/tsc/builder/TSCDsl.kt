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

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.edge.TSCBoundedEdge
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.edge.TSCLeafEdge
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

val CONST_TRUE : ((PredicateContext<*,*,*,*,*>) -> Boolean) = { true }

/**
 * Builds root node. Applies [init] function to [TSCNode].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param init The init function. Must add exactly one edge.
 *
 * @return The [TSCNode] at the root level of the TSC.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> root(
  init: TSCBoundedBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCNode<E, T, S, U, D> {
  val placeholderNode =
    TSCBoundedBuilder<E, T, S, U, D>("root")
      .apply { init() }
      .apply { this.bounds = edgesCount() to edgesCount() }
      .build()

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
 * @param init The init function.
 *
 * @return The [TSCEdge] with the given bounds.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.bounded(
  label: String,
  bounds: Pair<Int, Int>,
  init: TSCBoundedBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
  TSCBoundedBuilder<E, T, S, U, D>(label)
    .apply { init() }
    .apply { this.bounds = bounds }
    .build()
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
 * @param init The init function.
 *
 * @return The [TSCEdge] with the specific bounds (1,1).
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.exclusive(
  label: String,
  init: TSCBoundedBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
  TSCBoundedBuilder<E, T, S, U, D>(label)
    .apply { init() }
    .apply { bounds = 1 to 1 }
    .build()
    .also { this.addEdge(it) }

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
    D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.optional(
  label: String,
  init: TSCBoundedBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
  TSCBoundedBuilder<E, T, S, U, D>(label)
    .apply { init() }
    .apply { bounds = 0 to edgesCount() }
    .build()
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
 * @param init The init function.
 *
 * @return The [TSCEdge] with the specific bounds (1,#Edges).
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.any(
  label: String,
  init: TSCBoundedBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
  TSCBoundedBuilder<E, T, S, U, D>(label)
    .apply { init() }
    .apply { bounds = 1 to edgesCount() }
    .build()
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
 * @param init The init function.
 *
 * @return The [TSCEdge] with the specific bounds (#Edges,#Edges).
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.all(
  label: String,
  init: TSCBoundedBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCBoundedEdge<E, T, S, U, D> =
  TSCBoundedBuilder<E, T, S, U, D>(label)
    .apply { init() }
    .apply { bounds = edgesCount() to edgesCount() }
    .build()
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
 * @param init The init function.
 *
 * @return The [TSCEdge] that is connected to a leaf node.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.leaf(
  label: String,
  init: TSCLeafBuilder<E, T, S, U, D>.() -> Unit = {}
): TSCLeafEdge<E, T, S, U, D> =
  TSCLeafBuilder<E, T, S, U, D>(label)
    .apply { init() }
    .build()
    .also { this.addEdge(it) }

//fun <
//    E : EntityType<E, T, S, U, D>,
//    T : TickDataType<E, T, S, U, D>,
//    S : SegmentType<E, T, S, U, D>,
//    U : TickUnit<U, D>,
//    D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.monitors(
//    init: TSCMonitorBuilder<E, T, S, U, D>.() -> Unit = {}
//): TSCMonitorsEdge<E, T, S, U, D> =
//    TSCMonitorBuilder<E, T, S, U, D>(label)
//        .apply { init() }
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
//    D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.monitor(
//    label: String,
//    monitorFunction: (PredicateContext<E, T, S, U, D>) -> Boolean
// ): TSCEdge<E, T, S, U, D> =
//    this.bounded(label, 0 to 0) {
//      this.condition = { _ -> true }
//      this.onlyMonitor = true
//      this.monitorFunction = monitorFunction
//    }
