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

@file:Suppress("MemberVisibilityCanBePrivate")

package tools.aqua.stars.core.evaluation

import java.time.LocalDateTime
import java.util.logging.Logger
import kotlin.time.measureTime
import tools.aqua.stars.core.computeWhile
import tools.aqua.stars.core.forEachInstance
import tools.aqua.stars.core.hooks.*
import tools.aqua.stars.core.hooks.PreTSCEvaluationHook.Companion.evaluate
import tools.aqua.stars.core.hooks.PreTickEvaluationHook.Companion.evaluate
import tools.aqua.stars.core.hooks.defaulthooks.MinEntitiesPerTickHook
import tools.aqua.stars.core.hooks.defaulthooks.MinNodesInTSCHook
import tools.aqua.stars.core.hooks.defaulthooks.MinTicksPerTickSequenceHook
import tools.aqua.stars.core.metric.metrics.providers.Loggable
import tools.aqua.stars.core.metric.metrics.providers.MetricProvider
import tools.aqua.stars.core.metric.metrics.providers.Plottable
import tools.aqua.stars.core.metric.metrics.providers.PostEvaluationMetricProvider
import tools.aqua.stars.core.metric.metrics.providers.SerializableMetric
import tools.aqua.stars.core.metric.metrics.providers.Stateful
import tools.aqua.stars.core.metric.metrics.providers.TSCAndTSCInstanceNodeMetricProvider
import tools.aqua.stars.core.metric.metrics.providers.TSCInstanceMetricProvider
import tools.aqua.stars.core.metric.metrics.providers.TSCMetricProvider
import tools.aqua.stars.core.metric.metrics.providers.TickMetricProvider
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison.Companion.noMismatch
import tools.aqua.stars.core.metric.serialization.extensions.compareToPreviousResults
import tools.aqua.stars.core.metric.serialization.extensions.writeSerializedResults
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.logFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder
import tools.aqua.stars.core.metric.utils.saveAsJsonFiles
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.types.*

