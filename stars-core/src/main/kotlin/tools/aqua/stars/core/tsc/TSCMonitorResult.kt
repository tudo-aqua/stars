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

package tools.aqua.stars.core.tsc

import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * This class contains the validation result of all monitors for one TSCInstanceNode.
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property segmentIdentifier Uniquely identifies the [SegmentType] from which the TSCInstanceNode
 * results.
 * @property tscInstance The root [TSCInstanceNode] on which the monitor failed.
 * @property edgeList (Default: null) When there is an invalid monitor, it contains the [List] of
 * edge labels leading to the invalid monitor.
 * @property monitorsValid Flags whether there is an invalid monitor.
 */
class TSCMonitorResult<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val segmentIdentifier: String,
    var tscInstance: TSCInstanceNode<E, T, S, U, D>,
    var edgeList: List<String>? = null,
    var monitorsValid: Boolean,
)
