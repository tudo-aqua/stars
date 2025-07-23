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

package tools.aqua.stars.core.hooks

import tools.aqua.stars.core.types.*

/**
 * A pre-evaluation hook that can be registered to a TSCEvaluation to be executed before the
 * evaluation of a segment of data.
 *
 * @param E [EntityDataType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param identifier The identifier to be used in the error message.
 * @param evaluationFunction The function to be executed before the evaluation of the segment of
 *   data.
 */
open class PreTickEvaluationHook<
    E : EntityDataType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    identifier: String,
    evaluationFunction: (T) -> EvaluationHookResult
) : EvaluationHook<T>(identifier = identifier, evaluationFunction = evaluationFunction) {
  companion object {
    /**
     * Executes all [PreTickEvaluationHook]s on the [List] of [TickDataType]s and returns an
     * [EvaluationHookResult] for every [PreTickEvaluationHook].
     *
     * @param E [EntityDataType].
     * @param T [TickDataType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param segment The list of [TickDataType]s to evaluate.
     */
    fun <
        E : EntityDataType<E, T, U, D>,
        T : TickDataType<E, T, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> MutableList<PreTickEvaluationHook<E, T, U, D>>.evaluate(
        tick: T
    ): Pair<Boolean?, Map<PreTickEvaluationHook<E, T, U, D>, EvaluationHookResult>> {
      val hookResults = this.associateWith { it.evaluationFunction.invoke(tick) }

      val (result, hooks) = hookResults.evaluate()
      return when (result) {
        // Abort the evaluation using a EvaluationHookAbort exception
        EvaluationHookResult.ABORT -> {
          EvaluationHookStringWrapper.abort(tick, hooks)
          null // This will not be reached, as the exception will be thrown
        }
        // Cancel the evaluation by returning false
        EvaluationHookResult.CANCEL -> {
          EvaluationHookStringWrapper.cancel(tick, hooks)
          false
        }
        // Return without evaluating the segment
        EvaluationHookResult.SKIP -> {
          EvaluationHookStringWrapper.skip(tick, hooks)
          true
        }
        // Continue with evaluation
        EvaluationHookResult.OK -> {
          null
        }
      } to hookResults
    }
  }
}
