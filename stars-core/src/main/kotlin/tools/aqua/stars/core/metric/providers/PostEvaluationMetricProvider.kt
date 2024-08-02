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
import tools.aqua.stars.core.types.*

/**
 * The [PostEvaluationMetricProvider] implements the [MetricProvider] and provides an [postEvaluate]
 * function which is called after the evaluation phase. It also may depend on the results of metrics
 * that evaluated during the evaluation phase.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @see TSCEvaluation.runEvaluation
 */
interface PostEvaluationMetricProvider<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> : MetricProvider<E, T, S, U, D> {

  /**
   * Holds a reference to another metric that is evaluated during the evaluation phase.
   *
   * @see TSCEvaluation.runEvaluation
   */
  val dependsOn: Any?

  /**
   * Evaluate the metric after the evaluation phase.
   *
   * @return The post evaluation result.
   * @see TSCEvaluation.runEvaluation
   */
  fun postEvaluate(): Any?

  /**
   * Print the results of the [postEvaluate] function. This function is called after the evaluation
   * phase.
   *
   * @see TSCEvaluation.runEvaluation
   */
  fun printPostEvaluationResult()
}
