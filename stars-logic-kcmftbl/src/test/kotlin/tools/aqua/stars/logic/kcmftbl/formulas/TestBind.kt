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
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import tools.aqua.stars.logic.kcmftbl.bind

/** This class tests the CMFTBL operator [bind]. */
class TestBind {
  /** Test identity with list by reference. */
  @Test
  fun `Test identity with list by reference`() {
    val list = listOf(2, 4, 6, 8)
    assertTrue(bind(list) { it === list })
  }

  /** Test identity with list by content. */
  @Test
  fun `Test identity with list by content`() {
    val list = listOf(2, 4, 6, 8)
    assertTrue(bind(list) { it == list })
  }

  /** Test side effects. */
  @Test
  fun `Test side effects`() {
    val list = mutableListOf(2, 4, 6, 8)
    val expected = listOf(2, 4, 6, 8, 10)
    bind(list) { it.add(10) }

    assertEquals(list, expected)
  }

  /** Test bind on null. */
  @Test
  fun `Test bind on null`() {
    assertTrue(bind(null) { true })
  }
}
