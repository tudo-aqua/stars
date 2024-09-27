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
import org.junit.jupiter.api.assertThrows
import tools.aqua.stars.core.*
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.hooks.defaulthooks.MinNodesInTSCHook
import tools.aqua.stars.core.metric.metrics.evaluation.SegmentCountMetric
import tools.aqua.stars.core.tsc.builder.tsc

/** Class that contains tests for the [MinNodesInTSCHook]. */
class MinNodesInTSCHookTest {
  /** Test [MinNodesInTSCHook] with fail policy [EvaluationHookResult.OK]. */
  @Test
  fun `Test MinNodesInTSCHook with fail policy OK`() {
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
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 2, failPolicy = EvaluationHookResult.OK))

      runEvaluation(segments = segments())

      assertEquals(1, segmentCountMetric.getState())
    }
  }

  /** Test [MinNodesInTSCHook] with fail policy [EvaluationHookResult.SKIP]. */
  @Test
  fun `Test MinNodesInTSCHook with fail policy SKIP`() {
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
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 2, failPolicy = EvaluationHookResult.SKIP))

      runEvaluation(segments = segments())

      assertEquals(0, segmentCountMetric.getState())
    }
  }

  /** Test [MinNodesInTSCHook] with fail policy [EvaluationHookResult.ABORT]. */
  @Test
  fun `Test MinNodesInTSCHook with fail policy ABORT`() {
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
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 2, failPolicy = EvaluationHookResult.ABORT))

      assertThrows<EvaluationHookAbort> { runEvaluation(segments = segments()) }
    }
  }

  /** Test [MinNodesInTSCHook] with #nodes equal to minNodes. */
  @Test
  fun `Test MinNodesInTSCHook with #nodes equal to minNodes`() {
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
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 1, failPolicy = EvaluationHookResult.ABORT))

      runEvaluation(segments = segments())

      assertEquals(1, segmentCountMetric.getState())
    }
  }

  /** Test [MinNodesInTSCHook] with #nodes greater than minNodes. */
  @Test
  fun `Test MinNodesInTSCHook with #nodes greater than minNodes`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("") { any("") {} }
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val segmentCountMetric = setup()
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 1, failPolicy = EvaluationHookResult.ABORT))

      runEvaluation(segments = segments())

      assertEquals(1, segmentCountMetric.getState())
    }
  }

  /** Test [MinNodesInTSCHook] with #nodes negative. */
  @Test
  fun `Test MinNodesInTSCHook with #nodes negative`() {
    assertFailsWith<IllegalArgumentException> {
      MinNodesInTSCHook<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference>(
          minNodes = -1)
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

  private fun segments(): Sequence<SimpleSegment> {
    val entities = mutableListOf<SimpleEntity>()
    val tickDataList = mutableMapOf<SimpleTickDataUnit, SimpleTickData>()

    val segments = listOf(SimpleSegment(tickDataList)).asSequence()

    val tick = SimpleTickDataUnit(0)

    val tickData = SimpleTickData(tick, entities)

    tickDataList[tick] = tickData

    entities.add(SimpleEntity(0, tickData))

    return segments
  }
}
