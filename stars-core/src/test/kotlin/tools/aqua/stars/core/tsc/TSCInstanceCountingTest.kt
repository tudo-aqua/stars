/*
 * Copyright 2023-2025 The STARS Project Authors
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

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import tools.aqua.stars.core.*
import tools.aqua.stars.core.tsc.builder.tsc

/** Tests for counting of possible tsc instances. */
class TSCInstanceCountingTest {

  /** Test empty tsc. */
  @Test
  fun `Test empty tsc`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          leaf("root")
        }

    assertEquals(BigInteger.ONE, tsc.instanceCount)
  }

  /** Test simple tsc with only leaves and all node. */
  @Test
  fun `Test simple tsc with only leaves and all node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          all("root") {
            leaf("leaf1")
            leaf("leaf2")
            leaf("leaf3")
          }
        }

    assertEquals(BigInteger.ONE, tsc.instanceCount)
  }

  /**
   * Test simple tsc with only leaves and optional nodeTest simple tsc with only leaves and optional
   * node.
   */
  @Test
  fun `Test simple tsc with only leaves and optional node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          optional("root") {
            leaf("leaf1")
            leaf("leaf2")
            leaf("leaf3")
          }
        }

    // ()
    // (1), (2), (3)
    // (1,2), (1,3), (2,3)
    // (1,2,3)
    assertEquals(BigInteger.valueOf(8), tsc.instanceCount)
  }

  /** Test simple tsc with only leaves and bounded node. */
  @Test
  fun `Test simple tsc with only leaves and bounded node`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          bounded("root", 1 to 2) {
            leaf("leaf1")
            leaf("leaf2")
            leaf("leaf3")
          }
        }

    // (1), (2), (3)
    // (1,2), (1,3), (2,3)
    assertEquals(BigInteger.valueOf(6), tsc.instanceCount)
  }

  /** Test tsc with nested all nodes. */
  @Test
  fun `Test tsc with nested all nodes`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          all("root") { all("layer1") { all("layer2") { all("layer3") { leaf("leaf1") } } } }
        }

    assertEquals(BigInteger.ONE, tsc.instanceCount)
  }

  /** Test complex tsc. */
  @Test
  fun `Test complex tsc`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        > {
          bounded("root", 1 to 2) {
            bounded("inner", 1 to 2) {
              leaf("leaf1")
              leaf("leaf2")
            }
            leaf("leaf3")
            leaf("leaf4")
          }
        }

    // (i,1), (i,2), (i,1,2), (3), (4),
    // (i,1,3), (i,2,3), (i,1,2,3), (i,1,4), (i,2,4), (i,1,2,4), (3,4)

    assertEquals(BigInteger.valueOf(12), tsc.instanceCount)
  }
}
