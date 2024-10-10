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

import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.types.*

/**
 * A pre-evaluation hook that can be registered to a TSCEvaluation to be executed before the
 * evaluation of the [TSC].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param identifier The identifier to be used in the error message.
 * @param evaluationFunction The function to be executed before the evaluation of the [TSC].
 */
open class PreTSCEvaluationHook<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    identifier: String,
    evaluationFunction: (TSC<E, T, S, U, D>) -> EvaluationHookResult
) :
    EvaluationHook<TSC<E, T, S, U, D>>(
        identifier = identifier, evaluationFunction = evaluationFunction) {
  companion object {
    /**
     * Executes all [PreTSCEvaluationHook]s on the [tscList] and returns all passing TSCs.
     *
     * @param E [EntityType].
     * @param T [TickDataType].
     * @param S [SegmentType].
     * @param U [TickUnit].
     * @param D [TickDifference].
     * @param tscList The list of TSCs to evaluate.
     */
    fun <
        E : EntityType<E, T, S, U, D>,
        T : TickDataType<E, T, S, U, D>,
        S : SegmentType<E, T, S, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> List<PreTSCEvaluationHook<E, T, S, U, D>>.evaluate(
        tscList: List<TSC<E, T, S, U, D>>
    ): Pair<
        List<TSC<E, T, S, U, D>>?,
        Map<TSC<E, T, S, U, D>, Map<PreTSCEvaluationHook<E, T, S, U, D>, EvaluationHookResult>>> {
      // Evaluate PreEvaluationHooks
      val hookResults =
          tscList.associateWith { tsc -> this.associateWith { it.evaluationFunction.invoke(tsc) } }

      // Filter out all TSCs that have not returned OK. Do not optimize by using
      // preTSCEvaluationHookResults, since runEvaluation may be called multiple times.
      val resultingList = mutableListOf<TSC<E, T, S, U, D>>()
      hookResults.forEach { (tsc, results) ->
        val (result, hooks) = results.evaluate()
        when (result) {
          // Abort evaluation using a EvaluationHookAbort exception
          EvaluationHookResult.ABORT -> {
            EvaluationHookStringWrapper.abort(tsc, hooks)
          }
          // Cancel evaluation by returning
          EvaluationHookResult.CANCEL -> {
            EvaluationHookStringWrapper.cancel(tsc, hooks)
            return null to hookResults
          }
          // Don't include current TSC in the list
          EvaluationHookResult.SKIP -> {
            EvaluationHookStringWrapper.skip(tsc, hooks)
          }
          // Include current TSC in the list
          EvaluationHookResult.OK -> {
            resultingList.add(tsc)
          }
        }
      }
      return resultingList to hookResults
    }
  }
}
