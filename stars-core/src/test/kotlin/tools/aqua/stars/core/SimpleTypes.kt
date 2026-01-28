/*
 * Copyright 2023-2026 The STARS Project Authors
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

import kotlin.sequences.Sequence
import tools.aqua.stars.core.evaluation.TickSequence
import tools.aqua.stars.core.evaluation.TickSequence.Companion.asTickSequence
import tools.aqua.stars.core.types.*

/**
 * Simple entity.
 *
 * @property id The unique identifier of the entity.
 */
class SimpleEntity(
    val id: Int = 0,
) : EntityType<SimpleEntity, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference>() {
  override fun equals(other: Any?): Boolean =
      if (other !is SimpleEntity) false else id == (other).id

  override fun hashCode(): Int = id
}

/** Simple tick data. */
class SimpleTickData(
    currentTickUnit: SimpleTickDataUnit,
    entities: Set<SimpleEntity> = LinkedHashSet(),
    identifier: String = "SimpleTickData",
) :
    TickDataType<SimpleEntity, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference>(
        currentTickUnit,
        entities,
        identifier,
    ) {

  constructor(
      tickValue: Long = 0L,
      entities: Set<SimpleEntity> = LinkedHashSet(),
      identifier: String = "SimpleTickData",
  ) : this(SimpleTickDataUnit(tickValue), entities, identifier)

  override val ego: SimpleEntity
    get() = throw UnsupportedOperationException("Ego not defined for SimpleTickData")

  override fun toString(): String = "$currentTickUnit"
}

/**
 * Simple tick data unit.
 *
 * @property tickValue The tick value in milliseconds.
 */
class SimpleTickDataUnit(val tickValue: Long) :
    TickUnit<SimpleTickDataUnit, SimpleTickDataDifference>() {
  override fun plus(other: SimpleTickDataDifference): SimpleTickDataUnit =
      SimpleTickDataUnit(tickValue + other.tickDifference)

  override fun minus(other: SimpleTickDataDifference): SimpleTickDataUnit =
      SimpleTickDataUnit(tickValue - other.tickDifference)

  override fun minus(other: SimpleTickDataUnit): SimpleTickDataDifference =
      SimpleTickDataDifference(tickValue - other.tickValue)

  override fun compareTo(other: SimpleTickDataUnit): Int = tickValue.compareTo(other.tickValue)

  override fun serialize(): String = tickValue.toString()

  override fun deserialize(str: String): SimpleTickDataUnit = SimpleTickDataUnit(str.toLong())
}

/**
 * Simple tick data difference.
 *
 * @property tickDifference The tick difference in milliseconds.
 */
class SimpleTickDataDifference(val tickDifference: Long) :
    TickDifference<SimpleTickDataDifference>() {
  override fun plus(other: SimpleTickDataDifference): SimpleTickDataDifference =
      SimpleTickDataDifference(tickDifference + other.tickDifference)

  override fun minus(other: SimpleTickDataDifference): SimpleTickDataDifference =
      SimpleTickDataDifference(tickDifference - other.tickDifference)

  override fun compareTo(other: SimpleTickDataDifference): Int =
      tickDifference.compareTo(other.tickDifference)

  override fun serialize(): String = tickDifference.toString()

  override fun deserialize(str: String): SimpleTickDataDifference =
      SimpleTickDataDifference(str.toLong())
}

/** Generates a simple tick sequence with one tick. */
fun generateTicks(): Sequence<TickSequence<SimpleTickData>> =
    sequenceOf(
        mutableListOf(
                SimpleTickData(
                    SimpleTickDataUnit(0),
                    setOf(SimpleEntity(0)),
                )
            )
            .asTickSequence(bufferSize = 1)
    )
