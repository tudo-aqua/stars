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

package tools.aqua.stars.core.metrics.postEvaluation

import java.util.logging.Logger
import tools.aqua.stars.core.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metrics.providers.Loggable
import tools.aqua.stars.core.metrics.providers.PostEvaluationMetricProvider
import tools.aqua.stars.core.metrics.providers.SerializableMetric
import tools.aqua.stars.core.serialization.SerializablePredicateResult
import tools.aqua.stars.core.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.CONST_TRUE
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*
import tools.aqua.stars.core.utils.ApplicationConstantsHolder.CONSOLE_INDENT
import tools.aqua.stars.core.utils.ApplicationConstantsHolder.CONSOLE_SEPARATOR

/**
 * This class implements the [PostEvaluationMetricProvider] and calculates all missing predicates
 * for all [TSC]s.
 *
 * This class implements the [Loggable] interface. It logs and prints the count of missing
 * predicates for each [TSC]. It logs the missing predicates for each [TSC].
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
class MissedPredicatesPerTSCMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    override val dependsOn: ValidTSCInstancesPerTSCMetric<E, T, S, U, D>,
    override val loggerIdentifier: String = "missed-predicates",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier),
) : PostEvaluationMetricProvider<E, T, S, U, D>, SerializableMetric, Loggable {

  /** Holds the evaluation result after calling [postEvaluate]. */
  private var evaluationResultCache: Map<TSC<E, T, S, U, D>, Set<String>>? = null

  /**
   * Returns a [Map] of all missing predicates for all [TSC]s that are calculated by the
   * [ValidTSCInstancesPerTSCMetric].
   *
   * @return The [Map] of all missing predicates to its associated [TSC].
   */
  override fun postEvaluate(): Map<TSC<E, T, S, U, D>, Set<String>> =
      evaluationResultCache
          ?: dependsOn
              .getState()
              .mapValues { getAllMissingPredicatesForTSC(it.key, it.value.map { t -> t.key }) }
              .also { evaluationResultCache = it }

  /**
   * Prints the count of missed predicates for each [TSC] and then the actual list of the missed
   * predicates.
   */
  override fun printPostEvaluationResult() {
    val evaluationResult = postEvaluate()
    println("\n$CONSOLE_SEPARATOR\n$CONSOLE_INDENT Missing Predicates Per TSC \n$CONSOLE_SEPARATOR")
    evaluationResult.forEach { (tsc, missedPredicates) ->
      logInfo("Count of missing predicates for tsc '${tsc.identifier}': ${missedPredicates.size}.")
      missedPredicates.sorted().forEach { logFine(it) }
      logFine()
    }
  }

  /**
   * Calculate the [Set] of predicates that are missing.
   *
   * @param tsc The [TSC] for which the missing predicates should be calculated.
   * @param tscInstances The occurred [List] of [TSCInstanceNode]s.
   * @return A [Set] of predicates that can occur based on the given [tsc] but are not present in
   *   the given [tscInstances].
   */
  private fun getAllMissingPredicatesForTSC(
      tsc: TSC<E, T, S, U, D>,
      tscInstances: List<TSCInstanceNode<E, T, S, U, D>>,
  ): Set<String> {
    // Get all possible predicates
    val possiblePredicates = getAllPredicates(tsc.possibleTSCInstances)
    // Get all occurred predicates
    val occurredPredicates = getAllPredicates(tscInstances)
    // Return predicates that have not occurred
    return possiblePredicates.minus(occurredPredicates)
  }

  /**
   * Get all predicates for the given [List] of [TSCInstanceNode]s.
   *
   * @param tscInstances A [List] of [TSCInstanceNode] for which all possible predicates should be
   *   calculated.
   * @return the [Set] of predicates based on the [tscInstances].
   */
  private fun getAllPredicates(tscInstances: List<TSCInstanceNode<E, T, S, U, D>>): Set<String> {
    // Create a set for storage of all predicates
    val predicates = mutableSetOf<String>()
    tscInstances.forEach { t ->
      // Get all traversals that are possible for the current TSCInstance, excluding TSCAlwaysEdges,
      // as they do not represent a predicate
      val predicateTraversals =
          t.traverse()
              .filter { instance ->
                instance.getLeafNodeEdges(instance).any { leafNode ->
                  leafNode.tscEdge.condition != CONST_TRUE
                }
              }
              .map { it.toString() }
      predicates.addAll(predicateTraversals)
    }
    return predicates
  }

  override fun getSerializableResults(): List<SerializablePredicateResult> =
      evaluationResultCache
          ?.map { (tsc, predicates) ->
            SerializablePredicateResult(
                identifier = tsc.identifier,
                source = loggerIdentifier,
                tsc = SerializableTSCNode(tsc.rootNode),
                count = predicates.size,
                value = predicates.toList(),
            )
          }
          .orEmpty()
}
