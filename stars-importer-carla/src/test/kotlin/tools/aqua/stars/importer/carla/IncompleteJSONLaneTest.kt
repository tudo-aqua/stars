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

import kotlin.test.*
import tools.aqua.stars.data.av.dataclasses.Block
import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Road
import tools.aqua.stars.importer.carla.dataclasses.*

/** Tests behaviour on incomplete data from json. */
class IncompleteJSONLaneTest {

  private lateinit var incompleteBlock: Block
  private lateinit var incompleteRoad: Road
  private lateinit var incompleteJsonLane: JsonLane
  private lateinit var incompleteLane: Lane
  private lateinit var jsonLanes: List<JsonLane>
  private lateinit var lanes: List<Lane>

  /** Creates block, road and lane. */
  @BeforeTest
  fun setupData() {
    incompleteBlock = Block(id = "1", fileName = "", roads = listOf())
    incompleteRoad = Road(lanes = listOf(), id = 1, block = incompleteBlock, isJunction = false)
    incompleteJsonLane =
        JsonLane(
            laneId = 1,
            roadId = 1,
            s = 0.0,
            intersectingLanes = listOf(JsonContactLaneInfo(laneId = 1, roadId = 2)),
            laneLength = 10.0,
            laneMidpoints = listOf(),
            laneType = JsonLaneType.Driving,
            laneWidth = 2.0,
            predecessorLanes = listOf(JsonContactLaneInfo(laneId = 1, roadId = 0)),
            contactAreas =
                listOf(
                    JsonContactArea(
                        lane1Id = 1,
                        lane1RoadId = 1,
                        lane2Id = 1,
                        lane2RoadId = 2,
                        lane1StartPos = 5.0,
                        lane1EndPos = 10.0,
                        lane2StartPos = 5.0,
                        lane2EndPos = 5.0,
                        contactLocation = JsonLocation(0.0, 0.0, 0.0),
                        id = "")),
            successorLanes = listOf(JsonContactLaneInfo(laneId = 1, roadId = 3)),
            speedLimits = listOf(),
            trafficLights = listOf(),
            landmarks =
                listOf(
                    JsonLandmark(
                        id = 100,
                        roadId = 1,
                        location = JsonLocation(0.0, 0.0, 0.0),
                        text = "",
                        unit = "mph",
                        value = 30.0,
                        type = JsonLandmarkType.MaximumSpeed,
                        country = "Test",
                        distance = 10.0,
                        rotation = JsonRotation(0.0, 0.0, 0.0),
                        hOffset = 0.0,
                        height = 2.3,
                        isDynamic = false,
                        name = "SpeedLimit",
                        orientation = JsonLandmarkOrientation.Both,
                        pitch = 0.0,
                        roll = 0.0,
                        s = 0.0,
                        subType = "",
                        width = 1.0,
                        zOffset = 0.0)))
    incompleteLane = convertJsonLaneToLane(incompleteJsonLane, incompleteRoad)
    // The jsonLanes are not sufficiently filled out, as the connecting lanes are missing
    jsonLanes = listOf(incompleteJsonLane)
    lanes = listOf(incompleteLane)
  }

  /** Tests [updateLanes] for incomplete [JsonLane] and [Lane]. */
  @Test
  fun checkUpdatingOfContactLaneInfos() {
    assertFailsWith<NoSuchElementException> { updateLanes(jsonLanes, lanes) }
    assertEquals(incompleteLane.intersectingLanes.size, 0)
    assertEquals(incompleteLane.predecessorLanes.size, 0)
    assertEquals(incompleteLane.successorLanes.size, 0)
    assertEquals(incompleteLane.contactAreas.size, 0)
    assertEquals(incompleteLane.yieldLanes.size, 0)
  }

  /**
   * Tests that primitive attributes are equal on [JsonLane] [incompleteJsonLane] and [Lane]
   * [incompleteLane].
   */
  @Test
  fun checkSettingOfPrimitiveAttributes() {
    assertEquals(incompleteJsonLane.laneId, incompleteLane.laneId)
    assertEquals(incompleteJsonLane.roadId, incompleteLane.road.id)
    assertEquals(incompleteJsonLane.laneType.value, incompleteLane.laneType.value)
    assertEquals(incompleteJsonLane.laneLength, incompleteLane.laneLength)
    assertEquals(incompleteJsonLane.laneWidth, incompleteLane.laneWidth)
  }

  /** Checks that missing intersection points are correctly recognized. */
  @Test
  fun checkSettingOfIntersectingLanes() {
    incompleteJsonLane.intersectingLanes.forEach { jsonContactInfo ->
      assertNull(
          incompleteLane.intersectingLanes.firstOrNull { contactInfo ->
            jsonContactInfo.laneId == contactInfo.lane.laneId &&
                jsonContactInfo.roadId == contactInfo.lane.road.id
          })
    }
  }

  /** Checks that missing contact points are correctly recognized. */
  @Test
  fun checkMissingLanes() {
    incompleteJsonLane.contactAreas.forEach { jsonContactArea ->
      assertNull(
          incompleteLane.contactAreas.firstOrNull { contactArea ->
            jsonContactArea.id == contactArea.id &&
                jsonContactArea.lane1Id == contactArea.lane1.laneId &&
                jsonContactArea.lane2Id == contactArea.lane2.laneId &&
                jsonContactArea.lane2RoadId == contactArea.lane2.road.id &&
                jsonContactArea.lane2RoadId == contactArea.lane2.road.id &&
                jsonContactArea.lane1StartPos == contactArea.lane1StartPos &&
                jsonContactArea.lane1EndPos == contactArea.lane1EndPos &&
                jsonContactArea.lane2StartPos == contactArea.lane2StartPos &&
                jsonContactArea.lane2EndPos == contactArea.lane2EndPos &&
                jsonContactArea.contactLocation.x == contactArea.contactLocation.x &&
                jsonContactArea.contactLocation.y == contactArea.contactLocation.y &&
                jsonContactArea.contactLocation.z == contactArea.contactLocation.z
          })
    }
  }
}
