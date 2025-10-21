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

package tools.aqua.stars.importer.carla

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Road
import tools.aqua.stars.data.av.dataclasses.StaticTrafficLight
import tools.aqua.stars.data.av.dataclasses.TickData
import tools.aqua.stars.data.av.dataclasses.TrafficLight
import tools.aqua.stars.data.av.dataclasses.TrafficLightState
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.data.av.dataclasses.World
import tools.aqua.stars.importer.carla.dataclasses.JsonActorPosition
import tools.aqua.stars.importer.carla.dataclasses.JsonBoundingBox
import tools.aqua.stars.importer.carla.dataclasses.JsonLane
import tools.aqua.stars.importer.carla.dataclasses.JsonLaneType
import tools.aqua.stars.importer.carla.dataclasses.JsonLocation
import tools.aqua.stars.importer.carla.dataclasses.JsonRotation
import tools.aqua.stars.importer.carla.dataclasses.JsonStaticTrafficLight
import tools.aqua.stars.importer.carla.dataclasses.JsonTrafficLight
import tools.aqua.stars.importer.carla.dataclasses.JsonVector3D

/** Tests for traffic lights. */
class JSONTrafficLightTest {

  /** Tests the conversion of JSONTrafficLights to TrafficLights. */
  @Test
  fun testStaticTrafficLightConversion() {
    val staticJsonTrafficLight =
        JsonStaticTrafficLight(
            id = 1,
            location = JsonLocation(10.0, 10.0, 0.0),
            rotation = JsonRotation(10.0, 10.0, 0.0),
            stopLocations = listOf(JsonLocation(10.0, 10.0, 0.0)),
            positionDistance = 0F,
        )

    val staticTrafficLight = staticJsonTrafficLight.toStaticTrafficLight()
    assertEquals(staticJsonTrafficLight.id, staticTrafficLight.id)
    assertEquals(staticJsonTrafficLight.location.x, staticTrafficLight.location.x)
    assertEquals(staticJsonTrafficLight.location.y, staticTrafficLight.location.y)
    assertEquals(staticJsonTrafficLight.location.z, staticTrafficLight.location.z)
    assertEquals(staticJsonTrafficLight.rotation.yaw, staticTrafficLight.rotation.yaw)
    assertEquals(staticJsonTrafficLight.rotation.roll, staticTrafficLight.rotation.roll)
    assertEquals(staticJsonTrafficLight.rotation.pitch, staticTrafficLight.rotation.pitch)
    staticJsonTrafficLight.stopLocations.forEach { jsonLocation ->
      assertNotNull(
          staticTrafficLight.stopLocations.firstOrNull { location ->
            jsonLocation.x == location.x &&
                jsonLocation.y == location.y &&
                jsonLocation.z == location.z
          }
      )
    }
  }

  /** Tests the conversion of TrafficLights to JSONTrafficLights. */
  @Test
  fun testJsonTrafficLightToTrafficLightConversion() {
    val openDriveTrafficLightId = 100
    val staticTrafficLight = StaticTrafficLight(id = 100)

    val lane = Lane(trafficLights = listOf(staticTrafficLight))
    val road = Road(lanes = listOf(lane))
    val world = World(straights = listOf(road))
    val vehicles = setOf(Vehicle(isEgo = true, lane = lane))

    val trafficLight =
        TrafficLight(
            id = 0,
            relatedOpenDriveId = openDriveTrafficLightId,
            state = TrafficLightState.Red,
        )
    val tickData1 =
        TickData(
            entities = vehicles,
            trafficLights = listOf(trafficLight),
            world = world,
            identifier = "TickData",
        )

    val trafficLight2 =
        TrafficLight(
            id = 1,
            relatedOpenDriveId = openDriveTrafficLightId,
            state = TrafficLightState.Green,
        )
    val tickData2 =
        TickData(
            entities = vehicles,
            trafficLights = listOf(trafficLight2),
            world = world,
            identifier = "TickData",
        )

    assertNotNull(tickData1.world.straights[0].lanes[0].trafficLights[0].getStateInTick(tickData1))
    assertEquals(
        trafficLight.state,
        tickData1.world.straights[0].lanes[0].trafficLights[0].getStateInTick(tickData1),
    )

    assertNotNull(tickData2.world.straights[0].lanes[0].trafficLights[0].getStateInTick(tickData2))
    assertEquals(
        trafficLight2.state,
        tickData2.world.straights[0].lanes[0].trafficLights[0].getStateInTick(tickData2),
    )
  }

