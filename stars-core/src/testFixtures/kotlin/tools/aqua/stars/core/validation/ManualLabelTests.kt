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
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit
import tools.aqua.stars.core.utils.getTicksInInterval

/**
 * Abstract base class for testing predicates within manually labeled test files, which are
 * associated with ticks and intervals. This class provides a test factory for generating dynamic
 * tests which can be used in actual testing classes.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 */
abstract class ManualLabelTests<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
> {
  protected abstract val manualLabelTestFiles: List<ManualLabelFile<E, T, U, D>>

  /**
   * Generates dynamic tests for manually labeled test files by validating predicates on specified
   * intervals of tick sequences. The method processes manually labeled test files to generate test
   * cases for predicates expected to hold and not to hold in certain intervals of given tick
   * sequences.
   *
   * @return A list of dynamically generated tests based on the manual labeling configurations
   *   provided in the test files. For each entry in [manualLabelTestFiles], a list of dynamic tests
   *   for each predicate is generated.
   */
  @TestFactory
  fun testManualLabeledTestFiles(): List<DynamicTest> =
      manualLabelTestFiles.flatMap { manualLabelTestFile ->
        val allTicks = manualLabelTestFile.ticksToTest
        val dynamicTests = mutableListOf<DynamicTest>()
        dynamicTests.addAll(
            manualLabelTestFile.predicatesToHold.flatMap { manualLabelPredicate ->
              manualLabelPredicate.manualLabelIntervals.map { (from, to) ->
                createDynamicTest(manualLabelPredicate.predicate, from, to, allTicks, true)
              }
            }
        )
        dynamicTests.addAll(
            manualLabelTestFile.predicatesToNotHold.flatMap { manualLabelPredicate ->
              manualLabelPredicate.manualLabelIntervals.map { (from, to) ->
                createDynamicTest(manualLabelPredicate.predicate, from, to, allTicks, false)
              }
            }
        )
        return dynamicTests
      }

  /**
   * Creates a dynamic test that evaluates whether a given predicate holds or does not hold for a
   * specified interval within a sequence of ticks.
   *
   * @param predicate The predicate to be evaluated.
   * @param from The start of the interval (inclusive).
   * @param to The end of the interval (inclusive).
   * @param allTicks The list of all ticks to be analyzed.
   * @param shouldHold Indicates whether the predicate is expected to hold (true) or not hold
   *   (false) in the interval.
   * @return A dynamically generated test case for the specified predicate and interval.
   */
  private fun createDynamicTest(
      predicate: Predicate<E, T, U, D>,
      from: U,
      to: U,
      allTicks: List<T>,
      shouldHold: Boolean,
  ): DynamicTest {
    val matchingTicks = allTicks.getTicksInInterval(from, to)
    return DynamicTest.dynamicTest(
        "Predicate '${'$'}{predicate.name}' should ${if (shouldHold) "" else "not"} hold in '[${'$'}from,${'$'}to]s'"
    ) {
      val predicate = predicate
      if (shouldHold) {
        matchingTicks.forEach { tick -> assertTrue(predicate.holds(tick)) }
      } else {
        matchingTicks.forEach { tick -> assertFalse(predicate.holds(tick)) }
      }
    }
  }
}
