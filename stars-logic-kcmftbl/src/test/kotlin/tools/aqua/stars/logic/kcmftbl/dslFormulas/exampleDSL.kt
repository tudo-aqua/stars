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

import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.Ref

class exampleDSL {
  fun monitors() {
    val hasMidTrafficDensity = formula {
      exists { x: Ref<Vehicle> ->
        eventually {
          (const(6) leq
              term { x.now().let { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size } }) and
              (term { x.now().let { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size } } leq
                  const(15))
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
}
