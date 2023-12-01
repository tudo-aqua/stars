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

@file:Suppress("unused")

package tools.aqua.stars.core.tsc.builder

import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * Builds root node. Applies [init] function to [TSCNode].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param init The init function. Must add exactly one edge.
 */
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
 * @param label Name of the edge.
 * @param bounds Defines lower and upper limit of the BoundedNode.
 * @param init The init function.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .bounded(
    label: String,
    bounds: Pair<Int, Int> = Pair(1, 1),
    init: TSCBuilder<E, T, S>.() -> Unit = {}
): TSCEdge<E, T, S> =
    TSCBuilder<E, T, S>(label)
        .apply {
          init()
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
 * @param label name of the edge.
 * @param init The init function.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .exclusive(label: String, init: TSCBuilder<E, T, S>.() -> Unit = {}): TSCEdge<E, T, S> =
    this.bounded(label, 1 to 1) { init() }

/**
 * DSL function for an edge with BoundedNode with the limits of (0,#Edges).
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param label name of the edge.
 * @param init The init function.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .optional(label: String, init: TSCBuilder<E, T, S>.() -> Unit = {}): TSCEdge<E, T, S> =
    TSCBuilder<E, T, S>(label)
        .apply {
          init()
          bounds = 0 to edgesCount()
        }
        .buildBounded()
        .also { this.addEdge(it) }

/**
 * DSL function for an edge with BoundedNode with the limits of (#Edges,#Edges).
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param label name of the edge.
 * @param init The init function.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .all(label: String, init: TSCBuilder<E, T, S>.() -> Unit = {}): TSCEdge<E, T, S> =
    TSCBuilder<E, T, S>(label)
        .apply {
          init()
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
 * @param label name of the edge.
 * @param init The init function.
 */
fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> TSCBuilder<
    E, T, S>
    .leaf(label: String, init: TSCBuilder<E, T, S>.() -> Unit = {}): TSCEdge<E, T, S> =
    this.bounded(label, 0 to 0) { init() }
