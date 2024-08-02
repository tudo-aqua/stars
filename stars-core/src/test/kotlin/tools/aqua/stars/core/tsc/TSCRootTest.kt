/*
 * Copyright 2023-2024 The STARS Project Authors
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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.core.*
import tools.aqua.stars.core.tsc.builder.tsc

/** Tests for root node DSL function. */
class TSCRootTest {

  /** Test adding one node as root. */
  @Test
  fun `Test adding one node as root`() {
    val label = "root_node_label"
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all(label)
        }

    assertEquals(label, tsc.rootNode.label)
  }

  /** Test adding two nodes as root. */
  @Test
  fun `Test adding two nodes as root`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("all_1")
        all("all_2")
      }
    }
  }

  /** Test adding two nodes as root. */
  @Test
  fun `Test adding no node as root`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {}
    }
  }
}
