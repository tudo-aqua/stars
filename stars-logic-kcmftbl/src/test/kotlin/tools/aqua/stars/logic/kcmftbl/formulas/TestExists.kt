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
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import tools.aqua.stars.logic.kcmftbl.exists

/** This class tests the CMFTBL operator [exists]. */
class TestExists {
  /** Test when no element matches the predicate. */
  @Test
  fun `Test when no element matches the predicate`() {
    val list = listOf(2, 4, 6, 8)
    assertFalse(exists(list) { it % 2 != 0 })
  }

  /** Test when not all elements match the predicate. */
  @Test
  fun `Test when not all elements match the predicate`() {
    val list = listOf(2, 3, 6, 8)
    assertTrue(exists(list) { it % 2 != 0 })
  }

  /** Test when all elements match the predicate. */
  @Test
  fun `Test when all elements match the predicate`() {
    val list = listOf(1, 3, 5, 7)
    assertTrue(exists(list) { it % 2 != 0 })
  }

  /** Test when first element matches the predicate. */
  @Test
  fun `Test when first element matches the predicate`() {
    val list = listOf(1, 4, 6, 8)
    assertTrue(exists(list) { it % 2 != 0 })
  }

  /** Test when last element matches the predicate. */
  @Test
  fun `Test when last element matches the predicate`() {
    val list = listOf(2, 4, 6, 9)
    assertTrue(exists(list) { it % 2 != 0 })
  }

  /** Test when collection is empty. */
  @Test
  fun `Test when collection is empty`() {
    val list = emptyList<Int>()
    assertFalse(exists(list) { true })
  }
}
