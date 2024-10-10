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
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label Label of the [TSCBoundedNode].
 * @param edges [TSCEdge]s of the [TSCBoundedNode].
 * @param monitorsMap Map of monitor labels to their predicates of the [TSCBoundedNode].
 * @param projectionsMap Map of projections of the [TSCBoundedNode].
 * @param valueFunction Value function predicate of the [TSCBoundedNode].
 * @property bounds [Pair] of bounds of the [TSCBoundedNode].
 */
open class TSCBoundedNode<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    label: String,
    edges: List<TSCEdge<E, T, S, U, D>>,
    monitorsMap: Map<String, (PredicateContext<E, T, S, U, D>) -> Boolean>?,
    projectionsMap: Map<String, Boolean>?,
    valueFunction: (PredicateContext<E, T, S, U, D>) -> Any = {},
    val bounds: Pair<Int, Int>
) :
    TSCNode<E, T, S, U, D>(
        label = label,
        edges = edges,
        monitorsMap = monitorsMap,
        projectionsMap = projectionsMap,
        valueFunction = valueFunction) {

  override fun generateAllInstances(): List<TSCInstanceNode<E, T, S, U, D>> {
    val allSuccessorsList = mutableListOf<List<List<TSCInstanceEdge<E, T, S, U, D>>>>()
    edges.forEach { edge ->
      val successorList = mutableListOf<List<TSCInstanceEdge<E, T, S, U, D>>>()
      edge.destination.generateAllInstances().forEach { generatedChild ->
        successorList += listOf(TSCInstanceEdge(generatedChild, edge))
      }
      allSuccessorsList += successorList
    }

    val returnList = mutableListOf<TSCInstanceNode<E, T, S, U, D>>()

    // build all subsets of allSuccessorsList and filter to subsets.size in
    // (bounds.first...bounds.second)
    val boundedSuccessors =
        allSuccessorsList
            .powerlist()
            .filter { subset -> subset.size in bounds.first..bounds.second }
            .toList()

    boundedSuccessors.forEach { subset ->
      when (subset.size) {
        0 -> returnList += TSCInstanceNode(this)
        1 ->
            subset.first().forEach { successors ->
              val generatedNode = TSCInstanceNode(this)
              successors.forEach { successor -> generatedNode.edges += successor }
              returnList += generatedNode
            }
        else ->
            subset.crossProduct().forEach { successors ->
              val generatedNode = TSCInstanceNode(this)
              successors.forEach { successor -> generatedNode.edges += successor }
              returnList += generatedNode
            }
      }
    }

    return returnList
  }
}
