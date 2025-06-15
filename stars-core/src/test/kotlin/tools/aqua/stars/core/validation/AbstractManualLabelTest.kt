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

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import tools.aqua.stars.core.evaluation.BinaryPredicate
import tools.aqua.stars.core.evaluation.NullaryPredicate
import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.evaluation.UnaryPredicate

abstract class AbstractManualLabelTest {
  protected abstract val manuallyLabelledTests: List<FileSpec>

  @TestFactory
  fun labeledPredicateTests(): List<DynamicTest> =
      manuallyLabelledTests.flatMap { manualFile ->
        manualFile.specs.flatMap { spec ->
          spec.intervals.map { (from, to) ->
            DynamicTest.dynamicTest("${manualFile.path} | ${spec.name} @ [$from,$to]s") {
              val data = YourDataLoader.load(manualFile.path)
              val segment = data.segment(from, to)
              when (val p = spec.pred) {
                is NullaryPredicate<*, *, *, *, *> ->
                    assertTrue(p.eval(PredicateContext.of(segment)))

                is UnaryPredicate<*, *, *, *, *, *> ->
                    // e.g. assert holds for each entity in segment,
                    // or whatever your semantics are:
                    segment.entities.forEach { e ->
                      assertTrue(p.eval(PredicateContext.of(segment), e))
                    }

                is BinaryPredicate<*, *, *, *, *, *, *> ->
                    // maybe you want to test all pairs, or some canonical pair:
                    segment.entities.forEach { e1 ->
                      segment.entities.forEach { e2 ->
                        assertTrue(p.eval(PredicateContext.of(segment), e1, e2), "for $e1, $e2")
                      }
                    }

                else -> error("Unsupported predicate type: ${p::class}")
              }
            }
          }
        }
      }
}
