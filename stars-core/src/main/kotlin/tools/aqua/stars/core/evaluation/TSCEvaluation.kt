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

import java.time.LocalDateTime
import java.util.logging.Logger
import kotlin.time.measureTime
import tools.aqua.stars.core.computeWhile
import tools.aqua.stars.core.hooks.*
import tools.aqua.stars.core.hooks.PreSegmentEvaluationHook.Companion.evaluate
import tools.aqua.stars.core.hooks.PreTSCEvaluationHook.Companion.evaluate
import tools.aqua.stars.core.hooks.defaulthooks.MinTicksPerSegmentHook
import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison.Companion.noMismatch
import tools.aqua.stars.core.metric.serialization.extensions.compareToBaselineResults
import tools.aqua.stars.core.metric.serialization.extensions.compareToPreviousResults
import tools.aqua.stars.core.metric.serialization.extensions.writeSerializedResults
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.logFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder
import tools.aqua.stars.core.metric.utils.saveAsJsonFiles
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
 * @property writeSerializedResults (Default: ``true``) Whether to write result files and compare
 *   them to previous runs after the analysis.
 * @property compareToBaselineResults (Default: ``false``) Whether to compare the results to the
 *   baseline results.
 * @property compareToPreviousRun (Default: ``false``) Whether to compare the results to the
 *   previous run.
 * @property loggerIdentifier identifier (name) for the logger.
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
    val writeSerializedResults: Boolean = true,
    val compareToBaselineResults: Boolean = false,
    val compareToPreviousRun: Boolean = false,
    override val loggerIdentifier: String = "evaluation-time",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier),
) : Loggable {

  /** Test. */
  private val mutex: Any = Any()

  /**
   * Holds the aggregated [Boolean] verdict of all compared results with the baseline data. Setting
   * a new value will be conjugated with the old value such that a verdict 'false' may not be
   * changed to 'true' again.
   */
  var resultsReproducedFromBaseline: Boolean? = null
    set(value) {
      synchronized(mutex) {
        when {
          field == null -> field = value
          field != null && value != null -> field = field!! && value
        }
      }
    }

  /**
   * Holds the aggregated [Boolean] verdict of all compared results with the previous evaluation
   * results. Setting a new value will be conjugated with the old value such that a verdict 'false'
   * may not be changed to 'true' again.
   */
  var resultsReproducedFromPreviousRun: Boolean? = null
    set(value) {
      synchronized(mutex) {
        when {
          field == null -> field = value
          field != null && value != null -> field = field!! && value
        }
      }
    }

  /** Holds a [List] of all [MetricProvider]s registered by [registerMetricProviders]. */
  private val metricProviders: MutableList<MetricProvider<E, T, S, U, D>> = mutableListOf()

  /** Holds the results of the [PreTSCEvaluationHook]s after calling [runEvaluation]. */
  val preTSCEvaluationHookResults:
      MutableMap<TSC<E, T, S, U, D>, Map<String, EvaluationHookResult>> =
      mutableMapOf()

  /**
   * Holds a [List] of all [PreTSCEvaluationHook]s registered by [registerPreTSCEvaluationHooks].
   */
  private val preTSCEvaluationHooks: MutableList<PreTSCEvaluationHook<E, T, S, U, D>> =
      mutableListOf()

  /**
   * Holds the results (Map of [PreSegmentEvaluationHook.identifier] to [EvaluationHookResult]) of
   * the [PreSegmentEvaluationHook]s after calling [runEvaluation] that did not return
   * [EvaluationHookResult.OK] for each segment identifier obtained by
   * [SegmentType.getSegmentIdentifier].
   */
  val preSegmentEvaluationHookResults: MutableMap<String, Map<String, EvaluationHookResult>> =
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
      val tscListToEvaluate =
          preTSCEvaluationHooks.evaluate(tscList).let { (passingTSCs, results) ->
            results.forEach { (tsc, results) ->
              preTSCEvaluationHookResults[tsc] =
                  results.map { it.key.identifier to it.value }.toMap()
            }
            passingTSCs ?: return
          }

      // Evaluate all segments
      val segmentsEvaluationTime = measureTime {
        if (tscListToEvaluate.isNotEmpty())
            segments.computeWhile { evaluateSegment(segment = it, tscList = tscListToEvaluate) }
      }
      logInfo("The evaluation of all segments took: $segmentsEvaluationTime")
    }
    logInfo("The whole evaluation took: $totalEvaluationTime")
    ApplicationConstantsHolder.totalEvaluationTime += totalEvaluationTime
    ApplicationConstantsHolder.experimentEndTime = LocalDateTime.now()

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
    preSegmentEvaluationHooks.evaluate(segment).let { (verdict, results) ->
      if (verdict != null) {
        preSegmentEvaluationHookResults[segment.getSegmentIdentifier()] =
            results.map { it.key.identifier to it.value }.toMap()

        return verdict
      }
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
    ApplicationConstantsHolder.totalSegmentEvaluationTime += segmentEvaluationTime

    return true
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

    val serializableMetrics = metricProviders.filterIsInstance<Serializable>()
    if (serializableMetrics.any()) {
      ApplicationConstantsHolder.writeMetaInfo("$logFolder/$applicationStartTimeString/")

      // Write JSON files of all Serializable metrics
      if (writeSerializedResults) {
        println("Writing serialized results")
        ApplicationConstantsHolder.writeMetaInfo(
            "$serializedResultsFolder/$applicationStartTimeString/")
        serializableMetrics.forEach { t -> t.writeSerializedResults() }
      }

      // Compare the results to the baseline
      if (compareToBaselineResults) {
        println("Comparing to baseline")
        ApplicationConstantsHolder.writeMetaInfo(
            "$comparedResultsFolder/$applicationStartTimeString/")
        serializableMetrics.compareToBaselineResults().let {
          resultsReproducedFromBaseline = it.noMismatch()

          if (writeSerializedResults) it.saveAsJsonFiles(comparedToBaseline = true)
        }
      }

      // Compare the results to the latest run
      if (compareToPreviousRun) {
        println("Comparing to previous run")
        ApplicationConstantsHolder.writeMetaInfo(
            "$comparedResultsFolder/$applicationStartTimeString/")
        serializableMetrics.compareToPreviousResults().let {
          resultsReproducedFromPreviousRun = it.noMismatch()

          if (writeSerializedResults) it.saveAsJsonFiles(comparedToBaseline = false)
        }
      }
    }
  }
}
