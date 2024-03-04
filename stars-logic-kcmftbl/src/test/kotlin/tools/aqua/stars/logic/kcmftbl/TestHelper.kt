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

package tools.aqua.stars.logic.kcmftbl

import tools.aqua.stars.logic.kcmftbl.data.BooleanTick
import tools.aqua.stars.logic.kcmftbl.data.TestSegment
import tools.aqua.stars.logic.kcmftbl.data.TestUnit

/** Creates ticks and returns first by parsing INT-Lists to Boolean values. */
fun createTicks(phi1: List<Int>, phi2: List<Int>): BooleanTick {
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

  return ticks.values.first()
}
