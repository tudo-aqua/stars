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

package tools.aqua.stars.core.metrics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.core.*
import tools.aqua.stars.core.evaluation.TickSequence.Companion.asTickSequence
import tools.aqua.stars.core.metrics.evaluation.TotalTickDifferenceMetric
import tools.aqua.stars.core.types.TickUnit

/** Test for [TotalTickDifferenceMetric]. */
class TotalTickDifferenceMetricTest {

  /** Test only one tick. */
  @Test
  fun `Test only one tick`() {
    val simpleTick = SimpleTickData()
    val tickSequence = listOf(simpleTick).asTickSequence(name = "Simple Tick Sequence")

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    val evaluationResult = totalTickDifferenceMetric.evaluate(simpleTick, tickSequence)
    assertEquals(1, evaluationResult.size)
    assertEquals(tickSequence.name, evaluationResult.first().first)
    assertEquals(null, evaluationResult.first().second)
  }

  /** Test two different ticks with increasing [TickUnit]s. */
  @Test
  fun `Test two different ticks with increasing TickDataUnits`() {
    val simpleTick1 = SimpleTickData(1)
    val simpleTick2 = SimpleTickData(2)
    val tickSequence2 =
        listOf(simpleTick1, simpleTick2).asTickSequence(name = "Simple Tick Sequence")

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    val result1 = totalTickDifferenceMetric.evaluate(simpleTick1, tickSequence2)
    assertEquals(1, result1.size)
    assertEquals(tickSequence2.name, result1.first().first)
    assertEquals(null, result1.first().second)

    totalTickDifferenceMetric.evaluate(simpleTick1, tickSequence2)
    val result2 = totalTickDifferenceMetric.evaluate(simpleTick2, tickSequence2)
    assertEquals(1, result2.size)
    assertEquals(tickSequence2.name, result2.first().first)
    assertEquals(1L, result2.first().second?.tickDifference)
  }

  /** Test two different ticks with decreasing [TickUnit]s. */
  @Test
  fun `Test two different ticks with decreasing TickDataUnits`() {
    val simpleTick1 = SimpleTickData(2)
    val simpleTick2 = SimpleTickData(1)
    val tickSequence =
        listOf(simpleTick1, simpleTick2).asTickSequence(name = "Simple Tick Sequence")

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    val result1 = totalTickDifferenceMetric.evaluate(simpleTick1, tickSequence)
    assertEquals(1, result1.size)
    assertEquals(tickSequence.name, result1.first().first)
    assertEquals(null, result1.first().second)

    assertFailsWith<IllegalStateException> {
      totalTickDifferenceMetric.evaluate(simpleTick2, tickSequence)
    }
  }

  /** Test two different ticks with the same [TickUnit]. */
  @Test
  fun `Test two different ticks with the same TickDataUnit`() {
    val simpleTick1 = SimpleTickData(1)
    val simpleTick2 = SimpleTickData(1)
    val tickSequence =
        listOf(simpleTick1, simpleTick2).asTickSequence(name = "Simple Tick Sequence")

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    val result = totalTickDifferenceMetric.evaluate(simpleTick1, tickSequence)
    assertEquals(1, result.size)
    assertEquals(tickSequence.name, result.first().first)
    assertEquals(null, result.first().second)

    assertFailsWith<IllegalStateException> {
      totalTickDifferenceMetric.evaluate(simpleTick2, tickSequence)
    }
  }

  /** Test two identical ticks. */
  @Test
  fun `Test two identical ticks`() {
    val simpleTick1 = SimpleTickData()
    val tickSequence = listOf(simpleTick1).asTickSequence(name = "Simple Tick Sequence")

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertEquals(null, totalTickDifferenceMetric.evaluate(simpleTick1, tickSequence).first().second)
    assertEquals(null, totalTickDifferenceMetric.evaluate(simpleTick1, tickSequence).first().second)
  }

  /** Test two different ticks with the same value. */
  @Test
  fun `Test two different ticks with the same value`() {
    val simpleTick1 = SimpleTickData()
    val simpleTick2 = SimpleTickData()
    val tickSequence =
        listOf(simpleTick1, simpleTick2).asTickSequence(name = "Simple Tick Sequence")

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    val result = totalTickDifferenceMetric.evaluate(simpleTick1, tickSequence)
    assertEquals(1, result.size)
    assertEquals(tickSequence.name, result.first().first)
    assertEquals(null, result.first().second)

    assertFailsWith<IllegalStateException> {
      totalTickDifferenceMetric.evaluate(simpleTick2, tickSequence)
    }
  }
}
