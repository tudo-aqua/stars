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
import tools.aqua.stars.core.metric.metrics.evaluation.MissedTSCInstancesPerTSCMetric

/**
 * Tests the [SerializableResult] sealed class implementation for the [SerializableTSCResultTest].
 */
class SerializableTSCResultTest {

  /**
   * Test the correct calculation and return of [SerializableTSCResultTest] for one missed TSC
   * instance.
   */
  @Test
  fun `Test return of one missed TSC instance`() {
    val missedInstancesMetric =
        MissedTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    missedInstancesMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)

    val missedInstancesResult = missedInstancesMetric.getSerializableResults()
    assertEquals(1, missedInstancesResult.size)

    assertEquals(1, missedInstancesResult[0].value.size)
  }

  /**
   * Test the correct calculation and return of [SerializableTSCResultTest] for all missed TSC
   * instances.
   */
  @Test
  fun `Test return of all missed TSC instanced`() {
    val missedInstancesMetric =
        MissedTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    missedInstancesMetric.evaluate(simpleTSC3, simpleTSCInvalidInstance)

    val missedInstancesResult = missedInstancesMetric.getSerializableResults()
    assertEquals(1, missedInstancesResult.size)

    assertEquals(3, missedInstancesResult[0].value.size)
  }

  /**
   * Test the correct calculation and return of [SerializableTSCResultTest] for no missed TSC
   * instance.
   */
  @Test
  fun `Test return of no missed TSC instances`() {
    val missedInstancesMetric =
        MissedTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    missedInstancesMetric.evaluate(simpleTSC, simpleTSCValidInstance)

    val missedInstancesResult = missedInstancesMetric.getSerializableResults()
    assertEquals(1, missedInstancesResult.size)

    assertEquals(0, missedInstancesResult[0].value.size)
  }
}
