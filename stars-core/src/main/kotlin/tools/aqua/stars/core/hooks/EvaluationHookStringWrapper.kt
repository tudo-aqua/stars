/*
 * Copyright 2023-2026 The STARS Project Authors
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

import tools.aqua.stars.core.utils.ApplicationConstantsHolder

/**
 * Custom String wrapper indicating that an [EvaluationHook] returned [EvaluationHookResult.SKIP].
 */
object EvaluationHookStringWrapper {
  /**
   * Prints a message indicating that an [EvaluationHook] returned [EvaluationHookResult.OK] to
   * the console.
   */
  fun ok(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    require(hooks.isNotEmpty()) { "No hooks provided." }
    if (isLoggable(EvaluationHookResult.OK)) println(createMsg(EvaluationHookResult.OK, obj, hooks))
  }

  /**
   * Prints a message indicating that an [EvaluationHook] returned [EvaluationHookResult.SKIP] to
   * the console.
   */
  fun skip(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    require(hooks.isNotEmpty()) { "No hooks provided." }
    if (isLoggable(EvaluationHookResult.SKIP))
        println("Skipping evaluation since ${createMsg(EvaluationHookResult.SKIP, obj, hooks)}")
  }

  /**
   * Prints a message indicating that an [EvaluationHook] returned [EvaluationHookResult.CANCEL] to
   * the console.
   */
  fun cancel(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    require(hooks.isNotEmpty()) { "No hooks provided." }
    if (isLoggable(EvaluationHookResult.CANCEL))
        println("Cancelling evaluation since ${createMsg(EvaluationHookResult.CANCEL, obj, hooks)}")
  }

  /**
   * Throws a [EvaluationHookAbort] indicating that an [EvaluationHook] returned
   * [EvaluationHookResult.ABORT].
   */
  fun abort(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    throw EvaluationHookAbort(
        "Aborting evaluation since ${createMsg(EvaluationHookResult.ABORT, obj, hooks)}"
    )
  }

  private fun createMsg(
      hookResult: EvaluationHookResult,
      obj: Any,
      hooks: Collection<EvaluationHook<*>>,
  ) =
      "${hooks.joinToString(
        separator = ", ",
        prefix = "[",
        postfix = "]",
      ) { "${it.javaClass.name} ${it.identifier}" }
    } returned $hookResult for ${obj.javaClass.name} \n ${obj}."

  private fun isLoggable(result: EvaluationHookResult): Boolean =
      result.ordinal >= ApplicationConstantsHolder.evaluationHookLogLevel.ordinal
}
