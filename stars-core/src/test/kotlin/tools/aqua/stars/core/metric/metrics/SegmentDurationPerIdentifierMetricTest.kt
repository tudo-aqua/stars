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

package tools.aqua.stars.core.metric.metrics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.metric.metrics.evaluation.SegmentDurationPerIdentifierMetric
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/** Test for [SegmentDurationPerIdentifierMetric]. */
class SegmentDurationPerIdentifierMetricTest {

  /**
   * This test checks that the map and actual values inside the [SegmentDurationPerIdentifierMetric]
   * are correctly set for exactly one [SegmentType].
   */
  @Test
  fun testOneSegmentIdentifier() {
    val tickData = SimpleTickData(currentTick = 0.0)
    val tickData1 = SimpleTickData(currentTick = 2.0)
    val segment = SimpleSegment(tickData = listOf(tickData, tickData1), segmentSource = "Map1")

    val segmentDurationPerIdentifierMetric =
        SegmentDurationPerIdentifierMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    // Check that the evaluate function returns the correct value
    assertEquals(segmentDurationPerIdentifierMetric.evaluate(segment), 2.0)
    // Check that the segment identifier was registered in the map
    assert(segmentDurationPerIdentifierMetric.getState().containsKey(segment.segmentSource))
    // Check that the value for the segment identifier in the map was correctly updated
    assertEquals(segmentDurationPerIdentifierMetric.getState().getValue(segment.segmentSource), 2.0)
  }

  /**
   * This test checks that the map and actual values inside the [SegmentDurationPerIdentifierMetric]
   * are correctly set with default values with no [TickDataType]s in the [SegmentType].
   */
  @Test
  fun testOneSegmentIdentifierWithNoTickData() {
    val segment = SimpleSegment(segmentSource = "Map1")

    val segmentDurationPerIdentifierMetric =
        SegmentDurationPerIdentifierMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    // Check that the evaluate function returns the correct value
    assertEquals(segmentDurationPerIdentifierMetric.evaluate(segment), 0.0)
    // Check that the segment identifier was registered in the map
    assert(segmentDurationPerIdentifierMetric.getState().containsKey(segment.segmentSource))
    // Check that the value for the segment identifier in the map was correctly updated
    assertEquals(segmentDurationPerIdentifierMetric.getState().getValue(segment.segmentSource), 0.0)
  }

  /**
   * This test checks that the map and actual values inside the [SegmentDurationPerIdentifierMetric]
   * are correctly set with default values with exactly one [TickDataType] in the [SegmentType].
   */
  @Test
  fun testOneSegmentIdentifierWithOneTickData() {
    val tickData = SimpleTickData()
    val segment = SimpleSegment(tickData = listOf(tickData), segmentSource = "Map1")

    val segmentDurationPerIdentifierMetric =
        SegmentDurationPerIdentifierMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    // Check that the evaluate function returns the correct value
    assertEquals(segmentDurationPerIdentifierMetric.evaluate(segment), 0.0)
    // Check that the segment identifier was registered in the map
    assert(segmentDurationPerIdentifierMetric.getState().containsKey(segment.segmentSource))
    // Check that the value for the segment identifier in the map was correctly updated
    assertEquals(segmentDurationPerIdentifierMetric.getState().getValue(segment.segmentSource), 0.0)
  }

  /**
   * This test checks that an [IllegalStateException] is thrown when the segment duration is
   * negative.
   */
  @Test
  fun testOneSegmentIdentifierWithNegativeTimeLength() {
    val tickData = SimpleTickData(currentTick = 0.0)
    val tickData1 = SimpleTickData(currentTick = -2.0)
    val segment = SimpleSegment(tickData = listOf(tickData, tickData1), segmentSource = "Map1")

    val segmentDurationPerIdentifierMetric =
        SegmentDurationPerIdentifierMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    assertFailsWith<IllegalStateException> { segmentDurationPerIdentifierMetric.evaluate(segment) }
  }

  /**
   * This test checks that different [SegmentType.segmentSource]s are correctly stored as different
   * keys in the [Map] and that the values are also correctly added.
   */
  @Test
  fun testTwoSegmentIdentifiersOneSegmentEach() {
    val tickData11 = SimpleTickData(currentTick = 0.0)
    val tickData12 = SimpleTickData(currentTick = 2.0)
    val segment1 = SimpleSegment(tickData = listOf(tickData11, tickData12), segmentSource = "Map1")

    val tickData21 = SimpleTickData(currentTick = 1.0)
    val tickData22 = SimpleTickData(currentTick = 4.0)
    val segment2 = SimpleSegment(tickData = listOf(tickData21, tickData22), segmentSource = "Map2")

    val segmentDurationPerIdentifierMetric =
        SegmentDurationPerIdentifierMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    // Check that the evaluate function returns the correct value
    assertEquals(segmentDurationPerIdentifierMetric.evaluate(segment1), 2.0)
    // Check that the segment identifier was registered in the map
    assert(segmentDurationPerIdentifierMetric.getState().containsKey(segment1.segmentSource))
    // Check that the value for the segment identifier in the map was correctly updated
    assertEquals(
        segmentDurationPerIdentifierMetric.getState().getValue(segment1.segmentSource), 2.0)

    // Check that the evaluate function returns the correct value
    assertEquals(segmentDurationPerIdentifierMetric.evaluate(segment2), 3.0)
    // Check that the segment identifier was registered in the map
    assert(segmentDurationPerIdentifierMetric.getState().containsKey(segment2.segmentSource))
    // Check that the value for the segment identifier in the map was correctly updated
    assertEquals(
        segmentDurationPerIdentifierMetric.getState().getValue(segment2.segmentSource), 3.0)
  }

  /**
   * This test checks that same [SegmentType.segmentSource]s are stored using the same key and that
   * the values are correctly added up.
   */
  @Test
  fun testTwoSegmentIdentifiersTwoSegments() {
    val tickData11 = SimpleTickData(currentTick = 0.0)
    val tickData12 = SimpleTickData(currentTick = 2.0)
    val segment1 = SimpleSegment(tickData = listOf(tickData11, tickData12), segmentSource = "Map1")

    val tickData21 = SimpleTickData(currentTick = 5.0)
    val tickData22 = SimpleTickData(currentTick = 10.0)
    val segment2 = SimpleSegment(tickData = listOf(tickData21, tickData22), segmentSource = "Map1")

    val segmentDurationPerIdentifierMetric =
        SegmentDurationPerIdentifierMetric<SimpleEntity, SimpleTickData, SimpleSegment>()

    // Check that the evaluate function returns the correct value
    assertEquals(segmentDurationPerIdentifierMetric.evaluate(segment1), 2.0)
    // Check that the segment identifier was registered in the map
    assert(segmentDurationPerIdentifierMetric.getState().containsKey(segment1.segmentSource))
    // Check that the value for the segment identifier in the map was correctly updated
    assertEquals(
        segmentDurationPerIdentifierMetric.getState().getValue(segment1.segmentSource), 2.0)

    // Check that the evaluate function returns the correct value
    assertEquals(segmentDurationPerIdentifierMetric.evaluate(segment2), 7.0)
    // Check that the segment identifier was registered in the map
    assert(segmentDurationPerIdentifierMetric.getState().containsKey(segment2.segmentSource))
    // Check that the value for the segment identifier in the map was correctly updated
    assertEquals(
        segmentDurationPerIdentifierMetric.getState().getValue(segment2.segmentSource), 7.0)
  }
}
