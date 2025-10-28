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

  /**
   * Counts the number of **distinct** n-way leaf-label combinations that are **feasible** in this
   * TSC, **without enumerating** instances or materializing the combinations.
   *
   * Key idea:
   * - For each node, build a polynomial (truncated at degree n) whose coefficient [n] is the number
   *   of distinct n-sized leaf-subsets achievable in that subtree.
   * - For bounded nodes with upper bound U, we convolve child polynomials with a DP that tracks how
   *   many children contribute ≥1 element, and only sum states with j ≤ U.
   * - Lower bounds do **not** restrict subset feasibility because an instance can always include
   *   extra children that do not contribute to the chosen n-subset.
   *
   * @param n size of combinations (n ≥ 1)
   * @return number of distinct possible n-way combinations
   */
  fun countAllPossibleNWayPredicateCombinations(n: Int): BigInteger {
    require(n >= 1) { "n must be >= 1" }

    // Returns coefficients a[0..n] where a[k] is the number of distinct k-sized subsets
    // achievable in the subtree rooted at `node`.
    fun <
        E : EntityType<E, T, S, U, D>,
        T : TickDataType<E, T, S, U, D>,
        S : SegmentType<E, T, S, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>,
    > countPoly(node: TSCNode<E, T, S, U, D>): Array<BigInteger> {

      val zero = BigInteger.ZERO
      val one = BigInteger.ONE

      return when (node) {
        is tools.aqua.stars.core.tsc.node.TSCLeafNode<*, *, *, *, *> -> {
          // For a leaf: a[0] = 1 (pick nothing), a[1] = 1 (pick this leaf), higher = 0
          Array(n + 1) { i -> if (i == 0) one else if (i == 1) one else zero }
        }

        is tools.aqua.stars.core.tsc.node.TSCBoundedNode<*, *, *, *, *> -> {
          // Child polynomials
          val childPolys: List<Array<java.math.BigInteger>> =
              node.edges.map { edge ->
                @Suppress("UNCHECKED_CAST")
                countPoly(edge.destination as tools.aqua.stars.core.tsc.node.TSCNode<E, T, S, U, D>)
              }

          // Upper bound U (see rationale above for ignoring the lower bound for subset feasibility)
          val upper = node.bounds.second

          // dp[j][k] = number of ways using exactly j children that contribute >= 1 element,
          // picking total of k elements overall from processed children.
          val dp = Array(upper + 1) { Array(n + 1) { zero } }
          dp[0][0] = one

          fun addTo(
              dst: Array<Array<java.math.BigInteger>>,
              j: Int,
              k: Int,
              value: java.math.BigInteger,
          ) {
            dst[j][k] = dst[j][k].add(value)
          }

          var cur = dp
          childPolys.forEach { poly ->
            val next = Array(upper + 1) { Array(n + 1) { zero } }
            for (j in 0..upper) {
              for (k in 0..n) {
                val base = cur[j][k]
                if (base == zero) continue

                // Take 0 from this child: j stays, k stays
                addTo(next, j, k, base)

                // Take t >= 1 from this child
                for (t in 1..(n - k)) {
                  val coeff = poly[t]
                  if (coeff == zero) continue
                  val j2 = if (j + 1 <= upper) j + 1 else continue
                  addTo(next, j2, k + t, base.multiply(coeff))
                }
              }
            }
            cur = next
          }

          // Sum over j = 0..upper to get the coefficient vector a[0..n]
          Array(n + 1) { k ->
            var sum = zero
            for (j in 0..upper) sum = sum.add(cur[j][k])
            sum
          }
        }

        else -> {
          // Should not happen with current sealed hierarchy; be safe:
          Array(n + 1) { java.math.BigInteger.ZERO }
        }
      }
    }

    // Root polynomial; answer is coefficient for degree n
    val coeffs = countPoly(this.rootNode)
    return coeffs[n]
  }

  override fun toString(): String = this.rootNode.toString()

  override fun iterator(): Iterator<TSCNode<E, T, S, U, D>> = TSCIterator(rootNode)
}
