/*
 * Copyright 2023-2026 The STARS Project Authors
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

import java.math.BigInteger
import tools.aqua.stars.core.binomial
import tools.aqua.stars.core.evaluation.Predicate
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*
import tools.aqua.stars.core.utils.crossProduct
import tools.aqua.stars.core.utils.powerlist

/**
 * Bounded [TSC] node.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label Label of the [TSCBoundedNode].
 * @param edges [TSCEdge]s of the [TSCBoundedNode].
 * @param monitorsMap Map of monitor labels to their predicates of the [TSCBoundedNode].
 * @param valueFunction Value function predicate of the [TSCBoundedNode].
 * @property bounds [Pair] of bounds of the [TSCBoundedNode].
 */
open class TSCBoundedNode<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    label: String,
    edges: List<TSCEdge<E, T, U, D>>,
    monitorsMap: Map<String, Predicate<T>>?,
    valueFunction: (T) -> Any = {},
    val bounds: Pair<Int, Int>,
) :
    TSCNode<E, T, U, D>(
        label = label,
        edges = edges,
        monitorsMap = monitorsMap,
        valueFunction = valueFunction,
    ) {

  init {
    check(bounds.first >= 0) {
      "Lower bound (${bounds.first}) of TSC node $label must be non-negative."
    }
    check(bounds.second >= bounds.first) {
      "Upper bound (${bounds.first}) of TSC node $label must be >= lower bound (${bounds.second})."
    }
    check(bounds.second <= edges.size) {
      "Upper bound (${bounds.second}) of TSC node $label must be <= number of edges (${edges.size})."
    }
  }

  override fun countAllInstances(): BigInteger {
    val edgeCount = edges.map { it.destination.countAllInstances() }

    // Shortcut calculation if all edges lead to leaves
    if (edgeCount.all { it == BigInteger.ONE }) {
      // Shortcut if bounds are unbounded
      return if (bounds == 0 to edges.size) BigInteger.valueOf(2).pow(edges.size)
      else (bounds.first..bounds.second).sumOf { BigInteger.valueOf(binomial(edges.size, it)) }
    }

    // At least one edge leads to non-leaf, do full calculation
    val boundedSuccessors =
        edgeCount
            .powerlist()
            .filter { subset -> subset.size in bounds.first..bounds.second }
            .toList()

    return boundedSuccessors.sumOf { subset ->
      when (subset.size) {
        0 -> BigInteger.ONE
        1 -> subset.first()
        else -> subset.reduce { acc, possibleCombinations -> acc * possibleCombinations }
      }
    }
  }

  override fun generateAllInstances(): List<TSCInstance<E, T, U, D>> {
    val allSuccessorsList = mutableListOf<List<List<TSCInstanceEdge<E, T, U, D>>>>()
    edges.forEach { edge ->
      val successorList = mutableListOf<List<TSCInstanceEdge<E, T, U, D>>>()
      edge.destination.generateAllInstances().forEach { generatedChild ->
        successorList +=
            listOf(TSCInstanceEdge(destination = generatedChild.rootNode, tscEdge = edge, isUnknown = false))
      }
      allSuccessorsList += successorList
    }

    val returnList = mutableListOf<TSCInstance<E, T, U, D>>()

    // build all subsets of allSuccessorsList and filter to subsets.size in
    // (bounds.first...bounds.second)
    val boundedSuccessors =
        allSuccessorsList
            .powerlist()
            .filter { subset -> subset.size in bounds.first..bounds.second }
            .toList()

    boundedSuccessors.forEach { subset ->
      when (subset.size) {
        0 -> returnList += TSCInstance(TSCInstanceNode(this))
        1 ->
            subset.first().forEach { successors ->
              val generatedNode = TSCInstanceNode(this)
              successors.forEach { successor -> generatedNode.edges += successor }
              returnList += TSCInstance(generatedNode)
            }
        else ->
            subset.crossProduct().forEach { successors ->
              val generatedNode = TSCInstanceNode(this)
              successors.forEach { successor -> generatedNode.edges += successor }
              returnList += TSCInstance(generatedNode)
            }
      }
    }

    return returnList
  }
}
