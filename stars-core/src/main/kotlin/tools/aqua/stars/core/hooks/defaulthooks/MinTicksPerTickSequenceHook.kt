/*
 * Copyright 2023-2025 The STARS Project Authors
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

package tools.aqua.stars.core.hooks.defaulthooks

import tools.aqua.stars.core.hooks.EvaluationHookResult
import tools.aqua.stars.core.hooks.PreTickEvaluationHook
import tools.aqua.stars.core.types.*

/**
 * [PreTickEvaluationHook] that checks if a tick contains at least [minTicks] [TickDataType]s.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param minTicks The minimum number of [TickDataType]s a tick must contain.
 * @param failPolicy The [EvaluationHookResult] to return if the tick contains less than [minTicks]
 *   [TickDataType]s.
 */
open class MinTicksPerTickSequenceHook<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    minTicks: Int,
    failPolicy: EvaluationHookResult = EvaluationHookResult.SKIP,
) :
    PreTickEvaluationHook<E, T, U, D>(
        identifier = "MinTicksPerTickSequenceHook",
        evaluationFunction = { tick ->
          if (tick.sequenceLength >= minTicks) EvaluationHookResult.OK else failPolicy
        },
    ) {
  init {
    require(minTicks >= 0) { "minTicks must be >= 0" }
  }
}
