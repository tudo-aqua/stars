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

package tools.aqua.stars.core.metric.serialization

import kotlinx.serialization.Serializable
import tools.aqua.stars.core.metric.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.tsc.TSCFailedMonitorInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.SegmentType

/**
 * This is a serializable wrapper class for [TSCFailedMonitorInstance].
 *
 * @property segmentIdentifier Uniquely identifies the [SegmentType] from which the TSCInstanceNode
 *   results.
 * @property tscInstance The root [TSCInstanceNode] on which the monitor failed.
 * @property monitorLabel The label of the monitor that failed.
 * @property nodeLabel Specifies the [TSCInstanceNode] at which a monitor failed.
 */
@Serializable
data class SerializableFailedMonitorInstance(
    val segmentIdentifier: String,
    var tscInstance: SerializableTSCNode,
    val monitorLabel: String,
    var nodeLabel: String,
)