  /** Tests the conversion of Lanes to StaticTrafficLights. */
  @Test
  fun testStaticTrafficLightConversionFromLane() {
    val staticJsonTrafficLight =
        JsonStaticTrafficLight(
            id = 1,
            positionDistance = 0F,
            location = JsonLocation(10.0, 10.0, 0.0),
            rotation = JsonRotation(10.0, 10.0, 0.0),
            stopLocations = listOf(JsonLocation(10.0, 10.0, 0.0)),
        )

    val jsonLane =
        JsonLane(
            roadId = 1,
            laneId = 1,
            laneType = JsonLaneType.Driving,
            laneWidth = 3.5,
            laneLength = 50.0,
            s = 0.0,
            predecessorLanes = emptyList(),
            successorLanes = emptyList(),
            intersectingLanes = emptyList(),
            laneMidpoints = emptyList(),
            speedLimits = emptyList(),
            landmarks = emptyList(),
            contactAreas = emptyList(),
            trafficLights = listOf(staticJsonTrafficLight),
        )

    val lane = jsonLane.toLane(isJunction = false)

    val staticTrafficLight = lane.trafficLights.firstOrNull { it.id == staticJsonTrafficLight.id }
    assertNotNull(staticTrafficLight)
    assertEquals(staticJsonTrafficLight.id, staticTrafficLight.id)
    assertEquals(staticJsonTrafficLight.location.x, staticTrafficLight.location.x)
    assertEquals(staticJsonTrafficLight.location.y, staticTrafficLight.location.y)
    assertEquals(staticJsonTrafficLight.location.z, staticTrafficLight.location.z)
    assertEquals(staticJsonTrafficLight.rotation.yaw, staticTrafficLight.rotation.yaw)
    assertEquals(staticJsonTrafficLight.rotation.roll, staticTrafficLight.rotation.roll)
    assertEquals(staticJsonTrafficLight.rotation.pitch, staticTrafficLight.rotation.pitch)

    staticJsonTrafficLight.stopLocations.forEach { jsonLocation ->
      assertNotNull(
          staticTrafficLight.stopLocations.firstOrNull { location ->
            jsonLocation.x == location.x &&
                jsonLocation.y == location.y &&
                jsonLocation.z == location.z
          }
      )
    }
  }

  /** Tests ignoring of JSONTrafficLights as actors. */
  @Test
  fun testIgnoringJsonTrafficLightAsActorPosition() {
    val jsonTrafficLight =
        JsonTrafficLight(
            id = 100,
            typeId = "traffic_light",
            state = TrafficLightState.Green.value,
            location = JsonLocation(0.0, 0.0, 0.0),
            rotation = JsonRotation(0.0, 0.0, 0.0),
            relatedOpenDriveId = 100,
            attributes = emptyMap(),
            isAlive = true,
            isActive = true,
            isDormant = false,
            semanticTags = emptyList(),
            boundingBox =
                JsonBoundingBox(
                    extent = JsonVector3D(0.0, 0.0, 0.0),
                    location = JsonLocation(0.0, 0.0, 0.0),
                    rotation = JsonRotation(0.0, 0.0, 0.0),
                    vertices = emptyList(),
                ),
            collisions = emptyList(),
        )

    val jsonActorPosition =
        JsonActorPosition(actor = jsonTrafficLight, laneId = 1, roadId = 1, positionOnLane = 0.0)

    val lane = Lane(jsonActorPosition.laneId)
    val road = Road(jsonActorPosition.roadId, lanes = listOf(lane))
    val world = World(straights = listOf(road))

    assertNull(jsonActorPosition.toActorOrNull(world = world))
  }

  /** Tests TrafficLight conversion from StaticTrafficLight. */
  @Test
  fun testTrafficLightConversion() {
    val jsonTrafficLight =
        JsonTrafficLight(
            id = 1,
            typeId = "traffic_light",
            state = 0, // Red
            location = JsonLocation(0.0, 0.0, 0.0),
            rotation = JsonRotation(0.0, 0.0, 0.0),
            relatedOpenDriveId = 100,
            attributes = emptyMap(),
            isAlive = true,
            isActive = true,
            isDormant = false,
            semanticTags = emptyList(),
            boundingBox =
                JsonBoundingBox(
                    extent = JsonVector3D(0.0, 0.0, 0.0),
                    location = JsonLocation(0.0, 0.0, 0.0),
                    rotation = JsonRotation(0.0, 0.0, 0.0),
                    vertices = emptyList(),
                ),
            collisions = emptyList(),
        )

    var trafficLight = jsonTrafficLight.toTrafficLight()
    assertEquals(jsonTrafficLight.id, trafficLight.id)
    assertEquals(TrafficLightState.Red, trafficLight.state)

    jsonTrafficLight.state = 1 // Yellow
    trafficLight = jsonTrafficLight.toTrafficLight()
    assertEquals(jsonTrafficLight.id, trafficLight.id)
    assertEquals(TrafficLightState.Yellow, trafficLight.state)

    jsonTrafficLight.state = 2 // Green
    trafficLight = jsonTrafficLight.toTrafficLight()
    assertEquals(jsonTrafficLight.id, trafficLight.id)
    assertEquals(TrafficLightState.Green, trafficLight.state)

    jsonTrafficLight.state = 3 // Off
    trafficLight = jsonTrafficLight.toTrafficLight()
    assertEquals(jsonTrafficLight.id, trafficLight.id)
    assertEquals(TrafficLightState.Off, trafficLight.state)

    jsonTrafficLight.state = 4 // Unknown
    trafficLight = jsonTrafficLight.toTrafficLight()
    assertEquals(jsonTrafficLight.id, trafficLight.id)
    assertEquals(TrafficLightState.Unknown, trafficLight.state)
  }
}
