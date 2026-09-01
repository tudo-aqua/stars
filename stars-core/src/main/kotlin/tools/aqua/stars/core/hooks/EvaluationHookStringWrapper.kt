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

import java.util.logging.Logger
import tools.aqua.stars.core.metrics.providers.Loggable
import tools.aqua.stars.core.utils.ApplicationConstantsHolder

/**
 * Custom String wrapper indicating that an [EvaluationHook] returned [EvaluationHookResult.SKIP].
 */
object EvaluationHookStringWrapper : Loggable {
  override val loggerIdentifier: String = "evaluation-hooks"
  override val logger: Logger = Loggable.getLogger(loggerIdentifier)

  /** Logs a message indicating that an [EvaluationHook] returned [EvaluationHookResult.OK]. */
  fun ok(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    if (hooks.isEmpty()) return

    requireNotEmpty(hooks)
    if (isLoggable(EvaluationHookResult.OK)) logInfo(createMsg(EvaluationHookResult.OK, obj, hooks))
  }

  /** Logs a message indicating that an [EvaluationHook] returned [EvaluationHookResult.SKIP]. */
  fun skip(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    requireNotEmpty(hooks)
    if (isLoggable(EvaluationHookResult.SKIP))
        logWarning("Skipping evaluation since ${createMsg(EvaluationHookResult.SKIP, obj, hooks)}")
  }

  /** Logs a message indicating that an [EvaluationHook] returned [EvaluationHookResult.CANCEL]. */
  fun cancel(obj: Any, hooks: Collection<EvaluationHook<*>>) {
    requireNotEmpty(hooks)
    if (isLoggable(EvaluationHookResult.CANCEL))
        logSevere(
            "Cancelling evaluation since ${createMsg(EvaluationHookResult.CANCEL, obj, hooks)}"
        )
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

  private fun requireNotEmpty(hooks: Collection<EvaluationHook<*>>) {
    require(hooks.isNotEmpty()) { "No hooks provided." }
  }
}
