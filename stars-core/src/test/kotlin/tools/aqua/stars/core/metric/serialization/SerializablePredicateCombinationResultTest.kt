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

package tools.aqua.stars.core.metric.serialization

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.metrics.postEvaluation.MissedPredicateCombinationsPerTSCMetric

/**
 * Tests the [SerializableResult] sealed class implementation for the
 * [SerializablePredicateCombinationResult].
 */
class SerializablePredicateCombinationResultTest {

  /**
   * Tests the correct calculation and return of a [SerializablePredicateCombinationResult] for a
   * valid TSC instance with no remaining missed predicates combinations.
   */
  @Test
  fun `Test no missing predicate combinations`() {
    // Initialize base metric
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate and populate base metric
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCValidInstance)

    // Initialize actual metric
    val missedPredicateCombinationsPerTSCMetric =
        MissedPredicateCombinationsPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            validTSCInstancesPerTSCMetric)

    // Post evaluate and populate actual metric
    missedPredicateCombinationsPerTSCMetric.postEvaluate()

    // Get serialized results
    val result = missedPredicateCombinationsPerTSCMetric.getSerializableResults()
    assertEquals(1, result.size)
    assertEquals(0, result[0].value.size)
  }

  /**
   * Tests the correct calculation and return of a [SerializablePredicateCombinationResult] for a
   * valid TSC instance with one remaining missed predicates combination.
   */
  @Test
  fun `Test one missing predicate combination`() {
    // Initialize base metric
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate and populate base metric
    // Uses a valid TSC instance where only one of two leaf nodes is present
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC3, simpleTSC3ValidInstance2)

    // Initialize actual metric
    val missedPredicateCombinationsPerTSCMetric =
        MissedPredicateCombinationsPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            validTSCInstancesPerTSCMetric)

    // Post evaluate and populate actual metric
    missedPredicateCombinationsPerTSCMetric.postEvaluate()

    // Get serialized results
    val result = missedPredicateCombinationsPerTSCMetric.getSerializableResults()
    assertEquals(1, result.size)

    assertEquals(1, result[0].value.size)
    assertEquals("\n--> root\n  --> leaf1" to "\n--> root\n  --> leaf2", result[0].value.first())
  }

  /**
   * Tests the correct calculation and return of a [SerializablePredicateCombinationResult] for an
   * invalid TSC instance with one remaining missed predicates combination.
   */
  @Test
  fun `Test one missing predicate combination with no valid TSC instance`() {
    // Initialize base metric
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate and populate base metric
    // Uses a valid TSC instance where only one of two leaf nodes is present
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC3, simpleTSC3InvalidInstance)

    // Initialize actual metric
    val missedPredicateCombinationsPerTSCMetric =
        MissedPredicateCombinationsPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            validTSCInstancesPerTSCMetric)

    // Post evaluate and populate actual metric
    missedPredicateCombinationsPerTSCMetric.postEvaluate()

    // Get serialized results
    val result = missedPredicateCombinationsPerTSCMetric.getSerializableResults()
    assertEquals(1, result.size)

    assertEquals(1, result[0].value.size)
    assertEquals("\n--> root\n  --> leaf1" to "\n--> root\n  --> leaf2", result[0].value.first())
  }

  /**
   * Tests the correct calculation and return of a [SerializablePredicateCombinationResult] for a
   * valid but empty TSC instance with all remaining missed predicates combination.
   */
  @Test
  fun `Test multiple missing predicate combinations with valid empty TSC instance`() {
    // Initialize base metric
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Evaluate and populate base metric
    // Uses a valid TSC instance where none of three leaf nodes is present
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC4, simpleTSC4ValidInstance3)

    // Initialize actual metric
    val missedPredicateCombinationsPerTSCMetric =
        MissedPredicateCombinationsPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            validTSCInstancesPerTSCMetric)

    // Post evaluate and populate actual metric
    missedPredicateCombinationsPerTSCMetric.postEvaluate()

    // Get serialized results
    val result = missedPredicateCombinationsPerTSCMetric.getSerializableResults()
    assertEquals(1, result.size)

    assertEquals(3, result[0].value.size)
    assertContains(result[0].value, "\n--> root\n  --> leaf1" to "\n--> root\n  --> leaf2")
    assertContains(result[0].value, "\n--> root\n  --> leaf1" to "\n--> root\n  --> leaf3")
    assertContains(result[0].value, "\n--> root\n  --> leaf2" to "\n--> root\n  --> leaf3")
  }
}
