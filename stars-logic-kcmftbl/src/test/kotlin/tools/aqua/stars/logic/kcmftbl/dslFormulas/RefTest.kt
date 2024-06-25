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

package tools.aqua.stars.logic.kcmftbl.dslFormulas

import kotlin.test.assertFalse
import org.junit.jupiter.api.Test
import tools.aqua.stars.data.av.*
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.data.av.dataclasses.TickDataUnitSeconds
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.*
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula

class RefTest {
  @Test
  fun refTest() {
    val examplePosition = formula { x: Ref<Vehicle> ->
      pred(x) { x.now().positionOnLane < 8.0 } and pred(x) { x.now().egoVehicle }
    }

    val block = emptyBlock()
    val actor = emptyVehicle(egoVehicle = true, id = 0, positionOnLane = 2.0)
    val actor2 = emptyVehicle(egoVehicle = false, id = 1)
    val tickData =
        emptyTickData(
            blocks = listOf(block),
            currentTick = TickDataUnitSeconds(0.0),
            actors = listOf(actor, actor2))
    val tickData2 =
        emptyTickData(
            blocks = listOf(block),
            currentTick = TickDataUnitSeconds(1.0),
            actors = listOf(actor.copy(positionOnLane = 5.0), actor2))
    val tickData3 =
        emptyTickData(
            blocks = listOf(block),
            currentTick = TickDataUnitSeconds(2.0),
            actors = listOf(actor.copy(positionOnLane = 10.0), actor2))
    val segment = Segment(listOf(tickData, tickData2, tickData3), "", "")

    // sets segment for ref
    Ref.setSegment(segment)
    // sets id for ref
    val ref = makeFixedRef(actor)
    val formula1 = examplePosition.holds(ref)
    if (formula1 is And) {
      val lhs = formula1.lhs
      if (lhs is UnaryPredicate<*>) {
        assert(lhs.phi.invoke())
        lhs.ref.nextTick() // increases tickIdx - alternatively ref.nextTick()
        assert(lhs.phi.invoke())
        ref.nextTick() // alternatively lhs.ref.nextTick()
        assertFalse(lhs.phi.invoke())
      }
    }
    val formula2 = examplePosition.holds(makeFixedRef(actor2))
    if (formula2 is And) {
      val rhs = formula2.rhs
      if (rhs is UnaryPredicate<*>) {
        assertFalse(rhs.phi.invoke())
        // increases tickIdx
        rhs.ref.nextTick()
        assertFalse(rhs.phi.invoke())
        rhs.ref.nextTick()
        assertFalse(rhs.phi.invoke())
      }
    }
  }
}
