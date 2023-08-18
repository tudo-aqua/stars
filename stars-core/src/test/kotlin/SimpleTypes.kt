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

package tools.aqua.stars.core

import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

class SimpleEntity(override val id: Int, override val tickData: SimpleTickData) :
    EntityType<SimpleEntity, SimpleTickData, SimpleSegment>

class SimpleSegment(
    override val tickData: List<SimpleTickData>,
    override val ticks: Map<Double, SimpleTickData>,
    override val tickIDs: List<Double>,
    override val segmentIdentifier: String,
    override val firstTickId: Double,
    override val primaryEntityId: Int
) : SegmentType<SimpleEntity, SimpleTickData, SimpleSegment>

class SimpleTickData(
    override val currentTick: Double,
    override var entities: List<SimpleEntity>,
    override var segment: SimpleSegment
) : TickDataType<SimpleEntity, SimpleTickData, SimpleSegment>
