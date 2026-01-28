/*
 * Copyright 2025-2026 The STARS Project Authors
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
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.generateTicks
import tools.aqua.stars.core.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metrics.postEvaluation.MissedPredicatesPerTSCMetric
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.tsc

typealias E = SimpleEntity

typealias T = SimpleTickData

typealias U = SimpleTickDataUnit

typealias D = SimpleTickDataDifference

/** Tests the [MissedPredicatesPerTSCMetric] implementation. */
class MissedPredicatesPerTSCMetricTest {

  /**
   * Tests the correct calculation and return of a [MissedPredicatesPerTSCMetric] for a [TSC] in
   * which no predicate holds.
   */
  @Test
  fun `Test MissedPredicatesPerTSCMetric for one missing predicate`() {
    val simpleTsc = tsc<E, T, U, D> { all("root") { leaf("leaf1") { condition { false } } } }
    val evaluation =
        TSCEvaluation(
            tscList = listOf(simpleTsc),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    evaluation.clearHooks()
    val validTSCInstancesPerTSCMetric = ValidTSCInstancesPerTSCMetric<E, T, U, D>()
    evaluation.registerMetricProviders(validTSCInstancesPerTSCMetric)
    evaluation.runEvaluation(generateTicks())

    val missedPredicatesPerTSCMetric = MissedPredicatesPerTSCMetric(validTSCInstancesPerTSCMetric)

    val result = missedPredicatesPerTSCMetric.postEvaluate()
    assertEquals(1, result[simpleTsc]?.size)
    assertEquals("\n--> root\n  --> leaf1", result[simpleTsc]?.first())
  }

  /**
   * Tests the correct calculation and return of a [MissedPredicatesPerTSCMetric] for a [TSC] in
   * which all predicates hold.
   */
  @Test
  fun `Test MissedPredicatesPerTSCMetric for no missing predicates`() {
    val simpleTsc = tsc<E, T, U, D> { all("root") { leaf("leaf1") { condition { true } } } }
    val evaluation =
        TSCEvaluation(
            tscList = listOf(simpleTsc),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    evaluation.clearHooks()
    val validTSCInstancesPerTSCMetric = ValidTSCInstancesPerTSCMetric<E, T, U, D>()
    evaluation.registerMetricProviders(validTSCInstancesPerTSCMetric)
    evaluation.runEvaluation(generateTicks())

    val missedPredicatesPerTSCMetric = MissedPredicatesPerTSCMetric(validTSCInstancesPerTSCMetric)

    val result = missedPredicatesPerTSCMetric.postEvaluate()
    assertEquals(0, result[simpleTsc]?.size)
  }

  /**
   * Tests the correct calculation and return of a [MissedPredicatesPerTSCMetric] for two [TSC]s in
   * which no predicates hold.
   */
  @Test
  fun `Test MissedPredicatesPerTSCMetric for one missing predicate for two TSCs`() {
    val simpleTsc = tsc<E, T, U, D> { all("root1") { leaf("leaf1") { condition { false } } } }
    val simpleTsc2 = tsc<E, T, U, D> { all("root2") { leaf("leaf2") { condition { false } } } }
    val evaluation =
        TSCEvaluation(
            tscList = listOf(simpleTsc, simpleTsc2),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    evaluation.clearHooks()
    val validTSCInstancesPerTSCMetric = ValidTSCInstancesPerTSCMetric<E, T, U, D>()
    evaluation.registerMetricProviders(validTSCInstancesPerTSCMetric)
    evaluation.runEvaluation(generateTicks())

    val missedPredicatesPerTSCMetric = MissedPredicatesPerTSCMetric(validTSCInstancesPerTSCMetric)

    val result = missedPredicatesPerTSCMetric.postEvaluate()

    assertEquals(2, result.size)

    assertEquals(1, result[simpleTsc]?.size)
    assertEquals("\n--> root1\n  --> leaf1", result[simpleTsc]?.first())

    assertEquals(1, result[simpleTsc2]?.size)
    assertEquals("\n--> root2\n  --> leaf2", result[simpleTsc2]?.first())
  }

  /**
   * Tests the correct calculation and return of a [MissedPredicatesPerTSCMetric] for two [TSC]s in
   * which all predicates hold.
   */
  @Test
  fun `Test MissedPredicatesPerTSCMetric for no missing predicates for two TSCs`() {
    val simpleTsc = tsc<E, T, U, D> { all("root1") { leaf("leaf1") { condition { true } } } }
    val simpleTsc2 = tsc<E, T, U, D> { all("root2") { leaf("leaf2") { condition { true } } } }
    val evaluation =
        TSCEvaluation(
            tscList = listOf(simpleTsc, simpleTsc2),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    evaluation.clearHooks()
    val validTSCInstancesPerTSCMetric = ValidTSCInstancesPerTSCMetric<E, T, U, D>()
    evaluation.registerMetricProviders(validTSCInstancesPerTSCMetric)
    evaluation.runEvaluation(generateTicks())

    val missedPredicatesPerTSCMetric = MissedPredicatesPerTSCMetric(validTSCInstancesPerTSCMetric)

    val result = missedPredicatesPerTSCMetric.postEvaluate()

    assertEquals(2, result.size)

    assertEquals(0, result[simpleTsc]?.size)

    assertEquals(0, result[simpleTsc2]?.size)
  }

  /**
   * Tests the correct calculation and return of a [MissedPredicatesPerTSCMetric] for two [TSC]s in
   * which one predicate holds and the other does not.
   */
  @Test
  fun `Test MissedPredicatesPerTSCMetric for one missing predicate for one of two TSCs`() {
    val simpleTsc = tsc<E, T, U, D> { all("root1") { leaf("leaf1") { condition { false } } } }
    val simpleTsc2 = tsc<E, T, U, D> { all("root2") { leaf("leaf2") { condition { true } } } }
    val evaluation =
        TSCEvaluation(
            tscList = listOf(simpleTsc, simpleTsc2),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    evaluation.clearHooks()
    val validTSCInstancesPerTSCMetric = ValidTSCInstancesPerTSCMetric<E, T, U, D>()
    evaluation.registerMetricProviders(validTSCInstancesPerTSCMetric)
    evaluation.runEvaluation(generateTicks())

    val missedPredicatesPerTSCMetric = MissedPredicatesPerTSCMetric(validTSCInstancesPerTSCMetric)

    val result = missedPredicatesPerTSCMetric.postEvaluate()

    assertEquals(2, result.size)

    assertEquals(1, result[simpleTsc]?.size)
    assertEquals("\n--> root1\n  --> leaf1", result[simpleTsc]?.first())

    assertEquals(0, result[simpleTsc2]?.size)
  }
}
