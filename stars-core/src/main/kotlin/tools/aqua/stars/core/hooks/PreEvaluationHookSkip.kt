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
 * Custom String wrapper to indicate that a [PreEvaluationHook] returned [EvaluationHookResult.SKIP]
 * .
 */
class PreEvaluationHookSkip(segment: Any, hooks: List<PreEvaluationHook<*, *, *, *, *>>) {
  /**
   * Generated message indicating that a [PreEvaluationHook] returned [EvaluationHookResult.SKIP].
   */
  val msg: String =
      "PreEvaluationHook${
        if (hooks.size == 1) {
            " " + hooks.first()
        } else {
            "s " + hooks.joinToString(separator = ",", prefix = "[", postfix = "]") { it.identifier }
        }
    } returned SKIP for segment ${segment}. Skipping current segment."

  /** Prints the [msg] to the console. */
  fun println() = println(msg)
}
