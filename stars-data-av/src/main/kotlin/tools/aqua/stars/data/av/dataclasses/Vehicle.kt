/*
 * Copyright 2023-2026 The STARS Project Authors
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
 * @param id The identifier of the [Vehicle].
 * @param typeId The type identifier of the [Vehicle].
 * @param attributes The attributes of the [Vehicle].
 * @param isAlive Whether the [Vehicle] is alive.
 * @param isActive Whether the [Vehicle] is active.
 * @param isDormant Whether the [Vehicle] is dormant.
 * @param semanticTags The semantic tags of the [Vehicle].
 * @param boundingBox The [BoundingBox] of the [Vehicle].
 * @param location The [Location] of the [Vehicle].
 * @param rotation The [Rotation] of the [Vehicle].
 * @param collisions The [List] of all colliding [Actor] IDs of the [Vehicle]. Default: empty
 *   [List].
 * @param lane The [Vehicle]'s [Lane].
 * @param positionOnLane The [Vehicle]'s position in the [Lane].
 * @property isEgo Whether this is the own [Vehicle].
 * @property forwardVector The current forward vector of the [Vehicle].
 * @property velocity The current velocity in m/s of the [Vehicle].
 * @property acceleration The current acceleration m/s² of the [Vehicle].
 * @property angularVelocity The current angular velocity of the [Vehicle].
 * @property vehicleType The [VehicleType] of the [Vehicle].
 */
class Vehicle(
    id: Int = 0,
    typeId: String = "",
    attributes: Map<String, String> = emptyMap(),
    isAlive: Boolean = true,
    isActive: Boolean = true,
    isDormant: Boolean = false,
    semanticTags: List<Int> = emptyList(),
    boundingBox: BoundingBox = BoundingBox(),
    location: Location = Location(),
    rotation: Rotation = Rotation(),
    collisions: List<Int> = emptyList(),
    lane: Lane = Lane(),
    positionOnLane: Double = 0.0,
    var isEgo: Boolean = false,
    val forwardVector: Vector3D = Vector3D(),
    var velocity: Vector3D = Vector3D(),
    var acceleration: Vector3D = Vector3D(),
    val angularVelocity: Vector3D = Vector3D(),
    val vehicleType: VehicleType = VehicleType.CAR,
) :
    Actor(
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
        collisions = collisions,
        lane = lane,
        positionOnLane = positionOnLane,
    ) {

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
        this.lane.speedLimits.firstOrNull { speedLimit ->
          this.positionOnLane in (speedLimit.fromDistanceFromStart..speedLimit.toDistanceFromStart)
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
          collisions = collisions,
          lane = lane,
          positionOnLane = positionOnLane,
          isEgo = isEgo,
          forwardVector = forwardVector,
          velocity = velocity,
          acceleration = acceleration,
          angularVelocity = angularVelocity,
          vehicleType = vehicleType,
      )

  override fun toString(): String =
      "Vehicle(positionOnLane=$positionOnLane, lane=${lane.laneId}, road=${lane.road.id})"

  /**
   * Extension on Vehicle: set its `velocity` field to correspond to the given effective speed (in
   * miles per hour).
   */
  fun setVelocityFromEffVelocityMPH(effVelocityMPH: Double) {
    // 1) convert to meters per second
    val speedMps = effVelocityMPH / 2.237

    // 2) normalize the forward vector
    val dir = Vector3D(1.0, 0.0, 0.0).normalized()

    // 3) scale by speed and assign
    this.velocity = Vector3D(dir.x * speedMps, dir.y * speedMps, dir.z * speedMps)
  }
}
