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
import tools.aqua.stars.core.metric.utils.getGroundTruthSerializationResultPath
import tools.aqua.stars.core.metric.utils.getLatestSerializationResultPath
import tools.aqua.stars.core.metric.utils.getSerializedResultFromFileSystem
import tools.aqua.stars.core.metric.utils.saveAsJSONFile

class SerializableIntResultTest {

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
    val serializedResult = segmentCountMetric.getSerializableResults()
    val serializedResultJsonString = serializedResult.getJsonString()

    assertEquals(
        segmentCountMetric.getSerializableResults(),
        SerializableResult.getJsonContentFromString(serializedResultJsonString))

    saveAsJSONFile(serializedResult)
    var latestResultsPath = getLatestSerializationResultPath()
    var groundTruthPath = getGroundTruthSerializationResultPath()
    var s = ""

    if (latestResultsPath != null) {
      val deserializedResult =
          getSerializedResultFromFileSystem(latestResultsPath, serializedResult)
      val compared = segmentCountMetric.compareResults(deserializedResult)
      val s = ""
    }

    if (groundTruthPath != null) {
      val deserializedResult = getSerializedResultFromFileSystem(groundTruthPath, serializedResult)
      val compared = segmentCountMetric.compareResults(deserializedResult)
      val s = ""
    }
  }

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
    val serializedResultGroundTruthJsonString =
        segmentCountMetric.getSerializableResults().getJsonString()
    val deserializedResultGroundTruth =
        SerializableResult.getJsonContentFromString(serializedResultGroundTruthJsonString)

    assertEquals(segmentCountMetric.evaluate(simpleSegment1), 2)
    val serializedResultCompareJsonString =
        segmentCountMetric.getSerializableResults().getJsonString()
    val deserializedResultCompare =
        SerializableResult.getJsonContentFromString(serializedResultCompareJsonString)

    assertNotEquals(deserializedResultGroundTruth, deserializedResultCompare)
  }
}
