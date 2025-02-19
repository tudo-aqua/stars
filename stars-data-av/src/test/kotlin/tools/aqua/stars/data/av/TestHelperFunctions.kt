/*
 * Copyright 2023-2025 The STARS Project Authors
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

import tools.aqua.stars.data.av.dataclasses.*

/** Empty [Block]. */
fun emptyBlock(id: String = "", roads: List<Road>): Block = Block(id = id, roads = roads)

/** Empty [Road]. */
fun emptyRoad(id: Int = 0, isJunction: Boolean = false, lanes: List<Lane>): Road =
    Road(
        id = id,
        isJunction = isJunction,
        lanes = lanes,
    )

/** Empty [Lane]. */
fun emptyLane(
    laneId: Int = 1,
    laneLength: Double = 0.0,
    speedLimits: List<SpeedLimit> = listOf(),
    staticTrafficLights: List<StaticTrafficLight> = listOf(),
    successorLanes: List<ContactLaneInfo> = listOf(),
    landmarks: List<Landmark> = emptyList(),
    laneDirection: LaneDirection = LaneDirection.UNKNOWN
): Lane =
    Lane(
            laneId = laneId,
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
        .also { emptyBlock(roads = listOf(emptyRoad(lanes = listOf(it)))) }

/** Empty [Rotation]. */
fun emptyRotation(): Rotation = Rotation(0.0, 0.0, 0.0)

/** Empty [Location]. */
fun emptyLocation(): Location = Location(0.0, 0.0, 0.0)

/** Empty [Vector3D]. */
fun emptyVector3D(): Vector3D = Vector3D(0.0, 0.0, 0.0)

/**
 * Empty [WeatherParameters] with given [weatherType].
 *
 * @param weatherType (Default: [WeatherType.Clear]) The [WeatherType] to be assigned to the
 *   [WeatherParameters].
 * @return An empty [WeatherParameters] object.
 */
fun emptyWeatherParameters(weatherType: WeatherType = WeatherType.Clear): WeatherParameters =
    WeatherParameters(
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

/** Empty [TickData]. */
fun emptyTickData(
    currentTick: TickDataUnitSeconds = TickDataUnitSeconds(0.0),
    blocks: List<Block> = listOf(),
    trafficLights: List<TrafficLight> = listOf(),
    weatherParameters: WeatherParameters = emptyWeatherParameters(),
    actors: List<Actor>,
    daytime: Daytime = Daytime.Sunset
): TickData =
    TickData(
        currentTick = currentTick,
        entities = actors,
        blocks = blocks,
        trafficLights = trafficLights,
        weather = weatherParameters,
        daytime = daytime)

/** Empty [Pedestrian]. */
fun emptyPedestrian(
    id: Int = 1,
    lane: Lane = emptyLane(),
    positionOnLane: Double = 0.0
): Pedestrian = Pedestrian(id = id, positionOnLane = positionOnLane, lane = lane)

/** Empty non-ego [Vehicle]. */
fun emptyVehicle(
    isEgo: Boolean = false,
    id: Int = 0,
    lane: Lane = emptyLane(),
    positionOnLane: Double = 0.0,
    location: Location = emptyLocation()
): Vehicle =
    Vehicle(
        id = id,
        rotation = emptyRotation(),
        location = location,
        isEgo = isEgo,
        acceleration = emptyVector3D(),
        angularVelocity = emptyVector3D(),
        forwardVector = emptyVector3D(),
        lane = lane,
        positionOnLane = positionOnLane,
        typeId = "",
        vehicleType = VehicleType.CAR,
        velocity = emptyVector3D())

/** Empty [StaticTrafficLight]. */
fun emptyStaticTrafficLight(id: Int): StaticTrafficLight =
    StaticTrafficLight(
        id = id, rotation = emptyRotation(), location = emptyLocation(), stopLocations = listOf())

/** Empty [TrafficLight] with state [TrafficLightState.Unknown]. */
fun emptyTrafficLight(
    id: Int = 0,
    relatedOpenDriveId: Int = 0,
    state: TrafficLightState = TrafficLightState.Unknown
): TrafficLight = TrafficLight(id = id, state = state, relatedOpenDriveId = relatedOpenDriveId)
