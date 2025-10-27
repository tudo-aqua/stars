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

package tools.aqua.stars.core.metrics

import kotlin.test.Test
import kotlin.test.assertEquals
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.metrics.postEvaluation.NWayFeatureCombinationsPerTSCMetric
import tools.aqua.stars.core.tsc.builder.tsc

typealias E = SimpleEntity

typealias T = SimpleTickData

typealias S = SimpleSegment

typealias U = SimpleTickDataUnit

typealias D = SimpleTickDataDifference

class NWayFeatureCombinationsPerTSCMetricTest {
  // region Correct calculation of possible feature combinations
  @Test
  fun `Test calculation of possible n-way feature combinations for single TSC metric with n equals 3 and too small TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("all") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }
    val testSegment = SimpleSegment()

    val tscEvaluation =
        TSCEvaluation(
            tscList = listOf(testTSC),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    tscEvaluation.clearHooks()
    val nWayFeatureCombinationsPerTSCMetric = NWayFeatureCombinationsPerTSCMetric<E, T, S, U, D>(3)

    tscEvaluation.registerMetricProviders(nWayFeatureCombinationsPerTSCMetric)

    tscEvaluation.runEvaluation(sequenceOf(testSegment))

    assertEquals(0, nWayFeatureCombinationsPerTSCMetric.getState().second[testTSC]?.size)
  }

  @Test
  fun `Test calculation of possible n-way feature combinations for single TSC metric with n equals 3`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("all") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
          }
        }
    val testSegment = SimpleSegment()

    val tscEvaluation =
        TSCEvaluation(
            tscList = listOf(testTSC),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    tscEvaluation.clearHooks()
    val nWayFeatureCombinationsPerTSCMetric = NWayFeatureCombinationsPerTSCMetric<E, T, S, U, D>(3)

    tscEvaluation.registerMetricProviders(nWayFeatureCombinationsPerTSCMetric)

    tscEvaluation.runEvaluation(sequenceOf(testSegment))

    assertEquals(1, nWayFeatureCombinationsPerTSCMetric.getState().second[testTSC]?.size)
  }

  @Test
  fun `Test calculation of possible n-way feature combinations for single TSC metric with n equals 3 and larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("all") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
            leaf("leaf 5")
          }
        }
    val testSegment = SimpleSegment()

    val tscEvaluation =
        TSCEvaluation(
            tscList = listOf(testTSC),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    tscEvaluation.clearHooks()
    val nWayFeatureCombinationsPerTSCMetric = NWayFeatureCombinationsPerTSCMetric<E, T, S, U, D>(3)

    tscEvaluation.registerMetricProviders(nWayFeatureCombinationsPerTSCMetric)

    tscEvaluation.runEvaluation(sequenceOf(testSegment))
    assertEquals(10, nWayFeatureCombinationsPerTSCMetric.getState().second[testTSC]?.size)
  }

  @Test
  fun `Test calculation of possible n-way feature combinations for single TSC metric with n equals 2`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("all") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }
    val testSegment = SimpleSegment()

    val tscEvaluation =
        TSCEvaluation(
            tscList = listOf(testTSC),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    tscEvaluation.clearHooks()
    val nWayFeatureCombinationsPerTSCMetric = NWayFeatureCombinationsPerTSCMetric<E, T, S, U, D>(2)

    tscEvaluation.registerMetricProviders(nWayFeatureCombinationsPerTSCMetric)

    tscEvaluation.runEvaluation(sequenceOf(testSegment))
    assertEquals(1, nWayFeatureCombinationsPerTSCMetric.getState().second[testTSC]?.size)
  }

  @Test
  fun `Test calculation of possible n-way feature combinations for single TSC metric with n equals 1`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("all") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }
    val testSegment = SimpleSegment()

    val tscEvaluation =
        TSCEvaluation(
            tscList = listOf(testTSC),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    tscEvaluation.clearHooks()
    val nWayFeatureCombinationsPerTSCMetric = NWayFeatureCombinationsPerTSCMetric<E, T, S, U, D>(1)

    tscEvaluation.registerMetricProviders(nWayFeatureCombinationsPerTSCMetric)

    tscEvaluation.runEvaluation(sequenceOf(testSegment))
    assertEquals(2, nWayFeatureCombinationsPerTSCMetric.getState().second[testTSC]?.size)
  }

  // endregion

  // region Correct calculation of observed feature combinations
  @Test
  fun `Test calculation of observed n-way feature combinations for single TSC with n equals 2`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("all") {
            leaf("leaf 1")
            leaf("leaf 2") { condition { false } }
          }
        }
    val testSegment = SimpleSegment()

    val tscEvaluation =
        TSCEvaluation(
            tscList = listOf(testTSC),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    tscEvaluation.clearHooks()
    val nWayFeatureCombinationsPerTSCMetric = NWayFeatureCombinationsPerTSCMetric<E, T, S, U, D>(2)

    tscEvaluation.registerMetricProviders(nWayFeatureCombinationsPerTSCMetric)

    tscEvaluation.runEvaluation(sequenceOf(testSegment))
    assertEquals(0, nWayFeatureCombinationsPerTSCMetric.getState().first[testTSC]?.size)
  }

  @Test
  fun `Test calculation of observed n-way feature combinations for single TSC with n equals 2 with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("all") {
            leaf("leaf 1")
            leaf("leaf 2") { condition { false } }
            leaf("leaf 3")
          }
        }
    val testSegment = SimpleSegment()

    val tscEvaluation =
        TSCEvaluation(
            tscList = listOf(testTSC),
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
        )
    tscEvaluation.clearHooks()
    val nWayFeatureCombinationsPerTSCMetric = NWayFeatureCombinationsPerTSCMetric<E, T, S, U, D>(2)

    tscEvaluation.registerMetricProviders(nWayFeatureCombinationsPerTSCMetric)

    tscEvaluation.runEvaluation(sequenceOf(testSegment))
    assertEquals(1, nWayFeatureCombinationsPerTSCMetric.getState().first[testTSC]?.size)
  }
  // endregion
}
