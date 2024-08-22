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

/** The result of an evaluation hook. */
enum class EvaluationHookResult {
  /** Continue with the evaluation. */
  OK,

  /** Skip this evaluation step and continue with next element. */
  SKIP,

  /** Cancel the evaluation at this point but finish post evaluation steps normally. */
  CANCEL,

  /** Abort the evaluation and throw an Exception. */
  ABORT
}
