/*
 * Copyright 2024 The STARS Project Authors
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

package tools.aqua.stars.core.metric.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metric.metrics.evaluation.SegmentCountMetric
import tools.aqua.stars.core.types.SegmentType

/** Tests the [Serializable] interface implementation for the [SegmentCountMetric]. */
class SerializableSegmentCountMetricTest {

  /**
   * Tests the correct (de)serialization of the [SegmentCountMetric] after evaluating one
   * [SegmentType].
   */
  @Test
  fun `Test serialization of evaluation of single segment`() {
    val simpleSegment = SimpleSegment()

    val segmentCountMetric =
        SegmentCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate to populate segmentCountMetric with a correct value
    segmentCountMetric.evaluate(simpleSegment)

    val segmentCountResults = segmentCountMetric.getSerializableResults()
    val deserializedResults = serializeAndDeserialize(segmentCountMetric)
    assertEquals(segmentCountResults, deserializedResults)
  }

  /**
   * Tests the correct (de)serialization of the [SegmentCountMetric] after evaluating one
   * [SegmentType].
   */
  @Test
  fun `Test serialization of evaluation of two segments`() {
    val simpleSegment = SimpleSegment(segmentSource = "segment1")
    val simpleSegment2 = SimpleSegment(segmentSource = "segment2")

    val segmentCountMetric =
        SegmentCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate to populate segmentCountMetric with a correct value
    segmentCountMetric.evaluate(simpleSegment)
    segmentCountMetric.evaluate(simpleSegment2)

    val segmentCountResults = segmentCountMetric.getSerializableResults()

    // Test that for each segmentSource a result exists and one total result exists
    assertEquals(3, segmentCountResults.size)
    assertTrue(segmentCountResults.any { it.identifier.contains(simpleSegment.segmentSource) })
    assertTrue(segmentCountResults.any { it.identifier.contains(simpleSegment2.segmentSource) })
    assertTrue(
        segmentCountResults.any { it.identifier.contains(segmentCountMetric.loggerIdentifier) })

    // Check concrete values
    val simpleSegmentResult =
        segmentCountResults.first { it.identifier.contains(simpleSegment.segmentSource) }
    assertEquals(1, simpleSegmentResult.value)

    val simpleSegment2Result =
        segmentCountResults.first { it.identifier.contains(simpleSegment2.segmentSource) }
    assertEquals(1, simpleSegment2Result.value)

    val totalSegmentCountResult =
        segmentCountResults.first { it.identifier.contains(segmentCountMetric.loggerIdentifier) }
    assertEquals(2, totalSegmentCountResult.value)
  }
}
