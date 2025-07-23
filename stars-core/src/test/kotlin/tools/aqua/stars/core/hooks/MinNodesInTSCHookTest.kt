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

package tools.aqua.stars.core.hooks

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.assertThrows
import tools.aqua.stars.core.*
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.hooks.defaulthooks.MinNodesInTSCHook
import tools.aqua.stars.core.metric.metrics.evaluation.TickCountMetric
import tools.aqua.stars.core.tsc.builder.tsc

/** Class that contains tests for the [MinNodesInTSCHook]. */
class MinNodesInTSCHookTest {
  /** Test [MinNodesInTSCHook] with fail policy [EvaluationHookResult.OK]. */
  @Test
  fun `Test MinNodesInTSCHook with fail policy OK`() {
    val tsc =
        tsc<SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference> {
          any("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val tickCountMetric = setup()
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 2, failPolicy = EvaluationHookResult.OK))

      runEvaluation(ticks = ticks())

      assertEquals(1, tickCountMetric.getState())
    }
  }

  /** Test [MinNodesInTSCHook] with fail policy [EvaluationHookResult.SKIP]. */
  @Test
  fun `Test MinNodesInTSCHook with fail policy SKIP`() {
    val tsc =
        tsc<SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference> {
          any("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val tickCountMetric = setup()
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 2, failPolicy = EvaluationHookResult.SKIP))

      runEvaluation(ticks = ticks())

      assertEquals(0, tickCountMetric.getState())
    }
  }

  /** Test [MinNodesInTSCHook] with fail policy [EvaluationHookResult.ABORT]. */
  @Test
  fun `Test MinNodesInTSCHook with fail policy ABORT`() {
    val tsc =
        tsc<SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference> {
          any("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      setup()
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 2, failPolicy = EvaluationHookResult.ABORT))

      assertThrows<EvaluationHookAbort> { runEvaluation(ticks = ticks()) }
    }
  }

  /** Test [MinNodesInTSCHook] with #nodes equal to minNodes. */
  @Test
  fun `Test MinNodesInTSCHook with #nodes equal to minNodes`() {
    val tsc =
        tsc<SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference> {
          any("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val tickCountMetric = setup()
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 1, failPolicy = EvaluationHookResult.ABORT))

      runEvaluation(ticks = ticks())

      assertEquals(1, tickCountMetric.getState())
    }
  }

  /** Test [MinNodesInTSCHook] with #nodes greater than minNodes. */
  @Test
  fun `Test MinNodesInTSCHook with #nodes greater than minNodes`() {
    val tsc =
        tsc<SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference> {
          any("") { any("") {} }
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val tickCountMetric = setup()
      registerPreTSCEvaluationHooks(
          MinNodesInTSCHook(minNodes = 1, failPolicy = EvaluationHookResult.ABORT))

      runEvaluation(ticks = ticks())

      assertEquals(1, tickCountMetric.getState())
    }
  }

  /** Test [MinNodesInTSCHook] with #nodes negative. */
  @Test
  fun `Test MinNodesInTSCHook with #nodes negative`() {
    assertFailsWith<IllegalArgumentException> {
      MinNodesInTSCHook<SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference>(
          minNodes = -1)
    }
  }

  private fun TSCEvaluation<
      SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference>
      .setup():
      TickCountMetric<SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference> =
      TickCountMetric<SimpleEntityData, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference>()
          .also {
            // Clear hooks to test them individually
            clearHooks()
            registerMetricProviders(it)
          }

  private fun ticks(): Sequence<SimpleTickData> {
    val entities = mutableListOf<SimpleEntityData>()
    val tickDataList = mutableListOf<SimpleTickData>()

    entities.add(SimpleEntityData(0))

    return tickDataList.asSequence()
  }
}
