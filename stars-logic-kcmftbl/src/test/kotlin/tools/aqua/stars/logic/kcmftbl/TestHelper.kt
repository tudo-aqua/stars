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

package tools.aqua.stars.logic.kcmftbl

import tools.aqua.stars.logic.kcmftbl.data.BooleanTick
import tools.aqua.stars.logic.kcmftbl.data.TestDifference
import tools.aqua.stars.logic.kcmftbl.data.TestSegment
import tools.aqua.stars.logic.kcmftbl.data.TestUnit

/** Creates ticks and returns first by parsing INT-Lists to Boolean values. */
fun createTicks(phi1: List<Int>, phi2: List<Int>): List<BooleanTick> {
  val ticks =
      phi1.indices.associate {
        TestUnit(it) to
            BooleanTick(
                TestUnit(it),
                listOf(),
                TestSegment(listOf(), mapOf(), "", -1),
                phi1[it] == 1,
                phi2[it] == 1)
      }

  TestSegment(ticks.values.toList(), ticks.toMap(), "", -1).also {
    it.tickData.forEach { t -> t.segment = it }
  }

  return ticks.values.toList()
}

/**
 * Creates ticks and returns first by parsing INT-Lists to Boolean values. Phi2 is set to always
 * true
 */
fun createTicks(phi1: List<Int>): List<BooleanTick> = createTicks(phi1, List(phi1.size) { 1 })

/** Creates an interval by wrapping the INT-Values into [TestDifference]s. */
fun createInterval(interval: Pair<Int, Int>): Pair<TestDifference, TestDifference> =
    TestDifference(interval.first) to TestDifference(interval.second)

/** Creates all combinations of 0-1 arrays with length [n]. */
fun combinations(n: Int): Collection<List<Int>> {
  require(n >= 1)

  var result = listOf(listOf(0), listOf(1))

  repeat(n - 1) {
    val temp = mutableListOf<List<Int>>()

    result.forEach { r ->
      temp.add(r + 0)
      temp.add(r + 1)
    }
    result = temp
  }

  return result
}

/**
 * All interval combinations with lower bound (lb) 0 and upper bound (ub) [ub], where lb < ub
 * including null.
 */
fun intervals(ub: Int): List<Pair<TestDifference, TestDifference>?> {
  val result = mutableListOf<Pair<TestDifference, TestDifference>?>(null)

  for (i in 0 until ub) {
    for (j in i + 1..ub) {
      result.add(TestDifference(i) to TestDifference(j))
    }
  }

  return result
}
