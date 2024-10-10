/*
 * Copyright 2024 The STARS Project Authors
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
 * evaluation of the [SegmentType].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param identifier The identifier to be used in the error message.
 * @param evaluationFunction The function to be executed before the evaluation of the [SegmentType].
 */
open class PreSegmentEvaluationHook<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(identifier: String, evaluationFunction: (S) -> EvaluationHookResult) :
    EvaluationHook<S>(identifier = identifier, evaluationFunction = evaluationFunction) {
  companion object {
    /**
     * Executes all [PreSegmentEvaluationHook]s on the [segment].
     *
     * @param E [EntityType].
     * @param T [TickDataType].
     * @param S [SegmentType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param segment The segment to evaluate.
     * @return `true` if the segment should be skipped, `false` if the evaluation should be
     *   canceled, `null` if the evaluation should continue normally.
     */
    fun <
        E : EntityType<E, T, S, U, D>,
        T : TickDataType<E, T, S, U, D>,
        S : SegmentType<E, T, S, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> MutableList<PreSegmentEvaluationHook<E, T, S, U, D>>.evaluate(
        segment: S
    ): Pair<Boolean?, Map<PreSegmentEvaluationHook<E, T, S, U, D>, EvaluationHookResult>> {
      val hookResults = this.associateWith { it.evaluationFunction.invoke(segment) }

      val (result, hooks) = hookResults.evaluate()
      return when (result) {
        // Abort the evaluation using a EvaluationHookAbort exception
        EvaluationHookResult.ABORT -> {
          EvaluationHookStringWrapper.abort(segment, hooks)
          null
        }
        // Cancel the evaluation by returning false
        EvaluationHookResult.CANCEL -> {
          EvaluationHookStringWrapper.cancel(segment, hooks)
          false
        }
        // Return without evaluating the segment
        EvaluationHookResult.SKIP -> {
          EvaluationHookStringWrapper.skip(segment, hooks)
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
