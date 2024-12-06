/*
 * Copyright 2023-2024 The STARS Project Authors
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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package tools.aqua.stars.data.av.dataclasses

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Data class for vehicles.
 *
 * @property id The identifier of the vehicle.
 * @property tickData [TickData].
 * @property positionOnLane The [Vehicle]'s position in the [Lane].
 * @property lane The [Vehicle]'s [Lane].
 * @property typeId The type identifier.
 * @property vehicleType The [VehicleType].
 * @property isEgo Whether this is the own vehicle.
 * @property location The [Location] of the vehicle.
 * @property forwardVector The current forward vector.
 * @property rotation The [Rotation] of the vehicle.
 * @property velocity The current velocity in m/s.
 * @property acceleration The current acceleration m/s².
 * @property angularVelocity The current angular velocity.
 */
data class Vehicle(
    override val id: Int,
    override val typeId: String,
    override val attributes: Map<String, String>,
    override val isAlive: Boolean,
    override val isActive: Boolean,
    override val isDormant: Boolean,
    override val semanticTags: List<Int>,
    override val boundingBox: BoundingBox,
    override val location: Location,
    override val rotation: Rotation,
    var isEgo: Boolean,
    val forwardVector: Vector3D,
    var velocity: Vector3D,
    var acceleration: Vector3D,
    val angularVelocity: Vector3D,
    var lane: Lane,
    var positionOnLane: Double,
    val vehicleType: VehicleType,
    override val tickData: TickData,
) : Actor() {

  /** Whether the vehicle is of [VehicleType.BICYCLE]. */
  val isBicycle: Boolean
    get() = vehicleType == VehicleType.BICYCLE

  /** Effective velocity in m/s based on the [velocity] vector. */
  val effVelocityInMPerS: Double
    get() = sqrt(velocity.x.pow(2) + velocity.y.pow(2) + velocity.z.pow(2))

  /** Effective velocity in km/h based on [effVelocityInMPerS]. */
  val effVelocityInKmPH: Double
    get() = this.effVelocityInMPerS * 3.6

  /** Effective velocity in miles/hour based on [effVelocityInMPerS]. */
  val effVelocityInMPH: Double
    get() = this.effVelocityInMPerS * 2.237

  /** Effective acceleration in m/s² based on the [acceleration] vector. */
  val effAccelerationInMPerSSquared: Double
    get() = sqrt(acceleration.x.pow(2) + acceleration.y.pow(2) + acceleration.z.pow(2))

  /** SpeedLimit of the road/lane for the current location of this [Vehicle]. */
  val applicableSpeedLimit: SpeedLimit?
    get() =
        lane.speedLimits.firstOrNull { speedLimit ->
          positionOnLane in (speedLimit.fromDistanceFromStart..speedLimit.toDistanceFromStart)
        }

  override fun clone(newTickData: TickData): Actor =
      Vehicle(
          id = id,
          typeId = typeId,
          attributes = attributes,
          isAlive = isAlive,
          isActive = isActive,
          isDormant = isDormant,
          semanticTags = semanticTags,
          boundingBox = boundingBox,
          location = location,
          rotation = rotation,
          isEgo = isEgo,
          forwardVector = forwardVector,
          velocity = velocity,
          acceleration = acceleration,
          angularVelocity = angularVelocity,
          lane = lane,
          positionOnLane = positionOnLane,
          vehicleType = vehicleType,
          tickData = newTickData)

  override fun toString(): String =
      "Vehicle(id=$id, tickData=${tickData}, positionOnLane=$positionOnLane, lane=${lane.laneId}, road=${lane.road.id})"

  override fun equals(other: Any?): Boolean {
    if (other is Vehicle) {
      return id == other.id &&
          tickData.currentTick == other.tickData.currentTick &&
          positionOnLane == other.positionOnLane &&
          lane.laneId == other.lane.laneId &&
          lane.road.id == other.lane.road.id
    }
    return super.equals(other)
  }

  override fun hashCode(): Int {
    var result = id
    result = 31 * result + tickData.currentTick.hashCode()
    result = 31 * result + positionOnLane.hashCode()
    result = 31 * result + lane.laneId.hashCode()
    result = 31 * result + lane.road.id.hashCode()
    return result
  }
}
