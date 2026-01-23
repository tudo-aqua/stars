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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metrics.evaluation.TotalTickDifferenceMetric

/** Test for [TotalTickDifferenceMetric]. */
class TotalTickDifferenceMetricTest {

  /** Test only one tick. */
  @Test
  fun `Test only one tick`() {
    val simpleTick = SimpleTickData()

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertTrue(totalTickDifferenceMetric.evaluate(simpleTick).isEmpty)
  }

  /** Test two different ticks with increasing [TickDataUnit]s. */
  @Test
  fun `Test two different ticks with increasing TickDataUnits`() {
    val simpleTick1 = SimpleTickData(SimpleTickDataUnit(1))
    val simpleTick2 = SimpleTickData(SimpleTickDataUnit(2))

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    val result1 = totalTickDifferenceMetric.evaluate(simpleTick1)
    assertTrue(result1.isEmpty)

    val result2 = totalTickDifferenceMetric.evaluate(simpleTick2)
    assertFalse(result2.isEmpty)
    assertEquals(1L, result2.get().tickDifference)
  }

  /** Test two different ticks with decreasing [TickDataUnit]s. */
  @Test
  fun `Test two different ticks with decreasing TickDataUnits`() {
    val simpleTick1 = SimpleTickData(SimpleTickDataUnit(2))
    val simpleTick2 = SimpleTickData(SimpleTickDataUnit(1))

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertTrue(totalTickDifferenceMetric.evaluate(simpleTick1).isEmpty)
    assertThrows<IllegalStateException> { totalTickDifferenceMetric.evaluate(simpleTick2) }
  }

  /** Test two different ticks with the same [TickDataUnit]. */
  @Test
  fun `Test two different ticks with the same TickDataUnit`() {
    val simpleTick1 = SimpleTickData(SimpleTickDataUnit(1))
    val simpleTick2 = SimpleTickData(SimpleTickDataUnit(1))

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertTrue(totalTickDifferenceMetric.evaluate(simpleTick1).isEmpty)
    assertThrows<IllegalStateException> { totalTickDifferenceMetric.evaluate(simpleTick2) }
  }

  /** Test two identical ticks. */
  @Test
  fun `Test two identical ticks`() {
    val simpleTick1 = SimpleTickData()

    val totalTickDifferenceMetric =
        TotalTickDifferenceMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertTrue(totalTickDifferenceMetric.evaluate(simpleTick1).isEmpty)
    assertThrows<IllegalStateException> { totalTickDifferenceMetric.evaluate(simpleTick1) }
  }
}
