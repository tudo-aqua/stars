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

package tools.aqua.stars.core.metric.metrics

import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/** Wrapper class for [MetricProvider]s. */
class PostEvaluationMetrics<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    private val metrics:
        MutableMap<
            Class<PostEvaluationMetricProvider<E, T, S>>, PostEvaluationMetricProvider<E, T, S>> =
        mutableMapOf()
) : Metrics<PostEvaluationMetricProvider<E, T, S>, E, T, S>() {

  /** Registers metric providers. */
  fun register(metrics: List<PostEvaluationMetricProvider<E, T, S>>) {
    metrics.forEach { this.metrics[it.javaClass] = it }
  }

  override fun all():
      Map<Class<out PostEvaluationMetricProvider<E, T, S>>, PostEvaluationMetricProvider<E, T, S>> =
      metrics.toMap()

  /** Run the "evaluate" function for all [PostEvaluationMetricProvider]s. */
  fun evaluate() {
    metrics.values.forEach { it.evaluate() }
  }

  /** Deeply copies the [PostEvaluationMetrics] object resetting all saved instances. */
  override fun copy(): PostEvaluationMetrics<E, T, S> =
      PostEvaluationMetrics(metrics.map { (k, v) -> Pair(k, v.copy()) }.toMap(mutableMapOf()))
}
