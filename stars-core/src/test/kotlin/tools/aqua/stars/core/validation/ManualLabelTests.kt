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

package tools.aqua.stars.core.validation

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import tools.aqua.stars.core.evaluation.AbstractPredicate
import tools.aqua.stars.core.evaluation.BinaryPredicate
import tools.aqua.stars.core.evaluation.NullaryPredicate
import tools.aqua.stars.core.evaluation.UnaryPredicate
import tools.aqua.stars.core.getTicksInInterval
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit

abstract class ManualLabelTests<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> {
  protected abstract val manualLabelTestFiles: List<ManualLabelFile<E, T, U, D>>

  @TestFactory
  fun testManualLabeledTestFiles(): List<DynamicTest> =
      manualLabelTestFiles.flatMap { manualLabelTestFile ->
        val tickSequences = manualLabelTestFile.ticksToTest.toList()
        tickSequences.flatMap { tickSequence ->
          val allTicks = tickSequence.toList()
          val dynamicTests = mutableListOf<DynamicTest>()
          dynamicTests.addAll(
              manualLabelTestFile.predicatesToHold.flatMap { manualLabelPredicate ->
                manualLabelPredicate.manualLabelIntervals.map { (from, to) ->
                  createDynamicTest(manualLabelPredicate.predicate, from, to, allTicks, true)
                }
              })
          dynamicTests.addAll(
              manualLabelTestFile.predicatesToNotHold.flatMap { manualLabelPredicate ->
                manualLabelPredicate.manualLabelIntervals.map { (from, to) ->
                  createDynamicTest(manualLabelPredicate.predicate, from, to, allTicks, false)
                }
              })
          return dynamicTests
        }
      }

  private fun createDynamicTest(
      predicate: AbstractPredicate<E, T, U, D>,
      from: U,
      to: U,
      allTicks: List<T>,
      shouldHold: Boolean
  ): DynamicTest {
    val matchingTicks = allTicks.getTicksInInterval(from, to)
    return DynamicTest.dynamicTest(
        "Predicate '${predicate.name}' should ${if (shouldHold) "" else "not"} hold in '[$from,$to]s'") {
          val predicate = predicate
          if (shouldHold) {
            matchingTicks.forEach { tick -> assertTrue(evaluatePredicateAtTick(predicate, tick)) }
          } else {
            matchingTicks.forEach { tick -> assertFalse(evaluatePredicateAtTick(predicate, tick)) }
          }
        }
  }

  private fun evaluatePredicateAtTick(predicate: AbstractPredicate<E, T, U, D>, tick: T) =
      when (predicate) {
        is NullaryPredicate<E, T, U, D> -> predicate.holds(tick)
        is UnaryPredicate<*, E, T, U, D> -> predicate.holds(tick)
        is BinaryPredicate<*, *, E, T, U, D> -> predicate.holds(tick)
        else -> error("Unsupported predicate type: ${predicate::class}")
      }
}
