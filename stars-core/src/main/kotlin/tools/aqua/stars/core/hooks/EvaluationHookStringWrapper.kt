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
 * Custom String wrapper indicating that an [EvaluationHook] returned [EvaluationHookResult.SKIP].
 */
object EvaluationHookStringWrapper {
  /**
   * Prints a message indicating that an [EvaluationHook] returned [EvaluationHookResult.SKIP] to
   * the console.
   */
  fun skip(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    require(hooks.isNotEmpty()) { "No hooks provided." }
    println(createMsg(EvaluationHookResult.SKIP, obj, hooks))
  }

  /**
   * Prints a message indicating that an [EvaluationHook] returned [EvaluationHookResult.CANCEL] to
   * the console.
   */
  fun cancel(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    require(hooks.isNotEmpty()) { "No hooks provided." }
    println(createMsg(EvaluationHookResult.CANCEL, obj, hooks))
  }

  /**
   * Throws a [EvaluationHookAbort] indicating that an [EvaluationHook] returned
   * [EvaluationHookResult.ABORT].
   */
  fun abort(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    throw EvaluationHookAbort(createMsg(EvaluationHookResult.ABORT, obj, hooks))
  }

  private fun createMsg(
      hookResult: EvaluationHookResult,
      obj: Any,
      hooks: Collection<EvaluationHook<*>>
  ) =
      "$hookResult evaluation since ${
      hooks.joinToString(
        separator = ", ",
        prefix = "[",
        postfix = "]"
      ) { "${it.javaClass.name} ${it.identifier}" }
    } returned $hookResult for ${obj.javaClass.name} \n ${obj}."
}
