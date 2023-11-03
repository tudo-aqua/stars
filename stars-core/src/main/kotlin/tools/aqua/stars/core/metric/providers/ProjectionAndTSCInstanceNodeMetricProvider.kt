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

package tools.aqua.stars.core.metric.providers

import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.tsc.TSCInstance
import tools.aqua.stars.core.tsc.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * The [ProjectionAndTSCInstanceNodeMetricProvider] implements the [EvaluationMetricProvider] and
 * provides an [evaluate] function which gets a [TSCProjection] and a [TSCInstance] which is called
 * during the evaluation phase.
 *
 * @see TSCEvaluation.runEvaluation
 */
interface ProjectionAndTSCInstanceNodeMetricProvider<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> :
    EvaluationMetricProvider<E, T, S> {

  /**
   * Evaluate the metric based on the given parameters.
   *
   * @param projection The current [TSCProjection]
   * @param tscInstance The current [TSCInstance]
   */
  fun evaluate(projection: TSCProjection<E, T, S>, tscInstance: TSCInstance<E, T, S>)
}
