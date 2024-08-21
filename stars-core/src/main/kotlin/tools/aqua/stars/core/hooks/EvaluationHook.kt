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
)
