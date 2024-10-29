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
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metric.metrics.evaluation.InvalidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.serialization.extensions.compareTo

/**
 * Tests the [SerializableResult] sealed class implementation for the
 * [SerializableTSCOccurrenceResult].
 */
class SerializableTSCOccurrencesResultTest {

  /**
   * Tests the correct calculation and return of a [SerializableTSCOccurrenceResult] for a valid TSC
   * instance.
   */
  @Test
  fun `Test return of valid TSC Result`() {
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Test serialization and calculation of one valid TSC instance
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCValidInstance)
    var serializedResult = validTSCInstancesPerTSCMetric.getSerializableResults()
    assertEquals(1, serializedResult.size)
    assertEquals("root", serializedResult[0].value[0].tscInstance.label)
    assertEquals(
        "leaf1", serializedResult[0].value[0].tscInstance.outgoingEdges[0].destination.label)
    assertEquals(
        "leaf2", serializedResult[0].value[0].tscInstance.outgoingEdges[1].destination.label)

    // Test serialization and calculation of one additional invalid TSC instance
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)
    serializedResult = validTSCInstancesPerTSCMetric.getSerializableResults()
    assertEquals(1, serializedResult.size)
  }

  /**
   * Tests the correct calculation and return of a [SerializableTSCOccurrenceResult] for an invalid
   * TSC instance.
   */
  @Test
  fun `Test return of invalid TSC Result`() {
    val invalidTSCInstancesPerTSCMetric =
        InvalidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Test serialization and calculation of one invalid TSC instance
    invalidTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)
    var serializedResult = invalidTSCInstancesPerTSCMetric.getSerializableResults()
    assertEquals(1, serializedResult.size)
    assertEquals("root", serializedResult[0].value[0].tscInstance.label)
    assertEquals(
        "leaf1", serializedResult[0].value[0].tscInstance.outgoingEdges[0].destination.label)

    // Test serialization and calculation of one additional valid TSC instance
    invalidTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)
    serializedResult = invalidTSCInstancesPerTSCMetric.getSerializableResults()
    assertEquals(1, serializedResult.size)
  }

  /**
   * Tests the correct comparison of two valid [SerializableTSCOccurrenceResult] with different
   * results.
   */
  @Test
  fun `Test correct comparison of two different valid TSC results`() {
    val currentMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    val baselineMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    currentMetric.evaluate(simpleTSC, simpleTSCValidInstance)
    baselineMetric.evaluate(simpleTSC2, simpleTSC2ValidInstance)

    val currentResult = currentMetric.getSerializableResults()
    val baselineResult = baselineMetric.getSerializableResults()

    val comparison = currentResult.compareTo(baselineResult)

    assertEquals(1, comparison.size)
    assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, comparison[0].verdict)
  }

  /**
   * Tests the correct comparison of two invalid [SerializableTSCOccurrenceResult] with different
   * results.
   */
  @Test
  fun `Test correct comparison of two different invalid TSC results`() {
    val currentMetric =
        InvalidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    val baselineMetric =
        InvalidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    currentMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)
    baselineMetric.evaluate(simpleTSC2, simpleTSCInvalidInstance)

    val currentResult = currentMetric.getSerializableResults()
    assertEquals(1, currentResult.size)

    val baselineResult = baselineMetric.getSerializableResults()
    assertEquals(1, baselineResult.size)

    val comparison = currentResult.compareTo(baselineResult)

    assertEquals(1, comparison.size)
    assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, comparison[0].verdict)
  }

  /**
   * Tests the correct comparison of two valid [SerializableTSCOccurrenceResult] with the same
   * content from the same segment source.
   */
  @Test
  fun `Test correct comparison of two same valid TSC results from equal segment sources`() {
    val currentMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    val baselineMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    currentMetric.evaluate(simpleTSC, simpleTSCValidInstance)
    baselineMetric.evaluate(simpleTSC, simpleTSCValidInstance2)

    val currentResult = currentMetric.getSerializableResults()
    assertEquals(1, currentResult.size)

    val baselineResult = baselineMetric.getSerializableResults()
    assertEquals(1, baselineResult.size)

    val comparison = currentResult.compareTo(baselineResult)

    assertEquals(1, comparison.size)
    assertEquals(SerializableResultComparisonVerdict.EQUAL_RESULTS, comparison[0].verdict)
  }

  /**
   * Tests the correct comparison of two valid [SerializableTSCOccurrenceResult] with the same
   * content from different segment sources.
   */
  @Test
  fun `Test correct comparison of two same valid TSC results from different segment sources`() {
    val currentMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    val baselineMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    currentMetric.evaluate(simpleTSC, simpleTSCValidInstance)
    baselineMetric.evaluate(simpleTSC, simpleTSCValidInstance3)

    val currentResult = currentMetric.getSerializableResults()
    assertEquals(1, currentResult.size)

    val baselineResult = baselineMetric.getSerializableResults()
    assertEquals(1, baselineResult.size)

    val comparison = currentResult.compareTo(baselineResult)

    assertEquals(1, comparison.size)
    assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, comparison[0].verdict)
  }
}
