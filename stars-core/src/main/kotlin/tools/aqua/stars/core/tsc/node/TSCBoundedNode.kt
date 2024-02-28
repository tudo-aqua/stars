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

package tools.aqua.stars.core.tsc.node

import tools.aqua.stars.core.crossProduct
import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.powerlist
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * Bounded TSC node.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param valueFunction Value function predicate of the node.
 * @param monitorFunction Monitor function predicate of the node.
 * @param projectionIDMapper Mapper for projection identifiers.
 * @property bounds [Pair] of bounds.
 * @param edges [TSCEdge]s of the TSC.
 */
class TSCBoundedNode<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    valueFunction: (PredicateContext<E, T, S, U, D>) -> Any = {},
    monitorFunction: (PredicateContext<E, T, S, U, D>) -> Boolean = { true },
    projectionIDMapper: Map<Any, Boolean> = mapOf(),
    val bounds: Pair<Int, Int>,
    edges: List<TSCEdge<E, T, S, U, D>>
) : TSCNode<E, T, S, U, D>(valueFunction, monitorFunction, projectionIDMapper, edges) {

  override fun generateAllInstances(): List<TSCInstanceNode<E, T, S, U, D>> {
    val allSuccessorsList = mutableListOf<List<List<TSCInstanceEdge<E, T, S, U, D>>>>()
    edges.forEach { edge ->
      val successorList = mutableListOf<List<TSCInstanceEdge<E, T, S, U, D>>>()
      edge.destination.generateAllInstances().forEach { generatedChild ->
        successorList += listOf(TSCInstanceEdge(edge.label, generatedChild, edge))
      }
      allSuccessorsList += successorList
    }

    val returnList = mutableListOf<TSCInstanceNode<E, T, S, U, D>>()

    // build all subsets of allSuccessorsList and filter to subsets.size in
    // bounds.first..bounds.second
    val boundedSuccessors =
        allSuccessorsList
            .powerlist()
            .filter { subset -> subset.size in bounds.first..bounds.second }
            .toList()

    boundedSuccessors.forEach { subset ->
      when (subset.size) {
        0 -> returnList += TSCInstanceNode(Unit, true, this)
        1 ->
            subset.first().forEach { successors ->
              val generatedNode = TSCInstanceNode(Unit, true, this)
              successors.forEach { successor -> generatedNode.edges += successor }
              returnList += generatedNode
            }
        else ->
            subset.crossProduct().forEach { successors ->
              val generatedNode = TSCInstanceNode(Unit, true, this)
              successors.forEach { successor -> generatedNode.edges += successor }
              returnList += generatedNode
            }
      }
    }

    return returnList
  }

  override fun equals(other: Any?): Boolean =
      other is TSCBoundedNode<*, *, *, *, *> &&
          valueFunction == other.valueFunction &&
          monitorFunction == other.monitorFunction &&
          projectionIDMapper == other.projectionIDMapper &&
          bounds == other.bounds &&
          edges.containsAll(other.edges) &&
          other.edges.containsAll(edges)

  override fun hashCode(): Int =
      valueFunction.hashCode() +
          monitorFunction.hashCode() +
          projectionIDMapper.hashCode() +
          bounds.hashCode() +
          edges.sumOf { it.hashCode() }
}
