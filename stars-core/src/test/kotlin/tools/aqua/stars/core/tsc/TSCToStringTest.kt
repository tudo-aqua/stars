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
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows
import tools.aqua.stars.core.*
import tools.aqua.stars.core.tsc.builder.tsc

/** The tests in this class test the correct functionality of the [TSC.toString] method. */
class TSCToStringTest {

  /** Given an empty TSC, the println should also be empty. */
  @Test
  fun `Test empty TSC`() {
    assertThrows<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference>()
    }
  }

  // region Single node TSCs
  /** Given a TSC with only one single 'all' root node, "all(0..0)" should be returned. */
  @Test
  fun `Test TSC with single all node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          all("all")
        }
    assertEquals("all(0..0)", tsc.toString())
  }

  /** Given a TSC with only one single 'any' root node, "any(1..0)" should be returned. */
  @Test
  fun `Test TSC with single any node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          any("any")
        }
    assertEquals("any(1..0)", tsc.toString())
  }

  /**
   * Given a TSC with only one single 'exclusive' root node, "exclusive(1..1)" should be returned.
   */
  @Test
  fun `Test TSC with single exclusive node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          exclusive("exclusive")
        }
    assertEquals("exclusive(1..1)", tsc.toString())
  }

  /**
   * Given a TSC with only one single 'exclusive' bounded node, "bounded(1..1)" should be returned.
   */
  @Test
  fun `Test TSC with single bounded node with bounds (2,3) and no children`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          bounded("bounded", 2 to 3)
        }
    assertEquals("bounded(2..3)", tsc.toString())
  }

  /** Given a TSC with only one single 'leaf' root node, "leaf" should be returned. */
  @Test
  fun `Test TSC with single leaf node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          leaf("leaf")
        }
    assertEquals("leaf", tsc.toString())
  }

  /** Given a TSC with only one single 'optional' root node, "optional(0..0)" should be returned. */
  @Test
  fun `Test TSC with single optional node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          optional("optional")
        }
    assertEquals("optional(0..0)", tsc.toString())
  }

  // endregion

  // region Simple node hierarchy TSCs
  /**
   * Given a TSC with one 'all' root node and 3 child nodes, "all(3..3)\n-T-> leaf_1\n-T->
   * leaf_2\n-T-> leaf_3" should be returned.
   */
  @Test
  fun `Test TSC with all node and 3 children`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          all("all") {
            leaf("leaf_1")
            leaf("leaf_2")
            leaf("leaf_3")
          }
        }
    assertEquals("all(3..3)\n-T-> leaf_1\n-T-> leaf_2\n-T-> leaf_3", tsc.toString())
  }

  /**
   * Given a TSC with one 'exclusive' root node and 3 child nodes, "exclusive(1..1)" should be
   * returned.
   */
  @Test
  fun `Test TSC with exclusive node and 3 children`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          exclusive("exclusive") {
            leaf("leaf_1")
            leaf("leaf_2")
            leaf("leaf_3")
          }
        }
    assertEquals("exclusive(1..1)\n-T-> leaf_1\n-T-> leaf_2\n-T-> leaf_3", tsc.toString())
  }

  /** Given a TSC with one 'any' root node and 3 child nodes, "any(1..3)" should be returned. */
  @Test
  fun `Test TSC with any node and 3 children`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          any("any") {
            leaf("leaf_1")
            leaf("leaf_2")
            leaf("leaf_3")
          }
        }
    assertEquals("any(1..3)\n-T-> leaf_1\n-T-> leaf_2\n-T-> leaf_3", tsc.toString())
  }

  /**
   * Given a TSC with one 'bounded' root node, with bounds (2,3) and 3 child nodes, "bounded(2..3)"
   * should be returned.
   */
  @Test
  fun `Test TSC with bounded node, with bounds (2,3) and 3 children`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          bounded("bounded", 2 to 3) {
            leaf("leaf_1")
            leaf("leaf_2")
            leaf("leaf_3")
          }
        }
    assertEquals("bounded(2..3)\n-T-> leaf_1\n-T-> leaf_2\n-T-> leaf_3", tsc.toString())
  }

  /**
   * Given a TSC with one 'optional' root node and 3 child nodes, "optional(0..3)" should be
   * returned.
   */
  @Test
  fun `Test TSC with optional node and 3 children`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          optional("optional") {
            leaf("leaf_1")
            leaf("leaf_2")
            leaf("leaf_3")
          }
        }
    assertEquals("optional(0..3)\n-T-> leaf_1\n-T-> leaf_2\n-T-> leaf_3", tsc.toString())
  }

  // endregion

  // region TSC with multiple layers of hierarchy
  /**
   * Given a TSC with one 'all' root node and 1 child node with 2 children, "all(1..1) -T->
   * any(1..3) -T-> leaf_1 -T-> leaf_2 -T-> leaf_3" should be returned.
   */
  @Test
  fun `Test TSC with all node and 1 child node with 2 children`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>() {
          all("all") {
            any("any") {
              leaf("leaf_1")
              leaf("leaf_2")
              leaf("leaf_3")
            }
          }
        }
    assertEquals(
        "all(1..1)\n-T-> any(1..3)\n  -T-> leaf_1\n  -T-> leaf_2\n  -T-> leaf_3", tsc.toString())
  }
  // endregion
}
