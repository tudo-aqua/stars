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
import kotlin.test.assertNotEquals
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metric.metrics.evaluation.SegmentCountMetric
import tools.aqua.stars.core.metric.serialization.extensions.getJsonString
import tools.aqua.stars.core.metric.serialization.extensions.writeSerializedResults
import tools.aqua.stars.core.metric.utils.*

/**
 * Tests the [SerializableResult] sealed class implementation for the [SerializableIntResultTest].
 */
class SerializableIntResultTest {

  /** Tests that the [SerializableIntResult] is correctly (de)serialized. */
  @Test
  fun `Test simple serialization`() {
    val simpleSegment1 = SimpleSegment()

    val segmentCountMetric =
        SegmentCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    assertEquals(segmentCountMetric.evaluate(simpleSegment1), 1)
    assertEquals(segmentCountMetric.evaluate(simpleSegment1), 2)
    segmentCountMetric.writeSerializedResults()
  }

  /** Tests that the change of the [SerializableIntResult] is correctly (de)serialized. */
  @Test
  fun `Test changed result value`() {
    val simpleSegment1 = SimpleSegment()

    val segmentCountMetric =
        SegmentCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    assertEquals(segmentCountMetric.evaluate(simpleSegment1), 1)
    val serializedResultBaseline = segmentCountMetric.getSerializableResults()
    val deserializedResultBaseline =
        serializedResultBaseline.map { getJsonContentFromString(it.getJsonString()) }

    assertEquals(segmentCountMetric.evaluate(simpleSegment1), 2)
    val serializedResultCompare = segmentCountMetric.getSerializableResults()
    val deserializedResultCompare =
        serializedResultCompare.map { getJsonContentFromString(it.getJsonString()) }

    assertNotEquals(deserializedResultBaseline, deserializedResultCompare)
  }
}
