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
    jsonSpeedLimit1 = emptyJsonLandmark()
    jsonSpeedLimit1.id = 100
    jsonSpeedLimit1.s = 10.0
    jsonSpeedLimit1.unit = "mph"
    jsonSpeedLimit1.value = 30.0
    jsonSpeedLimit1.type = JsonLandmarkType.MaximumSpeed

    jsonSpeedLimit2 = emptyJsonLandmark()
    jsonSpeedLimit2.id = 100
    jsonSpeedLimit2.s = 30.0
    jsonSpeedLimit2.unit = "mph"
    jsonSpeedLimit2.value = 50.0
    jsonSpeedLimit2.type = JsonLandmarkType.MaximumSpeed

    speedLimitLane = emptyLane(laneLength = 40.0)

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
    val jsonLandmark = emptyJsonLandmark()
    jsonLandmark.s = 10.0
    jsonLandmark.unit = "kmh"
    jsonLandmark.value = 30.0
    jsonLandmark.type = JsonLandmarkType.MaximumSpeed

    val resultSpeedLimits = getSpeedLimitsFromLandmarks(speedLimitLane, listOf(jsonLandmark))
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
              val landmark = emptyJsonLandmark()
              landmark.type = it
              return@map landmark
            }

    assertEquals(0, getSpeedLimitsFromLandmarks(speedLimitLane, invalidLandmarks).size)
  }

  /** Tests [getSpeedLimitsFromLandmarks] returns 0 if no landmark is given. */
  @Test
  fun testEmptyLandmarksList() {
    assertEquals(0, getSpeedLimitsFromLandmarks(speedLimitLane, listOf()).size)
  }

  /** Tests [getSpeedLimitsFromLandmarks] from midpoint of lane. */
  @Test
  fun testSingleSpeedLimitBoundariesStartingInMidOfLane() {
    // Single SpeedLimit sign at s=10
    val expectedStart = 10.0
    val speedLimitLandmark = emptyJsonLandmark()
    speedLimitLandmark.type = JsonLandmarkType.MaximumSpeed
    speedLimitLandmark.s = expectedStart

    val resultSpeedLimits = getSpeedLimitsFromLandmarks(speedLimitLane, listOf(speedLimitLandmark))
    assertEquals(1, resultSpeedLimits.size)
    assertEquals(expectedStart, resultSpeedLimits[0].fromDistanceFromStart)
    assertEquals(speedLimitLane.laneLength, resultSpeedLimits[0].toDistanceFromStart)
  }

  /** Tests [getSpeedLimitsFromLandmarks] from start of lane. */
  @Test
  fun testSingleSpeedLimitBoundariesStartingAtStartOfLane() {
    // Single SpeedLimit sign at s=0
    val expectedStart = 0.0
    val speedLimitLandmark = emptyJsonLandmark()
    speedLimitLandmark.type = JsonLandmarkType.MaximumSpeed
    speedLimitLandmark.s = expectedStart

    val resultSpeedLimits = getSpeedLimitsFromLandmarks(speedLimitLane, listOf(speedLimitLandmark))
    assertEquals(1, resultSpeedLimits.size)
    assertEquals(expectedStart, resultSpeedLimits[0].fromDistanceFromStart)
    assertEquals(speedLimitLane.laneLength, resultSpeedLimits[0].toDistanceFromStart)
  }

  /** Tests [getSpeedLimitsFromLandmarks] from end of lane. Should throw [IllegalStateException]. */
  @Test
  fun testSingleSpeedLimitBoundariesStartingAtEndOfLane() {
    // Single SpeedLimit sign at s=lane_length
    val expectedStart = speedLimitLane.laneLength
    val speedLimitLandmark = emptyJsonLandmark()
    speedLimitLandmark.type = JsonLandmarkType.MaximumSpeed
    speedLimitLandmark.s = expectedStart

    assertFailsWith<IllegalStateException> {
      getSpeedLimitsFromLandmarks(speedLimitLane, listOf(speedLimitLandmark))
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
    val speedLimitLandmark = emptyJsonLandmark()
    speedLimitLandmark.type = JsonLandmarkType.MaximumSpeed
    speedLimitLandmark.s = expectedStart

    assertFailsWith<IllegalStateException> {
      getSpeedLimitsFromLandmarks(speedLimitLane, listOf(speedLimitLandmark))
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
    val vehicleLane: Lane = emptyLane()
    vehicleLane.speedLimits = listOf(speedLimit1, speedLimit2, speedLimit3)

    val vehicle: Vehicle = emptyVehicle()
    vehicle.lane = vehicleLane

    vehicle.positionOnLane = 0.0
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
    val lane = emptyLane()
    lane.speedLimits = listOf(speedLimit1, speedLimit2, speedLimit3)

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
    val jsonLane = emptyJsonLane()
    jsonLane.laneLength = 50.0

    val jsonLandmark1 = emptyJsonLandmark()
    jsonLandmark1.type = JsonLandmarkType.MaximumSpeed
    jsonLandmark1.s = 10.0
    jsonLandmark1.value = 30.0

    val jsonLandmark2 = emptyJsonLandmark()
    jsonLandmark2.type = JsonLandmarkType.MaximumSpeed
    jsonLandmark2.s = 30.0
    jsonLandmark2.value = 30.0

    jsonLane.landmarks = listOf(jsonLandmark1, jsonLandmark2)
    val road = emptyRoad()

    val lane = convertJsonLaneToLane(jsonLane, road)

    assertEquals(2, lane.speedLimits.size)
    // test boundaries
    assertEquals(jsonLandmark1.s, lane.speedLimits[0].fromDistanceFromStart)
    assertEquals(jsonLandmark2.s, lane.speedLimits[1].fromDistanceFromStart)
    assertEquals(jsonLandmark2.s, lane.speedLimits[0].toDistanceFromStart)
    assertEquals(jsonLane.laneLength, lane.speedLimits[1].toDistanceFromStart)

    // test correct values
    assertEquals(jsonLandmark1.value, lane.speedLimits[0].speedLimit)
    assertEquals(jsonLandmark2.value, lane.speedLimits[1].speedLimit)
  }
}
