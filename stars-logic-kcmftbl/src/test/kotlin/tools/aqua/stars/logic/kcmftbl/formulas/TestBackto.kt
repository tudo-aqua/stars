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

package tools.aqua.stars.logic.kcmftbl.formulas

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import tools.aqua.stars.logic.kcmftbl.backto
import tools.aqua.stars.logic.kcmftbl.createInterval
import tools.aqua.stars.logic.kcmftbl.createTicks

/** This class tests the CMFTBL operator [backto]. */
class TestBackto {

  /**
   * Test when phi1 is true at current time but phi2 is not, while no interval is given.
   * - phi1: true at start then false
   * - phi2: false
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true at current time but phi2 is not, while no interval is given`() {
    val phi1 = listOf(0, 0, 1)
    val phi2 = listOf(0, 0, 0)

    assertFalse { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi2 is true at current time but phi1 is not, while no interval is given.
   * - phi1: false
   * - phi2: true at start then false
   * - Expected: false
   */
  @Test
  fun `Test when phi2 is true at current time but phi1 is not, while no interval is given`() {
    val phi1 = listOf(0, 0, 0)
    val phi2 = listOf(0, 0, 1)

    assertFalse { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true at first two ticks but phi2 is only on first, while no interval is
   * given.
   * - phi1: true at first two ticks
   * - phi2: true at first tick
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true at first two ticks but phi2 is only on first, while no interval is given`() {
    val phi1 = listOf(0, 1, 1)
    val phi2 = listOf(0, 0, 1)

    assertTrue { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi2 is true at first two ticks but phi1 is only on first, while no interval is
   * given.
   * - phi1: true at first tick
   * - phi2: true at first two ticks
   * - Expected: false
   */
  @Test
  fun `Test when phi2 is true at first two ticks but phi1 is only on first, while no interval is given`() {
    val phi1 = listOf(0, 0, 1)
    val phi2 = listOf(0, 1, 1)

    assertTrue { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 and phi2 are false, while no interval is given.
   * - phi1: false
   * - phi2: false
   * - Expected: false
   */
  @Test
  fun `Test when phi1 and phi2 are false, while no interval is given`() {
    val phi1 = listOf(0, 0, 0)
    val phi2 = listOf(0, 0, 0)

    assertFalse { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 and phi2 are true, while no interval is given.
   * - phi1: true
   * - phi2: true
   * - Expected: true
   */
  @Test
  fun `Test when phi1 and phi2 are true, while no interval is given`() {
    val phi1 = listOf(1, 1, 1)
    val phi2 = listOf(1, 1, 1)

    assertTrue { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true and phi2 is false, while no interval is given.
   * - phi1: true
   * - phi2: false
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true and phi2 is false, while no interval is given`() {
    val phi1 = listOf(1, 1, 1)
    val phi2 = listOf(0, 0, 0)

    assertFalse { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true and phi2 is false, while no interval is given.
   * - phi1: false
   * - phi2: true
   * - Expected: true
   */
  @Test
  fun `Test when phi2 is true and phi1 is false, while no interval is given`() {
    val phi1 = listOf(0, 0, 0)
    val phi2 = listOf(1, 1, 1)

    assertTrue { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true and phi2 is false at the end, while no interval is given.
   * - phi1: true
   * - phi2: false at end true before
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true and phi2 is false at the end, while no interval is given`() {
    val phi1 = listOf(1, 1, 1)
    val phi2 = listOf(0, 1, 1)

    assertTrue { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi2 is true and phi1 is false at the end, while no interval is given.
   * - phi1: false at end true before
   * - phi2: true
   * - Expected: false
   */
  @Test
  fun `Test when phi2 is true and phi1 is false at the end, while no interval is given`() {
    val phi1 = listOf(0, 1, 1)
    val phi2 = listOf(1, 1, 1)

    assertTrue { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is false at first tick and phi2 is false at last tick, while no interval is
   * given.
   * - phi1: true but false at first tick
   * - phi2: false but true at last tick
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is false at first tick and phi2 is false at last tick, while no interval is given`() {
    val phi1 = listOf(1, 1, 0)
    val phi2 = listOf(0, 1, 1)

    assertTrue { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true at the second tick and phi2 is true until second tick, while no interval
   * is given.
   * - phi1: false but true at second tick
   * - phi1: true but false at last tick
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true at the second tick and phi2 is true until second tick, while no interval is given`() {
    val phi1 = listOf(0, 1, 0)
    val phi2 = listOf(0, 1, 1)

    assertTrue { backto(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test interval with size 0.
   * - phi1: true
   * - phi2: true
   * - interval: (0, 0)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with size 0`() {
    val phi1 = listOf(1, 1, 1)
    val phi2 = listOf(1, 1, 1)
    val interval = 0 to 0

    assertFailsWith<IllegalArgumentException> {
      backto(
          createTicks(phi1, phi2)[2],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 },
      )
    }
  }

  /**
   * Test interval with swapped bounds.
   * - phi1: true
   * - phi2: true
   * - interval: (1, 0)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with swapped bounds`() {
    val phi1 = listOf(1, 1, 1)
    val phi2 = listOf(1, 1, 1)
    val interval = 1 to 0

    assertFailsWith<IllegalArgumentException> {
      backto(
          createTicks(phi1, phi2)[2],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 },
      )
    }
  }

  /**
   * Test interval with negative bounds.
   * - phi1: true
   * - phi2: true
   * - interval: (-1, 1)
   * - Expected: IllegalArgumentException
   */
  @Test
  fun `Test interval with negative bound`() {
    val phi1 = listOf(1, 1, 1)
    val phi2 = listOf(1, 1, 1)
    val interval = -1 to 1

    assertFailsWith<IllegalArgumentException> {
      backto(
          createTicks(phi1, phi2)[2],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 },
      )
    }
  }

  /**
   * Test when phi1 is false and phi2 is true in the interval starting at zero.
   * - phi1: false
   * - phi2: true in interval, false afterward
   * - interval: (0, 2)
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is false and phi2 is true in the interval starting at zero`() {
    val phi1 = listOf(0, 0, 0, 0)
    val phi2 = listOf(0, 1, 1, 1)
    val interval = 0 to 2

    assertTrue {
      backto(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 },
      )
    }
  }

  /**
   * Test when phi1 is false and phi2 is true in the interval at end.
   * - phi1: false
   * - phi2: true in interval, false before
   * - interval: (0, 2)
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is false and phi2 is true in the interval at end`() {
    val phi1 = listOf(0, 0, 0, 0)
    val phi2 = listOf(1, 1, 1, 0)
    val interval = 1 to 3

    assertTrue {
      backto(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 },
      )
    }
  }

  /**
   * Test when phi1 is false and phi2 is true in the interval in the middle.
   * - phi1: false
   * - phi2: true in interval, false afterward and before
   * - interval: (1, 3)
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is false and phi2 is true in the interval in the middle`() {
    val phi1 = listOf(0, 0, 0, 0, 0)
    val phi2 = listOf(0, 1, 1, 1, 0)
    val interval = 1 to 3

    assertTrue {
      backto(
          createTicks(phi1, phi2)[4],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 },
      )
    }
  }
}
