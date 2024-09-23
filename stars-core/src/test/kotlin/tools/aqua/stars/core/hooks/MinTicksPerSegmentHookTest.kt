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

package tools.aqua.stars.core.hooks

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.core.*
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.hooks.defaulthooks.MinTicksPerSegmentHook
import tools.aqua.stars.core.metric.metrics.evaluation.SegmentCountMetric
import tools.aqua.stars.core.tsc.builder.tsc

/** Class that contains tests for the [MinTicksPerSegmentHook]. */
class MinTicksPerSegmentHookTest {
  /** Test [MinTicksPerSegmentHook] with fail policy [EvaluationHookResult.OK]. */
  @Test
  fun `Test MinTicksPerSegmentHook with fail policy OK`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val segmentCountMetric = setup()
      registerPreSegmentEvaluationHooks(
          MinTicksPerSegmentHook(minTicks = 2, failPolicy = EvaluationHookResult.OK))

      runEvaluation(segments = segments())

      assertEquals(2, segmentCountMetric.getState())
    }
  }

  /** Test [MinTicksPerSegmentHook] with fail policy [EvaluationHookResult.SKIP]. */
  @Test
  fun `Test MinTicksPerSegmentHook with fail policy SKIP`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val segmentCountMetric = setup()
      registerPreSegmentEvaluationHooks(
          MinTicksPerSegmentHook(minTicks = 2, failPolicy = EvaluationHookResult.SKIP))

      runEvaluation(segments = segments())

      assertEquals(1, segmentCountMetric.getState())
    }
  }

  /** Test [MinTicksPerSegmentHook] with fail policy [EvaluationHookResult.ABORT]. */
  @Test
  fun `Test MinTicksPerSegmentHook with fail policy ABORT`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      setup()
      registerPreSegmentEvaluationHooks(
          MinTicksPerSegmentHook(minTicks = 2, failPolicy = EvaluationHookResult.ABORT))

      assertFailsWith<EvaluationHookAbort> { runEvaluation(segments = segments()) }
    }
  }

  /** Test [MinTicksPerSegmentHook] with #ticks less than or equal to minTicks. */
  @Test
  fun `Test MinTicksPerSegmentHook with #ticks less than or equal to minTicks`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val segmentCountMetric = setup()
      registerPreSegmentEvaluationHooks(
          MinTicksPerSegmentHook(minTicks = 1, failPolicy = EvaluationHookResult.ABORT))

      runEvaluation(segments = segments())

      assertEquals(2, segmentCountMetric.getState())
    }
  }

  /** Test [MinTicksPerSegmentHook] with #ticks negative. */
  @Test
  fun `Test MinTicksPerSegmentHook with #ticks negative`() {
    assertFailsWith<IllegalArgumentException> {
      MinTicksPerSegmentHook<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference>(
          minTicks = -1)
    }
  }

  private fun TSCEvaluation<
      SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference>
      .setup():
      SegmentCountMetric<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> =
      SegmentCountMetric<
              SimpleEntity,
              SimpleTickData,
              SimpleSegment,
              SimpleTickDataUnit,
              SimpleTickDataDifference>()
          .also {
            // Clear hooks to test them individually
            clearHooks()
            registerMetricProviders(it)
          }

  /**
   * Creates two segments. The first segment has one tick with value 0 and the second two ticks with
   * values 1 and 2.
   */
  private fun segments(): Sequence<SimpleSegment> {
    val entities = mutableListOf<SimpleEntity>()
    val tickDataList1 = mutableMapOf<SimpleTickDataUnit, SimpleTickData>()
    val tickDataList2 = mutableMapOf<SimpleTickDataUnit, SimpleTickData>()

    val segments = listOf(SimpleSegment(tickDataList1), SimpleSegment(tickDataList2)).asSequence()

    val tick1 = SimpleTickDataUnit(0)
    val tick2 = SimpleTickDataUnit(1)
    val tick3 = SimpleTickDataUnit(2)

    val tickData1 = SimpleTickData(tick1, entities)
    val tickData2 = SimpleTickData(tick2, entities)
    val tickData3 = SimpleTickData(tick3, entities)

    tickDataList1[tick1] = tickData1
    tickDataList2[tick2] = tickData2
    tickDataList2[tick3] = tickData3

    entities.add(SimpleEntity(0, tickData1))

    return segments
  }
}
