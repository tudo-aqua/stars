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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import tools.aqua.stars.data.av.dataclasses.ContactSide
import tools.aqua.stars.data.av.dataclasses.LaneMarkingColor
import tools.aqua.stars.data.av.dataclasses.LaneMarkingType
import tools.aqua.stars.data.av.dataclasses.LaneTopology
import tools.aqua.stars.data.av.dataclasses.Road
import tools.aqua.stars.data.av.dataclasses.World
import tools.aqua.stars.importer.carla.dataclasses.JsonContactLaneInfo
import tools.aqua.stars.importer.carla.dataclasses.JsonLane
import tools.aqua.stars.importer.carla.dataclasses.JsonLaneMarking
import tools.aqua.stars.importer.carla.dataclasses.JsonLaneMarkingColor
import tools.aqua.stars.importer.carla.dataclasses.JsonLaneMarkingContact
import tools.aqua.stars.importer.carla.dataclasses.JsonLaneMarkingType
import tools.aqua.stars.importer.carla.dataclasses.JsonLaneType
import tools.aqua.stars.importer.carla.dataclasses.JsonLocation
import tools.aqua.stars.importer.carla.dataclasses.JsonPedestrian
import tools.aqua.stars.importer.carla.dataclasses.JsonRotation
import tools.aqua.stars.importer.carla.dataclasses.JsonVector3D

/** Tests conversion of the additional CARLA export fields to the AV data classes. */
class JSONAdditionalFieldsTest {

  private fun jsonLane(
      roadId: Int,
      laneId: Int,
      leftLane: JsonContactLaneInfo? = null,
      rightLane: JsonContactLaneInfo? = null,
      overlappingLanes: List<JsonContactLaneInfo> = emptyList(),
      leftMarking: JsonLaneMarking? = null,
      rightMarking: JsonLaneMarking? = null,
      topology: String = "",
  ): JsonLane =
      JsonLane(
          roadId = roadId,
          laneId = laneId,
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
          trafficLights = emptyList(),
          leftLaneMarking = leftMarking,
          rightLaneMarking = rightMarking,
          leftLane = leftLane,
          rightLane = rightLane,
          overlappingLanes = overlappingLanes,
          laneTopology = topology,
      )

  /** Lane markings and topology on a [JsonLane] are carried into the converted `Lane`. */
  @Test
  fun testLaneMarkingsAndTopologyConversion() {
    val jsonLane =
        jsonLane(
            roadId = 1,
            laneId = 1,
            leftMarking =
                JsonLaneMarking(JsonLaneMarkingType.Solid, JsonLaneMarkingColor.Yellow, 0.2),
            rightMarking =
                JsonLaneMarking(JsonLaneMarkingType.Broken, JsonLaneMarkingColor.Standard, 0.1),
            topology = "Merging & Diverging",
        )

    val lane = jsonLane.toLane(isJunction = false)

    assertEquals(LaneMarkingType.Solid, lane.leftLaneMarking?.markingType)
    assertEquals(LaneMarkingColor.Yellow, lane.leftLaneMarking?.color)
    assertEquals(0.2, lane.leftLaneMarking?.width)
    assertEquals(LaneMarkingType.Broken, lane.rightLaneMarking?.markingType)
    assertEquals(LaneMarkingColor.Standard, lane.rightLaneMarking?.color)
    assertEquals(LaneTopology.MergingAndDiverging, lane.laneTopology)
  }

  /** An empty topology string maps to [LaneTopology.None]. */
  @Test
  fun testEmptyTopologyMapsToNone() {
    assertEquals(LaneTopology.None, jsonLane(1, 1).toLane(isJunction = false).laneTopology)
  }

