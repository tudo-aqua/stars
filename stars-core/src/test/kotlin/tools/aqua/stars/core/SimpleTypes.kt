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

/** Simple entity. */
class SimpleEntity(
    override val id: Int = 0,
    override val tickData: SimpleTickData = SimpleTickData()
) : EntityType<SimpleEntity, SimpleTickData, SimpleSegment> {
  override fun toString(): String = "Entity[$id] in Tick[${tickData}]"
}

/** Simple segment. */
class SimpleSegment(
    override val tickData: List<SimpleTickData> = listOf(),
    override val ticks: Map<Double, SimpleTickData> = mapOf(),
    override val tickIDs: List<Double> = listOf(),
    override val segmentSource: String = "",
    override val firstTickId: Double = 0.0,
    override val primaryEntityId: Int = 0
) : SegmentType<SimpleEntity, SimpleTickData, SimpleSegment> {
  override fun toString(): String =
      "Segment[(${tickData.firstOrNull()}..${tickData.lastOrNull()})] with identifier: '$segmentSource'"
}

/** Simple tick data. */
class SimpleTickData(
    override val currentTick: Double = 0.0,
    override var entities: List<SimpleEntity> = listOf(),
    override var segment: SimpleSegment = SimpleSegment()
) : TickDataType<SimpleEntity, SimpleTickData, SimpleSegment> {
  override fun toString(): String = "$currentTick"
}
