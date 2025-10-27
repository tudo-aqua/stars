/*
 * Copyright 2023-2025 The STARS Project Authors
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

package tools.aqua.stars.core.tsc

import java.math.BigInteger
import tools.aqua.stars.core.evaluation.NWayPredicateCombination
import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.tsc.utils.combinations
import tools.aqua.stars.core.types.*

/**
 * TSC graph.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property rootNode The root node of the [TSC].
 * @property identifier The identifier of the [TSC].
 */
class TSC<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(val rootNode: TSCNode<E, T, S, U, D>, val identifier: String = "TSC") :
    Iterable<TSCNode<E, T, S, U, D>> {

  init {
    charArrayOf('"', '*', '<', '>', '?', '|', '\u0000').forEach {
      check(!identifier.contains(it)) { "Identifier must not contain illegal character $it." }
    }
  }

  /** Buffer for the number of possible [TSCInstance]s. */
  private var instanceCountBuffer: BigInteger? = null

  /**
   * Returns the number of possible [TSCInstance]s. The calculation is performed on-demand upon
   * first access and is the buffered.
   */
  val instanceCount: BigInteger
    get() = instanceCountBuffer ?: rootNode.countAllInstances().also { instanceCountBuffer = it }

  /** Buffer for the possible [TSCInstanceNode]s. */
  private var possibleTSCInstancesBuffer: List<TSCInstanceNode<E, T, S, U, D>>? = null

  /**
   * Returns the [List] of all possible [TSCInstanceNode]s. The generation is performed on-demand
   * upon first access and is then buffered.
   */
  val possibleTSCInstances: List<TSCInstanceNode<E, T, S, U, D>>
    get() =
        possibleTSCInstancesBuffer
            ?: rootNode.generateAllInstances().also { possibleTSCInstancesBuffer = it }

  /**
   * Evaluates [PredicateContext] on [TSC].
   *
   * @param context The [PredicateContext].
   * @return The calculated [TSCInstance] based on the evaluation.
   */
  fun evaluate(context: PredicateContext<E, T, S, U, D>): TSCInstance<E, T, S, U, D> =
      TSCInstance(rootNode.evaluate(context), context.segment.getSegmentIdentifier())

  /**
   * Builds all possible TSCs ignoring those in [projectionIgnoreList].
   *
   * @param projectionIgnoreList Projections to ignore.
   * @return The [List] of all [TSC]s for this [TSC].
   */
  fun buildProjections(projectionIgnoreList: List<Any> = emptyList()): List<TSC<E, T, S, U, D>> =
      rootNode.buildProjections(projectionIgnoreList)

  /**
   * Returns all possible leaf-label n-combinations for the given TSC.
   *
   * @param n The number of `n` for the n-way combinations.
   */
  fun getAllPossibleNWayPredicateCombinations(n: Int): Set<NWayPredicateCombination> {
    val all = mutableSetOf<NWayPredicateCombination>()
    possibleTSCInstances.forEach { referenceInstance ->
      val labels = referenceInstance.extractLeafLabels()
      combinations(labels, n).forEach { combo -> all += NWayPredicateCombination(combo.sorted()) }
    }
    return all
  }

  override fun toString(): String = this.rootNode.toString()

  override fun iterator(): Iterator<TSCNode<E, T, S, U, D>> = TSCIterator(rootNode)
}
