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

package tools.aqua.stars.logic.kcmftbl.formulas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.logic.kcmftbl.*

/** This class tests the CMFTBL operator [eventually]. */
class TestEventually {

  /**
   * Test equivalence to until.
   *
   * Eventually phi === True Until phi
   * - phi: All combinations of 0-1 arrays with length 5
   * - interval: All combinations including null
   * - Expected: true
   */
  @Test
  fun `Test equivalence to until`() {
    val phi1 = listOf(1, 1, 1, 1, 1)

    combinations(phi1.size).forEach { phi2 ->
      intervals(phi1.size).forEach { interval ->
        val tick = createTicks(phi1, phi2)[0]
        val u = until(tick, interval, phi1 = { it.phi1 }, phi2 = { it.phi2 })
        val e = eventually(tick, interval) { it.phi2 }
        assertEquals(
            expected = u,
            actual = e,
            message =
                "Unmatched result between Eventually ($e) and Until ($u) for phi = $phi2 with interval $interval")
      }
    }
  }

  /**
   * Test interval with size 0.
   * - phi: true
   * - interval: (0, 0)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with size 0`() {
    val phi = listOf(1, 1, 1)
    val interval = 0 to 0

    assertFailsWith<IllegalArgumentException> {
      eventually(createTicks(phi)[0], createInterval(interval), phi = { it.phi1 })
    }
  }

  /**
   * Test interval with swapped bounds.
   * - phi: true
   * - interval: (1, 0)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with swapped bounds`() {
    val phi = listOf(1, 1, 1)
    val interval = 1 to 0

    assertFailsWith<IllegalArgumentException> {
      eventually(createTicks(phi)[0], createInterval(interval), phi = { it.phi1 })
    }
  }

  /**
   * Test interval with negative bound.
   * - phi: true
   * - interval: (-1, 1)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with negative bound`() {
    val phi = listOf(1, 1, 1)
    val interval = -1 to 1

    assertFailsWith<IllegalArgumentException> {
      eventually(createTicks(phi)[0], createInterval(interval), phi = { it.phi1 })
    }
  }
}
