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

package tools.aqua.stars.core

import kotlin.test.Test

/** Tests for list extension functions. */
class ListExtensionsTest {

  /** Tests powerlist() on empty list. */
  @Test
  fun testPowerlistEmpty() {
    val list = listOf<Int>()
    val result = list.powerlist()
    assert(result == listOf(listOf<Int>()))
  }

  /** Tests powerlist() on list with one item. */
  @Test
  fun testPowerlistOneItem() {
    val list = listOf(0)
    val result = list.powerlist()
    assert(result == listOf(listOf(), listOf(0)))
  }

  /** Tests powerlist() on list with multiple items. */
  @Test
  fun testPowerlistMultipleItems() {
    val list = listOf(0, 1, 2)
    val result = list.powerlist()
    val expected =
        listOf(
            listOf(),
            listOf(0),
            listOf(1),
            listOf(2),
            listOf(0, 1),
            listOf(0, 2),
            listOf(1, 2),
            listOf(0, 1, 2))
    assert(result == expected)
  }

  /** Tests crossProduct() on three lists with two items each. */
  @Test
  fun crossProductTestTwoItemsEach() {
    val list1 = listOf(listOf("a"), listOf("b"))
    val list2 = listOf(listOf(0), listOf(1))
    val list3 = listOf(listOf(false), listOf(true))

    val result = listOf(list1, list2, list3).crossProduct()
    val expected =
        listOf(
            listOf("a", 0, false),
            listOf("a", 0, true),
            listOf("a", 1, false),
            listOf("a", 1, true),
            listOf("b", 0, false),
            listOf("b", 0, true),
            listOf("b", 1, false),
            listOf("b", 1, true),
        )

    assert(
        result.size == expected.size &&
            result.containsAll(expected) &&
            expected.containsAll(result))
  }
}
