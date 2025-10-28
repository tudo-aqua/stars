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
import tools.aqua.stars.core.tsc.builder.CONST_TRUE
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.node.TSCBoundedNode
import tools.aqua.stars.core.tsc.node.TSCLeafNode
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
   * Returns all possible n-way predicate-label combinations for the given TSC.
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

  /**
   * Counts the number of **distinct** n-way feature combinations that are possible in this [TSC],
   * **without listing** instances or materializing the combinations.
   *
   * @param n the size of the feature combinations (n ≥ 1)
   * @return number of distinct possible n-way combinations as [BigInteger]
   */
  fun countAllPossibleNWayPredicateCombinations(n: Int): BigInteger {
    require(n >= 1) { "n must be >= 1" }

    val zero = BigInteger.ZERO
    val one = BigInteger.ONE

    // Multiply a polynomial by (1 + x), truncated to degree n.
    fun mulByOnePlusX(poly: Array<BigInteger>): Array<BigInteger> {
      val res = Array(n + 1) { zero }
      // res[k] = poly[k] (choose node not selected) + poly[k-1] (node selected)
      for (k in 0..n) {
        var v = poly[k]
        if (k >= 1) v = v.add(poly[k - 1])
        res[k] = v
      }
      return res
    }

    // Returns coefficients a[0..n] where a[k] is count of distinct k-sized feature-subsets
    // achievable in the subtree rooted at `node`. `isFeatureEdge` indicates whether the
    // (incoming) edge to this node is a feature edge (edge.condition !== CONST_TRUE).
    fun <
        E : EntityType<E, T, S, U, D>,
        T : TickDataType<E, T, S, U, D>,
        S : SegmentType<E, T, S, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>,
    > countPoly(
        node: TSCNode<E, T, S, U, D>,
        isFeatureEdge: Boolean,
    ): Array<BigInteger> =
        when (node) {
          is TSCLeafNode<*, *, *, *, *> -> {
            // Leaf contributes 1 way to pick nothing. If its incoming edge is a feature edge,
            // it also contributes 1 way to pick the leaf itself.
            val base = Array(n + 1) { zero }
            base[0] = one
            val arr = base
            if (isFeatureEdge) mulByOnePlusX(arr) else arr
          }

          is TSCBoundedNode<*, *, *, *, *> -> {
            // Compute child polynomials with proper feature-edge flags from their incoming edges.
            val childPolys: List<Array<BigInteger>> =
                node.edges.map { edge ->
                  countPoly(
                      edge.destination,
                      // Feature edge criterion must follow the new behavior: !== CONST_TRUE
                      edge.condition !== CONST_TRUE,
                  )
                }

            val upper = node.bounds.second

            // dp[j][k] = number of ways using exactly j children that contribute >= 1 selected
            // feature,
            // accumulating k selected features in total from processed children (k ≤ n).
            var cur = Array(upper + 1) { Array(n + 1) { zero } }
            cur[0][0] = one

            fun addTo(
                dst: Array<Array<BigInteger>>,
                j: Int,
                k: Int,
                value: BigInteger,
            ) {
              dst[j][k] = dst[j][k].add(value)
            }

            childPolys.forEach { poly ->
              val next = Array(upper + 1) { Array(n + 1) { zero } }

              for (j in 0..upper) {
                for (k in 0..n) {
                  val base = cur[j][k]
                  if (base == zero) continue

                  // Case 1: Take 0 features from this child (child not contributing)
                  addTo(next, j, k, base)

                  // Case 2: Take t >= 1 features from this child (child contributing)
                  var t = 1
                  while (k + t <= n) {
                    val coeff = poly[t]
                    if (coeff != zero && j + 1 <= upper) {
                      addTo(next, j + 1, k + t, base.multiply(coeff))
                    }
                    t++
                  }
                }
              }

              cur = next
            }

            // Sum over j = 0..upper for coefficients of the combined children
            val childrenPoly =
                Array(n + 1) { kk ->
                  var sum = zero
                  for (j in 0..upper) sum = sum.add(cur[j][kk])
                  sum
                }

            // If THIS node is reached via a feature edge, multiply by (1 + x) to optionally select
            // it
            if (isFeatureEdge) mulByOnePlusX(childrenPoly) else childrenPoly
          }
        }

    // Root has no incoming edge -> not a feature edge.
    val coeffs = countPoly(this.rootNode, isFeatureEdge = false)
    return coeffs[n]
  }

  override fun toString(): String = this.rootNode.toString()

  override fun iterator(): Iterator<TSCNode<E, T, S, U, D>> = TSCIterator(rootNode)
}
