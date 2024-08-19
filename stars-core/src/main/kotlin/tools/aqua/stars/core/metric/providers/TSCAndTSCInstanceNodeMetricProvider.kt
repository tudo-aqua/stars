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
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.types.*

/**
 * The [TSCAndTSCInstanceNodeMetricProvider] implements the [EvaluationMetricProvider] and provides
 * an [evaluate] function which gets a [TSC] and a [TSCInstance] which is called during the
 * evaluation phase.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @see TSCEvaluation.runEvaluation
 */
interface TSCAndTSCInstanceNodeMetricProvider<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> : EvaluationMetricProvider<E, T, S, U, D> {

  /**
   * Evaluate the metric based on the given parameters.
   *
   * @param tsc The current [TSC].
   * @param tscInstance The current [TSCInstance].
   */
  fun evaluate(tsc: TSC<E, T, S, U, D>, tscInstance: TSCInstance<E, T, S, U, D>)
}
