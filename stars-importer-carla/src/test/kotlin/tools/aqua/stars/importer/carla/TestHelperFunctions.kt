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

package tools.aqua.stars.importer.carla

import tools.aqua.stars.data.av.dataclasses.Block
import tools.aqua.stars.importer.carla.dataclasses.*

/** Empty [JsonVehicle]. */
fun emptyJsonVehicle(): JsonVehicle =
    JsonVehicle(
        id = 0,
        typeId = "",
        attributes = mapOf(),
        isAlive = true,
        isActive = true,
        isDormant = false,
        semanticTags = listOf(),
        boundingBox = defaultJsonBoundingBox(),
        location = emptyJsonLocation(),
        rotation = emptyJsonRotation(),
        egoVehicle = false,
        forwardVector = emptyJsonVector3D(),
        velocity = emptyJsonVector3D(),
        acceleration = emptyJsonVector3D(),
        angularVelocity = emptyJsonVector3D(),
    )

/** Empty [JsonActorPosition]. */
fun emptyJsonActorPosition(
    actor: JsonActor = emptyJsonVehicle(),
    positionOnLane: Double = 0.0,
    laneId: Int = 0,
    roadId: Int = 0
): JsonActorPosition =
    JsonActorPosition(
        actor = actor, positionOnLane = positionOnLane, laneId = laneId, roadId = roadId)

/** Empty [Block]. */
fun emptyBlock(id: String = ""): Block = Block(id = id, roads = listOf(), fileName = "")

/** Empty [JsonLane]. */
fun emptyJsonLane(): JsonLane =
    JsonLane(
        laneId = 0,
        roadId = 0,
        s = 0.0,
        intersectingLanes = listOf(),
        laneLength = 0.0,
        laneMidpoints = listOf(),
        laneType = JsonLaneType.Driving,
        laneWidth = 0.0,
        predecessorLanes = listOf(),
        contactAreas = listOf(),
        successorLanes = listOf(),
        speedLimits = listOf(),
        trafficLights = listOf(),
        landmarks = listOf())

/** Empty [JsonRotation]. */
fun emptyJsonRotation(): JsonRotation = JsonRotation(0.0, 0.0, 0.0)

/** Empty [JsonLocation]. */
fun emptyJsonLocation(): JsonLocation = JsonLocation(0.0, 0.0, 0.0)

/** Empty [JsonVector3D]. */
fun emptyJsonVector3D(): JsonVector3D = JsonVector3D(0.0, 0.0, 0.0)

/** Empty [JsonStaticTrafficLight]. */
fun emptyJsonStaticTrafficLight(): JsonStaticTrafficLight =
    JsonStaticTrafficLight(
        id = 0,
        rotation = emptyJsonRotation(),
        location = emptyJsonLocation(),
        stopLocations = listOf(),
        positionDistance = 0.0f)

/** Empty [JsonTrafficLight]. */
fun emptyJsonTrafficLight(id: Int = 0, state: Int = 0): JsonTrafficLight =
    JsonTrafficLight(
        id = id,
        typeId = "",
        attributes = mapOf(),
        isAlive = true,
        isActive = true,
        isDormant = false,
        semanticTags = listOf(),
        boundingBox = defaultJsonBoundingBox(),
        location = emptyJsonLocation(),
        rotation = emptyJsonRotation(),
        state = state,
        relatedOpenDriveId = 0,
    )

/** Empty [JsonLandmark]. */
fun emptyJsonLandmark(): JsonLandmark =
    JsonLandmark(
        id = 0,
        roadId = 0,
        location = JsonLocation(0.0, 0.0, 0.0),
        text = "",
        unit = "",
        value = 0.0,
        type = JsonLandmarkType.LightPost,
        country = "",
        distance = 0.0,
        rotation = JsonRotation(0.0, 0.0, 0.0),
        hOffset = 0.0,
        height = 0.0,
        isDynamic = false,
        name = "",
        orientation = JsonLandmarkOrientation.Both,
        pitch = 0.0,
        roll = 0.0,
        s = 0.0,
        subType = "",
        width = 0.0,
        zOffset = 0.0)

/** Default [JsonBoundingBox]. */
fun defaultJsonBoundingBox(): JsonBoundingBox =
    JsonBoundingBox(
        extent = JsonVector3D(1.0, 1.0, 1.0),
        location = JsonLocation(0.0, 0.0, 0.0),
        rotation = JsonRotation(0.0, 0.0, 0.0))
