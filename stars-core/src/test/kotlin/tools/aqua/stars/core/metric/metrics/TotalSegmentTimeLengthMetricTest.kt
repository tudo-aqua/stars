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

package tools.aqua.stars.core.tools.aqua.stars.core.metric.metrics

import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.metric.metrics.TotalSegmentTimeLengthMetric
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

class TotalSegmentTimeLengthMetricTest {

  /**
   * This test checks that no time duration is added to the [TotalSegmentTimeLengthMetric] when
   * there is no [TickDataType] in the list of [SegmentType.tickData]s. The resulting state should
   * be 0.0.
   */
  @Test
  fun testEmptySegment() {
    val segment = SimpleSegment()

    val totalSegmentTimeLengthMetric =
        TotalSegmentTimeLengthMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    assertEquals(totalSegmentTimeLengthMetric.evaluate(segment), 0.0)
    assertEquals(totalSegmentTimeLengthMetric.getState(), 0.0)
  }

  /**
   * This test checks that no time duration is added to the [TotalSegmentTimeLengthMetric] when
   * there is only one [TickDataType] is the list of [SegmentType.tickData]s. The resulting state
   * should be 0.0.
   */
  @Test
  fun testSingleTickSegment() {
    val tickData = SimpleTickData()
    val segment = SimpleSegment(tickData = listOf(tickData))

    val totalSegmentTimeLengthMetric =
        TotalSegmentTimeLengthMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    assertEquals(totalSegmentTimeLengthMetric.evaluate(segment), 0.0)
    assertEquals(totalSegmentTimeLengthMetric.getState(), 0.0)
  }

  /**
   * This test checks that the time duration of the built [SegmentType] is exactly the difference
   * between the two [TickDataType]s.
   */
  @Test
  fun testTwoTickSegment() {
    val tickData1 = SimpleTickData(currentTick = 0.0)
    val tickData2 = SimpleTickData(currentTick = 2.0)
    val segment = SimpleSegment(tickData = listOf(tickData1, tickData2))

    val totalSegmentTimeLengthMetric =
        TotalSegmentTimeLengthMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    assertEquals(
        totalSegmentTimeLengthMetric.evaluate(segment),
        tickData2.currentTick - tickData1.currentTick)
    assertEquals(
        totalSegmentTimeLengthMetric.getState(), tickData2.currentTick - tickData1.currentTick)
  }

  /**
   * This test checks that the time duration of the built [SegmentType] is exactly the difference
   * between the first and last [TickDataType]s.
   */
  @Test
  fun testThreeTickSegment() {
    val tickData1 = SimpleTickData(currentTick = 0.0)
    val tickData2 = SimpleTickData(currentTick = 2.0)
    val tickData3 = SimpleTickData(currentTick = 4.0)
    val segment = SimpleSegment(tickData = listOf(tickData1, tickData2, tickData3))

    val totalSegmentTimeLengthMetric =
        TotalSegmentTimeLengthMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    assertEquals(
        totalSegmentTimeLengthMetric.evaluate(segment),
        tickData3.currentTick - tickData1.currentTick)
    assertEquals(
        totalSegmentTimeLengthMetric.getState(), tickData3.currentTick - tickData1.currentTick)
  }

  /**
   * This test checks that an [IllegalStateException] is thrown when the time duration between the
   * first and last [TickDataType] of a [SegmentType] is negative.
   */
  @Test
  fun testNegativeTimeDuration() {
    val tickData1 = SimpleTickData(currentTick = 0.0)
    val tickData2 = SimpleTickData(currentTick = -2.0)
    val segment = SimpleSegment(tickData = listOf(tickData1, tickData2))

    val totalSegmentTimeLengthMetric =
        TotalSegmentTimeLengthMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    assertFailsWith<IllegalStateException> { totalSegmentTimeLengthMetric.evaluate(segment) }
  }
}
