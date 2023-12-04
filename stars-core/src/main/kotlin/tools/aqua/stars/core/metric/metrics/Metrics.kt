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

package tools.aqua.stars.core.metric.metrics

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

class Metrics<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    private val segmentMetrics: MutableList<SegmentMetricProvider<E, T, S>> = mutableListOf(),
    private val projectionMetrics: MutableList<ProjectionMetricProvider<E, T, S>> = mutableListOf(),
    private val tscInstanceMetrics: MutableList<TSCInstanceMetricProvider<E, T, S>> =
        mutableListOf(),
    private val tscInstanceAndProjectionMetrics:
        MutableList<TSCInstanceAndProjectionNodeMetricProvider<E, T, S>> =
        mutableListOf(),
    private val postEvaluationMetrics: MutableList<PostEvaluationMetricProvider<E, T, S>> =
        mutableListOf()
) {

  /** Returns all registered[MetricProvider]s. */
  fun all(): List<MetricProvider<E, T, S>> =
      segmentMetrics +
          projectionMetrics +
          tscInstanceMetrics +
          tscInstanceAndProjectionMetrics +
          postEvaluationMetrics

  /** Returns 'true' if at least one metric has been registered. */
  fun any(): Boolean = all().isNotEmpty()

  /** Registers metric providers. */
  fun register(vararg metrics: MetricProvider<E, T, S>) {
    metrics.forEach {
      when (it) {
        is SegmentMetricProvider -> segmentMetrics.add(it)
        is ProjectionMetricProvider -> projectionMetrics.add(it)
        is TSCInstanceMetricProvider -> tscInstanceMetrics.add(it)
        is TSCInstanceAndProjectionNodeMetricProvider -> tscInstanceAndProjectionMetrics.add(it)
        is PostEvaluationMetricProvider -> postEvaluationMetrics.add(it)
      }
    }
  }

  /**
   * Run the "evaluate" function for all [SegmentMetricProvider]s on the current [segment].
   *
   * @param segment The current [SegmentType].
   */
  fun evaluateSegmentMetrics(segment: SegmentType<E, T, S>) {
    segmentMetrics.forEach { it.evaluate(segment) }
  }

  /**
   * Run the "evaluate" function for all [ProjectionMetricProvider]s on the current [projection].
   *
   * @param projection The current [TSCProjection].
   */
  fun evaluateProjectionMetrics(projection: TSCProjection<E, T, S>) {
    projectionMetrics.forEach { it.evaluate(projection) }
  }

  /**
   * Run the "evaluate" function for all [TSCInstanceMetricProvider]s on the current [instance].
   *
   * @param instance The current [TSCInstance].
   */
  fun evaluateTSCInstanceMetrics(instance: TSCInstance<E, T, S>) {
    tscInstanceMetrics.forEach { it.evaluate(instance) }
  }

  /**
   * Run the "evaluate" function for all [TSCInstanceAndProjectionNodeMetricProvider]s on the
   * current [instance] and [projection].
   *
   * @param instance The current [TSCInstance].
   * @param projection The current [TSCProjection].
   */
  fun evaluateTSCInstanceAndProjectionMetrics(
      instance: TSCInstance<E, T, S>,
      projection: TSCProjection<E, T, S>
  ) {
    tscInstanceAndProjectionMetrics.forEach { it.evaluate(instance, projection) }
  }

  /** Run the "evaluate" function for all [PostEvaluationMetricProvider]s. */
  fun evaluatePostEvaluationMetrics() {
    postEvaluationMetrics.forEach { it.evaluate() }
  }

  /** Print the results of all Stateful metrics. */
  fun printState() {
    all().filterIsInstance<Stateful>().forEach { it.printState() }
  }

  /** Plot the results of all Plottable metrics. */
  fun plotData() {
    all().filterIsInstance<Plottable>().let { plottables ->
      runBlocking { plottables.forEach { launch { it.plotData() } } }
    }
  }

  /** Close all logging handlers to prevent .lck files to remain. */
  fun close() {
    all().filterIsInstance<Loggable>().forEach { it.closeLogger() }
  }

  /** Deeply copies the [Metrics] object resetting all saved instances. */
  fun copy(): Metrics<E, T, S> =
      Metrics(
          segmentMetrics.map { it.copy() }.toMutableList(),
          projectionMetrics.map { it.copy() }.toMutableList(),
          tscInstanceMetrics.map { it.copy() }.toMutableList(),
          tscInstanceAndProjectionMetrics.map { it.copy() }.toMutableList(),
          postEvaluationMetrics.map { it.copy() }.toMutableList())

  companion object {
    fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> merge(
        metrics: List<Metrics<E, T, S>>
    ): Metrics<E, T, S> {
      TODO()
    }
  }
}
