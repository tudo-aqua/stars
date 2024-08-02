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
import tools.aqua.stars.core.tsc.edge.TSCLeafEdge
import tools.aqua.stars.core.tsc.node.TSCLeafNode
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating leaf nodes in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property label Label of the [TSCLeafNode].
 */
open class TSCLeafBuilder<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(val label: String) : TSCBuilder<E, T, S, U, D>() {

  /**
   * Creates a [TSCLeafEdge] with a [TSCLeafNode].
   *
   * @return The created [TSCLeafEdge].
   */
  fun build(): TSCLeafEdge<E, T, S, U, D> =
      TSCLeafEdge(
          condition = condition ?: CONST_TRUE,
          destination =
              TSCLeafNode(
                  label = label,
                  monitorsMap = monitors,
                  projectionsMap = projections,
                  valueFunction = valueFunction))

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
      D : TickDifference<D>> TSCLeafBuilder<E, T, S, U, D>.condition(
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
      D : TickDifference<D>> TSCLeafBuilder<E, T, S, U, D>.valueFunction(
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
   * @return The [TSCEdge] that is connected to a projections node.
   */
  fun <
      E : EntityType<E, T, S, U, D>,
      T : TickDataType<E, T, S, U, D>,
      S : SegmentType<E, T, S, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>> TSCLeafBuilder<E, T, S, U, D>.projections(
      init: TSCProjectionsBuilder<E, T, S, U, D>.() -> Unit = {}
  ) = TSCProjectionsBuilder<E, T, S, U, D>().apply { init() }.also { this.projections = it.build() }

  /**
   * DSL function for an edge with MonitorsEdge in the leaf node scope.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param S [SegmentType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param init The init function.
   * @return The [TSCEdge] that is connected to a monitors node.
   */
  fun <
      E : EntityType<E, T, S, U, D>,
      T : TickDataType<E, T, S, U, D>,
      S : SegmentType<E, T, S, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>> TSCLeafBuilder<E, T, S, U, D>.monitors(
      init: TSCMonitorsBuilder<E, T, S, U, D>.() -> Unit = {}
  ) = TSCMonitorsBuilder<E, T, S, U, D>().apply { init() }.also { this.monitors = it.build() }
}
