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

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.data.av.*
import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.SpeedLimit
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.importer.carla.dataclasses.JsonLandmark
import tools.aqua.stars.importer.carla.dataclasses.JsonLandmarkType

/** Tests various configurations of speed limits on a lane. */
class JSONSpeedLimitTest {

  private lateinit var jsonSpeedLimit1: JsonLandmark
  private lateinit var jsonSpeedLimit2: JsonLandmark
  private lateinit var jsonLandmarks: List<JsonLandmark>
  private lateinit var speedLimitLane: Lane
  private lateinit var speedLimits: List<SpeedLimit>

  /** Creates the speed limits, landmarks and the lane. */
  @BeforeTest
  fun setup() {
    jsonSpeedLimit1 =
        JsonLandmark(
            id = 30,
            roadId = 1,
            name = "Speed Limit 30",
            distance = 0.0,
            s = 10.0,
            isDynamic = false,
            orientation = JsonLandmarkOrientation.Positive,
            zOffset = 0.0,
            country = "US",
            type = JsonLandmarkType.MaximumSpeed,
            subType = "",
            value = 30.0,
            unit = "mph",
            height = 2.0,
            width = 0.2,
            text = "30",
            hOffset = 0.0,
            pitch = 0.0,
            roll = 0.0,
            location = JsonLocation(0.0, 0.0, 0.0),
            rotation = JsonRotation(0.0, 0.0, 0.0))

    jsonSpeedLimit2 =
        JsonLandmark(
            id = 50,
            roadId = 1,
            name = "Speed Limit 50",
            distance = 0.0,
            s = 30.0,
            isDynamic = false,
            orientation = JsonLandmarkOrientation.Positive,
            zOffset = 0.0,
            country = "US",
            type = JsonLandmarkType.MaximumSpeed,
            subType = "",
            value = 50.0,
            unit = "mph",
            height = 2.0,
            width = 0.2,
            text = "30",
            hOffset = 0.0,
            pitch = 0.0,
            roll = 0.0,
            location = JsonLocation(0.0, 0.0, 0.0),
            rotation = JsonRotation(0.0, 0.0, 0.0))

    speedLimitLane = Lane(laneLength = 40.0)
    jsonLandmarks = listOf(jsonSpeedLimit1, jsonSpeedLimit2)
    speedLimits = getSpeedLimitsFromLandmarks(speedLimitLane, jsonLandmarks)

    assertEquals(2, speedLimits.size)
  }

  /**
   * Asserts that the landmark type is [JsonLandmarkType.MaximumSpeed], unit is "mph" and the values
   * are identical.
   */
  @Test
  fun testMPHUnitConversion() {
    assertEquals(JsonLandmarkType.MaximumSpeed, jsonSpeedLimit1.type)
    assertEquals(JsonLandmarkType.MaximumSpeed, jsonSpeedLimit2.type)

    assertEquals("mph", jsonSpeedLimit1.unit)
    assertEquals("mph", jsonSpeedLimit2.unit)

    assertEquals(speedLimits[0].speedLimit, jsonLandmarks[0].value)
    assertEquals(speedLimits[1].speedLimit, jsonLandmarks[1].value)
  }

  /** Tests [getSpeedLimitsFromLandmarks] with unis "kmh". */
  @Test
  fun testOtherUnitConversions() {
    jsonSpeedLimit1.unit = "kmh"

    val resultSpeedLimits = getSpeedLimitsFromLandmarks(speedLimitLane, listOf(jsonSpeedLimit1))
    assertEquals(1, resultSpeedLimits.size)
    assertEquals(30.0, resultSpeedLimits[0].speedLimit)
  }

  /** Tests speed limits if multiple exists on the lane. */
  @Test
  fun testBoundariesForMultipleSpeedLimits() {
    assertEquals(10.0, speedLimits[0].fromDistanceFromStart)
    assertEquals(30.0, speedLimits[0].toDistanceFromStart)

    assertEquals(30.0, speedLimits[1].fromDistanceFromStart)
    assertEquals(40.0, speedLimits[1].toDistanceFromStart)
  }

