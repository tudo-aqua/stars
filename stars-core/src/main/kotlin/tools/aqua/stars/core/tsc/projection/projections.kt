/*
 * Copyright 2023-2024 The STARS Project Authors
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

@file:Suppress("unused")

package tools.aqua.stars.core.tsc.projection

/**
 * Projection to [Pair] ([id], 'false'). This is used to not propagate the [TSCProjection]
 * calculation further down the tree.
 *
 * @return ([id], 'false')
 */
fun proj(id: Any): Pair<Any, Boolean> = Pair(id, false)

/**
 * Projection to [Pair] ([id], 'true'). This is used to propagate the [TSCProjection] calculation
 * further down the tree.
 *
 * @return ([id], 'true')
 */
fun projRec(id: Any): Pair<Any, Boolean> = Pair(id, true)
