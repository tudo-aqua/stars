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
import tools.aqua.stars.core.hooks.PreSegmentEvaluationHook
import tools.aqua.stars.core.types.*

/**
 * [PreSegmentEvaluationHook] that checks if a segment contains at least [minEntities] [EntityType]s in
 * every tick.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param minEntities The minimum number of [EntityType]s each tick must contain. Must not be negative.
 * @param failPolicy The [EvaluationHookResult] to return if the minimum number of [EntityType]s is
 *   not reached.
 */
open class MinEntitiesPerSegmentHook<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    minEntities: Int,
    failPolicy: EvaluationHookResult = EvaluationHookResult.SKIP,
) :
    PreSegmentEvaluationHook<E, T, U, D>(
        identifier = "MinEntitiesPerSegmentHook",
        evaluationFunction = { segment ->
          if (segment.all { it.entities.size >= minEntities }) EvaluationHookResult.OK
          else failPolicy
        }) {
  init {
    require(minEntities >= 0) { "minEntities must be >= 0" }
  }
}