  /**
   * Tests that [JsonLandmark]s other than [JsonLandmarkType.MaximumSpeed] have a speed limit of 0.
   */
  @Test
  fun testNonSpeedLimitLandmarks() {
    val invalidLandmarks: List<JsonLandmark> =
        JsonLandmarkType.entries
            .filter { it != JsonLandmarkType.MaximumSpeed }
            .map {
              val landmark = jsonSpeedLimit1.copy()
              landmark.type = it
              return@map landmark
            }

    assertEquals(0, getSpeedLimitsFromLandmarks(speedLimitLane, invalidLandmarks).size)
  }

  /** Tests [getSpeedLimitsFromLandmarks] returns 0 if no landmark is given. */
  @Test
  fun testEmptyLandmarksList() {
    assertEquals(0, getSpeedLimitsFromLandmarks(speedLimitLane, emptyList()).size)
  }

  /** Tests [getSpeedLimitsFromLandmarks] from midpoint of lane. */
  @Test
  fun testSingleSpeedLimitBoundariesStartingInMidOfLane() {
    // Single SpeedLimit sign at s=10
    val expectedStart = 10.0
    jsonSpeedLimit1.s = expectedStart

    val resultSpeedLimits = getSpeedLimitsFromLandmarks(speedLimitLane, listOf(jsonSpeedLimit1))
    assertEquals(1, resultSpeedLimits.size)
    assertEquals(expectedStart, resultSpeedLimits[0].fromDistanceFromStart)
    assertEquals(speedLimitLane.laneLength, resultSpeedLimits[0].toDistanceFromStart)
  }

  /** Tests [getSpeedLimitsFromLandmarks] from start of lane. */
  @Test
  fun testSingleSpeedLimitBoundariesStartingAtStartOfLane() {
    // Single SpeedLimit sign at s=0
    val expectedStart = 0.0
    jsonSpeedLimit1.s = expectedStart

    val resultSpeedLimits = getSpeedLimitsFromLandmarks(speedLimitLane, listOf(jsonSpeedLimit1))
    assertEquals(1, resultSpeedLimits.size)
    assertEquals(expectedStart, resultSpeedLimits[0].fromDistanceFromStart)
    assertEquals(speedLimitLane.laneLength, resultSpeedLimits[0].toDistanceFromStart)
  }

  /** Tests [getSpeedLimitsFromLandmarks] from end of lane. Should throw [IllegalStateException]. */
  @Test
  fun testSingleSpeedLimitBoundariesStartingAtEndOfLane() {
    // Single SpeedLimit sign at s=lane_length
    val expectedStart = speedLimitLane.laneLength
    jsonSpeedLimit1.s = expectedStart

    assertFailsWith<IllegalStateException> {
      getSpeedLimitsFromLandmarks(speedLimitLane, listOf(jsonSpeedLimit1))
    }
  }

  /**
   * Tests [getSpeedLimitsFromLandmarks] from behind end of lane. Should throw
   * [IllegalStateException].
   */
  @Test
  fun testSingleSpeedLimitBoundariesStartAfterEndOfLane() {
    // Single SpeedLimit sign at s=lane_length+1
    val expectedStart = speedLimitLane.laneLength + 1
    jsonSpeedLimit1.s = expectedStart

    assertFailsWith<IllegalStateException> {
      getSpeedLimitsFromLandmarks(speedLimitLane, listOf(jsonSpeedLimit1))
    }
  }

