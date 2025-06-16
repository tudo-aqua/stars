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

import kotlin.test.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit

abstract class ManualLabelTests<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> {
  protected abstract val manualLabelTestFiles: List<ManualLabelFile<E, T, S, U, D>>

  @TestFactory
  fun testManualLabeledTestFiles(): List<DynamicTest> =
      manualLabelTestFiles.flatMap { manualLabelTestFile ->
        manualLabelTestFile.predicates.flatMap { manualLabelPredicate ->
          manualLabelPredicate.manualLabelIntervals.map { (from, to) ->
            DynamicTest.dynamicTest(
                "${manualLabelTestFile.testFilePath} | ${manualLabelPredicate.name} @ [$from,$to]s") {
                  assertEquals(
                      1,
                      2,
                  )

                  //              val data = YourDataLoader.load(manualFile.path)
                  //              val segment = data.segment(from, to)
                  //              when (val p = spec.pred) {
                  //                is NullaryPredicate<*, *, *, *, *> ->
                  //                    assertTrue(p.eval(PredicateContext.of(segment)))
                  //
                  //                is UnaryPredicate<*, *, *, *, *, *> ->
                  //                    // e.g. assert holds for each entity in segment,
                  //                    // or whatever your semantics are:
                  //                    segment.entities.forEach { e ->
                  //                      assertTrue(p.eval(PredicateContext.of(segment), e))
                  //                    }
                  //
                  //                is BinaryPredicate<*, *, *, *, *, *, *> ->
                  //                    // maybe you want to test all pairs, or some canonical pair:
                  //                    segment.entities.forEach { e1 ->
                  //                      segment.entities.forEach { e2 ->
                  //                        assertTrue(p.eval(PredicateContext.of(segment), e1, e2),
                  // "for $e1, $e2")
                  //                      }
                  //                    }

                  //                else -> error("Unsupported predicate type: ${p::class}")
                  //              }
                }
          }
        }
      }
}
