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

package tools.aqua.stars.core.evaluation

import java.util.concurrent.Executors
import java.util.logging.Logger
import kotlin.time.measureTime
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import tools.aqua.stars.core.metric.metrics.EvaluationMetrics
import tools.aqua.stars.core.metric.metrics.PostEvaluationMetrics
import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * This class holds every important data to evaluate a given TSC. The [TSCEvaluation.presentSegment]
 * function evaluates the [TSC] based on the given [SegmentType]. The [TSCProjection]s are filtered
 * by the [projectionIgnoreList]. This class implements [Loggable].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @property tsc The [TSC].
 * @property projectionIgnoreList List of projections to ignore.
 * @property numThreads Number of parallel segment executions.
 * @property logger Logger instance.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class TSCEvaluation<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val tsc: TSC<E, T, S>,
    val projectionIgnoreList: List<String> = listOf(),
    val numThreads: Int,
    override val logger: Logger = Loggable.getLogger("evaluation-time"),
) : Loggable {

  /** Holds the [List] of [TSCProjection] based on the base [tsc]. */
  private val tscProjections: MutableList<TSCProjection<E, T, S>> = mutableListOf()

  /** Holds a [List] of all [MetricProvider]s registered by [registerMetricProviders]. */
  private val evaluationMetrics: EvaluationMetrics<E, T, S> = EvaluationMetrics()

  /** Holds a [List] of all [MetricProvider]s registered by [registerMetricProviders]. */
  private val postEvaluationMetrics: PostEvaluationMetrics<E, T, S> = PostEvaluationMetrics()

  /** Hold all [Deferred] instances returned by [evaluateSegment]. */
  private val segmentEvaluationJobs: MutableList<Deferred<EvaluationMetrics<E, T, S>>> =
      mutableListOf()

  /** Coroutine dispatcher for segment evaluations. */
  val dispatcher = Executors.newFixedThreadPool(numThreads).asCoroutineDispatcher()

  /** Coroutine scope for segment evaluations. */
  val scope: CoroutineScope = CoroutineScope(dispatcher)

  /** Coroutine channel for segment evaluations. */
  val channel = Channel<S>(numThreads)

  /**
   * Registers new [MetricProvider]s to the list of metrics that should be called during evaluation.
   *
   * @param metricProviders The [MetricProvider]s that should be registered.
   */
  fun registerMetricProviders(vararg metricProviders: MetricProvider<E, T, S>) {
    evaluationMetrics.register(
        metricProviders.filterIsInstance<EvaluationMetricProvider<E, T, S>>())
    postEvaluationMetrics.register(
        metricProviders.filterIsInstance<PostEvaluationMetricProvider<E, T, S>>())
  }

  /**
   * Registers new [EvaluationMetricProvider]s to the list of metrics that should be called during
   * evaluation.
   *
   * @param metricProviders The [EvaluationMetricProvider]s that should be registered.
   */
  fun registerEvaluationMetricProviders(vararg metricProviders: EvaluationMetricProvider<E, T, S>) {
    this.evaluationMetrics.register(metricProviders.toList())
  }

  /**
   * Registers new [PostEvaluationMetricProvider]s to the list of metrics that should be called
   * during evaluation.
   *
   * @param metricProviders The [PostEvaluationMetricProvider]s that should be registered.
   */
  fun registerPostEvaluationMetricProviders(
      vararg metricProviders: PostEvaluationMetricProvider<E, T, S>
  ) {
    this.postEvaluationMetrics.register(metricProviders.toList())
  }

  /**
   * Prepares the evaluation by building the projections on the current [TSC] instance. Requires at
   * least one [MetricProvider].
   *
   * @throws IllegalArgumentException When there are no [MetricProvider]s registered.
   */
  fun prepare() {
    check(evaluationMetrics.any() || postEvaluationMetrics.any()) {
      "There needs to be at least one registered MetricProviders."
    }
    check(tscProjections.isEmpty()) { "TSCEvaluation.prepare() has been called before." }

    // Build all projections of the base TSC
    val tscProjectionCalculationTime = measureTime {
      tscProjections.addAll(tsc.buildProjections(projectionIgnoreList))
      check(tscProjections.isNotEmpty()) { "Found no projections on current TSC." }
    }

    logFine(
        "The calculation of the projections for the given tsc took: $tscProjectionCalculationTime")

    // Start work jobs
    repeat(numThreads) { segmentEvaluationJobs.add(scope.async { work() }) }
  }

  /**
   * Adds the presented segments to the execution worker. Runs the evaluation of the [TSC] based on
   * the [segments]. For each [SegmentType], [TSCProjection] and [TSCInstanceNode], the related
   * [MetricProvider] is called.
   *
   * @param segments Segments to be added to the execution worker.
   * @throws IllegalArgumentException If [prepare] has not been called.
   */
  fun presentSegment(vararg segments: S) {
    runBlocking { segments.forEach { channel.send(it) } }
    // segmentEvaluationJobs.addAll(segments.map { scope.async { evaluateSegment(it) } })
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private suspend fun work(): EvaluationMetrics<E, T, S> {
    val result = evaluationMetrics.copy()

    while (!channel.isClosedForReceive) {
      val next = channel.receiveCatching()

      if (!next.isSuccess) break

      evaluateSegment(next.getOrThrow(), result)
    }

    return result
  }

  private suspend fun evaluateSegment(segment: S, metricHolder: EvaluationMetrics<E, T, S>) {
    val segmentEvaluationTime = measureTime {
      // Run the "evaluate" function for all SegmentMetricProviders on the current segment
      metricHolder.evaluateSegmentMetrics(segment)

      val projectionsEvaluationTime = measureTime {
        val evaluatedProjections = evaluateProjections(segment)

        evaluatedProjections.forEach { (projection, tscInstance) ->
          val projectionEvaluationTime = measureTime {
            metricHolder.evaluateProjectionMetrics(projection)
            metricHolder.evaluateTSCInstanceMetrics(tscInstance)
            metricHolder.evaluateTSCInstanceAndProjectionMetrics(tscInstance, projection)
          }
          logFine(
              "The evaluation of projection '${projection.id}' for segment '$segment' took: $projectionEvaluationTime.")
        }
      }
      logFine(
          "The evaluation of all projections for segment '$segment' took: $projectionsEvaluationTime")
    }
    logFine("The evaluation of segment '$segment' took: $segmentEvaluationTime")

    EvaluationState.finishedSegments.incrementAndGet()
  }

  private suspend fun evaluateProjections(
      segment: S
  ): List<Pair<TSCProjection<E, T, S>, TSCInstance<E, T, S>>> = coroutineScope {
    val jobs =
        tscProjections.map { projection ->
          async { projection to projection.tsc.evaluate(PredicateContext(segment)) }
        }

    jobs.awaitAll()
  }

  /** Closes the [TSCEvaluation] instance printing the results. */
  fun close() {
    runBlocking {
      channel.close()

      EvaluationState.isFinished.set(true)

      EvaluationMetrics.merge(segmentEvaluationJobs.awaitAll()).apply {
        tscProjections.clear()
        printState()
        plotData()
        close()
      }

      postEvaluationMetrics.apply {
        evaluate()
        plotData()
        close()
      }
    }

    dispatcher.close()
    closeLogger()

    EvaluationState.checkFinished(true)
  }
}
