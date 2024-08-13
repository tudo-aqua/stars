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

/** MinTicksPerSegmentHookTest. */
class MinTicksPerSegmentHookTest {
  /** Test MinTicksPerSegmentHookTest with fail policy OK. */
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
    TSCEvaluation(tsc = tsc, segments = segments()).apply {
      val segmentCountMetric = setup()
      registerPreSegmentEvaluationHooks(
          MinTicksPerSegmentHook(minTicks = 2, failPolicy = EvaluationHookResult.OK))

      runEvaluation(writePlots = false, writePlotDataCSV = false)

      assertEquals(2, segmentCountMetric.getState())
    }
  }

  /** Test MinTicksPerSegmentHookTest with fail policy SKIP. */
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
    TSCEvaluation(tsc = tsc, segments = segments()).apply {
      val segmentCountMetric = setup()
      registerPreSegmentEvaluationHooks(
          MinTicksPerSegmentHook(minTicks = 2, failPolicy = EvaluationHookResult.SKIP))

      runEvaluation(writePlots = false, writePlotDataCSV = false)

      assertEquals(1, segmentCountMetric.getState())
    }
  }

  /** Test MinTicksPerSegmentHookTest with fail policy ABORT. */
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
    TSCEvaluation(tsc = tsc, segments = segments()).apply {
      setup()
      registerPreSegmentEvaluationHooks(
          MinTicksPerSegmentHook(minTicks = 2, failPolicy = EvaluationHookResult.ABORT))

      assertFailsWith<PreSegmentEvaluationHookAbort> {
        runEvaluation(writePlots = false, writePlotDataCSV = false)
      }
    }
  }

  /** Test MinTicksPerSegmentHookTest with #ticks less than or equal to minTicks. */
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
    TSCEvaluation(tsc = tsc, segments = segments()).apply {
      val segmentCountMetric = setup()
      registerPreSegmentEvaluationHooks(
          MinTicksPerSegmentHook(minTicks = 1, failPolicy = EvaluationHookResult.ABORT))

      runEvaluation(writePlots = false, writePlotDataCSV = false)

      assertEquals(2, segmentCountMetric.getState())
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
            preEvaluationHooks.clear()
            preSegmentEvaluationHooks.clear()
            registerMetricProviders(it)
          }

  /**
   * Creates two segments. The first segment has one tick with value 0 and the second two ticks with
   * values 1 and 2.
   */
  private fun segments(): Sequence<SimpleSegment> {
    val entities = mutableListOf<SimpleEntity>()
    val tickdatas1 = mutableMapOf<SimpleTickDataUnit, SimpleTickData>()
    val tickdatas2 = mutableMapOf<SimpleTickDataUnit, SimpleTickData>()

    val segments = listOf(SimpleSegment(tickdatas1), SimpleSegment(tickdatas2)).asSequence()

    val tick1 = SimpleTickDataUnit(0)
    val tick2 = SimpleTickDataUnit(1)
    val tick3 = SimpleTickDataUnit(2)

    val tickdata1 = SimpleTickData(tick1)
    val tickdata2 = SimpleTickData(tick2)
    val tickdata3 = SimpleTickData(tick3)

    tickdatas1[tick1] = tickdata1
    tickdatas2[tick2] = tickdata2
    tickdatas2[tick3] = tickdata3

    entities.add(SimpleEntity(0, tickdata1))
    //    entities.add(SimpleEntity(0, tickdata2))
    //    entities.add(SimpleEntity(0, tickdata3))

    return segments
  }
}
