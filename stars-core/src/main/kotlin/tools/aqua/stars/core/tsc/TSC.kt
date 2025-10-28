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

    // =========================
    // n == 1: exact + bounded
    // =========================
    if (n == 1) {
      // Returns the set of DISTINCT labels that are feasible as a 1-way feature selection
      // in the subtree, respecting the node's UPPER bound on contributing children.
      fun <
          E : EntityType<E, T, S, U, D>,
          T : TickDataType<E, T, S, U, D>,
          S : SegmentType<E, T, S, U, D>,
          U : TickUnit<U, D>,
          D : TickDifference<D>,
      > feasibleLabels1(
          node: TSCNode<E, T, S, U, D>,
          isFeatureEdge: Boolean,
      ): Set<String> =
          when (node) {
            is TSCLeafNode<*, *, *, *, *> -> {
              // A leaf is feasible iff its incoming edge is a feature edge
              if (isFeatureEdge) setOf(node.label) else emptySet()
            }

            is TSCBoundedNode<*, *, *, *, *> -> {
              // Gather feasible labels from each child (as sets)
              val childSets: List<Set<String>> =
                  node.edges.map { edge ->
                    feasibleLabels1(
                        edge.destination,
                        edge.condition !== CONST_TRUE,
                    )
                  }

              val upper = node.bounds.second

              // DP over number of contributing children j (children from which we take >=1 label).
              // Since n==1, taking from a child means selecting exactly ONE label from that child's
              // set.
              // dp[j] = DISTINCT labels achievable using exactly j contributing children so far.
              var dp = Array(upper + 1) { emptySet<String>() }
              dp[0] = emptySet() // with 0 contributing children we currently pick no label

              childSets.forEach { childLabels ->
                val next = Array(upper + 1) { mutableSetOf<String>() }

                for (j in 0..upper) {
                  // Case A: do NOT take a label from this child → j stays
                  next[j].addAll(dp[j])

                  // Case B: take ONE label from this child → j + 1 (if within bound)
                  if (j + 1 <= upper && childLabels.isNotEmpty()) {
                    // We can pick ANY one label from this child; union yields all distinct
                    // possibilities.
                    next[j + 1].addAll(childLabels)
                    // Note: dp[j] may already contain labels from earlier children,
                    // but for n==1 we only keep sets of size 1, so union by label suffices.
                  }
                }

                dp = next.map { it.toSet() }.toTypedArray()
              }

              // Combine all j ≤ upper; optionally add THIS node's label if its incoming edge is a
              // feature.
              val fromChildren = (0..upper).flatMapTo(mutableSetOf()) { j -> dp[j] }
              if (isFeatureEdge) fromChildren.add(node.label)
              fromChildren
            }
          }

      val labels = feasibleLabels1(this.rootNode, isFeatureEdge = false)
      return BigInteger.valueOf(labels.size.toLong())
    }

    // ==========================================================
    // n >= 2: fast count-only DP (counts "ways", NOT deduped)
    // ==========================================================
    val zero = BigInteger.ZERO
    val one = BigInteger.ONE

    fun mulByOnePlusX(poly: Array<BigInteger>): Array<BigInteger> {
      val res = Array(n + 1) { zero }
      for (k in 0..n) {
        var v = poly[k]
        if (k >= 1) v = v.add(poly[k - 1])
        res[k] = v
      }
      return res
    }

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
            val base = Array(n + 1) { zero }
            base[0] = one
            if (isFeatureEdge) mulByOnePlusX(base) else base
          }

          is TSCBoundedNode<*, *, *, *, *> -> {
            val childPolys: List<Array<BigInteger>> =
                node.edges.map { edge ->
                  countPoly(
                      edge.destination,
                      edge.condition !== CONST_TRUE,
                  )
                }

            val upper = node.bounds.second

            // dp[j][k] = number of ways using exactly j contributing children (>=1 from that
            // child),
            // totaling k selected features.
            var dp = Array(upper + 1) { Array(n + 1) { zero } }
            dp[0][0] = one

            fun addTo(dst: Array<Array<BigInteger>>, j: Int, k: Int, value: BigInteger) {
              dst[j][k] = dst[j][k].add(value)
            }

            childPolys.forEach { poly ->
              val next = Array(upper + 1) { Array(n + 1) { zero } }
              for (j in 0..upper) {
                for (k in 0..n) {
                  val baseWays = dp[j][k]
                  if (baseWays == zero) continue

                  // Child not contributing
                  addTo(next, j, k, baseWays)

                  // Child contributing: take t >= 1 from this child
                  if (j + 1 <= upper) {
                    var t = 1
                    while (k + t <= n) {
                      val coeff = poly[t]
                      if (coeff != zero) addTo(next, j + 1, k + t, baseWays.multiply(coeff))
                      t++
                    }
                  }
                }
              }
              dp = next
            }

            val childrenPoly = Array(n + 1) { zero }
            for (j in 0..upper) {
              for (k in 0..n) {
                val v = dp[j][k]
                if (v != zero) childrenPoly[k] = childrenPoly[k].add(v)
              }
            }

            if (isFeatureEdge) mulByOnePlusX(childrenPoly) else childrenPoly
          }
        }

    val coeffs = countPoly(this.rootNode, isFeatureEdge = false)
    return coeffs[n]
  }

  /** Extract all leaf node *labels* from a [TSC]. */
  fun extractFeatureLabels() = this.rootNode.extractFeatureLabels()

  override fun toString(): String = this.rootNode.toString()

  override fun iterator(): Iterator<TSCNode<E, T, S, U, D>> = TSCIterator(rootNode)
}
