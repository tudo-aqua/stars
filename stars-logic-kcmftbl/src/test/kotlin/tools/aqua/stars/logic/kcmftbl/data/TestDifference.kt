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

package tools.aqua.stars.logic.kcmftbl.data

import tools.aqua.stars.core.types.TickDifference

/**
 * This class is used for tests and implements the [TickDifference] interface. It holds a single
 * [Int] value representing the difference between two [TestUnit]s.
 *
 * @property diff The [Int] difference between two [TestUnit]s.
 */
class TestDifference(val diff: Int) : TickDifference<TestDifference> {
  override fun compareTo(other: TestDifference): Int = diff.compareTo(other.diff)

  override fun plus(other: TestDifference): TestDifference = TestDifference(diff + other.diff)

  override fun minus(other: TestDifference): TestDifference = TestDifference(diff - other.diff)

  override fun serialize(): String = diff.toString()

  override fun deserialize(str: String): TestDifference = TestDifference(str.toInt())
}
