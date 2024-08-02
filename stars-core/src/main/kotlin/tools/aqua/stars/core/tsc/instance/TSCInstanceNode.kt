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

package tools.aqua.stars.core.tsc.instance

import tools.aqua.stars.core.tsc.TSCFailedMonitorInstance
import tools.aqua.stars.core.tsc.builder.ROOT_NODE_LABEL
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.node.TSCBoundedNode
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * Evaluated TSC node.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property tscNode Associated [TSCNode].
 * @property monitorResults Monitor results of this node.
 * @property value Value of this node.
 */
class TSCInstanceNode<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val tscNode: TSCNode<E, T, S, U, D>,
    val monitorResults: Map<String, Boolean> = emptyMap(),
    val value: Any = Unit
) {

  /** Edges of this [TSCInstanceNode]. */
  val edges: MutableList<TSCInstanceEdge<E, T, S, U, D>> = mutableListOf()

  /** Returns all edges. */
  fun getAllEdges(): List<TSCEdge<E, T, S, U, D>> =
      edges.map { it.tscEdge } + edges.flatMap { it.destination.getAllEdges() }

  /**
   * Validates own (and recursively all children's) successor constraints imposed by the
   * [TSCNode<E,T,S>] types the instances were built from (e.g. exactly one for 'TSCXorNode' or
   * correct range for [TSCBoundedNode]).
   *
   * @param label the label used to build the human-readable string; uses [TSCEdge.label] in
   *   recursive call.
   * @return non-validating nodes; first element of pair is the node that failed to validate; second
   *   element is a human-readable explanation for the failure.
   */
  fun validate(
      label: String = ROOT_NODE_LABEL
  ): List<Pair<TSCInstanceNode<E, T, S, U, D>, String>> {
    val returnList = mutableListOf<Pair<TSCInstanceNode<E, T, S, U, D>, String>>()
    when (tscNode) {
      is TSCBoundedNode ->
          if (edges.size !in tscNode.bounds.first..tscNode.bounds.second)
              returnList +=
                  this to
                      "[$label] TSCBoundedNode successor count must be within " +
                          "[${tscNode.bounds.first},${tscNode.bounds.second}], but ${edges.size} successor(s) found."
    }
    returnList += edges.flatMap { it.destination.validate(it.label) }
    return returnList
  }

  /**
   * Validates own (and recursively all children's) results of the
   * [TSCNode<E,T,S,U,D>.monitorFunction] results and returns a [TSCFailedMonitorInstance] for each
   * incoming edge label with results != true.
   *
   * @param segmentIdentifier Identifier of the segment.
   * @param label specifies the starting point in the TSC for the search.
   * @return list of edge labels leading to a node with `false` monitor result.
   */
  fun validateMonitors(
      segmentIdentifier: String,
      label: String = ROOT_NODE_LABEL
  ): List<TSCFailedMonitorInstance<E, T, S, U, D>> =
      validateMonitorsRec(label).map {
        TSCFailedMonitorInstance(
            segmentIdentifier = segmentIdentifier,
            tscInstance = this,
            monitorLabel = it.first,
            nodeLabel = it.second)
      }

  /**
   * Validates own (and recursively all children's) results of the
   * [TSCNode<E,T,S,U,D>.monitorFunction] results and collects incoming edge labels for results !=
   * true.
   *
   * @param label the label added to the return list if [monitorResult] == `false`; uses
   *   [TSCEdge.label] in recursive call.
   * @return List of [Pair]s of the failed monitor to the node label.
   */
  private fun validateMonitorsRec(label: String): List<Pair<String, String>> =
      this.monitorResults.filterValues { !it }.keys.map { it to label } +
          edges.flatMap { it.destination.validateMonitorsRec(it.label) }

  /**
   * Prints [TSCInstanceNode] up to given [depth].
   *
   * @param depth Depth to print up to.
   */
  fun toString(depth: Int): String =
      StringBuilder()
          .apply {
            append(if (value is Unit) "\n" else "($value)\n")

            edges.forEach { instanceEdge ->
              append("  ".repeat(depth))
              append("-> ${instanceEdge.label} ")
              append(instanceEdge.destination.toString(depth + 1))
            }
          }
          .toString()

  override fun toString(): String = toString(0)

  override fun equals(other: Any?): Boolean =
      other is TSCInstanceNode<*, *, *, *, *> &&
          edges.size == other.edges.size &&
          edges.withIndex().all { iv -> iv.value == other.edges[iv.index] }

  override fun hashCode(): Int = edges.sumOf { it.hashCode() }
}
