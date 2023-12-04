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

import kotlinx.coroutines.*
import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/** Wrapper class for [EvaluationMetricProvider]s. */
class EvaluationMetrics<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    private val segmentMetrics:
        MutableMap<Class<SegmentMetricProvider<E, T, S>>, SegmentMetricProvider<E, T, S>> =
        mutableMapOf(),
    private val projectionMetrics:
        MutableMap<Class<ProjectionMetricProvider<E, T, S>>, ProjectionMetricProvider<E, T, S>> =
        mutableMapOf(),
    private val tscInstanceMetrics:
        MutableMap<Class<TSCInstanceMetricProvider<E, T, S>>, TSCInstanceMetricProvider<E, T, S>> =
        mutableMapOf(),
    private val tscInstanceAndProjectionMetrics:
        MutableMap<
            Class<TSCInstanceAndProjectionNodeMetricProvider<E, T, S>>,
            TSCInstanceAndProjectionNodeMetricProvider<E, T, S>> =
        mutableMapOf()
) : Metrics<EvaluationMetricProvider<E, T, S>, E, T, S>() {

  /** Returns all registered [EvaluationMetricProvider]s. */
  override fun all():
      Map<Class<out EvaluationMetricProvider<E, T, S>>, EvaluationMetricProvider<E, T, S>> =
      segmentMetrics + projectionMetrics + tscInstanceMetrics + tscInstanceAndProjectionMetrics

  /** Registers metric providers. */
  fun register(metrics: List<EvaluationMetricProvider<E, T, S>>) {
    metrics.forEach {
      when (it) {
        is SegmentMetricProvider -> segmentMetrics[it.javaClass] = it
        is ProjectionMetricProvider -> projectionMetrics[it.javaClass] = it
        is TSCInstanceMetricProvider -> tscInstanceMetrics[it.javaClass] = it
        is TSCInstanceAndProjectionNodeMetricProvider ->
            tscInstanceAndProjectionMetrics[it.javaClass] = it
      }
    }
  }

  /**
   * Run the "evaluate" function for all [SegmentMetricProvider]s on the current [segment].
   *
   * @param segment The current [SegmentType].
   */
  fun evaluateSegmentMetrics(segment: SegmentType<E, T, S>) {
    segmentMetrics.values.forEach { it.evaluate(segment) }
  }

  /**
   * Run the "evaluate" function for all [ProjectionMetricProvider]s on the current [projection].
   *
   * @param projection The current [TSCProjection].
   */
  fun evaluateProjectionMetrics(projection: TSCProjection<E, T, S>) {
    projectionMetrics.values.forEach { it.evaluate(projection) }
  }

  /**
   * Run the "evaluate" function for all [TSCInstanceMetricProvider]s on the current [instance].
   *
   * @param instance The current [TSCInstance].
   */
  fun evaluateTSCInstanceMetrics(instance: TSCInstance<E, T, S>) {
    tscInstanceMetrics.values.forEach { it.evaluate(instance) }
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
    tscInstanceAndProjectionMetrics.values.forEach { it.evaluate(instance, projection) }
  }

  /** Deeply copies the [EvaluationMetrics] object resetting all saved instances. */
  override fun copy(): EvaluationMetrics<E, T, S> =
      EvaluationMetrics(
          segmentMetrics.map { (k, v) -> Pair(k, v.copy()) }.toMap(mutableMapOf()),
          projectionMetrics.map { (k, v) -> Pair(k, v.copy()) }.toMap(mutableMapOf()),
          tscInstanceMetrics.map { (k, v) -> Pair(k, v.copy()) }.toMap(mutableMapOf()),
          tscInstanceAndProjectionMetrics.map { (k, v) -> Pair(k, v.copy()) }.toMap(mutableMapOf()))

  companion object {
    /**
     * Merges given list of [EvaluationMetrics] in parallel. Returns a new [EvaluationMetrics]
     * instance.
     */
    fun <E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> merge(
        metrics: List<EvaluationMetrics<E, T, S>>
    ): EvaluationMetrics<E, T, S> =
        EvaluationMetrics<E, T, S>().apply {
          register(
              runBlocking {
                metrics
                    // Receive all provider maps
                    .map { it.all() }
                    .asSequence()
                    // Flatmap and group to one map
                    .flatMap { it.asSequence() }
                    .groupBy({ it.key }, { it.value })
                    // Create async jobs
                    .map { async { combineResults(it.value.toMutableList()) } }
                    .awaitAll() // Await completion
              })
        }

    private fun <
        K : EvaluationMetricProvider<E, T, S>,
        E : EntityType<E, T, S>,
        T : TickDataType<E, T, S>,
        S : SegmentType<E, T, S>> combineResults(providers: MutableList<K>): K {
      check(providers.isNotEmpty()) { "Empty list of metric providers encountered." }

      val instances = providers.toMutableList()
      val instance = instances.removeFirst()

      instances.forEach { instance.merge(it) }

      return instance
    }
  }
}
