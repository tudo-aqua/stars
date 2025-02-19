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
import tools.aqua.stars.data.av.*
import tools.aqua.stars.data.av.dataclasses.TrafficLightState
import tools.aqua.stars.importer.carla.dataclasses.JsonActorPosition
import tools.aqua.stars.importer.carla.dataclasses.JsonLane
import tools.aqua.stars.importer.carla.dataclasses.JsonLocation
import tools.aqua.stars.importer.carla.dataclasses.JsonRotation
import tools.aqua.stars.importer.carla.dataclasses.JsonStaticTrafficLight
import tools.aqua.stars.importer.carla.dataclasses.JsonTrafficLight

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
            stopLocations = listOf(JsonLocation(10.0, 10.0, 0.0)))

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
          })
    }
  }

  /** Tests the conversion of TrafficLights to JSONTrafficLights. */
  @Test
  fun testJsonTrafficLightToTrafficLightConversion() {
    val openDriveTrafficLightId = 100
    val staticTrafficLight = emptyStaticTrafficLight(id = 100)

    val lane = emptyLane(staticTrafficLights = listOf(staticTrafficLight))
    val road = emptyRoad(lanes = listOf(lane))
    val block = emptyBlock(roads = listOf(road))
    val blocks = listOf(block)
    val vehicles = listOf(emptyVehicle(isEgo = true))

    val trafficLight =
        emptyTrafficLight(
            id = 0, relatedOpenDriveId = openDriveTrafficLightId, state = TrafficLightState.Red)
    val tickData1 =
        emptyTickData(blocks = blocks, trafficLights = listOf(trafficLight), actors = vehicles)

    val trafficLight2 =
        emptyTrafficLight(
            id = 1, relatedOpenDriveId = openDriveTrafficLightId, state = TrafficLightState.Green)
    val tickData2 =
        emptyTickData(blocks = blocks, trafficLights = listOf(trafficLight2), actors = vehicles)

    assertNotNull(tickData1.blocks[0].roads[0].lanes[0].trafficLights[0].getStateInTick(tickData1))
    assertEquals(
        trafficLight.state,
        tickData1.blocks[0].roads[0].lanes[0].trafficLights[0].getStateInTick(tickData1))

    assertNotNull(tickData2.blocks[0].roads[0].lanes[0].trafficLights[0].getStateInTick(tickData2))
    assertEquals(
        trafficLight2.state,
        tickData2.blocks[0].roads[0].lanes[0].trafficLights[0].getStateInTick(tickData2))
  }

  /** Tests the conversion of Lanes to StaticTrafficLights. */
  @Test
  fun testStaticTrafficLightConversionFromLane() {
    val staticJsonTrafficLight =
        JsonStaticTrafficLight(
            id = 1,
            location = JsonLocation(10.0, 10.0, 0.0),
            rotation = JsonRotation(10.0, 10.0, 0.0),
            stopLocations = listOf(JsonLocation(10.0, 10.0, 0.0)))
    val lane =
        convertJsonLaneToLane(
            jsonLane = JsonLane(trafficLights = listOf(staticJsonTrafficLight)), isJunction = false)

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
          })
    }
  }

  /** Tests ignoring of JSONTrafficLights as actors. */
  @Test
  fun testIgnoringJsonTrafficLightAsActorPosition() {
    val jsonTrafficLight = JsonTrafficLight(id = 100, state = 2)
    val jsonActorPosition = JsonActorPosition(actor = jsonTrafficLight, laneId = 1, roadId = 1)

    val lane = emptyLane(jsonActorPosition.laneId)
    val road = emptyRoad(jsonActorPosition.roadId, lanes = listOf(lane))
    val block = emptyBlock(roads = listOf(road))

    assertNull(
        convertJsonActorPositionToEntity(position = jsonActorPosition, blocks = listOf(block)))
  }

  /** Tests TrafficLight conversion from StaticTrafficLight. */
  @Test
  fun testTrafficLightConversion() {
    val jsonTrafficLight = JsonTrafficLight()
    jsonTrafficLight.id = 1
    jsonTrafficLight.state = 0 // Red

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
