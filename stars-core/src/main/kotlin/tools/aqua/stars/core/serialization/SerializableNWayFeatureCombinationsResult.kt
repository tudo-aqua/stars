/*
 * Copyright 2025 The STARS Project Authors
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

package tools.aqua.stars.core.serialization

import kotlinx.serialization.Serializable
import tools.aqua.stars.core.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.tsc.TSC

/**
 * A serializable data class that represents the result of generating n-way feature combinations for
 * a specific metric. It extends [SerializableResult] and provides additional attributes specific to
 * n-way combinations.
 *
 * @property identifier The identifier of this specific result.
 * @property source The source (i.e., the metric) which produced this result.
 * @property n The number of features considered in the combinations.
 * @property tsc The root node of the [TSC] that was evaluated.
 * @property seenCombinations The count of feature combinations that were observed.
 * @property possibleCombinations The total number of possible feature combinations.
 * @property value The found feature combinations.
 */
@Serializable
data class SerializableNWayFeatureCombinationsResult(
    override val identifier: String,
    override val source: String,
    val n: Int,
    val tsc: SerializableTSCNode,
    val seenCombinations: Int,
    val possibleCombinations: String,
    override val value: List<List<String>>,
) : SerializableResult()
