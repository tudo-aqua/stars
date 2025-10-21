/*
 * Copyright 2023-2025 The STARS Project Authors
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

package tools.aqua.stars.core.serialization.tsc

import kotlinx.serialization.Serializable
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.instance.TSCInstance

/**
 * This class stores a pair of the [TSCInstance] as [String] and a [List] of identifiers in which
 * the [TSCInstance] occurred for serialization.
 *
 * @property tscInstance The [List] of active [TSCEdge]s.
 * @property identifiers The [List] of identifiers in which the [TSCInstance] occurred.
 */
@Serializable
data class SerializableTSCOccurrence(
    val tscInstance: SerializableTSCNode,
    val identifiers: List<String>,
)
