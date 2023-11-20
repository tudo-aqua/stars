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

package tools.aqua.stars.data.av.dataclasses

import kotlin.math.pow
import kotlin.math.sqrt

data class Vehicle(
    override val id: Int,
    override val tickData: TickData,
    var positionOnLane: Double,
    var lane: Lane,
    val typeId: String,
    var egoVehicle: Boolean,
    val location: Location,
    val forwardVector: Vector3D,
    val rotation: Rotation,
    /** The velocity vector in m/s */
    var velocity: Vector3D,
    /** The acceleration vector in m/s² */
    var acceleration: Vector3D,
    val angularVelocity: Vector3D,
) : Actor() {
  /** Effective velocity in m/s based on the [velocity] vector */
  val effVelocityInMPerS
    get() = sqrt(velocity.x.pow(2) + velocity.y.pow(2) + velocity.z.pow(2))
  /** Effective velocity in km/h based on [effVelocityInMPerS] */
  val effVelocityInKmPH
    get() = this.effVelocityInMPerS * 3.6
  /** Effective velocity in miles/hour based on [effVelocityInMPerS] */
  val effVelocityInMPH
    get() = this.effVelocityInMPerS * 2.237
  /** Effective acceleration in m/s² based on the [acceleration] vector */
  val effAccelerationInMPerSSquared
    get() = sqrt(acceleration.x.pow(2) + acceleration.y.pow(2) + acceleration.z.pow(2))
  /** SpeedLimit of the road/lane for the current location of this vehicle */
  val applicableSpeedLimit
    get() =
        this.lane.speedLimits.firstOrNull { speedLimit ->
          this.positionOnLane in (speedLimit.fromDistanceFromStart..speedLimit.toDistanceFromStart)
        }

  override fun clone(newTickData: TickData): Actor =
      Vehicle(
          id,
          newTickData,
          positionOnLane,
          lane,
          typeId,
          egoVehicle,
          location,
          forwardVector,
          rotation,
          velocity,
          acceleration,
          angularVelocity)

  override fun toString() =
      "Vehicle(id=$id, tickData=${tickData.currentTick}, positionOnLane=$positionOnLane, lane=${lane.laneId}, road=${lane.road.id})"
}