/**
 * This class runs the evaluation of [TSC]s. The [TSCEvaluation.runEvaluation] function evaluates
 * the [TSC]s based on the given [Sequence] of [TickDataType]s. This class implements
 * [tools.aqua.stars.core.metric.metrics.providers.Loggable].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property tscList The list of [TSC]s to evaluate.
 * @property writePlots (Default: ``true``) Whether to write plots after the analysis.
 * @property writePlotDataCSV (Default: ``false``) Whether to write CSV files after the analysis.
 * @property writeSerializedResults (Default: ``true``) Whether to write result files and compare
 *   them to previous runs after the analysis.
 * @property compareToPreviousRun (Default: ``false``) Whether to compare the results to the
 *   previous run.
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
class TSCEvaluation<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    val tscList: List<TSC<E, T, U, D>>,
    val writePlots: Boolean = true,
    val writePlotDataCSV: Boolean = false,
    val writeSerializedResults: Boolean = true,
    val compareToPreviousRun: Boolean = false,
    override val loggerIdentifier: String = "evaluation-time",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier),
) : Loggable {

  /** Mutex. */
  private val mutex: Any = Any()

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
          field != null && value != null -> field = checkNotNull(field) && value
        }
      }
    }

  /**
   * Holds a [List] of all [tools.aqua.stars.core.metric.metrics.providers.MetricProvider]s
   * registered by [registerMetricProviders].
   */
  private val metricProviders: MutableList<MetricProvider<E, T, U, D>> = mutableListOf()

  /** Holds the results of the [PreTSCEvaluationHook]s after calling [runEvaluation]. */
  val preTSCEvaluationHookResults: MutableMap<TSC<E, T, U, D>, Map<String, EvaluationHookResult>> =
      mutableMapOf()

  /**
   * Holds a [List] of all [PreTSCEvaluationHook]s registered by [registerPreTSCEvaluationHooks].
   */
  private val preTSCEvaluationHooks: MutableList<PreTSCEvaluationHook<E, T, U, D>> = mutableListOf()

  /**
   * Holds the results (Map of [PreTickEvaluationHook.identifier] to [EvaluationHookResult]) of the
   * [PreTickEvaluationHook]s after calling [runEvaluation] that did not return
   * [EvaluationHookResult.OK] for each identifier.
   */
  val preTickEvaluationHookResults: MutableMap<String, Map<String, EvaluationHookResult>> =
      mutableMapOf() // TODO: String = Tick Identifier -> Change to range

  /**
   * Holds a [List] of all [PreTickEvaluationHook]s registered by [registerPreTickEvaluationHooks].
   */
  private val preTickEvaluationHooks: MutableList<PreTickEvaluationHook<E, T, U, D>> =
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
  fun registerMetricProviders(vararg metricProviders: MetricProvider<E, T, U, D>) {
    metricProviders.forEach { provider ->
      require(this.metricProviders.none { it.javaClass == provider.javaClass }) {
        "The MetricProvider ${provider.javaClass.simpleName} is already registered."
      }
      this.metricProviders.add(provider)
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
   *   aborted, an [EvaluationHookAbort] is thrown, and no post evaluation steps are performed.
   *
   * @param preTSCEvaluationHooks The [PreTSCEvaluationHook]s that should be registered.
   */
  fun registerPreTSCEvaluationHooks(
      vararg preTSCEvaluationHooks: PreTSCEvaluationHook<E, T, U, D>
  ) {
    this.preTSCEvaluationHooks.addAll(preTSCEvaluationHooks)
  }

  /**
   * Registers all [PreTickEvaluationHook]s to the list of hooks that should be called before the
   * evaluation of each tick of data.
   * - If the [PreTickEvaluationHook] returns [EvaluationHookResult.SKIP], the evaluation proceeds
   *   with the next tick.
   * - If the [PreTickEvaluationHook] returns [EvaluationHookResult.CANCEL], the evaluation is
   *   canceled at this point but post evaluation steps are performed.
   * - If the [PreTickEvaluationHook] returns [EvaluationHookResult.ABORT], the evaluation is
   *   aborted, an [EvaluationHookAbort] is thrown, and no post evaluation steps are performed.
   *
   * @param preTickEvaluationHooks The [PreTickEvaluationHook]s that should be registered.
   */
  fun registerPreTickEvaluationHooks(
      vararg preTickEvaluationHooks: PreTickEvaluationHook<E, T, U, D>
  ) {
    this.preTickEvaluationHooks.addAll(preTickEvaluationHooks)
  }

  /**
   * Registers all default hooks to the list of hooks that should be called during evaluation. This
   * includes:
   * - [MinEntitiesPerTickHook] with a minimum of 1 entity per tick.
   * - [MinTicksPerTickSequenceHook] with a minimum of 1 tick per [TickSequence].
   * - [MinNodesInTSCHook] with a minimum of 1 node in each TSC.
   *
   * The lists of hooks [preTSCEvaluationHooks] and [preTickEvaluationHooks] are NOT cleared before.
   * [clearHooks] may be called before to clear them.
   */
  fun registerDefaultHooks() {
    preTickEvaluationHooks.add(MinEntitiesPerTickHook(minEntities = 1))
    preTickEvaluationHooks.add(MinTicksPerTickSequenceHook(minTicks = 1))
    preTSCEvaluationHooks.add(MinNodesInTSCHook(minNodes = 1))
  }

  /** Clears all [PreTSCEvaluationHook]s that have been registered. */
  fun clearHooks() {
    preTSCEvaluationHooks.clear()
    preTickEvaluationHooks.clear()
  }

  /**
   * Runs the evaluation of the [TSC]s based on a sequence of [TickSequence]s.
   *
   * @param ticks The [Sequence] of [TickSequence]s to evaluate.
   * @throws IllegalArgumentException When there are no [MetricProvider]s registered.
   */
  fun runEvaluation(ticks: Sequence<TickSequence<T>>) {
    require(metricProviders.any()) { "There needs to be at least one registered MetricProvider." }

    val totalEvaluationTime = measureTime {
      // Filter TSCs to be evaluated
      val tscListToEvaluate = filterTSCs() ?: return
      if (tscListToEvaluate.isNotEmpty()) {

        // Run the "evaluate" function for all TSCMetricProviders
        tscListToEvaluate.forEach { tsc ->
          metricProviders.forEachInstance<TSCMetricProvider<E, T, U, D>> { metric ->
            metric.evaluate(tsc)
          }
        }

        val tscEvaluationTime = measureTime {
          tscListToEvaluate.forEach { tsc ->
            metricProviders.filterIsInstance<TSCMetricProvider<E, T, U, D>>().forEach {
              it.evaluate(tsc)
            }
          }
        }
        logInfo("The evaluation of all TSCs took: $tscEvaluationTime")

        // Evaluate all ticks
        val evaluationTime = measureTime {
          ticks.forEach { tickSequence ->
            tickSequence.computeWhile { tick ->
              evaluateTick(tick = tick, tscList = tscListToEvaluate)
            }
          }
        }
        logInfo("The evaluation of all ticks took: $evaluationTime")
      } else {
        logWarning("No TSCs to evaluate. Skipping evaluation.")
      }
    }
    logInfo("The whole evaluation took: $totalEvaluationTime")
    ApplicationConstantsHolder.totalEvaluationTime += totalEvaluationTime
    ApplicationConstantsHolder.experimentEndTime = LocalDateTime.now()

    postEvaluate()
  }

  /**
   * Evaluates the [preTSCEvaluationHooks] on the [tscList], adds the results to the
   * [preTSCEvaluationHookResults] [Map] and returns the list of TSCs that should be evaluated.
   * Returns `null` if some [PreTSCEvaluationHook] returned [EvaluationHookResult.CANCEL].
   *
   * @return The list of [TSC]s that should be evaluated, or `null` if the evaluation was canceled.
   */
  private fun filterTSCs(): List<TSC<E, T, U, D>>? {
    val (passingTSCs, results) = preTSCEvaluationHooks.evaluate(tscList)

    results.forEach { (tsc, results) ->
      preTSCEvaluationHookResults[tsc] = results.map { it.key.identifier to it.value }.toMap()
    }

    return passingTSCs
  }

  /**
   * Evaluates the given [List] of [TickDataType]s on the given [TSC]s. The function returns `false`
   * if the iteration should be stopped due to an [EvaluationHook] returning
   * [EvaluationHookResult.CANCEL].
   *
   * @param tick The [TickDataType] to evaluate.
   * @param tscList The list of [TSC]s to evaluate.
   * @return Whether the evaluation should continue.
   */
  private fun evaluateTick(tick: T, tscList: List<TSC<E, T, U, D>>): Boolean {
    // Evaluate PreTickEvaluationHooks
    preTickEvaluationHooks.evaluate(tick).let { (verdict, results) ->
      if (verdict != null) {
        preTickEvaluationHookResults[tick.toString()] =
            results.map { it.key.identifier to it.value }.toMap()

        return verdict
      }
    }

    // Evaluate tick
    val tickEvaluationTime = measureTime {
      // Run the "evaluate" function for all TickMetricProviders on the current tick
      metricProviders.forEachInstance<TickMetricProvider<E, T, U, D>> { it.evaluate(tick) }

      // Run the evaluation for all TSCs
      tscList.forEach { tsc ->
        val tscEvaluationTime = measureTime {
          // Evaluate the TSC on the current tick.
          val tscInstance = tsc.evaluate(tick)

          // Run the "evaluate" function for all TSCInstanceMetricProviders on the
          // current tsc instance
          metricProviders.forEachInstance<TSCInstanceMetricProvider<E, T, U, D>> {
            it.evaluate(tscInstance)
          }

          // Run the "evaluate" function for all TSCAndTSCInstanceNodeMetricProviders on the
          // current tsc and instance
          metricProviders.forEachInstance<TSCAndTSCInstanceNodeMetricProvider<E, T, U, D>> {
            it.evaluate(tsc, tscInstance)
          }
        }
        logFiner(
            "The evaluation of tsc with root node '${tsc.rootNode.label}' for tick '$tick' took: $tscEvaluationTime"
        )
      }
    }
    logFine("The evaluation of tick '$tick' took: $tickEvaluationTime")
    ApplicationConstantsHolder.totalTickEvaluationTime += tickEvaluationTime

    return true
  }

  /** Runs post evaluation steps such as printing, logging and plotting. */
  private fun postEvaluate() {
    // Print the results of all Stateful metrics
    metricProviders.forEachInstance<Stateful> { it.printState() }

    // Call the 'evaluate' and then the 'print' function for all PostEvaluationMetricProviders
    println("Running post evaluation metrics")
    metricProviders.forEachInstance<PostEvaluationMetricProvider<E, T, U, D>> {
      it.postEvaluate()
      it.printPostEvaluationResult()
    }

    // Plot the results of all Plottable metrics
    if (writePlots) {
      println("Creating Plots")
      metricProviders.forEachInstance<Plottable> { it.writePlots() }
    }

    // Write CSV of the results of all Plottable metrics
    if (writePlotDataCSV) {
      println("Writing CSVs")
      metricProviders.forEachInstance<Plottable> { it.writePlotDataCSV() }
    }

    val serializableMetrics = metricProviders.filterIsInstance<SerializableMetric>()
    if (serializableMetrics.any()) {
      ApplicationConstantsHolder.writeMetaInfo("$logFolder/$applicationStartTimeString/")

      // Write JSON files of all Serializable metrics
      if (writeSerializedResults) {
        println("Writing serialized results")
        ApplicationConstantsHolder.writeMetaInfo(
            "$serializedResultsFolder/$applicationStartTimeString/"
        )
        serializableMetrics.forEach { t -> t.writeSerializedResults() }
      }

      // Compare the results to the latest run
      if (compareToPreviousRun) {
        println("Comparing to previous run")
        ApplicationConstantsHolder.writeMetaInfo(
            "$comparedResultsFolder/$applicationStartTimeString/"
        )
        serializableMetrics.compareToPreviousResults().let {
          resultsReproducedFromPreviousRun = it.noMismatch()

          if (writeSerializedResults) it.saveAsJsonFiles(comparedToBaseline = false)
        }
      }
    }
  }
}
