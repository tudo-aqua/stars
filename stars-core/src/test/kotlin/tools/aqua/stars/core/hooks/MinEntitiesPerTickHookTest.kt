/*
 * Copyright 2023-2026 The STARS Project Authors
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
import tools.aqua.stars.core.hooks.defaulthooks.MinEntitiesPerTickHook
import tools.aqua.stars.core.metrics.evaluation.TickCountMetric
import tools.aqua.stars.core.tsc.builder.tsc

/** Class that contains tests for the [MinEntitiesPerTickHook]. */
class MinEntitiesPerTickHookTest {
  /** Test [MinEntitiesPerTickHook] with fail policy [EvaluationHookResult.OK]. */
  @Test
  fun `Test MinEntitiesPerTickHook with fail policy OK`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          all("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val tickCountMetric = setup()
      registerPreTickEvaluationHooks(
          MinEntitiesPerTickHook(minEntities = 2, failPolicy = EvaluationHookResult.OK)
      )

      runEvaluation(ticks = generateTicks())

      assertEquals(1, tickCountMetric.getState())
    }
  }

  /** Test [MinEntitiesPerTickHook] with fail policy [EvaluationHookResult.SKIP]. */
  @Test
  fun `Test MinEntitiesPerTickHook with fail policy SKIP`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          all("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val tickCountMetric = setup()
      registerPreTickEvaluationHooks(
          MinEntitiesPerTickHook(minEntities = 2, failPolicy = EvaluationHookResult.SKIP)
      )

      runEvaluation(ticks = generateTicks())

      assertEquals(0, tickCountMetric.getState())
    }
  }

  /** Test [MinEntitiesPerTickHook] with fail policy [EvaluationHookResult.ABORT]. */
  @Test
  fun `Test MinEntitiesPerTickHook with fail policy ABORT`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          all("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      setup()
      registerPreTickEvaluationHooks(
          MinEntitiesPerTickHook(minEntities = 2, failPolicy = EvaluationHookResult.ABORT)
      )

      assertThrows<EvaluationHookAbort> { runEvaluation(ticks = generateTicks()) }
    }
  }

  /** Test [MinEntitiesPerTickHook] with #entities equal to minEntities. */
  @Test
  fun `Test MinEntitiesPerTickHook with #entities equal to minEntities`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          all("") {}
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val tickCountMetric = setup()
      registerPreTickEvaluationHooks(
          MinEntitiesPerTickHook(minEntities = 1, failPolicy = EvaluationHookResult.ABORT)
      )

      runEvaluation(ticks = generateTicks())

      assertEquals(1, tickCountMetric.getState())
    }
  }

  /** Test [MinEntitiesPerTickHook] with #entities greater than minEntities. */
  @Test
  fun `Test MinEntitiesPerTickHook with #entities greater than minEntities`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          all("") { all("") {} }
        }
    TSCEvaluation(tscList = listOf(tsc), writePlots = false, writePlotDataCSV = false).apply {
      val tickCountMetric = setup()
      registerPreTickEvaluationHooks(
          MinEntitiesPerTickHook(minEntities = 0, failPolicy = EvaluationHookResult.ABORT)
      )

      runEvaluation(ticks = generateTicks())

      assertEquals(1, tickCountMetric.getState())
    }
  }

  /** Test [MinEntitiesPerTickHook] with #entities negative. */
  @Test
  fun `Test MinEntitiesPerTickHook with #entities negative`() {
    assertFailsWith<IllegalArgumentException> {
      MinEntitiesPerTickHook<
          SimpleEntity,
          SimpleTickData,
          SimpleTickDataUnit,
          SimpleTickDataDifference,
      >(
          minEntities = -1
      )
    }
  }

  private fun TSCEvaluation<
      SimpleEntity,
      SimpleTickData,
      SimpleTickDataUnit,
      SimpleTickDataDifference,
  >
      .setup():
      TickCountMetric<SimpleEntity, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference> =
      TickCountMetric<SimpleEntity, SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference>()
          .also {
            // Clear hooks to test them individually
            clearHooks()
            registerMetricProviders(it)
          }
}
