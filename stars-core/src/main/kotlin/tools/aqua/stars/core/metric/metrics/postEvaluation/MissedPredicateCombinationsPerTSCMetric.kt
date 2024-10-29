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

package tools.aqua.stars.core.metric.metrics.postEvaluation

import java.util.logging.Logger
import tools.aqua.stars.core.evaluation.PredicateCombination
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.PostEvaluationMetricProvider
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.serialization.SerializablePredicateCombinationResult
import tools.aqua.stars.core.metric.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_INDENT
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_SEPARATOR
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.CONST_TRUE
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * This class implements the [PostEvaluationMetricProvider] and calculates all missing
 * [PredicateCombination]s for all [TSC]s.
 *
 * This class implements the [Loggable] interface. It logs and prints the count of missing
 * [PredicateCombination]s for each [TSC]. It logs the missing [PredicateCombination]s for each
 * [TSC].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property dependsOn The instance of a [ValidTSCInstancesPerTSCMetric] on which this metric
 *   depends on and needs for its calculation.
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
@Suppress("unused")
class MissedPredicateCombinationsPerTSCMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val dependsOn: ValidTSCInstancesPerTSCMetric<E, T, S, U, D>,
    override val loggerIdentifier: String = "missed-predicate-combinations",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) : PostEvaluationMetricProvider<E, T, S, U, D>, Serializable, Loggable {

  /** Holds the evaluation result after calling [postEvaluate]. */
  private var evaluationResultCache: Map<TSC<E, T, S, U, D>, Set<PredicateCombination>>? = null

  /**
   * Returns a [Map] of all missing [PredicateCombination]s for all [TSC]s that are calculated by
   * the [ValidTSCInstancesPerTSCMetric].
   *
   * @return The [Map] of all missing [PredicateCombination]s to its associated [TSC].
   */
  override fun postEvaluate(): Map<TSC<E, T, S, U, D>, Set<PredicateCombination>> =
      evaluationResultCache
          ?: dependsOn
              .getState()
              .mapValues {
                getAllMissingPredicateCombinationsForTSC(it.key, it.value.map { t -> t.key })
              }
              .also { evaluationResultCache = it }

  /**
   * Prints the count of missed [PredicateCombination]s for each [TSC] and then the actual list of
   * the missed predicates.
   */
  override fun printPostEvaluationResult() {
    val evaluationResult = postEvaluate()
    println(
        "\n$CONSOLE_SEPARATOR\n$CONSOLE_INDENT Missing Predicate Combinations Per TSC \n$CONSOLE_SEPARATOR")
    evaluationResult.forEach { (tsc, missedPredicates) ->
      logInfo(
          "Count of missing predicate combinations for tsc '${tsc.identifier}': ${missedPredicates.size}.")
      missedPredicates
          .sortedWith(compareBy<PredicateCombination> { it.predicate1 }.thenBy { it.predicate2 })
          .forEach { logFine(it) }
      logFine()
    }
  }

  /**
   * Calculate the [Set] of [PredicateCombination]s that are missing.
   *
   * @param tsc The [TSC] for which the missing [PredicateCombination]s should be calculated.
   * @param tscInstances The occurred [List] of [TSCInstanceNode]s.
   * @return A [Set] of [PredicateCombination]s that can occur based on the given [tsc] but are not
   *   present in the given [tscInstances].
   */
  private fun getAllMissingPredicateCombinationsForTSC(
      tsc: TSC<E, T, S, U, D>,
      tscInstances: List<TSCInstanceNode<E, T, S, U, D>>
  ): Set<PredicateCombination> {
    // Get all possible predicate combinations
    val possiblePredicateCombinations = getAllPredicateCombinations(tsc.possibleTSCInstances)
    // Get all occurred predicate combinations
    val occurredPredicateCombinations = getAllPredicateCombinations(tscInstances)
    // Return predicate combinations that have not occurred
    return possiblePredicateCombinations.minus(occurredPredicateCombinations)
  }

  /**
   * Get all [PredicateCombination]s for the given [List] of [TSCInstanceNode]s.
   *
   * @param tscInstances A [List] of [TSCInstanceNode] for which all possible [PredicateCombination]
   *   s should be calculated.
   * @return the [Set] of [PredicateCombination]s based on the [tscInstances].
   */
  private fun getAllPredicateCombinations(
      tscInstances: List<TSCInstanceNode<E, T, S, U, D>>
  ): Set<PredicateCombination> {
    // Create set for storage of all combinations
    val predicateCombinations = mutableSetOf<PredicateCombination>()
    tscInstances.forEach { t ->
      // Get all traversals that are possible for the current TSCInstance, excluding TSCAlwaysEdges,
      // as they do not represent a predicate
      val predicateTraversals =
          t.traverse()
              .filter { it.getLeafNodeEdges(it).any { it.tscEdge.condition != CONST_TRUE } }
              .map { it.toString() }
      // Combine all TSCEdges with each other
      predicateTraversals.forEach { predicatePath1 ->
        predicateTraversals
            .filter { it != predicatePath1 }
            .forEach { predicatePath2 ->
              predicateCombinations += PredicateCombination(predicatePath1, predicatePath2)
            }
      }
    }
    return predicateCombinations
  }

  override fun getSerializableResults(): List<SerializablePredicateCombinationResult> =
      evaluationResultCache?.map { (tsc, predicates) ->
        val resultList = predicates.map { it.predicate1 to it.predicate2 }
        SerializablePredicateCombinationResult(
            identifier = tsc.identifier,
            source = loggerIdentifier,
            tsc = SerializableTSCNode(tsc.rootNode),
            count = resultList.size,
            value = resultList)
      } ?: emptyList()
}
