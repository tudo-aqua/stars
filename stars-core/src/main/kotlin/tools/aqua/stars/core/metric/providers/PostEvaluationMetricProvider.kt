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

package tools.aqua.stars.core.metric.providers

import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * The [PostEvaluationMetricProvider] implements the [MetricProvider] and provides an [evaluate]
 * function which is called after the evaluation phase. It also may depend on the results of metrics
 * that evaluated during the evaluation phase.
 *
 * @see TSCEvaluation.registerMetricProviders
 */
abstract class PostEvaluationMetricProvider<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> :
    MetricProvider<E, T, S>() {
  /** Holds a reference to another metric that is evaluated during the evaluation phase. */
  abstract val dependsOn: Any?

  /** Evaluate the metric after the evaluation phase. */
  abstract fun evaluate(): Any?

  /**
   * Print the results of the [evaluate] function. This function is called after the evaluation
   * phase.
   */
  abstract fun print()

  /** Deeply copies Metric instance. */
  abstract fun copy(): PostEvaluationMetricProvider<E, T, S>
}
