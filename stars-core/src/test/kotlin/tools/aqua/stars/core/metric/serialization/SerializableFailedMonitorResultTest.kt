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
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.metrics.postEvaluation.FailedMonitorsMetric

/** Tests the [Serializable] interface implementation for the [FailedMonitorsMetric]. */
class SerializableFailedMonitorResultTest {

  /**
   * Tests the correct calculation and return of a [SerializableFailedMonitorsResult] for a TSC in
   * which no monitor was specified.
   */
  @Test
  fun `Test TSC with no specified monitors`() {
    // Initialize base metric
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate and populate base metric
    // Uses a valid TSC instance where only one of two leaf nodes is present
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCValidInstance)

    // Initialize actual metric
    val failedMonitorsMetric =
        FailedMonitorsMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            validTSCInstancesPerTSCMetric)

    // Post evaluate and populate actual metric
    failedMonitorsMetric.postEvaluate()

    // Get serialized results
    val result = failedMonitorsMetric.getSerializableResults()
    assertEquals(1, result.size)
    assertEquals(0, result[0].value.size)
  }

  /**
   * Tests the correct calculation and return of a [SerializableFailedMonitorsResult] for a TSC in
   * which all specified monitors evaluate to `true`.
   */
  @Test
  fun `Test TSC with only true specified monitors`() {
    // Initialize base metric
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate and populate base metric
    // Uses a valid TSC instance where only one of two leaf nodes is present
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC2, simpleTSC2ValidInstance)

    // Initialize actual metric
    val failedMonitorsMetric =
        FailedMonitorsMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            validTSCInstancesPerTSCMetric)

    // Post evaluate and populate actual metric
    failedMonitorsMetric.postEvaluate()

    // Get serialized results
    val result = failedMonitorsMetric.getSerializableResults()
    assertEquals(1, result.size)
    assertEquals(0, result[0].value.size)
  }

  /**
   * Tests the correct calculation and return of a [SerializableFailedMonitorsResult] for a TSC in
   * which at least 1 monitor fails.
   */
  @Test
  fun `Test TSC with failing monitors`() {
    // Initialize base metric
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate and populate base metric
    // Uses a valid TSC instance where only one of two leaf nodes is present
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC4, simpleTSC4ValidInstance2)

    // Initialize actual metric
    val failedMonitorsMetric =
        FailedMonitorsMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            validTSCInstancesPerTSCMetric)

    // Post evaluate and populate actual metric
    failedMonitorsMetric.postEvaluate()

    // Get serialized results
    val result = failedMonitorsMetric.getSerializableResults()
    assertEquals(1, result.size)
    assertEquals(2, result[0].value.size)
  }
}
