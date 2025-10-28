/*
 * Copyright 2025 The STARS Project Authors
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

package tools.aqua.stars.core.metrics.providers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.metrics.evaluation.ValidTSCInstancesPerTSCMetric

typealias E = SimpleEntity

typealias T = SimpleTickData

typealias S = SimpleSegment

typealias U = SimpleTickDataUnit

typealias D = SimpleTickDataDifference

/** Test for [MetricProvider]. */
class MetricProviderTest {

  /**
   * Tests that an [IllegalArgumentException] is thrown if multiple [MetricProvider]s with the same
   * identifier are registered.
   */
  @Test
  fun `Test multiple MetricProviders with same names`() {
    val metricProvider1 = ValidTSCInstancesPerTSCMetric<E, T, S, U, D>()
    val metricProvider2 = ValidTSCInstancesPerTSCMetric<E, T, S, U, D>()

    val tscEvaluation =
        TSCEvaluation<E, T, S, U, D>(
            emptyList(),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
        )

    assertThrows<IllegalArgumentException> {
      tscEvaluation.registerMetricProviders(metricProvider1, metricProvider2)
    }
  }

  /**
   * Tests that no [IllegalArgumentException] is thrown if multiple [MetricProvider]s with different
   * identifiers are registered.
   */
  @Test
  fun `Test multiple MetricProviders with different names`() {
    val metricProvider1 = ValidTSCInstancesPerTSCMetric<E, T, S, U, D>(identifier = "Metric1")
    val metricProvider2 = ValidTSCInstancesPerTSCMetric<E, T, S, U, D>(identifier = "Metric2")

    val tscEvaluation =
        TSCEvaluation<E, T, S, U, D>(
            emptyList(),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
        )

    assertDoesNotThrow { tscEvaluation.registerMetricProviders(metricProvider1, metricProvider2) }
  }
}
