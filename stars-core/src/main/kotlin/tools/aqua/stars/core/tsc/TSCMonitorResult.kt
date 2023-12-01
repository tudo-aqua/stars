/*
 * Copyright 2023 The STARS Project Authors
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

package tools.aqua.stars.core.tsc

import tools.aqua.stars.core.types.SegmentType

/**
 * This class contains the validation result of all monitors for one TSCInstanceNode.
 *
 * @property segmentIdentifier Uniquely identifies the [SegmentType] from which the TSCInstanceNode
 * results.
 * @property monitorsValid Flags whether there is an invalid monitor.
 * @property edgeList Default: null. When there is an invalid monitor, it contains the [List] of
 * edge labels leading to the invalid monitor.
 */
class TSCMonitorResult(
    val segmentIdentifier: String,
    var monitorsValid: Boolean,
    var edgeList: List<String>? = null
)
