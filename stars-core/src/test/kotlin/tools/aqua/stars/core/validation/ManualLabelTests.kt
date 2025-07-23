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

import tools.aqua.stars.core.types.EntityDataType
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit

abstract class ManualLabelTests<
    E : EntityDataType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> {
  protected abstract val manualLabelTestFiles: List<ManualLabelFile<E, T, U, D>>

  //  @TestFactory
  //  fun testManualLabeledTestFiles(): List<DynamicTest> =
  //      manualLabelTestFiles.flatMap { manualLabelTestFile ->
  //        manualLabelTestFile.predicates.flatMap { manualLabelPredicate ->
  //          manualLabelPredicate.manualLabelIntervals.map { (from, to) ->
  //            val correctSegment = manualLabelTestFile.segmentsToTest.first() // TODO
  //            DynamicTest.dynamicTest(
  //                "Predicate '${manualLabelPredicate.name}' should hold in '[$from,$to]s' in
  // segment '${correctSegment.getSegmentIdentifier()}'") {
  //                  val ctx = PredicateContext(correctSegment)
  //                  val predicate = manualLabelPredicate.predicate
  //                  when (predicate) {
  //                    is NullaryPredicate<E, T, S, U, D> -> assertTrue(predicate.holds(ctx))
  //                    is UnaryPredicate<*, E, T, S, U, D> -> assertTrue(predicate.holds(ctx))
  //                    is BinaryPredicate<*, *, E, T, S, U, D> -> assertTrue(predicate.holds(ctx))
  //                    else ->
  //                        error(
  //                            "Unsupported predicate type:
  // ${manualLabelPredicate.predicate::class}")
  //                  }
  //                }
  //          }
  //        }
  //      }
}
