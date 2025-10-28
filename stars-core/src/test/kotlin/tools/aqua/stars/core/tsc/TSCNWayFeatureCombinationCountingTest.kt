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
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.tsc.builder.tsc

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
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.TWO, testTSC.countAllPossibleNWayPredicateCombinations(1))
  }

  /** Test 1-way feature combination counting for optional node. */
  @Test
  fun `Test 1-way feature combination counting for optional node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.TWO, testTSC.countAllPossibleNWayPredicateCombinations(1))
  }

  /** Test 1-way feature combination counting for any node. */
  @Test
  fun `Test 1-way feature combination counting for any node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.TWO, testTSC.countAllPossibleNWayPredicateCombinations(1))
  }

  /** Test 1-way feature combination counting for exclusive node. */
  @Test
  fun `Test 1-way feature combination counting for exclusive node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.TWO, testTSC.countAllPossibleNWayPredicateCombinations(1))
  }

  /** Test 2-way feature combination counting for bounded node. */
  @Test
  fun `Test 1-way feature combination counting for bounded node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 2) {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.TWO, testTSC.countAllPossibleNWayPredicateCombinations(1))
  }

  /** Test 2-way feature combination counting for bounded node with bounds equals to 0. */
  @Test
  fun `Test 1-way feature combination counting for bounded node with bounds equals to 0`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 0) {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.ZERO, testTSC.countAllPossibleNWayPredicateCombinations(1))
  }

  // endregion

  // region n = 2

  /** Test 2-way feature combination counting for all node. */
  @Test
  fun `Test 2-way feature combination counting for all node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.ONE, testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test 2-way feature combination counting for optional node. */
  @Test
  fun `Test 2-way feature combination counting for optional node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.ONE, testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test 2-way feature combination counting for any node. */
  @Test
  fun `Test 2-way feature combination counting for any node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.ONE, testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test 2-way feature combination counting for exclusive node. */
  @Test
  fun `Test 2-way feature combination counting for exclusive node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.ZERO, testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test 2-way feature combination counting for bounded node. */
  @Test
  fun `Test 2-way feature combination counting for bounded node`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 0 to 2) {
            leaf("leaf 1")
            leaf("leaf 2")
          }
        }

    assertEquals(BigInteger.ONE, testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  // endregion

  // region n = 2 with larger TSC

  /** Test 2-way feature combination counting for all node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for all node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger("6"), testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test 2-way feature combination counting for optional node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for optional node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger("6"), testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test 2-way feature combination counting for any node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for any node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger("6"), testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test 2-way feature combination counting for exclusive node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for exclusive node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger.ZERO, testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  /** Test 2-way feature combination counting for bounded node with larger TSC. */
  @Test
  fun `Test 2-way feature combination counting for bounded node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 1 to 3) {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger("6"), testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  // endregion

  // region n = 2 with complex TSC

  /** Test 2-way feature combination counting with complex TSC. */
  @Test
  fun `Test 2-way feature combination counting with complex TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            optional("optional 1") {
              leaf("leaf 3")
              leaf("leaf 4")
              exclusive("exclusive 1") {
                leaf("leaf 5")
                leaf("leaf 6")
              }
            }
          }
        }

    assertEquals(BigInteger("14"), testTSC.countAllPossibleNWayPredicateCombinations(2))
  }

  // endregion

  // region n = 3 with larger TSC

  /** Test 3-way feature combination counting for all node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for all node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger("4"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /** Test 3-way feature combination counting for optional node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for optional node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          optional("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger("4"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /** Test 3-way feature combination counting for any node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for any node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          any("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger("4"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /** Test 3-way feature combination counting for exclusive node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for exclusive node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          exclusive("root") {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger.ZERO, testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /** Test 3-way feature combination counting for bounded node with larger TSC. */
  @Test
  fun `Test 3-way feature combination counting for bounded node with larger TSC`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          bounded("root", 1 to 3) {
            leaf("leaf 1")
            leaf("leaf 2")
            leaf("leaf 3")
            leaf("leaf 4")
          }
        }

    assertEquals(BigInteger("4"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  // endregion

  // region n = 3 with huge TSC (20,000 leaf nodes)

  /** Test 3-way feature combination counting for all node with huge TSC (20,000 leaf nodes). */
  @Test
  fun `Test 3-way feature combination counting for all node with huge TSC (20,000 leaf nodes)`() {
    val testTSC = tsc<E, T, S, U, D> { all("root") { repeat(20_000) { leaf("leaf $it") } } }

    assertEquals(BigInteger("1333133340000"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /**
   * Test 3-way feature combination counting for optional node with huge TSC (20,000 leaf nodes).
   */
  @Test
  fun `Test 3-way feature combination counting for optional node with huge TSC (20,000 leaf nodes)`() {
    val testTSC = tsc<E, T, S, U, D> { optional("root") { repeat(20_000) { leaf("leaf $it") } } }

    assertEquals(BigInteger("1333133340000"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /** Test 3-way feature combination counting for any node with huge TSC (20,000 leaf nodes). */
  @Test
  fun `Test 3-way feature combination counting for any node with huge TSC (20,000 leaf nodes)`() {
    val testTSC = tsc<E, T, S, U, D> { any("root") { repeat(20_000) { leaf("leaf $it") } } }

    assertEquals(BigInteger("1333133340000"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /**
   * Test 3-way feature combination counting for exclusive node with huge TSC (20,000 leaf nodes).
   */
  @Test
  fun `Test 3-way feature combination counting for exclusive node with huge TSC (20,000 leaf nodes)`() {
    val testTSC = tsc<E, T, S, U, D> { exclusive("root") { repeat(20_000) { leaf("leaf $it") } } }

    assertEquals(BigInteger.ZERO, testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  /** Test 3-way feature combination counting for bounded node with huge TSC (20,000 leaf nodes). */
  @Test
  fun `Test 3-way feature combination counting for bounded node with huge TSC (20,000 leaf nodes)`() {
    val testTSC =
        tsc<E, T, S, U, D> { bounded("root", 1 to 3) { repeat(20_000) { leaf("leaf $it") } } }

    assertEquals(BigInteger("1333133340000"), testTSC.countAllPossibleNWayPredicateCombinations(3))
  }

  // endregion
}
