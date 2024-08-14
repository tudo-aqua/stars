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
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import tools.aqua.stars.logic.kcmftbl.*
import tools.aqua.stars.logic.kcmftbl.data.TestUnit

/** This class tests the CMFTBL operator [minPrevalence]. */
class TestMinPrevalence {

  /**
   * Test when phi is true and percentage is 80, while no interval is given.
   * - phi: true
   * - percentage: 0.8
   * - Expected: true
   */
  @Test
  fun `Test when phi is true and percentage is 80, while no interval is given`() {
    val phi = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    val percentage = 0.8

    assertTrue { minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi is true and percentage is 100, while no interval is given.
   * - phi: true
   * - percentage: 1.0
   * - Expected: true
   */
  @Test
  fun `Test when phi is true and percentage is 100, while no interval is given`() {
    val phi = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    val percentage = 1.0

    assertTrue { minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi is true for 8 of 10 and percentage is 80, while no interval is given.
   * - phi: true
   * - percentage: 0.8
   * - Expected: true
   */
  @Test
  fun `Test when phi is true for 8 of 10 ticks and percentage is 80, while no interval is given`() {
    val phi = listOf(1, 1, 0, 1, 1, 1, 0, 1, 1, 1)
    val percentage = 0.8

    assertTrue { minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi is true for the first 8 of 10 and percentage is 80, while no interval is given.
   * - phi: true
   * - percentage: 0.8
   * - Expected: true
   */
  @Test
  fun `Test when phi is true for the first 8 of 10 ticks and percentage is 80, while no interval is given`() {
    val phi = listOf(1, 1, 1, 1, 1, 1, 1, 1, 0, 0)
    val percentage = 0.8

    assertTrue { minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi is true for the last 8 of 10 and percentage is 80, while no interval is given.
   * - phi: true
   * - percentage: 0.8
   * - Expected: true
   */
  @Test
  fun `Test when phi is true for the last 8 of 10 ticks and percentage is 80, while no interval is given`() {
    val phi = listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 1)
    val percentage = 0.8

    assertTrue { minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi is true for 7 of 10 and percentage is 80, while no interval is given.
   * - phi: true
   * - percentage: 0.8
   * - Expected: false
   */
  @Test
  fun `Test when phi is true for 7 of 10 ticks and percentage is 80, while no interval is given`() {
    val phi = listOf(1, 1, 0, 1, 0, 1, 0, 1, 1, 1)
    val percentage = 0.8

    assertFalse { minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi is false but percentage is 0, while no interval is given.
   * - phi: false
   * - percentage: 0.8
   * - Expected: true
   */
  @Test
  fun `Test when phi is false but percentage is 0, while no interval is given`() {
    val phi = listOf(0, 0, 0)
    val percentage = 0.0

    assertTrue { minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi is true for 8 of 10 ticks and percentage is 80, and 4 of 5 in interval are true.
   * - phi: true
   * - percentage: 0.8
   * - interval: (0, 5)
   * - Expected: true
   */
  @Test
  fun `Test when phi is true for 8 of 10 ticks and percentage is 80, and 4 of 5 in interval are true`() {
    val phi = listOf(1, 1, 0, 1, 1, 0, 1, 1, 1, 1)
    val percentage = 0.8
    val interval = 0 to 5

    assertTrue {
      minPrevalence(createTicks(phi)[0], percentage, createInterval(interval), phi = { it.phi1 })
    }
  }

  /**
   * Test when phi is true for 8 of 10 ticks and percentage is 80, and 3 of 5 in interval are true.
   * - phi: true
   * - percentage: 0.8
   * - interval: (0, 5)
   * - Expected: false
   */
  @Test
  fun `Test when phi is true for 8 of 10 ticks and percentage is 80, and 3 of 5 in interval are true`() {
    val phi = listOf(1, 1, 0, 0, 1, 1, 1, 1, 1, 1)
    val percentage = 0.8
    val interval = 0 to 5

    assertFalse {
      minPrevalence(createTicks(phi)[0], percentage, createInterval(interval), phi = { it.phi1 })
    }
  }

  /**
   * Test when phi is true and percentage is 100, but no tick is in the interval.
   * - phi: true
   * - percentage: 1.0
   * - interval: (1, 2)
   * - Expected: true
   */
  @Test
  fun `Test when phi is true and percentage is 100, but no tick is in the interval`() {
    val ticks = createTicks(listOf(1, 1, 1))
    ticks[0].currentTick = TestUnit(0)
    ticks[1].currentTick = TestUnit(2)
    ticks[2].currentTick = TestUnit(4)

    val percentage = 1.0
    val interval = 1 to 2

    assertTrue { minPrevalence(ticks[0], percentage, createInterval(interval), phi = { it.phi1 }) }
  }

  /**
   * Test percentage minus 0.
   * - phi: true
   * - percentage: -0.0
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test percentage minus 0`() {
    val phi = listOf(1, 1, 1)
    val percentage = -0.0

    assertTrue { minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 }) }
  }

  /**
   * Test percentage less than 0.
   * - phi: true
   * - percentage: -0.1
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test percentage less than 0`() {
    val phi = listOf(1, 1, 1)
    val percentage = -0.1

    assertFailsWith<IllegalArgumentException> {
      minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 })
    }
  }

  /**
   * Test percentage greater than 1.
   * - phi: true
   * - percentage: 1.1
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test percentage greater than 1`() {
    val phi = listOf(1, 1, 1)
    val percentage = 1.1

    assertFailsWith<IllegalArgumentException> {
      minPrevalence(createTicks(phi)[0], percentage, null, phi = { it.phi1 })
    }
  }

  /**
   * Test interval with size 0.
   * - phi: true
   * - percentage: 0.8
   * - interval: (0, 0)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with size 0`() {
    val phi = listOf(1, 1, 1)
    val percentage = 0.8
    val interval = 0 to 0

    assertFailsWith<IllegalArgumentException> {
      minPrevalence(createTicks(phi)[0], percentage, createInterval(interval), phi = { it.phi1 })
    }
  }

  /**
   * Test interval with swapped bounds.
   * - phi: true
   * - percentage: 0.8
   * - interval: (1, 0)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with swapped bounds`() {
    val phi = listOf(1, 1, 1)
    val percentage = 0.8
    val interval = 1 to 0

    assertFailsWith<IllegalArgumentException> {
      minPrevalence(createTicks(phi)[0], percentage, createInterval(interval), phi = { it.phi1 })
    }
  }

  /**
   * Test interval with negative bounds.
   * - phi: true
   * - percentage: 0.8
   * - interval: (-1, 1)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with negative bound`() {
    val phi = listOf(1, 1, 1)
    val percentage = 0.8
    val interval = -1 to 1

    assertFailsWith<IllegalArgumentException> {
      minPrevalence(createTicks(phi)[0], percentage, createInterval(interval), phi = { it.phi1 })
    }
  }
}
