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
import tools.aqua.stars.core.tsc.edge.TSCBoundedEdge
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.edge.TSCLeafEdge
import tools.aqua.stars.core.tsc.edge.TSCMonitorsEdge
import tools.aqua.stars.core.tsc.node.TSCBoundedNode
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating bounded nodes in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label Name of the edge.
 * @param bounds (Default: 0 to 0) Bounds of the node.
 */
open class TSCBoundedBuilder<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    label: String,
    bounds: Pair<Int, Int> = Pair(0, 0),
) : TSCBuilder<E, T, S, U, D, TSCBoundedEdge<E, T, S, U, D>>(label, bounds) {

  /**
   * Creates a [TSCEdge] with a [TSCBoundedNode]. Only functions where [bounds] is relevant.
   *
   * @return The created [TSCEdge].
   */
  override fun build(): TSCBoundedEdge<E, T, S, U, D> =
      TSCBoundedEdge(
          label,
          condition ?: CONST_TRUE,
          TSCBoundedNode(valueFunction, projectionIDs, bounds, edges.toList(), monitors))

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
      TSCLeafBuilder<E, T, S, U, D>(label).apply { init() }.build().also { this.addEdge(it) }

  /**
   * DSL function for an edge with MonitorsEdge in the bounded node scope.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param S [SegmentType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param init The init function.
   *
   * @return The [TSCEdge] that is connected to a monitors node.
   */
  fun <
      E : EntityType<E, T, S, U, D>,
      T : TickDataType<E, T, S, U, D>,
      S : SegmentType<E, T, S, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>> TSCBoundedBuilder<E, T, S, U, D>.monitors(
      init: TSCMonitorsBuilder<E, T, S, U, D>.() -> Unit = {}
  ): TSCMonitorsEdge<E, T, S, U, D> =
      TSCMonitorsBuilder<E, T, S, U, D>().apply { init() }.build().also { this.monitors = it }
}
