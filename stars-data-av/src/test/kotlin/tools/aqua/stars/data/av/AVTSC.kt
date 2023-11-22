/*
 * Copyright 2023 The STARS Project Authors
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

package tools.aqua.stars.data.av

import kotlin.test.Test
import tools.aqua.stars.core.evaluation.*
import tools.aqua.stars.core.tsc.builder.all
import tools.aqua.stars.core.tsc.builder.leaf
import tools.aqua.stars.core.tsc.builder.root
import tools.aqua.stars.data.av.dataclasses.*
import tools.aqua.stars.logic.kcmftbl.*

class AVTSC {

  @Test
  fun testTSCConstruction() {

    val soBetween =
        predicate(Vehicle::class to Vehicle::class) { _, v0, v1 ->
          v1.tickData.vehicles
              .filter { it.id != v0.id && it.id != v1.id }
              .any { vx ->
                (v0.lane.uid == vx.lane.uid || v1.lane.uid == vx.lane.uid) &&
                    (!(v0.lane.uid == vx.lane.uid) || (v0.positionOnLane < vx.positionOnLane)) &&
                    (!(v1.lane.uid == vx.lane.uid) || (v1.positionOnLane > vx.positionOnLane))
              }
        }

    val obeyedSpeedLimit =
        predicate(Vehicle::class) { _, v ->
          globally(v) { v -> (v.effVelocityInMPH) <= v.lane.speedAt(v.positionOnLane) }
        }

    root<Actor, TickData, Segment> {
      all("TSC Root") {
        leaf("someone between") {
          condition = { ctx ->
            ctx.segment.vehicleIds.any { v1 ->
              soBetween.holds(ctx, ctx.segment.firstTickId, ctx.primaryEntityId, v1)
            }
          }
        }
        leaf("obeyed speed limit") { condition = { ctx -> obeyedSpeedLimit.holds(ctx) } }
      }
    }
  }
}
