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

@file:Suppress("MemberVisibilityCanBePrivate")

package tools.aqua.stars.core.evaluation

import java.util.logging.Logger
import kotlin.time.measureTime
import tools.aqua.stars.core.computeWhile
import tools.aqua.stars.core.hooks.*
import tools.aqua.stars.core.hooks.defaulthooks.MinTicksPerSegmentHook
import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * This class runs the evaluation of [TSC]s. The [TSCEvaluation.runEvaluation] function evaluates
 * the [TSC]s based on the given [Sequence] of [SegmentType]s. This class implements [Loggable].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property tscList The list of [TSC]s to evaluate.
 * @property writePlots (Default: ``true``) Whether to write plots after the analysis.
 * @property writePlotDataCSV (Default: ``false``) Whether to write CSV files after the analysis.
 * @property logger [Logger] instance.
 */
class TSCEvaluation<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val tscList: List<TSC<E, T, S, U, D>>,
    val writePlots: Boolean = true,
    val writePlotDataCSV: Boolean = false,
    override val logger: Logger = Loggable.getLogger("evaluation-time")
) : Loggable {

  /** Holds a [List] of all [MetricProvider]s registered by [registerMetricProviders]. */
  private val metricProviders: MutableList<MetricProvider<E, T, S, U, D>> = mutableListOf()

  /** Holds the results of the [PreTSCEvaluationHook]s after calling [runEvaluation]. */
  val preTSCEvaluationHookResults:
      MutableMap<
          TSC<E, T, S, U, D>, Map<PreTSCEvaluationHook<E, T, S, U, D>, EvaluationHookResult>> =
      mutableMapOf()

  /**
   * Holds a [List] of all [PreTSCEvaluationHook]s registered by [registerPreTSCEvaluationHooks].
   */
  private val preTSCEvaluationHooks: MutableList<PreTSCEvaluationHook<E, T, S, U, D>> =
      mutableListOf()

  /** Holds the results of the [PreSegmentEvaluationHook]s after calling [runEvaluation]. */
  val preSegmentEvaluationHookResults:
      MutableMap<S, Map<PreSegmentEvaluationHook<E, T, S, U, D>, EvaluationHookResult>> =
      mutableMapOf()

  /**
   * Holds a [List] of all [PreSegmentEvaluationHook]s registered by
   * [registerPreSegmentEvaluationHooks].
   */
  private val preSegmentEvaluationHooks: MutableList<PreSegmentEvaluationHook<E, T, S, U, D>> =
      mutableListOf()

  init {
    registerDefaultHooks()
  }

  /**
   * Registers all [MetricProvider]s to the list of metrics that should be called during evaluation.
   *
   * @param metricProviders The [MetricProvider]s that should be registered.
   * @throws IllegalArgumentException When a given [MetricProvider] is already added.
   */
  fun registerMetricProviders(vararg metricProviders: MetricProvider<E, T, S, U, D>) {
    metricProviders.forEach {
      require(it.javaClass !in this.metricProviders.map { t -> t.javaClass }) {
        "The MetricProvider ${it.javaClass.simpleName} is already registered."
      }
      this.metricProviders.add(it)
    }
  }

  /**
   * Registers all [PreTSCEvaluationHook]s to the list of hooks that should be called before the
   * evaluation of the [TSC].
   * - If the [PreTSCEvaluationHook] returns [EvaluationHookResult.SKIP], the evaluation proceeds
   *   with the next TSC.
   * - If the [PreTSCEvaluationHook] returns [EvaluationHookResult.CANCEL], the evaluation is
   *   canceled at this point.
   * - If the [PreTSCEvaluationHook] returns [EvaluationHookResult.ABORT], the evaluation is
   *   aborted, a [EvaluationHookAbort] is thrown, and no post evaluation steps are performed.
   *
   * @param preTSCEvaluationHooks The [PreTSCEvaluationHook]s that should be registered.
   */
  fun registerPreTSCEvaluationHooks(
      vararg preTSCEvaluationHooks: PreTSCEvaluationHook<E, T, S, U, D>
  ) {
    this.preTSCEvaluationHooks.addAll(preTSCEvaluationHooks)
  }

  /**
   * Registers all [PreSegmentEvaluationHook]s to the list of hooks that should be called before the
   * evaluation of each [SegmentType].
   * - If the [PreSegmentEvaluationHook] returns [EvaluationHookResult.SKIP], the evaluation
   *   proceeds with the next [SegmentType].
   * - If the [PreSegmentEvaluationHook] returns [EvaluationHookResult.CANCEL], the evaluation is
   *   canceled at this point but post evaluation steps are performed.
   * - If the [PreSegmentEvaluationHook] returns [EvaluationHookResult.ABORT], the evaluation is
   *   aborted, an [EvaluationHookAbort] is thrown, and no post evaluation steps are performed.
   *
   * @param preSegmentEvaluationHooks The [PreSegmentEvaluationHook]s that should be registered.
   */
  fun registerPreSegmentEvaluationHooks(
      vararg preSegmentEvaluationHooks: PreSegmentEvaluationHook<E, T, S, U, D>
  ) {
    this.preSegmentEvaluationHooks.addAll(preSegmentEvaluationHooks)
  }

  /**
   * Registers all default hooks to the list of hooks that should be called during evaluation. This
   * includes:
   * - [MinTicksPerSegmentHook] with a minimum of 1 tick per segment.
   *
   * The lists of hooks [preTSCEvaluationHooks] and [preSegmentEvaluationHooks] are NOT cleared
   * before. [clearHooks] may be called before to clear them.
   */
  fun registerDefaultHooks() {
    preSegmentEvaluationHooks.add(MinTicksPerSegmentHook(minTicks = 1))
  }

  /**
   * Clears all [PreTSCEvaluationHook]s and [PreSegmentEvaluationHook]s that have been registered.
   */
  fun clearHooks() {
    preTSCEvaluationHooks.clear()
    preSegmentEvaluationHooks.clear()
  }

  /**
   * Runs the evaluation of the [TSC]s based on the [segments]. For each [SegmentType], [TSC] and
   * [TSCInstanceNode], the related [MetricProvider] is called. It requires at least one
   * [MetricProvider].
   *
   * @param segments The [Sequence] of [SegmentType]s to evaluate.
   * @throws IllegalArgumentException When there are no [MetricProvider]s registered.
   */
  fun runEvaluation(segments: Sequence<S>) {
    require(metricProviders.any()) { "There needs to be at least one registered MetricProvider." }

    val totalEvaluationTime = measureTime {
      val tscListToEvaluate = runPreTSCEvaluationHooks() ?: return

      // Evaluate all segments
      val segmentsEvaluationTime = measureTime {
        if (tscListToEvaluate.isNotEmpty())
            segments.computeWhile { evaluateSegment(segment = it, tscList = tscListToEvaluate) }
      }
      logInfo("The evaluation of all segments took: $segmentsEvaluationTime")
    }
    logInfo("The whole evaluation took: $totalEvaluationTime")

    postEvaluate()
  }

  /**
   * Evaluates the given [SegmentType] on the given [TSC]s. The function returns `false` if the
   * iteration should be stopped due to an [EvaluationHook] returning [EvaluationHookResult.CANCEL].
   *
   * @param segment The [SegmentType] to evaluate.
   * @param tscList The list of [TSC]s to evaluate.
   * @return Whether the evaluation should continue.
   */
  private fun evaluateSegment(segment: S, tscList: List<TSC<E, T, S, U, D>>): Boolean {
    // Evaluate PreSegmentEvaluationHooks
    runPreSegmentEvaluationHook(segment = segment)?.let {
      return it
    }

    // Evaluate segment
    val segmentEvaluationTime = measureTime {
      // Run the "evaluate" function for all SegmentMetricProviders on the current segment
      metricProviders.filterIsInstance<SegmentMetricProvider<E, T, S, U, D>>().forEach {
        it.evaluate(segment)
      }
      val allTSCEvaluationTime = measureTime {
        tscList.forEach { tsc ->
          val tscEvaluationTime = measureTime {
            // Run the "evaluate" function for all TSCMetricProviders on the current segment
            metricProviders.filterIsInstance<TSCMetricProvider<E, T, S, U, D>>().forEach {
              it.evaluate(tsc)
            }
            // Holds the PredicateContext for the current segment
            val context = PredicateContext(segment)

            // Holds the [TSCInstanceNode] of the current [tsc] using the
            // [PredicateContext], representing a whole TSC.
            val segmentTSCInstance = tsc.evaluate(context)

            // Run the "evaluate" function for all TSCInstanceMetricProviders on the
            // current segment
            metricProviders.filterIsInstance<TSCInstanceMetricProvider<E, T, S, U, D>>().forEach {
              it.evaluate(segmentTSCInstance)
            }

            // Run the "evaluate" function for all TSCAndTSCInstanceNodeMetricProviders on the
            // current tsc and instance
            metricProviders
                .filterIsInstance<TSCAndTSCInstanceNodeMetricProvider<E, T, S, U, D>>()
                .forEach { it.evaluate(tsc, segmentTSCInstance) }
          }
          logFine(
              "The evaluation of tsc with root node '${tsc.rootNode.label}' for segment '$segment' took: $tscEvaluationTime")
        }
      }
      logFine("The evaluation of all TSCs for segment '$segment' took: $allTSCEvaluationTime")
    }
    logFine("The evaluation of segment '$segment' took: $segmentEvaluationTime")

    return true
  }

  /** Executes all [PreTSCEvaluationHook]s on the [tscList] and returns all passing TSCs. */
  private fun runPreTSCEvaluationHooks(): List<TSC<E, T, S, U, D>>? {
    // Evaluate PreEvaluationHooks
    val hookResults =
        tscList.associateWith { tsc ->
          this.preTSCEvaluationHooks.associateWith { it.evaluationFunction.invoke(tsc) }
        }

    // Save results to preTSCEvaluationHookResults
    preTSCEvaluationHookResults.putAll(hookResults)

    // Filter out all TSCs that have not returned OK. Do not optimize by using
    // preTSCEvaluationHookResults, since runEvaluation may be called multiple times.
    val tscList = mutableListOf<TSC<E, T, S, U, D>>()
    hookResults.forEach { (tsc, results) ->
      val (result, hooks) = evaluateHooks(results)
      when (result) {
        // Abort evaluation using a EvaluationHookAbort exception
        EvaluationHookResult.ABORT -> {
          EvaluationHookStringWrapper.abort(tsc, hooks)
        }
        // Cancel evaluation by returning
        EvaluationHookResult.CANCEL -> {
          EvaluationHookStringWrapper.cancel(tsc, hooks)
          return null
        }
        // Don't include current TSC in the list
        EvaluationHookResult.SKIP -> {
          EvaluationHookStringWrapper.skip(tsc, hooks)
        }
        // Include current TSC in the list
        EvaluationHookResult.OK -> {
          tscList.add(tsc)
        }
      }
    }
    return tscList
  }

  /**
   * Executes all [PreSegmentEvaluationHook]s on the [segment].
   *
   * @return `true` if the segment should be skipped, `false` if the evaluation should be canceled,
   *   `null` if the evaluation should continue normally.
   */
  private fun runPreSegmentEvaluationHook(segment: S): Boolean? {
    val hookResults =
        this.preSegmentEvaluationHooks.associateWith { it.evaluationFunction.invoke(segment) }

    // Save results to preSegmentEvaluationHookResults
    preSegmentEvaluationHookResults[segment] = hookResults

    val (result, hooks) = evaluateHooks(hookResults)
    return when (result) {
      // Abort the evaluation using a EvaluationHookAbort exception
      EvaluationHookResult.ABORT -> {
        EvaluationHookStringWrapper.abort(segment, hooks)
        null
      }
      // Cancel the evaluation by returning false
      EvaluationHookResult.CANCEL -> {
        EvaluationHookStringWrapper.cancel(segment, hooks)
        false
      }
      // Return without evaluating the segment
      EvaluationHookResult.SKIP -> {
        EvaluationHookStringWrapper.skip(segment, hooks)
        true
      }
      // Continue with evaluation
      EvaluationHookResult.OK -> {
        null
      }
    }
  }

  /**
   * Evaluates given [results] by grouping them by [EvaluationHookResult] and returning the most
   * severe result.
   */
  private fun <T : EvaluationHook<*>> evaluateHooks(
      results: Map<T, EvaluationHookResult>
  ): Pair<EvaluationHookResult, Collection<EvaluationHook<*>>> {
    val groupedResults = results.toList().groupBy({ it.second }, { it.first })

    // Abort the evaluation and throw exception if any hook returns ABORT
    val abortingHooks = groupedResults[EvaluationHookResult.ABORT] ?: emptyList()
    if (abortingHooks.isNotEmpty()) return Pair(EvaluationHookResult.ABORT, abortingHooks)

    // Cancel the evaluation if any hook returns CANCEL
    val cancelingHooks = groupedResults[EvaluationHookResult.CANCEL] ?: emptyList()
    if (cancelingHooks.isNotEmpty()) return Pair(EvaluationHookResult.CANCEL, cancelingHooks)

    // Skip all TSCs that have a hook returning SKIP
    val skippingHooks = groupedResults[EvaluationHookResult.SKIP] ?: emptyList()
    if (skippingHooks.isNotEmpty()) return Pair(EvaluationHookResult.SKIP, skippingHooks)

    return Pair(EvaluationHookResult.OK, results.keys)
  }

  /** Runs post evaluation steps such as printing, logging and plotting. */
  private fun postEvaluate() {
    // Print the results of all Stateful metrics
    metricProviders.filterIsInstance<Stateful>().forEach { it.printState() }

    // Call the 'evaluate' and then the 'print' function for all PostEvaluationMetricProviders
    println("Running post evaluation metrics")
    metricProviders.filterIsInstance<PostEvaluationMetricProvider<E, T, S, U, D>>().forEach {
      it.postEvaluate()
      it.printPostEvaluationResult()
    }

    // Plot the results of all Plottable metrics
    if (writePlots) {
      println("Creating Plots")
      metricProviders.filterIsInstance<Plottable>().forEach { it.writePlots() }
    }

    // Write CSV of the results of all Plottable metrics
    if (writePlotDataCSV) {
      println("Writing CSVs")
      metricProviders.filterIsInstance<Plottable>().forEach { it.writePlotDataCSV() }
    }
  }
}
