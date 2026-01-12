/*
 * Copyright 2026 The STARS Project Authors
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

package tools.aqua.stars.importer.carla

import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Road
import tools.aqua.stars.data.av.dataclasses.World
import tools.aqua.stars.importer.carla.dataclasses.JsonActorPosition
import tools.aqua.stars.importer.carla.dataclasses.JsonBoundingBox
import tools.aqua.stars.importer.carla.dataclasses.JsonDataWeatherParameters
import tools.aqua.stars.importer.carla.dataclasses.JsonLocation
import tools.aqua.stars.importer.carla.dataclasses.JsonRotation
import tools.aqua.stars.importer.carla.dataclasses.JsonTickData
import tools.aqua.stars.importer.carla.dataclasses.JsonVector3D
import tools.aqua.stars.importer.carla.dataclasses.JsonVehicle

/** A simple [Lane] instance for testing purposes. */
val simpleLane: Lane = Lane()

/** A simple [Road] instance with a single lane for testing purposes. */
val simpleRoad: Road = Road(id = 1, lanes = listOf(simpleLane))

/** Creates an empty [World] instance for testing purposes. */
val simpleWorld: World = World(straights = listOf(simpleRoad))

/** A simple [JsonLocation] instance for testing purposes. */
val simpleLocation: JsonLocation = JsonLocation(x = 0.0, y = 0.0, z = 0.0)

/** A simple [JsonBoundingBox] instance for testing purposes. */
val simpleBoundingBox: JsonBoundingBox =
    JsonBoundingBox(
        extent = JsonVector3D(0.0, 0.0, 0.0),
        location = JsonLocation(0.0, 0.0, 0.0),
        rotation = JsonRotation(0.0, 0.0, 0.0),
        vertices = (0..7).map { simpleLocation },
    )

/** A simple [JsonRotation] instance for testing purposes. */
val simpleRotation: JsonRotation = JsonRotation(pitch = 0.0, yaw = 0.0, roll = 0.0)

/** A simple forward [JsonVector3D] instance for testing purposes. */
val simpleForwardVector: JsonVector3D = JsonVector3D(x = 1.0, y = 0.0, z = 0.0)

/** A simple [JsonVector3D] instance for testing purposes. */
val simpleVelocity: JsonVector3D = JsonVector3D(x = 0.0, y = 0.0, z = 0.0)

/** A simple [JsonVector3D] instance for testing purposes. */
val simpleAcceleration: JsonVector3D = JsonVector3D(x = 0.0, y = 0.0, z = 0.0)

/** Creates a list of [JsonTickData] with one lane and [numberOfVehicles] vehicles per tick. */
fun getTickDataWithOneLaneWithNVehicles(
    numberOfVehicles: Int,
    hasEgo: Boolean = false,
    numberOfTicks: Int = 1,
): List<JsonTickData> {
  val ticks = mutableListOf<JsonTickData>()
  for (tickIndex in 0 until numberOfTicks) {
    val actorPositions = mutableListOf<JsonActorPosition>()
    for (vehicleIndex in 0 until numberOfVehicles) {
      val vehicle = getSimpleVehicle(id = vehicleIndex, isEgo = false)
      if (hasEgo && vehicleIndex == 0) {
        vehicle.egoVehicle = true
      }
      actorPositions +=
          JsonActorPosition(
              positionOnLane = 0 * 10.0,
              laneId = simpleLane.laneId,
              roadId = simpleRoad.id,
              actor = vehicle,
          )
    }
    ticks.add(
        JsonTickData(
            currentTick = tickIndex.toDouble(),
            actorPositions = actorPositions,
            weatherParameters = JsonDataWeatherParameters(),
        )
    )
  }
  return ticks
}

/** Creates a simple [JsonVehicle] instance for testing purposes. */
fun getSimpleVehicle(id: Int, isEgo: Boolean): JsonVehicle =
    JsonVehicle(
        id = id,
        typeId = cars.first(),
        attributes = emptyMap(),
        isAlive = true,
        isActive = true,
        isDormant = true,
        semanticTags = emptyList(),
        boundingBox = simpleBoundingBox,
        location = simpleLocation,
        rotation = simpleRotation,
        egoVehicle = isEgo,
        forwardVector = simpleForwardVector,
        velocity = simpleVelocity,
        acceleration = simpleAcceleration,
        angularVelocity = simpleVelocity,
        collisions = emptyList(),
    )
