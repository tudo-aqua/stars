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

package tools.aqua.stars.core

import tools.aqua.stars.core.types.*

/** Simple entity. */
class SimpleEntity(
    override val id: Int = 0,
    override val tickData: SimpleTickData = SimpleTickData()
) :
    EntityType<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> {
  override fun toString(): String = "Entity[$id] in Tick[${tickData}]"
}

/** Simple segment. */
class SimpleSegment(
    override val ticks: Map<SimpleTickDataUnit, SimpleTickData> = mapOf(),
    override val segmentSource: String = "",
    override val primaryEntityId: Int = 0
) :
    SegmentType<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> {
  override fun toString(): String =
      "Segment[(${tickData.firstOrNull()}..${tickData.lastOrNull()})] with identifier: '$segmentSource'"

  override fun getSegmentIdentifier(): String = this.toString()
}

/** Simple tick data. */
class SimpleTickData(
    override val currentTick: SimpleTickDataUnit = SimpleTickDataUnit(0),
    override var entities: List<SimpleEntity> = listOf(),
    override var segment: SimpleSegment = SimpleSegment()
) :
    TickDataType<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> {
  override fun toString(): String = "$currentTick"
}

/**
 * Simple tick data unit.
 *
 * @property tickValue The tick value in milliseconds.
 */
class SimpleTickDataUnit(val tickValue: Long) :
    TickUnit<SimpleTickDataUnit, SimpleTickDataDifference> {
  override fun compareTo(other: SimpleTickDataUnit): Int = tickValue.compareTo(other.tickValue)

  override fun minus(other: SimpleTickDataDifference): SimpleTickDataUnit =
      SimpleTickDataUnit(tickValue - other.tickDifference)

  override fun minus(other: SimpleTickDataUnit): SimpleTickDataDifference =
      SimpleTickDataDifference(tickValue - other.tickValue)

  override fun plus(other: SimpleTickDataDifference): SimpleTickDataUnit =
      SimpleTickDataUnit(tickValue + other.tickDifference)
}

/**
 * Simple tick data difference.
 *
 * @property tickDifference The tick difference in milliseconds.
 */
class SimpleTickDataDifference(val tickDifference: Long) :
    TickDifference<SimpleTickDataDifference> {
  override fun compareTo(other: SimpleTickDataDifference): Int =
      tickDifference.compareTo(other.tickDifference)

  override fun plus(other: SimpleTickDataDifference): SimpleTickDataDifference =
      SimpleTickDataDifference(tickDifference + other.tickDifference)

  override fun minus(other: SimpleTickDataDifference): SimpleTickDataDifference =
      SimpleTickDataDifference(tickDifference - other.tickDifference)

  override fun serialize(): String = tickDifference.toString()

  override fun deserialize(str: String): SimpleTickDataDifference =
      SimpleTickDataDifference(str.toLong())
}
