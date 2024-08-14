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
import tools.aqua.stars.logic.kcmftbl.createInterval
import tools.aqua.stars.logic.kcmftbl.createTicks
import tools.aqua.stars.logic.kcmftbl.data.TestUnit
import tools.aqua.stars.logic.kcmftbl.next

/** This class tests the CMFTBL operator [next]. */
class TestNext {

  /**
   * Test when phi is true, while no interval is given.
   * - phi: true
   * - Expected: true
   */
  @Test
  fun `Test when phi is true, while no interval is given`() {
    val phi = listOf(1, 1, 1)

    assertTrue { next(createTicks(phi)[0], null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi holds now and at the next tick but not after that, while no interval is given.
   * - phi: true until after the next tick
   * - Expected: true
   */
  @Test
  fun `Test when phi holds now and at the next tick but not after that, while no interval is given`() {
    val phi = listOf(1, 1, 0)

    assertTrue { next(createTicks(phi)[0], null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi holds at the next tick but not after that, while no interval is given.
   * - phi: true only at the next tick
   * - Expected: true
   */
  @Test
  fun `Test when phi holds at the next tick but not after that, while no interval is given`() {
    val phi = listOf(0, 1, 0)

    assertTrue { next(createTicks(phi)[0], null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi holds now and after the next tick, while no interval is given.
   * - phi: true now and after the next tick
   * - Expected: false
   */
  @Test
  fun `Test when phi holds now and after the next tick, while no interval is given`() {
    val phi = listOf(1, 0, 1)

    assertFalse { next(createTicks(phi)[0], null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi holds only now, while no interval is given.
   * - phi: true only now
   * - Expected: false
   */
  @Test
  fun `Test when phi holds only now, while no interval is given`() {
    val phi = listOf(1, 0, 0)

    assertFalse { next(createTicks(phi)[0], null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi holds only after the next tick, while no interval is given.
   * - phi: true after the next tick
   * - Expected: false
   */
  @Test
  fun `Test when phi holds only after the next tick, while no interval is given`() {
    val phi = listOf(0, 0, 1)

    assertFalse { next(createTicks(phi)[0], null, phi = { it.phi1 }) }
  }

  /**
   * Test when there is no next tick.
   * - phi: true
   * - Expected: false
   */
  @Test
  fun `Test when there is no next tick`() {
    val phi = listOf(1)

    assertFalse { next(createTicks(phi)[0], null, phi = { it.phi1 }) }
  }

  /**
   * Test when phi holds at the next tick and the tick is in the interval.
   * - phi: true
   * - interval: (1, 2)
   * - Expected: false
   */
  @Test
  fun `Test when phi holds at the next tick and the tick is in the interval`() {
    val phi = listOf(1, 1, 1)
    val interval = 1 to 2

    assertTrue { next(createTicks(phi)[0], createInterval(interval), phi = { it.phi1 }) }
  }

  /**
   * Test when phi holds at the next tick, but the tick is before the interval starts.
   * - phi: true
   * - interval: (1, 2)
   * - Expected: false
   */
  @Test
  fun `Test when phi holds at the next tick, but the tick is before the interval starts`() {
    val ticks = createTicks(listOf(1, 1, 1))
    ticks[0].currentTick = TestUnit(0)
    ticks[1].currentTick = TestUnit(2)
    ticks[2].currentTick = TestUnit(4)

    val interval = 1 to 2

    assertFalse { next(ticks[0], createInterval(interval), phi = { it.phi1 }) }
  }

  /**
   * Test when phi holds at the next tick, but the tick is after the interval ends.
   * - phi: true
   * - interval: (2, 3)
   * - Expected: false
   */
  @Test
  fun `Test when phi holds at the next tick, but the tick is after the interval ends`() {
    val phi = listOf(1, 1, 1)
    val interval = 2 to 3

    assertFalse { next(createTicks(phi)[0], createInterval(interval), phi = { it.phi1 }) }
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
      next(createTicks(phi)[0], createInterval(interval), phi = { it.phi1 })
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
      next(createTicks(phi)[0], createInterval(interval), phi = { it.phi1 })
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
      next(createTicks(phi)[0], createInterval(interval), phi = { it.phi1 })
    }
  }
}
