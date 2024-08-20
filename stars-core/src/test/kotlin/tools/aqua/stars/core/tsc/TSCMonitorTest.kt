/*
 * Copyright 2023-2024 The STARS Project Authors
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

package tools.aqua.stars.core.tsc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import tools.aqua.stars.core.*
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.metrics.postEvaluation.FailedMonitorsMetric
import tools.aqua.stars.core.tsc.builder.tsc

/** Tests for monitors. */
class TSCMonitorTest {

  /** This test check that a monitor attached to the root node triggers correctly. */
  @Test
  fun `Test monitor on root node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all("root") {
            projections { projectionRecursive("all") }

            monitors {
              monitor("RootMonitorFalse") { _ -> false } // Always trigger
              monitor("RootMonitorTrue") { _ -> true } // Never trigger
            }
          }
        }

    val validInstancesMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()
    val failedMonitorsMetric = FailedMonitorsMetric(validInstancesMetric)

    TSCEvaluation(tscList = tsc.buildProjections(), writePlots = false, writePlotDataCSV = false)
        .apply {
          registerMetricProviders(validInstancesMetric, failedMonitorsMetric)
          runEvaluation(segments = segments())
        }

    val failedMonitors = failedMonitorsMetric.failedMonitors.values.first()
    assertEquals(1, failedMonitors.size)
    assertEquals("root", failedMonitors.first().nodeLabel)
    assertEquals("RootMonitorFalse", failedMonitors.first().monitorLabel)
  }

  /** This test check that a monitor attached to a leaf node triggers correctly. */
  @Test
  fun `Test monitor on leaf node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all("root") {
            projections { projectionRecursive("all") }

            leaf("leaf") {
              monitors {
                monitor("LeafMonitorFalse") { _ -> false } // Always trigger
                monitor("LeafMonitorTrue") { _ -> true } // Never trigger
              }
            }
          }
        }

    val validInstancesMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()
    val failedMonitorsMetric = FailedMonitorsMetric(validInstancesMetric)

    TSCEvaluation(tscList = tsc.buildProjections(), writePlots = false, writePlotDataCSV = false)
        .apply {
          registerMetricProviders(validInstancesMetric, failedMonitorsMetric)
          runEvaluation(segments = segments())
        }

    val failedMonitors = failedMonitorsMetric.failedMonitors.values.first()
    assertEquals(1, failedMonitors.size)
    assertEquals("leaf", failedMonitors.first().nodeLabel)
    assertEquals("LeafMonitorFalse", failedMonitors.first().monitorLabel)
  }

  /** This test check that all monitors in root and leaf nodes trigger correctly. */
  @Test
  fun `Test monitor on root and leaf node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all("root") {
            projections { projectionRecursive("all") }

            monitors {
              monitor("RootMonitorFalse") { _ -> false } // Always trigger
              monitor("RootMonitorTrue") { _ -> true } // Never trigger
            }

            leaf("leaf") {
              monitors {
                monitor("LeafMonitorFalse") { _ -> false } // Always trigger
                monitor("LeafMonitorTrue") { _ -> true } // Never trigger
              }
            }
          }
        }

    val validInstancesMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()
    val failedMonitorsMetric = FailedMonitorsMetric(validInstancesMetric)

    TSCEvaluation(tscList = tsc.buildProjections(), writePlots = false, writePlotDataCSV = false)
        .apply {
          registerMetricProviders(validInstancesMetric, failedMonitorsMetric)
          runEvaluation(segments = segments())
        }

    val failedMonitors = failedMonitorsMetric.failedMonitors.values.first()
    assertEquals(2, failedMonitors.size)
    assertTrue(
        failedMonitors.any { it.nodeLabel == "root" && it.monitorLabel == "RootMonitorFalse" })
    assertTrue(
        failedMonitors.any { it.nodeLabel == "leaf" && it.monitorLabel == "LeafMonitorFalse" })
  }

  /**
   * This test check that all monitors in root and leaf nodes trigger correctly with identical
   * label.
   */
  @Test
  fun `Test monitor on root and leaf node with identical label`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all("root") {
            projections { projectionRecursive("all") }

            monitors {
              monitor("MonitorFalse") { _ -> false } // Always trigger
              monitor("MonitorTrue") { _ -> true } // Never trigger
            }

            leaf("leaf") {
              monitors {
                monitor("MonitorFalse") { _ -> false } // Always trigger
                monitor("MonitorTrue") { _ -> true } // Never trigger
              }
            }
          }
        }

    val validInstancesMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()
    val failedMonitorsMetric = FailedMonitorsMetric(validInstancesMetric)

    TSCEvaluation(tscList = tsc.buildProjections(), writePlots = false, writePlotDataCSV = false)
        .apply {
          registerMetricProviders(validInstancesMetric, failedMonitorsMetric)
          runEvaluation(segments = segments())
        }

    assertTrue { failedMonitorsMetric.failedMonitors.any() }

    val failedMonitors = failedMonitorsMetric.failedMonitors.values.first()

    assertEquals(2, failedMonitors.size)
    assertTrue(failedMonitors.any { it.nodeLabel == "root" && it.monitorLabel == "MonitorFalse" })
    assertTrue(failedMonitors.any { it.nodeLabel == "leaf" && it.monitorLabel == "MonitorFalse" })
  }

  private fun segments(): Sequence<SimpleSegment> {
    val entities = mutableListOf<SimpleEntity>()
    val tickdatas = mutableMapOf<SimpleTickDataUnit, SimpleTickData>()
    val segments = listOf(SimpleSegment(tickdatas)).asSequence()
    val tick = SimpleTickDataUnit(0)
    val tickdata = SimpleTickData(tick)
    tickdatas[tick] = tickdata
    entities.add(SimpleEntity(0, tickdata))

    return segments
  }
}