  /** [updateLanes] resolves left/right/overlapping lane references to actual `Lane`s. */
  @Test
  fun testAdjacentAndOverlappingLaneResolution() {
    val jsonLaneA =
        jsonLane(
            roadId = 1,
            laneId = 1,
            leftLane = JsonContactLaneInfo(roadId = 1, laneId = 2),
            rightLane = JsonContactLaneInfo(roadId = 99, laneId = 5), // not in the map
            overlappingLanes = listOf(JsonContactLaneInfo(roadId = 2, laneId = 1)),
        )
    val jsonLaneB = jsonLane(roadId = 1, laneId = 2)
    val jsonLaneC = jsonLane(roadId = 2, laneId = 1)

    val laneA = jsonLaneA.toLane(isJunction = false)
    val laneB = jsonLaneB.toLane(isJunction = false)
    val laneC = jsonLaneC.toLane(isJunction = false)
    val roads =
        listOf(Road(id = 1, lanes = listOf(laneA, laneB)), Road(id = 2, lanes = listOf(laneC)))
    World(straights = roads)

    updateLanes(listOf(jsonLaneA, jsonLaneB, jsonLaneC), listOf(laneA, laneB, laneC))

    assertNotNull(laneA.leftLane)
    assertEquals(2, laneA.leftLane?.lane?.laneId)
    assertNull(laneA.rightLane) // reference to a missing lane is skipped
    assertEquals(1, laneA.overlappingLanes.size)
    assertEquals(2, laneA.overlappingLanes.first().lane.road.id)
  }

  /** Vehicle blinker and steering fields are carried through the converter. */
  @Test
  fun testVehicleBlinkerAndSteeringConversion() {
    val jsonVehicle =
        getSimpleVehicle(id = 1, isEgo = true)
            .copy(leftBlinker = true, rightBlinker = false, steeringAngle = -0.4)

    val vehicle = jsonVehicle.toVehicle(positionOnLane = 0.0, lane = simpleLane)

    assertTrue(vehicle.leftBlinker)
    assertTrue(!vehicle.rightBlinker)
    assertEquals(-0.4, vehicle.steeringAngle)
  }

  /** Lane marking contacts on an actor are carried through the converter. */
  @Test
  fun testLaneMarkingContactConversion() {
    val contact =
        JsonLaneMarkingContact(
            side = "Left",
            roadId = 7,
            laneId = 2,
            marking = JsonLaneMarking(JsonLaneMarkingType.Curb, JsonLaneMarkingColor.Other, 0.15),
            isCrossing = true,
            penetration = 0.27,
        )
    val jsonVehicle =
        getSimpleVehicle(id = 1, isEgo = true).copy(laneMarkingContacts = listOf(contact))

    val vehicle = jsonVehicle.toVehicle(positionOnLane = 0.0, lane = simpleLane)

    assertEquals(1, vehicle.laneMarkingContacts.size)
    val converted = vehicle.laneMarkingContacts.first()
    assertEquals(ContactSide.LEFT, converted.side)
    assertEquals(7, converted.roadId)
    assertEquals(LaneMarkingType.Curb, converted.marking?.markingType)
    assertTrue(converted.isCrossing)
    assertEquals(0.27, converted.penetration)
  }

  /** Pedestrian kinematics fields are carried through the converter. */
  @Test
  fun testPedestrianKinematicsConversion() {
    val jsonPedestrian =
        JsonPedestrian(
            id = 1,
            typeId = "walker.pedestrian.0001",
            attributes = emptyMap(),
            isAlive = true,
            isActive = true,
            isDormant = false,
            semanticTags = emptyList(),
            boundingBox = simpleBoundingBox,
            location = JsonLocation(0.0, 0.0, 0.0),
            rotation = JsonRotation(0.0, 0.0, 0.0),
            collisions = emptyList(),
            velocity = JsonVector3D(1.0, 2.0, 3.0),
            acceleration = JsonVector3D(4.0, 5.0, 6.0),
            angularVelocity = JsonVector3D(7.0, 8.0, 9.0),
        )

    val pedestrian = jsonPedestrian.toPedestrian(positionOnLane = 0.0, lane = simpleLane)

    assertEquals(1.0, pedestrian.velocity.x)
    assertEquals(5.0, pedestrian.acceleration.y)
    assertEquals(9.0, pedestrian.angularVelocity.z)
  }
}