  /** Tests [Vehicle.applicableSpeedLimit] for multiple speed limits on lane. */
  @Test
  fun testApplicableSpeedLimitsForVehicle() {
    val speedLimit1 =
        SpeedLimit(speedLimit = 30.0, fromDistanceFromStart = 10.0, toDistanceFromStart = 30.0)
    val speedLimit2 =
        SpeedLimit(speedLimit = 50.0, fromDistanceFromStart = 30.0, toDistanceFromStart = 50.0)
    val speedLimit3 =
        SpeedLimit(speedLimit = 80.0, fromDistanceFromStart = 50.0, toDistanceFromStart = 80.0)
    val vehicleLane = Lane(speedLimits = listOf(speedLimit1, speedLimit2, speedLimit3))

    val vehicle = Vehicle(lane = vehicleLane, positionOnLane = 0.0)
    assertEquals(null, vehicle.applicableSpeedLimit)

    vehicle.positionOnLane = 10.0
    assertEquals(speedLimit1, vehicle.applicableSpeedLimit)

    vehicle.positionOnLane = 11.0
    assertEquals(speedLimit1, vehicle.applicableSpeedLimit)

    vehicle.positionOnLane = 30.0
    assertEquals(speedLimit1, vehicle.applicableSpeedLimit)

    vehicle.positionOnLane = 31.0
    assertEquals(speedLimit2, vehicle.applicableSpeedLimit)

    vehicle.positionOnLane = 50.0
    assertEquals(speedLimit2, vehicle.applicableSpeedLimit)

    vehicle.positionOnLane = 51.0
    assertEquals(speedLimit3, vehicle.applicableSpeedLimit)

    vehicle.positionOnLane = 80.0
    assertEquals(speedLimit3, vehicle.applicableSpeedLimit)

    vehicle.positionOnLane = 81.0
    assertEquals(null, vehicle.applicableSpeedLimit)
  }

  /** Tests [Lane.speedAt] for multiple speed limits on lane. */
  @Test
  fun testSpeedAt() {
    val speedLimit1 =
        SpeedLimit(speedLimit = 30.0, fromDistanceFromStart = 10.0, toDistanceFromStart = 30.0)
    val speedLimit2 =
        SpeedLimit(speedLimit = 50.0, fromDistanceFromStart = 30.0, toDistanceFromStart = 50.0)
    val speedLimit3 =
        SpeedLimit(speedLimit = 80.0, fromDistanceFromStart = 50.0, toDistanceFromStart = 80.0)
    val lane = Lane(speedLimits = listOf(speedLimit1, speedLimit2, speedLimit3))

    assertEquals(30.0, lane.speedAt(0.0))
    assertEquals(speedLimit1.speedLimit, lane.speedAt(10.0))
    assertEquals(speedLimit1.speedLimit, lane.speedAt(11.0))
    assertEquals(speedLimit2.speedLimit, lane.speedAt(30.0))
    assertEquals(speedLimit2.speedLimit, lane.speedAt(31.0))
    assertEquals(speedLimit3.speedLimit, lane.speedAt(50.0))
    assertEquals(speedLimit3.speedLimit, lane.speedAt(51.0))
    assertEquals(30.0, lane.speedAt(80.0))
  }

  /** Tests speed limit conversion from json. */
  @Test
  fun testSpeedLimitCreationFromJsonLaneConversion() {
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
            speedLimits = listOf(JsonSpeedLimit(30.0, 0.0, 10.0), JsonSpeedLimit(50.0, 10.0, 50.0)),
            landmarks = listOf(jsonSpeedLimit1, jsonSpeedLimit2),
            contactAreas = emptyList(),
            trafficLights = emptyList())

    val lane = jsonLane.toLane(isJunction = false)

    assertEquals(2, lane.speedLimits.size)
    // test boundaries
    assertEquals(jsonSpeedLimit1.s, lane.speedLimits[0].fromDistanceFromStart)
    assertEquals(jsonSpeedLimit2.s, lane.speedLimits[1].fromDistanceFromStart)
    assertEquals(jsonSpeedLimit2.s, lane.speedLimits[0].toDistanceFromStart)
    assertEquals(jsonLane.laneLength, lane.speedLimits[1].toDistanceFromStart)

    // test correct values
    assertEquals(jsonSpeedLimit1.value, lane.speedLimits[0].speedLimit)
    assertEquals(jsonSpeedLimit2.value, lane.speedLimits[1].speedLimit)
  }
}
