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
import tools.aqua.stars.core.tsc.edge.*
import tools.aqua.stars.core.tsc.node.TSCBoundedNode
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating bounded nodes in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property label Name of the destination [TSCNode].
 * @property bounds (Default: 0 to 0) Bounds of the node.
 */
open class TSCBoundedBuilder<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val label: String,
    var bounds: Pair<Int, Int> = Pair(0, 0),
) : TSCBuilder<E, T, S, U, D>() {

  /**
   * Creates a [TSCBoundedEdge] with a [TSCBoundedNode].
   *
   * @return The created [TSCBoundedEdge].
   */
  fun build(): TSCBoundedEdge<E, T, S, U, D> =
      TSCBoundedEdge(
          condition = condition ?: CONST_TRUE,
          destination =
              TSCBoundedNode(
                  label = label,
                  edges = edges.toList(),
                  monitorsMap = monitors,
                  projectionsMap = projections,
                  bounds = bounds))

  /**
   * DSL function for edge conditions.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param S [SegmentType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param condition The edge condition.
   */
  fun <
      E : EntityType<E, T, S, U, D>,
      T : TickDataType<E, T, S, U, D>,
      S : SegmentType<E, T, S, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.condition(
      condition: (PredicateContext<E, T, S, U, D>) -> Boolean
  ) {
    this.condition = condition
  }

  /**
   * DSL function for a value function.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param S [SegmentType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param valueFunction The value function.
   */
  fun <
      E : EntityType<E, T, S, U, D>,
      T : TickDataType<E, T, S, U, D>,
      S : SegmentType<E, T, S, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.valueFunction(
      valueFunction: (PredicateContext<E, T, S, U, D>) -> Any
  ) {
    this.valueFunction = valueFunction
  }

  /**
   * DSL function for the projections block.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param S [SegmentType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param init The init function.
   * @return The [TSCEdge] that is connected to a projections' node.
   */
  fun <
      E : EntityType<E, T, S, U, D>,
      T : TickDataType<E, T, S, U, D>,
      S : SegmentType<E, T, S, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.projections(
      init: TSCProjectionsBuilder<E, T, S, U, D>.() -> Unit = {}
  ) = TSCProjectionsBuilder<E, T, S, U, D>().apply { init() }.also { this.projections = it.build() }

  /**
   * DSL function for the monitors block.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param S [SegmentType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param init The init function.
   * @return The [TSCEdge] that is connected to a monitors' node.
   */
  fun <
      E : EntityType<E, T, S, U, D>,
      T : TickDataType<E, T, S, U, D>,
      S : SegmentType<E, T, S, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.monitors(
      init: TSCMonitorsBuilder<E, T, S, U, D>.() -> Unit = {}
  ) = TSCMonitorsBuilder<E, T, S, U, D>().apply { init() }.also { this.monitors = it.build() }

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
   * @param label Name of the edge.
   * @param init The init function.
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
   * @param label Name of the edge.
   * @param init The init function.
   * @return The [TSCEdge] with the specific bounds (0,#Edges).
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
   * @param label Name of the edge.
   * @param init The init function.
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
   * @param label Name of the edge.
   * @param init The init function.
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
   * @param label Name of the edge.
   * @param init The init function.
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
      TSCLeafBuilder<E, T, S, U, D>(label).apply { init() }.build().also { this.addEdge(it) }
}
