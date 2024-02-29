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

import tools.aqua.stars.core.tsc.TSCMonitorResult
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
 * @property value Value of this node.
 * @property monitorResult Monitor result of this node.
 * @property tscNode Associated [TSCNode].
 */
class TSCInstanceNode<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val value: Any,
    val monitorResult: Boolean,
    val tscNode: TSCNode<E, T, S, U, D>
) {

  /** Edges of this [TSCInstanceNode]. */
  val edges: MutableList<TSCInstanceEdge<E, T, S, U, D>> = mutableListOf()

  /** Returns all edges. */
  fun getAllEdges(): List<TSCEdge<E, T, S, U, D>> =
      this.edges.map { it.tscEdge } + this.edges.flatMap { it.destination.getAllEdges() }

  /**
   * Validates own (and recursively all children's) successor constraints imposed by the
   * [TSCNode<E,T,S>] types the instances were built from (e.g. exactly one for 'TSCXorNode' or
   * correct range for [TSCBoundedNode])
   *
   * @param label the label used to build the human-readable string; uses [TSCEdge.label] in
   * recursive call
   * @return non-validating nodes; first element of pair is the node that failed to validate; second
   * element is a human-readable explanation for the failure
   */
  fun validate(label: String = "RootNode"): List<Pair<TSCInstanceNode<E, T, S, U, D>, String>> {
    val returnList = mutableListOf<Pair<TSCInstanceNode<E, T, S, U, D>, String>>()
    when (this.tscNode) {
      is TSCBoundedNode ->
          if (edges.size !in tscNode.bounds.first..tscNode.bounds.second)
              returnList +=
                  this to
                      "[$label] TSCBoundedNode successor count must be within [${tscNode.bounds.first},${tscNode.bounds.second}], but ${edges.size} successor(s) found."
    }
    returnList += edges.flatMap { it.destination.validate(it.label) }
    return returnList
  }

  /**
   * Validates own (and recursively all children's) results of the [TSCNode<E,T,S>.monitorFunction]
   * results and collects incoming edge labels for results != true.
   *
   * @param segmentIdentifier Identifier of the segment.
   * @param label the label added to the return list if [monitorResult] == `false`; uses
   * [TSCEdge.label] in recursive call
   * @return list of edge labels leading to a node with `false` monitor result.
   */
  fun validateMonitors(segmentIdentifier: String, label: String = "RootNode"): TSCMonitorResult {
    val monitorResult =
        TSCMonitorResult(segmentIdentifier = segmentIdentifier, monitorsValid = true)
    val edgeLabelListLeadingToFalseMonitor = validateMonitorsRec(label)
    if (edgeLabelListLeadingToFalseMonitor.any()) {
      monitorResult.monitorsValid = false
      monitorResult.edgeList = edgeLabelListLeadingToFalseMonitor
    }
    return monitorResult
  }

  /**
   * Validates own (and recursively all children's) results of the [TSCNode<E,T,S>.monitorFunction]
   * results and collects incoming edge labels for results != true.
   *
   * @param label the label added to the return list if [monitorResult] == `false`; uses
   * [TSCEdge.label] in recursive call
   * @return list of edge labels leading to a node with `false` monitor result.
   */
  private fun validateMonitorsRec(label: String): List<String> {
    val returnList = mutableListOf<String>()
    if (!monitorResult) returnList += label
    returnList += edges.flatMap { it.destination.validateMonitorsRec(it.label) }
    return returnList
  }

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
