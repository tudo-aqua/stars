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

import java.util.logging.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * This class holds every important data to evaluate a given TSC. The [TSCEvaluation.runEvaluation]
 * function evaluates the [TSC] based on the given [SegmentType]s. The [TSCProjection]s are filtered
 * by the [projectionIgnoreList]. This class implements [Loggable].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @property tsc The [TSC].
 * @property segments Sequence of [SegmentType]s.
 * @property projectionIgnoreList List of projections to ignore.
 * @property logger Logger instance.
 */
@OptIn(ExperimentalTime::class)
class TSCEvaluation<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val tsc: TSC<E, T, S>,
    val segments: Sequence<S>,
    val projectionIgnoreList: List<String> = listOf(),
    override val logger: Logger = Loggable.getLogger("evaluation-time")
) : Loggable {
  /** Holds a [List] of all [MetricProvider]s registered by [registerMetricProvider]. */
  private val metricProviders: MutableList<MetricProvider<E, T, S>> = mutableListOf()

  /**
   * Registers a new [MetricProvider] to the list of metrics that should be called during
   * evaluation.
   *
   * @param metricProvider The [MetricProvider] that should be registered.
   */
  fun registerMetricProvider(metricProvider: MetricProvider<E, T, S>) {
    metricProviders.add(metricProvider)
  }

  /**
   * Runs the evaluation of the [TSC] based on the [segments]. For each [SegmentType],
   * [TSCProjection] and [TSCInstanceNode], the related [MetricProvider] is called. It requires at
   * least one [MetricProvider].
   *
   * @throws IllegalArgumentException When there are no [MetricProvider]s registered.
   */
  fun runEvaluation() {
    require(metricProviders.any()) { "There needs to be at least one registered MetricProviders." }

    val totalEvaluationTime = measureTime {
      /** Holds the [List] of [TSCProjection] based on the base [tsc]. */
      var tscProjections: List<TSCProjection<E, T, S>>

      // Build all projections of the base TSC
      val tscProjectionCalculationTime = measureTime {
        tscProjections = tsc.buildProjections(projectionIgnoreList)
      }

      logFine(
          "The calculation of the projections for the given tsc took: $tscProjectionCalculationTime")

      val segmentsEvaluationTime = measureTime {
        segments.forEach { segment ->
          val segmentEvaluationTime = measureTime {
            // Run the "evaluate" function for all SegmentMetricProviders on the current segment
            metricProviders.filterIsInstance<SegmentMetricProvider<E, T, S>>().forEach {
              it.evaluate(segment)
            }
            val projectionsEvaluationTime = measureTime {
              tscProjections.forEach { projection ->
                val projectionEvaluationTime = measureTime {
                  // Run the "evaluate" function for all ProjectionMetricProviders on the current
                  // segment
                  metricProviders.filterIsInstance<ProjectionMetricProvider<E, T, S>>().forEach {
                    it.evaluate(projection)
                  }
                  // Holds the PredicateContext for the current segment
                  val context = PredicateContext(segment)

                  // Holds the [TSCInstanceNode] of the current [projection] using the
                  // [PredicateContext], representing a whole TSC.
                  val segmentProjectionTSCInstance = projection.tsc.evaluate(context)

                  // Run the "evaluate" function for all TSCInstanceMetricProviders on the current
                  // segment
                  metricProviders.filterIsInstance<TSCInstanceMetricProvider<E, T, S>>().forEach {
                    it.evaluate(segmentProjectionTSCInstance)
                  }

                  // Run the "evaluate" function for all
                  // ProjectionAndTSCInstanceNodeMetricProviders on the current  projection and
                  // instance
                  metricProviders
                      .filterIsInstance<ProjectionAndTSCInstanceNodeMetricProvider<E, T, S>>()
                      .forEach { it.evaluate(projection, segmentProjectionTSCInstance) }
                }
                logFine(
                    "The evaluation of projection '${projection.id}' for segment '$segment' took: $projectionEvaluationTime")
              }
            }
            logFine(
                "The evaluation of all projections for segment '$segment' took: $projectionsEvaluationTime")
          }
          logFine("The evaluation of segment '$segment' took: $segmentEvaluationTime")
        }
      }
      logInfo("The evaluation of all segments took: $segmentsEvaluationTime")
    }
    logInfo("The whole evaluation took: $totalEvaluationTime")

    // Print the results of all Stateful metrics
    metricProviders.filterIsInstance<Stateful>().forEach { it.printState() }

    // Call the 'evaluate' and then the 'print' function for all PostEvaluationMetricProviders
    metricProviders.filterIsInstance<PostEvaluationMetricProvider<E, T, S>>().forEach {
      it.evaluate()
      it.print()
    }

    // Plot the results of all Plottable metrics
    metricProviders.filterIsInstance<Plottable>().forEach { it.plotData() }

    // Close all logging handlers to prevent .lck files to remain
    metricProviders.filterIsInstance<Loggable>().forEach { it.closeLogger() }

    closeLogger()
  }
}
