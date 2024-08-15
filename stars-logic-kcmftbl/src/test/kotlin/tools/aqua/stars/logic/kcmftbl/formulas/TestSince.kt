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
import tools.aqua.stars.logic.kcmftbl.since

/** This class tests the CMFTBL operator [since]. */
class TestSince {

  /**
   * Test when phi2 is true at current time, while no interval is given.
   * - phi1: false
   * - phi2: true at start then false
   * - Expected: true
   */
  @Test
  fun `Test when phi2 is true at current time, while no interval is given`() {
    val phi1 = listOf(0, 0, 0)
    val phi2 = listOf(0, 0, 1)

    assertTrue { since(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
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

    assertFalse { since(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is false and phi2 is true at the end, while no interval is given.
   * - phi1: false
   * - phi2: true at end false before
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is false and phi2 is true at the end, while no interval is given`() {
    val phi1 = listOf(0, 0, 0)
    val phi2 = listOf(1, 0, 0)

    assertFalse { since(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true from the second tick and phi2 is true at the end, while no interval is
   * given.
   * - phi1: true from the second tick since phi2 was true
   * - phi2: true at end false before
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true from the second tick and phi2 is true at the end, while no interval is given`() {
    val phi1 = listOf(1, 1, 0)
    val phi2 = listOf(1, 0, 0)

    assertFalse { since(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true since phi2 was true, while no interval is given.
   * - phi1: true since phi2 was true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true since phi2 was true, while no interval is given`() {
    val phi1 = listOf(0, 1, 1)
    val phi2 = listOf(1, 0, 0)

    assertTrue { since(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true since and when phi2 was true, while no interval is given.
   * - phi1: true since phi2 was true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true since and when phi2 was true, while no interval is given`() {
    val phi1 = listOf(1, 1, 1)
    val phi2 = listOf(1, 0, 0)

    assertTrue { since(createTicks(phi1, phi2)[2], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true since phi2 was true and phi2 is true in the middle, while no interval is
   * given.
   * - phi1: true since phi2 was true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true since phi2 is true and phi2 is true in the middle, while no interval is given`() {
    val phi1 = listOf(0, 0, 1, 1)
    val phi2 = listOf(0, 1, 0, 0)

    assertTrue { since(createTicks(phi1, phi2)[3], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true since and when phi2 was true and phi2 is true in the middle, while no
   * interval is given.
   * - phi1: true since phi2 was true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true since and when phi2 was true and phi2 is true in the middle, while no interval is given`() {
    val phi1 = listOf(0, 1, 1, 1)
    val phi2 = listOf(0, 1, 0, 0)

    assertTrue { since(createTicks(phi1, phi2)[3], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true and phi2 is true in the middle, while no interval is given.
   * - phi1: true since phi2 was true
   * - phi2: true in the middle false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true and phi2 is true in the middle, while no interval is given`() {
    val phi1 = listOf(1, 1, 1, 1)
    val phi2 = listOf(0, 1, 0, 0)

    assertTrue { since(createTicks(phi1, phi2)[3], null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
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
      since(
          createTicks(phi1, phi2)[2],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi2 is true with interval with swapped bounds.
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
      since(
          createTicks(phi1, phi2)[2],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi2 is true with interval with negative bounds.
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
      since(
          createTicks(phi1, phi2)[2],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi1 is true and phi2 holds is true in the middle of the interval starting at zero.
   * - phi1: true since phi2 was true
   * - phi2: true at end false before
   * - interval: (0, 3)
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true and phi2 is true in the middle of the interval starting at zero`() {
    val phi1 = listOf(1, 1, 1, 1)
    val phi2 = listOf(0, 1, 0, 0)
    val interval = 0 to 3

    assertTrue {
      since(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi1 is true and phi2 is true after the interval starting at zero.
   * - phi1: true since phi2 was true
   * - phi2: true directly after the interval
   * - interval: (0, 1)
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true and phi2 is true after the interval starting at zero`() {
    val phi1 = listOf(1, 1, 1, 1)
    val phi2 = listOf(0, 1, 0, 0)
    val interval = 0 to 1

    assertFalse {
      since(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi1 is true and phi2 holds is true in the middle of the interval starting at zero.
   * - phi1: true since phi2 was true
   * - phi2: true at end false before
   * - interval: (1, 3)
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true and phi2 is true in the middle of the interval starting at one`() {
    val phi1 = listOf(1, 1, 1, 1)
    val phi2 = listOf(0, 1, 0, 0)
    val interval = 1 to 3

    assertTrue {
      since(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi1 is true and phi2 is true after the interval starting at zero.
   * - phi1: true since phi2 was true
   * - phi2: true directly after the interval
   * - interval: (1, 2)
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true and phi2 is true after the interval starting at one`() {
    val phi1 = listOf(1, 1, 1, 1)
    val phi2 = listOf(0, 1, 0, 0)
    val interval = 1 to 2

    assertFalse {
      since(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi1 is true and phi2 holds is true in the middle of the interval starting at zero.
   * - phi1: true since phi2 was true
   * - phi2: true at end false before
   * - interval: (1, 3)
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true and phi2 is true before and in the middle of the interval starting at one`() {
    val phi1 = listOf(1, 1, 1, 1)
    val phi2 = listOf(0, 1, 0, 1)
    val interval = 1 to 3

    assertTrue {
      since(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi1 is true and phi2 is true after the interval starting at zero.
   * - phi1: true since phi2 was true
   * - phi2: true directly after the interval
   * - interval: (1, 2)
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true and phi2 is true before and after the interval starting at one`() {
    val phi1 = listOf(1, 1, 1, 1)
    val phi2 = listOf(1, 0, 0, 1)
    val interval = 1 to 2

    assertFalse {
      since(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }

  /**
   * Test when phi1 is false before the interval and phi2 is true in the interval starting at zero.
   * - phi1: one time false before the interval
   * - phi2: true directly after the interval
   * - interval: (1, 2)
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is false before interval and phi2 is true before and after the interval starting at one`() {
    val phi1 = listOf(1, 1, 1, 0)
    val phi2 = listOf(1, 0, 0, 1)
    val interval = 1 to 2

    assertFalse {
      since(
          createTicks(phi1, phi2)[3],
          createInterval(interval),
          phi1 = { it.phi1 },
          phi2 = { it.phi2 })
    }
  }
}
