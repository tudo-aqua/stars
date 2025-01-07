/*
 * Copyright 2024-2025 The STARS Project Authors
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

import tools.aqua.stars.core.types.TickUnit

/**
 * This class is used for test cases and implements the [TickUnit] interface with an [Int] tick
 * property.
 *
 * @property tick Stores a tick as an [Int].
 */
class TestUnit(val tick: Int) : TickUnit<TestUnit, TestDifference> {
  override fun compareTo(other: TestUnit): Int = tick.compareTo(other.tick)

  override fun plus(other: TestDifference): TestUnit = TestUnit(tick + other.diff)

  override fun minus(other: TestDifference): TestUnit = TestUnit(tick - other.diff)

  override fun minus(other: TestUnit): TestDifference = TestDifference(tick - other.tick)
}
