/*
 * Copyright 2023 The STARS Project Authors
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

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.TSCInstanceNode
import tools.aqua.stars.core.tsc.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * This class holds every important data to evaluate a given TSC. The [TSCEvaluation.runEvaluation]
 * function evaluates the [tsc] based on the given [segments]. The [TSCProjection]s are filtered by
 * the [projectionIgnoreList].
 */
@OptIn(ExperimentalTime::class)
class TSCEvaluation<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    private val tsc: TSC<E, T, S>,
    private val segments: Sequence<SegmentType<E, T, S>>,
    private val projectionIgnoreList: List<String> = listOf(),
    private val evaluationTimeEnabled: Boolean = true
) {
  private val metricProviders: MutableList<MetricProvider<E, T, S>> = mutableListOf()

  /**
   * Register a new [MetricProvider] to the list of metrics that should be called during evaluation
   *
   * @param metricProvider The [MetricProvider] that should be registered
   */
  fun registerMetricProvider(metricProvider: MetricProvider<E, T, S>) {
    metricProviders.add(metricProvider)
  }

  /**
   * Runs the evaluation of the [TSC] based on the [segments]. For each [SegmentType],
   * [TSCProjection] and [TSCInstanceNode], the related [MetricProvider] is called. It requires at
   * least one [MetricProvider].
   *
   * @throws IllegalArgumentException When there are no [MetricProvider]s registered
   */
  fun runEvaluation() {
    require(metricProviders.any()) { "There needs to be at least one registered MetricProviders." }
    val totalEvaluationTime = measureTime {
      /** Holds the [List] of [TSCProjection] based on the base [tsc]. */
      var tscProjections: List<TSCProjection<E, T, S>>
      val tscProjectionCalculationTime = measureTime {
        // Build all projections of the base TSC
        tscProjections = tsc.buildProjections(projectionIgnoreList)
      }
      if (evaluationTimeEnabled)
          println(
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
                  /** Holds the PredicateContext for the current segment */
                  val context = PredicateContext(segment as S)
                  /**
                   * Holds the [TSCInstanceNode] of the current [projection] using the
                   * [PredicateContext], representing a whole TSC
                   */
                  val segmentProjectionTSCInstance = projection.tsc.evaluate(context)
                  // Run the "evaluate" function for all ProjectionMetricProviders on the current
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
                if (evaluationTimeEnabled)
                    println(
                        "The evaluation of projection '${projection.id}' for segment '$segment' took: $projectionEvaluationTime")
              }
            }
            if (evaluationTimeEnabled)
                println(
                    "The evaluation of all projections for segment '$segment' took: $projectionsEvaluationTime")
          }
          if (evaluationTimeEnabled)
              println("The evaluation of segment '$segment' took: $segmentEvaluationTime")
        }
      }
      if (evaluationTimeEnabled)
          println("The evaluation of all segments took: $segmentsEvaluationTime")
    }
    if (evaluationTimeEnabled) println("The whole evaluation took: $totalEvaluationTime")
    // Print the results of all Stateful metrics
    metricProviders.filterIsInstance<Stateful>().forEach { it.printState() }
    // Call the 'evaluate' function for all PostEvaluationMetricProviders
    metricProviders.filterIsInstance<PostEvaluationMetricProvider<E, T, S>>().forEach { it.print() }
  }
}
