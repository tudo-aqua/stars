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

@file:Suppress("unused")

package tools.aqua.stars.logic.kcmftbl.dslFormulas

import kotlin.math.abs
import kotlin.math.sign
import org.junit.jupiter.api.Test
import tools.aqua.stars.data.av.dataclasses.*
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.Ref

class exampleDSL {
  @Test
  fun monitors() {
    val hasMidTrafficDensity = formula {
      forall() { x: Ref<Vehicle> ->
        eventually {
          (const(6) leq
              term { x.now().let { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size } }) and
              (term { x.now().let { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size } } leq
                  const(15))
        }
      }
    }
    val hasMidTrafficDensityPred = formula {
      exists { x: Ref<Vehicle> ->
        eventually {
          pred(x) {
            6 <= x.now().let { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size }
          } and
              pred(x) {
                x.now().let { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size } <= 6
              }
        }
      }
    }
    formula { v: Ref<Vehicle> ->
      minPrevalence(0.6) {
        neg(hasMidTrafficDensity) or
            (term { v.now().effVelocityInKmPH } leq
                term { v.now().lane.speedLimits[v.now().positionOnLane.toInt()].speedLimit })
      }
    }

    val changedLane = formula { v: Ref<Vehicle> ->
      binding(term { v.now().lane }) { l -> eventually { l ne term { v.now().lane } } }
    }
  }

  @Test
  fun varyingInOut() {
    val outFormula = formula { x: Ref<Vehicle> -> tt() }
    val noAnd = formula { exists { x: Ref<Vehicle> -> tt() } }
    val predAnd = formula { exists { x: Ref<Vehicle> -> tt() and ff() } }
    val outAnd = formula {
      exists { x: Ref<Vehicle> -> outFormula.holds(x) and outFormula.holds(x) }
    }
  }

  @Test
  fun overtaking() {
    val isBehind = formula { r1: Ref<Vehicle>, r2: Ref<Vehicle> ->
      pred(r1, r2) { r1.now().lane.road == r2.now().lane.road } and
          pred(r1, r2) { r1.now().lane.laneId.sign == r2.now().lane.laneId.sign } and
          pred(r1, r2) { abs(r1.now().positionOnLane - r2.now().positionOnLane) <= 2.0 } and
          pred(r1, r2) { (r1.now().positionOnLane + 2.0) < r2.now().positionOnLane }
    }
    val bothOver10Mph = formula { r1: Ref<Vehicle>, r2: Ref<Vehicle> ->
      pred(r1, r2) { r1.now().effVelocityInMPH > 10 } and
          pred(r1, r2) { r2.now().effVelocityInMPH > 10 }
    }
    val besides = formula { r1: Ref<Vehicle>, r2: Ref<Vehicle> ->
      pred(r1, r2) { r1.now().lane.road == r2.now().lane.road } and
          pred(r1, r2) { r1.now().lane.laneId.sign == r2.now().lane.laneId.sign } and
          pred(r1, r2) { abs(r1.now().positionOnLane - r2.now().positionOnLane) <= 2.0 } and
          pred(r1, r2) { abs(r2.now().positionOnLane - r1.now().positionOnLane) <= 2.0 }
    }
    val overtaking = formula { r1: Ref<Vehicle> ->
      exists { r2: Ref<Vehicle> ->
        isBehind.holds(r1, r2) and
            bothOver10Mph.holds(r1, r2) and
            until(1 to 100) {
              isBehind.holds(r1, r2) and
                  isBehind.holds(r1, r2) and
                  isBehind.holds(r1, r2) and
                  bothOver10Mph.holds(r1, r2)
              besides.holds(r1, r2) and
                  bothOver10Mph.holds(r1, r2) and
                  until(1 to 100) {
                    besides.holds(r1, r2) and bothOver10Mph.holds(r1, r2)
                    isBehind.holds(r1, r2) and bothOver10Mph.holds(r1, r2)
                  }
            }
      }
    }
  }
}
