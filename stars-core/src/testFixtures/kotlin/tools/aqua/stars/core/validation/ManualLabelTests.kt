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

package tools.aqua.stars.core.validation

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import tools.aqua.stars.core.evaluation.Predicate
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit
import tools.aqua.stars.core.utils.getTicksInInterval

/**
 * Abstract base class for testing predicates within manually labeled test files, which are
 * associated with ticks and intervals. This class provides a test factory for generating dynamic
 * tests which can be used in actual testing classes.
 *
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 */
abstract class ManualLabelTests<
    T : TickDataType<*, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
> {
  protected abstract val manualLabelTestFiles: List<ManualLabelFile<T, U, D>>

  /**
   * Generates dynamic tests for manually labeled test files by validating predicates on tick
   * sequences. The method processes manually labeled test files to generate test cases for
   * predicates expected to hold and not to hold.
   *
   * A predicate configured without any interval is validated against every tick in the file;
   * otherwise one test per configured interval is generated.
   *
   * @return A list of dynamically generated tests based on the manual labeling configurations
   *   provided in the test files. For each entry in [manualLabelTestFiles], a list of dynamic tests
   *   for each predicate is generated.
   */
  @TestFactory
  fun testManualLabeledTestFiles(): List<DynamicTest> =
      manualLabelTestFiles.flatMap { manualLabelTestFile ->
        val allTicks = manualLabelTestFile.ticksToTest
        check(allTicks.any()) { "There has to be at least one tick in the sequence of ticks." }

        val labeledPredicates =
            manualLabelTestFile.predicatesToHold.map { it to true } +
                manualLabelTestFile.predicatesToNotHold.map { it to false }

        labeledPredicates.flatMap { (manualLabelPredicate, shouldHold) ->
          val intervals = manualLabelPredicate.manualLabelIntervals
          if (intervals.isEmpty()) {
            // No interval configured: validate the predicate against the whole tick sequence.
            listOf(createDynamicTest(manualLabelPredicate.predicate, null, allTicks, shouldHold))
          } else {
            intervals.map { interval ->
              createDynamicTest(manualLabelPredicate.predicate, interval, allTicks, shouldHold)
            }
          }
        }
      }

  /**
   * Creates a dynamic test that evaluates whether a given predicate holds or does not hold within a
   * sequence of ticks.
   *
   * @param predicate The predicate to be evaluated.
   * @param interval The interval to restrict the evaluation to, or `null` to evaluate every tick.
   * @param allTicks The list of all ticks to be analyzed.
   * @param shouldHold Indicates whether the predicate is expected to hold (true) or not hold
   *   (false).
   * @return A dynamically generated test case for the specified predicate and interval.
   */
  private fun createDynamicTest(
      predicate: Predicate<T>,
      interval: ManualLabelInterval<U, D>?,
      allTicks: List<T>,
      shouldHold: Boolean,
  ): DynamicTest {
    val matchingTicks =
        interval?.let { allTicks.getTicksInInterval(it.fromTickUnit, it.toTickUnit) } ?: allTicks
    val intervalDescription =
        interval?.let { "[${it.fromTickUnit}, ${it.toTickUnit}]" } ?: "the whole tick sequence"
    return DynamicTest.dynamicTest(
        "Predicate '${predicate.name}' should ${if (shouldHold) "" else "not"} hold in $intervalDescription"
    ) {
      // A configured interval that selects no ticks is a misconfiguration, not a pass.
      if (interval != null) {
        check(matchingTicks.isNotEmpty()) {
          "Interval [${interval.fromTickUnit}, ${interval.toTickUnit}] selects no ticks; " +
              "check the tick units against the ticks in the file."
        }
      }
      when (shouldHold) {
        true -> matchingTicks.forEach { tick -> assertTrue(predicate.holds(tick)) }
        false -> matchingTicks.forEach { tick -> assertFalse(predicate.holds(tick)) }
      }
    }
  }
}
