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

import tools.aqua.stars.data.av.*

fun emptyBlock(id: String = ""): Block {
  return Block(id = id, roads = listOf(), fileName = "")
}

fun emptyRoad(id: Int = 0, isJunction: Boolean = false, block: Block = emptyBlock()): Road {
  return Road(
      id = id,
      block = block,
      isJunction = isJunction,
      lanes = listOf(),
  )
}

fun emptyLane(
    laneId: Int = 1,
    road: Road = emptyRoad(),
    laneLength: Double = 0.0,
    speedLimits: List<SpeedLimit> = listOf(),
    staticTrafficLights: List<StaticTrafficLight> = listOf(),
    successorLanes: List<ContactLaneInfo> = listOf(),
    landmarks: List<Landmark> = emptyList(),
    laneDirection: LaneDirection = LaneDirection.UNKNOWN
): Lane {
  return Lane(
      laneId = laneId,
      road = road,
      intersectingLanes = listOf(),
      laneLength = laneLength,
      laneMidpoints = listOf(),
      laneType = LaneType.Driving,
      laneWidth = 0.0,
      predecessorLanes = listOf(),
      contactAreas = listOf(),
      successorLanes = successorLanes,
      speedLimits = speedLimits,
      trafficLights = staticTrafficLights,
      landmarks = landmarks,
      laneDirection = laneDirection,
      yieldLanes = listOf())
}

fun emptyRotation(): Rotation {
  return Rotation(0.0, 0.0, 0.0)
}

fun emptyLocation(): Location {
  return Location(0.0, 0.0, 0.0)
}

fun emptyVector3D(): Vector3D {
  return Vector3D(0.0, 0.0, 0.0)
}

fun emptyWeatherParameters(weatherType: WeatherType = WeatherType.Clear): WeatherParameters {
  return WeatherParameters(
      type = weatherType,
      cloudiness = 0.0,
      rayleighScatteringScale = 0.0,
      mieScatteringScale = 0.0,
      scatteringIntensity = 0.0,
      fogFalloff = 0.0,
      wetness = 0.0,
      fogDistance = 0.0,
      fogDensity = 0.0,
      sunAltitudeAngle = 0.0,
      sunAzimuthAngle = 0.0,
      windIntensity = 0.0,
      precipitationDeposits = 0.0,
      precipitation = 0.0)
}

fun emptyTickData(
    currentTick: Double = 0.0,
    blocks: List<Block> = listOf(),
    trafficLights: List<TrafficLight> = listOf(),
    weatherParameters: WeatherParameters = emptyWeatherParameters(),
    actors: List<Actor> = listOf(),
    daytime: Daytime = Daytime.Sunset
): TickData {
  return TickData(
      currentTick = currentTick,
      entities = actors,
      blocks = blocks,
      trafficLights = trafficLights,
      weather = weatherParameters,
      daytime = daytime)
}

fun emptyPedestrian(
    id: Int = 1,
    lane: Lane = emptyLane(),
    positionOnLane: Double = 0.0,
    tickData: TickData = emptyTickData()
): Pedestrian {
  return Pedestrian(id = id, positionOnLane = positionOnLane, tickData = tickData, lane = lane)
}

fun emptyVehicle(
    egoVehicle: Boolean = false,
    id: Int = 0,
    lane: Lane = emptyLane(),
    positionOnLane: Double = 0.0,
    tickData: TickData = emptyTickData(),
    location: Location = emptyLocation()
): Vehicle {
  return Vehicle(
      id = id,
      rotation = emptyRotation(),
      location = location,
      egoVehicle = egoVehicle,
      acceleration = emptyVector3D(),
      angularVelocity = emptyVector3D(),
      forwardVector = emptyVector3D(),
      lane = lane,
      positionOnLane = positionOnLane,
      tickData = tickData,
      typeId = "",
      velocity = emptyVector3D())
}

fun emptyStaticTrafficLight(): StaticTrafficLight {
  return StaticTrafficLight(
      id = 0, rotation = emptyRotation(), location = emptyLocation(), stopLocations = listOf())
}

fun emptyTrafficLight(
    id: Int = 0,
    relatedOpenDriveId: Int = 0,
    state: TrafficLightState = TrafficLightState.Unknown
): TrafficLight {
  return TrafficLight(id = id, state = state, relatedOpenDriveId = relatedOpenDriveId)
}
