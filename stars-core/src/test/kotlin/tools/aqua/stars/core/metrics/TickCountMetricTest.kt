/*
 * Copyright 2023-2025 The STARS Project Authors
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

package tools.aqua.stars.core.metrics

import kotlin.test.Test
import kotlin.test.assertEquals
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metric.metrics.evaluation.TickCountMetric

/** Test for [TickCountMetric]. */
class TickCountMetricTest {

  /**
   * Tests the [TickCountMetric.evaluate] function with one empty [SimpleTickData]. The function
   * should return 1, as there is only one [SimpleTickData].
   */
  @Test
  fun testOneTick() {
    val simpleTick = SimpleTickData()

    val tickCountMetric =
        TickCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertEquals(1, tickCountMetric.evaluate(simpleTick))
  }

  /**
   * Tests the [TickCountMetric.evaluate] function with two [SimpleTickData]s. The function should
   * first return 1 and with the second call 2, as the internal counter should increase with each
   * observed [SimpleTickData].
   */
  @Test
  fun testTwoEmptyTicks() {
    val simpleTick1 = SimpleTickData()
    val simpleTick2 = SimpleTickData()

    val tickCountMetric =
        TickCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertEquals(1, tickCountMetric.evaluate(simpleTick1))
    assertEquals(2, tickCountMetric.evaluate(simpleTick2))
  }
}
