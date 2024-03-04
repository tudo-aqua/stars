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
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import tools.aqua.stars.logic.kcmftbl.createTicks
import tools.aqua.stars.logic.kcmftbl.until

class TestUntil {

  /**
   * Test when phi2 is true at current time, while no interval is given.
   * - phi1: false
   * - phi2: true at start then false
   * - Expected: true
   */
  @Test
  fun `Test when phi2 is true at current time, while no interval is given`() {
    val phi1 = listOf(0, 0, 0)
    val phi2 = listOf(1, 0, 0)

    assertTrue { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
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

    assertFalse { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
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
    val phi2 = listOf(0, 0, 1)

    assertFalse { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true from the second tick and phi2 is true at the end, while no interval is
   * given.
   * - phi1: true from the second tick until phi2 gets true
   * - phi2: true at end false before
   * - Expected: false
   */
  @Test
  fun `Test when phi1 is true from the second tick and phi2 is true at the end, while no interval is given`() {
    val phi1 = listOf(0, 1, 1)
    val phi2 = listOf(0, 0, 1)

    assertFalse { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true until phi2 is true, while no interval is given.
   * - phi1: true until phi2 gets true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true until phi2 is true, while no interval is given`() {
    val phi1 = listOf(1, 1, 0)
    val phi2 = listOf(0, 0, 1)

    assertTrue { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true until and when phi2 is true, while no interval is given.
   * - phi1: true until phi2 gets true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true until and when phi2 is true, while no interval is given`() {
    val phi1 = listOf(1, 1, 1)
    val phi2 = listOf(0, 0, 1)

    assertTrue { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true until phi2 is true and phi2 is true in the middle, while no interval is
   * given.
   * - phi1: true until phi2 gets true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true until phi2 is true and phi2 is true in the middle, while no interval is given`() {
    val phi1 = listOf(1, 1, 0, 0)
    val phi2 = listOf(0, 0, 1, 0)

    assertTrue { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true until and when phi2 is true and phi2 is true in the middle, while no
   * interval is given.
   * - phi1: true until phi2 gets true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true until and when phi2 is true and phi2 is true in the middle, while no interval is given`() {
    val phi1 = listOf(1, 1, 1, 0)
    val phi2 = listOf(0, 0, 1, 0)

    assertTrue { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }

  /**
   * Test when phi1 is true and phi2 is true in the middle, while no interval is given.
   * - phi1: true until phi2 gets true
   * - phi2: true at end false before
   * - Expected: true
   */
  @Test
  fun `Test when phi1 is true and phi2 is true in the middle, while no interval is given`() {
    val phi1 = listOf(1, 1, 1, 1)
    val phi2 = listOf(0, 0, 1, 0)

    assertTrue { until(createTicks(phi1, phi2), null, phi1 = { it.phi1 }, phi2 = { it.phi2 }) }
  }
}
