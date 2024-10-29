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

package tools.aqua.stars.core.metric.metrics

import kotlin.test.Test
import kotlin.test.assertEquals
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metric.metrics.evaluation.SegmentCountMetric

/** Test for [SegmentCountMetric]. */
class SegmentCountMetricTest {

  /**
   * Tests the [SegmentCountMetric.evaluate] function with one empty [SimpleSegment]. The function
   * should return 1, as there is only one [SimpleSegment].
   */
  @Test
  fun testOneEmptySegment() {
    val simpleSegment1 = SimpleSegment()

    val segmentCountMetric =
        SegmentCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    assertEquals(1, segmentCountMetric.evaluate(simpleSegment1))
  }

  /**
   * Tests the [SegmentCountMetric.evaluate] function with two empty [SimpleSegment]s. The function
   * should first return 1 and with the second call 2, as the internal counter should increase with
   * each observed [SimpleSegment].
   */
  @Test
  fun testTwoEmptySegments() {
    val simpleSegment1 = SimpleSegment()
    val simpleSegment2 = SimpleSegment()

    val segmentCountMetric =
        SegmentCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    assertEquals(1, segmentCountMetric.evaluate(simpleSegment1))
    assertEquals(2, segmentCountMetric.evaluate(simpleSegment2))
  }
}
