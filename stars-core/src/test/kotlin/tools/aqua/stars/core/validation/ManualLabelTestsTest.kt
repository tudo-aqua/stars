/*
 * Copyright 2026 The STARS Project Authors
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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.evaluation.Predicate

/** Tests for [ManualLabelTests], focusing on the optional interval configuration. */
class ManualLabelTestsTest {

  private val ticks: List<SimpleTickData> = (0L..4L).map { SimpleTickData(tickValue = it) }

  private val alwaysHolds = Predicate<SimpleTickData>("alwaysHolds") { true }
  private val neverHolds = Predicate<SimpleTickData>("neverHolds") { false }
  private val holdsUntilTick2 =
      Predicate<SimpleTickData>("holdsUntilTick2") { it.currentTickUnit.tickValue <= 2L }

  private fun runnerFor(
      file: ManualLabelFile<SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference>
  ) =
      object : ManualLabelTests<SimpleTickData, SimpleTickDataUnit, SimpleTickDataDifference>() {
        override val manualLabelTestFiles = listOf(file)
      }

  /** A `predicateHolds` without any interval yields a single test over the whole sequence. */
  @Test
  fun testPredicateHoldsWithoutIntervalCoversWholeSequence() {
    val file = manuallyLabelledFile(ticks) { predicateHolds(alwaysHolds) }

    val tests = runnerFor(file).testManualLabeledTestFiles()

    assertEquals(1, tests.size)
    assertTrue(tests.single().displayName.contains("whole tick sequence"))
    tests.single().executable.execute()
  }

  /** A `predicateHolds` without interval fails if the predicate does not hold on every tick. */
  @Test
  fun testPredicateHoldsWithoutIntervalFailsWhenNotAlwaysTrue() {
    val file = manuallyLabelledFile(ticks) { predicateHolds(holdsUntilTick2) }

    val test = runnerFor(file).testManualLabeledTestFiles().single()

    assertFailsWith<AssertionError> { test.executable.execute() }
  }

  /** A `predicateDoesNotHold` without interval passes if the predicate never holds. */
  @Test
  fun testPredicateDoesNotHoldWithoutIntervalPassesWhenNeverTrue() {
    val file = manuallyLabelledFile(ticks) { predicateDoesNotHold(neverHolds) }

    runnerFor(file).testManualLabeledTestFiles().single().executable.execute()
  }

  /** An explicit interval that selects no ticks fails instead of silently passing. */
  @Test
  fun testIntervalMatchingNoTicksFails() {
    val file =
        manuallyLabelledFile(ticks) {
          predicateHolds(alwaysHolds) {
            interval(SimpleTickDataUnit(10L), SimpleTickDataUnit(20L))
          }
        }

    val test = runnerFor(file).testManualLabeledTestFiles().single()

    assertFailsWith<IllegalStateException> { test.executable.execute() }
  }

  /** Configured intervals still produce one test per interval. */
  @Test
  fun testConfiguredIntervalsProduceOneTestEach() {
    val file =
        manuallyLabelledFile(ticks) {
          predicateHolds(holdsUntilTick2) {
            interval(SimpleTickDataUnit(0L), SimpleTickDataUnit(1L))
            interval(SimpleTickDataUnit(1L), SimpleTickDataUnit(2L))
          }
        }

    val tests = runnerFor(file).testManualLabeledTestFiles()

    assertEquals(2, tests.size)
    tests.forEach { it.executable.execute() }
  }

  /** `predicateHolds` and `predicateDoesNotHold` in the same file both generate a test. */
  @Test
  fun testHoldAndNotHoldWithoutIntervalsBothGenerateTests() {
    val file =
        manuallyLabelledFile(ticks) {
          predicateHolds(alwaysHolds)
          predicateDoesNotHold(neverHolds)
        }

    val tests = runnerFor(file).testManualLabeledTestFiles()

    assertEquals(2, tests.size)
    tests.forEach { it.executable.execute() }
  }
}
