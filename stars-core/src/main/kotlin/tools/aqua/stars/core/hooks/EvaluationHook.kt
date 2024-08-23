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

/**
 * Base class for evaluation hooks that can be registered to a TSCEvaluation to be executed before
 * the evaluation.
 *
 * @param T The type of the parameter to the [evaluationFunction].
 * @property identifier The identifier to be used in the error message. *
 * @property evaluationFunction The function to be executed before the evaluation.
 */
sealed class EvaluationHook<T>(
    val identifier: String,
    val evaluationFunction: (T) -> EvaluationHookResult
) {
  companion object {
    /**
     * Evaluates given the results map by grouping them by [EvaluationHookResult] and returning the
     * most severe result.
     */
    fun <T : EvaluationHook<*>> Map<T, EvaluationHookResult>.evaluate():
        Pair<EvaluationHookResult, Collection<EvaluationHook<*>>> {
      val groupedResults = this.toList().groupBy({ it.second }, { it.first })

      // Abort the evaluation and throw exception if any hook returns ABORT
      val abortingHooks = groupedResults[EvaluationHookResult.ABORT] ?: emptyList()
      if (abortingHooks.isNotEmpty()) return Pair(EvaluationHookResult.ABORT, abortingHooks)

      // Cancel the evaluation if any hook returns CANCEL
      val cancelingHooks = groupedResults[EvaluationHookResult.CANCEL] ?: emptyList()
      if (cancelingHooks.isNotEmpty()) return Pair(EvaluationHookResult.CANCEL, cancelingHooks)

      // Skip all TSCs that have a hook returning SKIP
      val skippingHooks = groupedResults[EvaluationHookResult.SKIP] ?: emptyList()
      if (skippingHooks.isNotEmpty()) return Pair(EvaluationHookResult.SKIP, skippingHooks)

      return Pair(EvaluationHookResult.OK, this.keys)
    }
  }
}
