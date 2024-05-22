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
 * @property valueFunction (Default: empty) Value function predicate of the node.
 * @property projectionIDs (Default: empty map) Projection identifier of the node.
 * @property bounds Bounds of the node, only relevant for bounded nodes.
 * @property condition (Default: null) Condition predicate of the edge.
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
    var valueFunction: (PredicateContext<E, T, S, U, D>) -> Any,
    var projectionIDs: Map<Any, Boolean>,
    var bounds: Pair<Int, Int>,
    var condition: ((PredicateContext<E, T, S, U, D>) -> Boolean)?
) {

  /** Holds all edges of the node. */
  protected val edges: MutableList<B> = mutableListOf()

  /** Builds the [TSCEdge]. */
  abstract fun build(): B

  /**
   * Adds the given [edge] to [edges]. This will become the edges of the node that will be created
   * off of this object.
   *
   * @param edge [TSCEdge] to be added.
   */
  fun addEdge(edge: B) {
    check(edges.none { it.label == edge.label }) { "Edge with label ${edge.label} already exists in this scope." }
    edges.add(edge)
  }

  /**
   * Returns the amount of elements in [edges].
   *
   * @return The amount of [TSCEdge]s.
   */
  fun edgesCount(): Int = edges.size
}
