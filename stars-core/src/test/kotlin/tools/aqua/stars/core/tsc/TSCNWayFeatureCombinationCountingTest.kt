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

package tools.aqua.stars.core.tsc

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.evaluation.NWayPredicateCombination
import tools.aqua.stars.core.tsc.builder.tsc
import tools.aqua.stars.core.tsc.utils.combinations

typealias E = SimpleEntity

typealias T = SimpleTickData

typealias S = SimpleSegment

typealias U = SimpleTickDataUnit

typealias D = SimpleTickDataDifference

/** Test class for counting of possible tsc n-way feature combinations. */
class TSCNWayFeatureCombinationCountingTest {
  // region n = 1

  /** Test 1-way feature combination counting for all node. */
  @Test
  fun `Test 1-way feature combination counting for all node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 1, 2)
  }

  /** Test 1-way feature combination counting for optional node. */
  @Test
  fun `Test 1-way feature combination counting for optional node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 1, 2)
  }

  /** Test 1-way feature combination counting for any node. */
  @Test
  fun `Test 1-way feature combination counting for any node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 1, 2)
  }

  /** Test 1-way feature combination counting for exclusive node. */
  @Test
  fun `Test 1-way feature combination counting for exclusive node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 1, 2)
  }

  /** Test 2-way feature combination counting for bounded node. */
  @Test
  fun `Test 1-way feature combination counting for bounded node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 2) {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 1, 2)
  }

  /** Test 1-way feature combination counting for bounded node with bounds equals to 0. */
  @Test
  fun `Test 1-way feature combination counting for bounded node with bounds equals to 0`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 0) {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 1, 0)
  }

  /** Test 1-way feature combination counting for with inner feature nodes. */
  @Test
  fun `Test 1-way feature combination counting for with inner feature nodes without condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              optional("optional 2") { leaf("leaf 1") { condition { true } } }
            }
          }
        }

    executeTests(testTSC, 1, 1)
  }

  /** Test 1-way feature combination counting with one inner feature node with a condition. */
  @Test
  fun `Test 1-way feature combination counting with one inner feature node with a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              condition { true }
              optional("optional 2") { leaf("leaf 1") { condition { true } } }
            }
          }
        }

    executeTests(testTSC, 1, 2)
  }

  /** Test 1-way feature combination counting with two inner feature nodes with a condition. */
  @Test
  fun `Test 1-way feature combination counting with two inner feature nodes with a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              condition { true }
              optional("optional 2") {
                condition { true }
                leaf("leaf 1") { condition { true } }
              }
            }
          }
        }

    executeTests(testTSC, 1, 3)
  }

  /** Test 1-way feature combination counting with two inner feature nodes with a condition. */
  @Test
  fun `Test 1-way feature combination counting with two inner feature nodes without a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              optional("optional 2") { leaf("leaf 1") { condition { true } } }
            }
          }
        }

    executeTests(testTSC, 1, 1)
  }

  /** Test 1-way feature combination counting with all nodes without a condition. */
  @Test
  fun `Test 1-way feature combination counting with all nodes without a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") { optional("optional 1") { optional("optional 2") { leaf("leaf 1") } } }
        }

    executeTests(testTSC, 1, 0)
  }

  // endregion

  // region n = 2

  /** Test 2-way feature combination counting for all node. */
  @Test
  fun `Test 2-way feature combination counting for all node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 1)
  }

  /** Test 2-way feature combination counting for optional node. */
  @Test
  fun `Test 2-way feature combination counting for optional node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 1)
  }

  /** Test 2-way feature combination counting for any node. */
  @Test
  fun `Test 2-way feature combination counting for any node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 1)
  }

  /** Test 2-way feature combination counting for exclusive node. */
  @Test
  fun `Test 2-way feature combination counting for exclusive node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 0)
  }

  /** Test 2-way feature combination counting for bounded node. */
  @Test
  fun `Test 2-way feature combination counting for bounded node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 2) {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 1)
  }

  /** Test 2-way feature combination counting for bounded node with bounds equals to 0. */
  @Test
  fun `Test 2-way feature combination counting for bounded node with bounds equals to 0`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 0) {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 0)
  }

  /** Test 2-way feature combination counting for with inner feature nodes. */
  @Test
  fun `Test 2-way feature combination counting for with inner feature nodes without condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              optional("optional 2") { leaf("leaf 1") { condition { true } } }
            }
          }
        }

    executeTests(testTSC, 2, 0)
  }

  /** Test 2-way feature combination counting with one inner feature node with a condition. */
  @Test
  fun `Test 2-way feature combination counting with one inner feature node with a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              condition { true }
              optional("optional 2") { leaf("leaf 1") { condition { true } } }
            }
          }
        }

    executeTests(testTSC, 2, 1)
  }

  /** Test 2-way feature combination counting with two inner feature nodes with a condition. */
  @Test
  fun `Test 2-way feature combination counting with two inner feature nodes with a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              condition { true }
              optional("optional 2") {
                condition { true }
                leaf("leaf 1") { condition { true } }
              }
            }
          }
        }

    executeTests(testTSC, 2, 3)
  }

  /** Test 2-way feature combination counting with two inner feature nodes with a condition. */
  @Test
  fun `Test 2-way feature combination counting with two inner feature nodes without a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              optional("optional 2") { leaf("leaf 1") { condition { true } } }
            }
          }
        }

    executeTests(testTSC, 2, 0)
  }

  /** Test 2-way feature combination counting with all nodes without a condition. */
  @Test
  fun `Test 2-way feature combination counting with all nodes without a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") { optional("optional 1") { optional("optional 2") { leaf("leaf 1") } } }
        }

    executeTests(testTSC, 2, 0)
  }

  // endregion

  // region n = 3

  /** Test 3-way feature combination counting for all node. */
  @Test
  fun `Test 3-way feature combination counting for all node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 1)
  }

  /** Test 3-way feature combination counting for optional node. */
  @Test
  fun `Test 3-way feature combination counting for optional node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 1)
  }

  /** Test 3-way feature combination counting for any node. */
  @Test
  fun `Test 3-way feature combination counting for any node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 1)
  }

  /** Test 3-way feature combination counting for exclusive node. */
  @Test
  fun `Test 3-way feature combination counting for exclusive node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 0)
  }

  /** Test 3-way feature combination counting for bounded node. */
  @Test
  fun `Test 3-way feature combination counting for bounded node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 3) {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 1)
  }

  /** Test 3-way feature combination counting for bounded node with bounds equals to 0. */
  @Test
  fun `Test 3-way feature combination counting for bounded node with bounds equals to 0`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 0) {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 0)
  }

  /** Test 3-way feature combination counting for with inner feature nodes. */
  @Test
  fun `Test 3-way feature combination counting for with inner feature nodes without condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              optional("optional 2") { leaf("leaf 1") { condition { true } } }
            }
          }
        }

    executeTests(testTSC, 3, 0)
  }

  /** Test 3-way feature combination counting with one inner feature node with a condition. */
  @Test
  fun `Test 3-way feature combination counting with one inner feature node with a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              condition { true }
              optional("optional 2") {
                leaf("leaf 1") { condition { true } }
                leaf("leaf 2") { condition { true } }
              }
            }
          }
        }

    executeTests(testTSC, 3, 1)
  }

  /** Test 3-way feature combination counting with two inner feature nodes with a condition. */
  @Test
  fun `Test 3-way feature combination counting with two inner feature nodes with a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              condition { true }
              optional("optional 2") {
                condition { true }
                leaf("leaf 1") { condition { true } }
              }
            }
          }
        }

    executeTests(testTSC, 3, 1)
  }

  /** Test 3-way feature combination counting with two inner feature nodes with a condition. */
  @Test
  fun `Test 3-way feature combination counting with two inner feature nodes without a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("optional 1") {
              optional("optional 2") { leaf("leaf 1") { condition { true } } }
            }
          }
        }

    executeTests(testTSC, 3, 0)
  }

  /** Test 3-way feature combination counting with all nodes without a condition. */
  @Test
  fun `Test 3-way feature combination counting with all nodes without a condition`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") { optional("optional 1") { optional("optional 2") { leaf("leaf 1") } } }
        }

    executeTests(testTSC, 3, 0)
  }

  // endregion

  // region n = 2 with larger TSC

  /** Test 2-way feature combination counting for all node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for all node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 6)
  }

  /** Test 2-way feature combination counting for optional node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for optional node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 6)
  }

  /** Test 2-way feature combination counting for any node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for any node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 6)
  }

  /** Test 2-way feature combination counting for exclusive node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for exclusive node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 0)
  }

  /** Test 2-way feature combination counting for bounded node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for bounded node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 1 to 3) {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 2, 6)
  }

  // endregion

  // region n = 2 with complex TSC

  /** Test 2-way feature combination counting with complex TSC. */
  @Test
  fun `Test 2-way feature combination counting with complex TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            optional("optional 1") {
              leaf("leaf 3") { condition { true } }
              leaf("leaf 4") { condition { true } }
              exclusive("exclusive 1") {
                leaf("leaf 5") { condition { true } }
                leaf("leaf 6") { condition { false } }
              }
            }
          }
        }

    executeTests(testTSC, 2, 14)
  }

  // endregion

  // region n = 3 with larger TSC

  /** Test 3-way feature combination counting for all node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for all node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 4)
  }

  /** Test 3-way feature combination counting for optional node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for optional node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 4)
  }

  /** Test 3-way feature combination counting for any node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for any node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 4)
  }

  /** Test 3-way feature combination counting for exclusive node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for exclusive node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 0)
  }

  /** Test 3-way feature combination counting for bounded node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for bounded node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 1 to 3) {
            leaf("leaf 1") { condition { true } }
            leaf("leaf 2") { condition { true } }
            leaf("leaf 3") { condition { true } }
            leaf("leaf 4") { condition { true } }
          }
        }

    executeTests(testTSC, 3, 4)
  }

  // endregion

  // region n = 3 with huge TSC (20,000 leaf nodes)

  /** Test 3-way feature combination counting for all node with huge TSC (20,000 leaf nodes). */
  @Test
  fun `Test 3-way feature combination counting for all node with huge TSC (20,000 leaf nodes)`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") { repeat(20_000) { leaf("leaf $it") { condition { true } } } }
        }

    assertEquals(BigInteger("1333133340000"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /**
   * Test 3-way feature combination counting for optional node with huge TSC (20,000 leaf nodes).
   */
  @Test
  fun `Test 3-way feature combination counting for optional node with huge TSC (20,000 leaf nodes)`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") { repeat(20_000) { leaf("leaf $it") { condition { true } } } }
        }

    assertEquals(BigInteger("1333133340000"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /** Test 3-way feature combination counting for any node with huge TSC (20,000 leaf nodes). */
  @Test
  fun `Test 3-way feature combination counting for any node with huge TSC (20,000 leaf nodes)`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") { repeat(20_000) { leaf("leaf $it") { condition { true } } } }
        }

    assertEquals(BigInteger("1333133340000"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /**
   * Test 3-way feature combination counting for exclusive node with huge TSC (20,000 leaf nodes).
   */
  @Test
  fun `Test 3-way feature combination counting for exclusive node with huge TSC (20,000 leaf nodes)`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") { repeat(20_000) { leaf("leaf $it") { condition { true } } } }
        }

    assertEquals(BigInteger.ZERO, testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /** Test 3-way feature combination counting for bounded node with huge TSC (20,000 leaf nodes). */
  @Test
  fun `Test 3-way feature combination counting for bounded node with huge TSC (20,000 leaf nodes)`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 1 to 3) { repeat(20_000) { leaf("leaf $it") { condition { true } } } }
        }

    assertEquals(BigInteger("1333133340000"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  // endregion

  // region Handcrafted TSCs

  /** Test handcrafted TSC without duplicate feature nodes. */
  @Test
  fun `Test handcrafted TSC without duplicate feature nodes`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            optional("Optional 1") {
              leaf("Leaf 1") { condition { true } }
              leaf("Leaf 2") { condition { true } }
            }
            exclusive("Exclusive") {
              leaf("Leaf 3") { condition { true } }
              leaf("Leaf 4") { condition { true } }
            }
          }
        }

    // 1-way
    executeTests(testTSC, 1, 4)
    val combinations1Way = testTSC.getAllPossibleNWayPredicateCombinations(1)
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 1") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 2") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 3") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 4") } }

    // 2-way
    executeTests(testTSC, 2, 5)
    val combinations2Way = testTSC.getAllPossibleNWayPredicateCombinations(2)
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 2") }
    }
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 3") }
    }
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 4") }
    }
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 2") && it.elements.contains("Leaf 3") }
    }
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 2") && it.elements.contains("Leaf 4") }
    }

    // 3-way
    executeTests(testTSC, 3, 2)
    val combinations3Way = testTSC.getAllPossibleNWayPredicateCombinations(3)
    assertTrue {
      combinations3Way.any {
        it.elements.contains("Leaf 1") &&
            it.elements.contains("Leaf 2") &&
            it.elements.contains("Leaf 3")
      }
    }
    assertTrue {
      combinations3Way.any {
        it.elements.contains("Leaf 1") &&
            it.elements.contains("Leaf 2") &&
            it.elements.contains("Leaf 4")
      }
    }
  }

  /** Test handcrafted tested tsc with duplicate features. */
  @Test
  fun `Test handcrafted tested tsc with duplicate features`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("Exclusive") {
            optional("Optional") {
              leaf("Leaf 1") { condition { true } }
              leaf("Leaf 2") { condition { true } }
            }
            all("All") { leaf("Leaf 1") { condition { true } } }
          }
        }

    // 1-way
    executeTests(testTSC, 1, 2)
    val combinations1Way = testTSC.getAllPossibleNWayPredicateCombinations(1)
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 1") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 2") } }

    // 2-way
    executeTests(testTSC, 2, 1)
    val combinations2Way = testTSC.getAllPossibleNWayPredicateCombinations(2)
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 2") }
    }

    // 3-way
    executeTests(testTSC, 3, 0)
  }

  /** Test handcrafted tested tsc with duplicate features and exclusive node. */
  @Test
  fun `Test handcrafted tested tsc with duplicate features and exclusive node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            exclusive("Exclusive 1") {
              leaf("Leaf 1") { condition { true } }
              leaf("Leaf 2") { condition { true } }
            }
            exclusive("Exclusive 2") {
              optional("Optional 1") {
                leaf("Leaf 3") { condition { true } }
                leaf("Leaf 4") { condition { true } }
              }
              optional("Optional 2") {
                leaf("Leaf 3") { condition { true } }
                leaf("Leaf 5") { condition { true } }
              }
            }
          }
        }

    // 1-way
    executeTests(testTSC, 1, 5)
    val combinations1Way = testTSC.getAllPossibleNWayPredicateCombinations(1)
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 1") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 2") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 3") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 4") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 5") } }

    // 2-way
    executeTests(testTSC, 2, 8)
    val combinations2Way = testTSC.getAllPossibleNWayPredicateCombinations(2)
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 3") }
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 4") }
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 5") }
      combinations2Way.any { it.elements.contains("Leaf 2") && it.elements.contains("Leaf 3") }
      combinations2Way.any { it.elements.contains("Leaf 2") && it.elements.contains("Leaf 4") }
      combinations2Way.any { it.elements.contains("Leaf 2") && it.elements.contains("Leaf 5") }
      combinations2Way.any { it.elements.contains("Leaf 3") && it.elements.contains("Leaf 5") }
    }

    // 3-way
    executeTests(testTSC, 3, 4)
    val combinations3Way = testTSC.getAllPossibleNWayPredicateCombinations(3)
    assertTrue {
      combinations3Way.any {
        it.elements.contains("Leaf 1") &&
            it.elements.contains("Leaf 3") &&
            it.elements.contains("Leaf 4")
      }
      combinations3Way.any {
        it.elements.contains("Leaf 1") &&
            it.elements.contains("Leaf 3") &&
            it.elements.contains("Leaf 5")
      }
      combinations3Way.any {
        it.elements.contains("Leaf 2") &&
            it.elements.contains("Leaf 3") &&
            it.elements.contains("Leaf 4")
      }
      combinations3Way.any {
        it.elements.contains("Leaf 2") &&
            it.elements.contains("Leaf 3") &&
            it.elements.contains("Leaf 5")
      }
    }
  }

  /** Test handcrafted tested tsc with duplicate features under the same exclusive node. */
  @Test
  fun `Test handcrafted tested tsc with duplicate features under the same exclusive node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            exclusive("Exclusive 1") {
              leaf("Leaf 1") { condition { true } }
              leaf("Leaf 2") { condition { true } }
            }
            exclusive("Exclusive 2") {
              all("all 1") { leaf("Leaf 3") { condition { true } } }
              all("all 2") { leaf("Leaf 3") { condition { true } } }
            }
          }
        }

    // 1-way
    executeTests(testTSC, 1, 3)
    val combinations1Way = testTSC.getAllPossibleNWayPredicateCombinations(1)
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 1") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 2") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 3") } }

    // 2-way
    executeTests(testTSC, 2, 2)
    val combinations2Way = testTSC.getAllPossibleNWayPredicateCombinations(2)
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 2") }
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 3") }
    }

    // 3-way
    executeTests(testTSC, 3, 0)
  }

  /** Test handcrafted tested tsc with two equal optional nodes. */
  @Test
  fun `Test handcrafted tested tsc with with two equal optional nodes`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            optional("optional 1") {
              leaf("Leaf 1") { condition { true } }
              leaf("Leaf 2") { condition { true } }
            }
            exclusive("optional 2") {
              leaf("Leaf 1") { condition { true } }
              leaf("Leaf 2") { condition { true } }
            }
          }
        }

    // 1-way
    executeTests(testTSC, 1, 2)
    val combinations1Way = testTSC.getAllPossibleNWayPredicateCombinations(1)
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 1") } }
    assertTrue { combinations1Way.any { it.elements.contains("Leaf 2") } }

    // 2-way
    executeTests(testTSC, 2, 1)
    val combinations2Way = testTSC.getAllPossibleNWayPredicateCombinations(2)
    assertTrue {
      combinations2Way.any { it.elements.contains("Leaf 1") && it.elements.contains("Leaf 2") }
    }

    // 3-way
    executeTests(testTSC, 3, 0)
  }

  /** Test NFM Full TSC. */
  @Test
  fun `Test NFM Full TSC`() {
    val testTSC =
        tsc<E, T, S, U, D>("TSC Full") {
          all("TSCRoot") {
            exclusive("Road Type") {
              all("Junction") {
                condition { true }

                optional("Dynamic Relation") {
                  leaf("Pedestrian Crossed") { condition { true } }

                  leaf("Must Yield") { condition { true } }

                  leaf("Following Leading Vehicle") { condition { true } }
                }

                exclusive("Maneuver") {
                  leaf("No Turn") { condition { true } }
                  leaf("Right Turn") { condition { true } }
                  leaf("Left Turn") { condition { true } }
                }
              }
              all("Multi-Lane") {
                condition { true }

                optional("Dynamic Relation") {
                  leaf("Oncoming traffic") { condition { true } }
                  leaf("Overtaking") { condition { true } }
                  leaf("Pedestrian Crossed") { condition { true } }
                  leaf("Following Leading Vehicle") { condition { true } }
                }

                exclusive("Maneuver") {
                  leaf("Lane Change") { condition { true } }
                  leaf("Lane Follow") { condition { true } }
                }

                bounded("Stop Type", 0 to 1) { leaf("Has Red Light") { condition { true } } }
              }
              all("Single-Lane") {
                condition { true }

                optional("Dynamic Relation") {
                  leaf("Oncoming traffic") { condition { true } }

                  leaf("Pedestrian Crossed") { condition { true } }

                  leaf("Following Leading Vehicle") { condition { true } }
                }

                bounded("Stop Type", 0 to 1) {
                  leaf("Has Stop Sign") { condition { true } }
                  leaf("Has Yield Sign") { condition { true } }
                  leaf("Has Red Light") { condition { true } }
                }
              }
            }

            exclusive("Traffic Density") {
              leaf("High Traffic") { condition { true } }
              leaf("Middle Traffic") { condition { true } }
              leaf("Low Traffic") { condition { true } }
            }

            exclusive("Weather") {
              leaf("Clear") { condition { true } }
              leaf("Rain") { condition { true } }
            }

            exclusive("Time of Day") {
              leaf("Day") { condition { true } }
              leaf("Night") { condition { true } }
            }
          }
        }

    executeTests(testTSC, 1, 23)
    executeTests(testTSC, 2, 184)
    executeTests(testTSC, 3, 750)
    executeTests(testTSC, 4, 1798)
    executeTests(testTSC, 5, 2714)
    executeTests(testTSC, 6, 2667)
    executeTests(testTSC, 7, 1725)
    executeTests(testTSC, 8, 726)
    executeTests(testTSC, 9, 188)
    executeTests(testTSC, 10, 24)
    executeTests(testTSC, 11, 0)
    getCombinationsFromPossibleInstances(testTSC, 10).forEach { println(it) }
  }

  /** Test NFM Layer 4 Flat TSC. */
  @Test
  fun `Test NFM Layer 4 Flat TSC`() {
    val testTSC =
        tsc<E, T, S, U, D>("TSC Layer 4 Flat") {
          optional("TSCRoot") {
            leaf("Junction") { condition { true } }
            leaf("Pedestrian Crossed") { condition { true } }
            leaf("Must Yield") { condition { true } }
            leaf("Following Leading Vehicle") { condition { true } }
            leaf("Multi-Lane") { condition { true } }
            leaf("Oncoming traffic") { condition { true } }
            leaf("Overtaking") { condition { true } }
            leaf("Single-Lane") { condition { true } }
            leaf("High Traffic") { condition { true } }
            leaf("Middle Traffic") { condition { true } }
            leaf("Low Traffic") { condition { true } }
          }
        }

    executeTests(testTSC, 1, 11)
    executeTests(testTSC, 2, 55)
    executeTests(testTSC, 3, 165)
    executeTests(testTSC, 4, 330)
    executeTests(testTSC, 5, 462)
    executeTests(testTSC, 6, 462)
    executeTests(testTSC, 7, 330)
    executeTests(testTSC, 8, 165)
    executeTests(testTSC, 9, 55)
    executeTests(testTSC, 10, 11)
    executeTests(testTSC, 11, 1)
    executeTests(testTSC, 12, 0)
  }

  // endregion

  private fun executeTests(tsc: TSC<*, *, *, *, *>, n: Int, resultSize: Int) {
    val nWayCombinations = tsc.getAllPossibleNWayPredicateCombinations(n)
    val nWayCombinationsFromPossibleTSCInstances = getCombinationsFromPossibleInstances(tsc, n)

    assertEquals(
        BigInteger.valueOf(resultSize.toLong()),
        tsc.countAllPossibleNWayPredicateCombinations(n),
    )
    assertTrue(nWayCombinations.all { it.elements.size == n })
    assertTrue(nWayCombinationsFromPossibleTSCInstances.all { it.elements.size == n })
    assertEquals(resultSize, nWayCombinations.size)
    assertEquals(resultSize, nWayCombinationsFromPossibleTSCInstances.size)
    assertEquals(nWayCombinations, nWayCombinationsFromPossibleTSCInstances)
  }

  private fun getCombinationsFromPossibleInstances(
      tsc: TSC<*, *, *, *, *>,
      n: Int,
  ): Set<NWayPredicateCombination> {
    val possibleInstances = tsc.possibleTSCInstances
    val possibleNWayCombinations: MutableSet<NWayPredicateCombination> = mutableSetOf()
    possibleInstances.forEach { instance ->
      val featureNodes = instance.extractLeafLabels()
      val combinations = combinations(featureNodes, n)
      combinations.forEach { combo ->
        possibleNWayCombinations.add(NWayPredicateCombination(combo.sorted()))
      }
    }
    return possibleNWayCombinations
  }
}
