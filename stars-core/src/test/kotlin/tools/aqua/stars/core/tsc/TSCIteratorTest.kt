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

package tools.aqua.stars.core.tsc

import kotlin.test.Test
import tools.aqua.stars.core.*
import tools.aqua.stars.core.tsc.builder.tsc

/** Test class for TSC iterator. */
class TSCIteratorTest {
  /** Test TSC Iterator. */
  @Test
  fun `Test TSC Iterator`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all("root") {
            exclusive("exclusive") {
              leaf("leaf_exclusive_1")
              leaf("leaf_exclusive_2")
            }

            any("any") {
              leaf("leaf_any_1")
              leaf("leaf_any_2")

              bounded("bounded", 1 to 2) {
                leaf("leaf_bounded_1")
                leaf("leaf_bounded_2")
                leaf("leaf_bounded_3")
              }
            }
          }
        }

    val expectedLabels =
        listOf(
            "root",
            "exclusive",
            "leaf_exclusive_1",
            "leaf_exclusive_2",
            "any",
            "leaf_any_1",
            "leaf_any_2",
            "bounded",
            "leaf_bounded_1",
            "leaf_bounded_2",
            "leaf_bounded_3")
    val iteratorLabels = tsc.map { it.label }

    assert(expectedLabels == iteratorLabels)
  }
}
