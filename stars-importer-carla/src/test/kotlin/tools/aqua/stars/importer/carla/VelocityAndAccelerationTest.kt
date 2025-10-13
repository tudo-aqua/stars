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

import java.lang.IllegalStateException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Location
import tools.aqua.stars.data.av.dataclasses.Pedestrian
import tools.aqua.stars.data.av.dataclasses.Road
import tools.aqua.stars.data.av.dataclasses.TickData
import tools.aqua.stars.data.av.dataclasses.TickDataUnitSeconds
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.data.av.dataclasses.World

/** Tests the velocity and acceleration calculations. */
class VelocityAndAccelerationTest {

  /** Lane for testing. */
  lateinit var lane: Lane

  /** Test setup. */
  @BeforeTest
  fun setup() {
    lane = Lane().also { World(straights = listOf(Road(lanes = listOf(it)))) }
  }

  /** Tests the velocity and acceleration calculations on pre-defined values. */
  @Test
  fun testVelocityCalculation() {
    val vehicle1 = Vehicle(location = Location(0.0, 0.0, 0.0), isEgo = true, lane = lane)
    val vehicle2 = Vehicle(location = Location(3.0, 4.0, 5.0), isEgo = true, lane = lane)
    val vehicle3 = Vehicle(location = Location(8.0, 2.0, 10.0), isEgo = true, lane = lane)

    // Create TickData for all vehicles.
    val tickData1 = TickData(currentTickUnit = TickDataUnitSeconds(0.0), entities = setOf(vehicle1))
    val tickData2 = TickData(currentTickUnit = TickDataUnitSeconds(1.0), entities = setOf(vehicle2))
    val tickData3 = TickData(currentTickUnit = TickDataUnitSeconds(2.0), entities = setOf(vehicle3))

    // Vehicle 0 has no previous state. The velocity and acceleration values should be set to 0.0
    updateActorVelocityAndAcceleration(vehicle = vehicle1, previousActor = null, timeDelta = 0.0)
    assertEquals(0.0, vehicle1.velocity.x)
    assertEquals(0.0, vehicle1.velocity.y)
    assertEquals(0.0, vehicle1.velocity.z)
    assertEquals(0.0, vehicle1.acceleration.x)
    assertEquals(0.0, vehicle1.acceleration.y)
    assertEquals(0.0, vehicle1.acceleration.z)

    // Vehicle 1 has Vehicle 1 as previous state.
    updateActorVelocityAndAcceleration(
        vehicle = vehicle2,
        previousActor = vehicle1,
        timeDelta = (tickData2.currentTickUnit - tickData1.currentTickUnit).differenceSeconds,
    )

    // Velocity = ((3, 4, 5) - (0, 0, 0)) / 1 = (3, 4, 5)
    assertEquals(3.0, vehicle2.velocity.x)
    assertEquals(4.0, vehicle2.velocity.y)
    assertEquals(5.0, vehicle2.velocity.z)

    // Acceleration = ((3, 4, 5) - (0, 0, 0)) / 1 = (3, 4, 5)
    assertEquals(3.0, vehicle2.acceleration.x)
    assertEquals(4.0, vehicle2.acceleration.y)
    assertEquals(5.0, vehicle2.acceleration.z)

    // Vehicle 2 has Vehicle 1 as previous state.
    updateActorVelocityAndAcceleration(
        vehicle = vehicle3,
        previousActor = vehicle2,
        timeDelta = (tickData3.currentTickUnit - tickData2.currentTickUnit).differenceSeconds,
    )

    // Velocity = ((8, 2, 10) - (3, 4, 5)) / 1 = (5, -2, 5)
    assertEquals(5.0, vehicle3.velocity.x)
    assertEquals(-2.0, vehicle3.velocity.y)
    assertEquals(5.0, vehicle3.velocity.z)

    // Acceleration = ((5, -2, 5) - (3, 4, 5)) / 1 = (2, -6, 0)
    assertEquals(2.0, vehicle3.acceleration.x)
    assertEquals(-6.0, vehicle3.acceleration.y)
    assertEquals(0.0, vehicle3.acceleration.z)
  }

  /**
   * Tests the velocity and acceleration calculations and check the case when there is no time
   * difference between two ticks (namely: time difference = 0.0).
   */
  @Test
  fun testZeroTimeDifference() {
    val vehicle1 = Vehicle(isEgo = true, lane = lane)
    val vehicle2 = Vehicle(isEgo = false, lane = lane)
    val vehicle3 = Vehicle(isEgo = false, lane = lane)

    // Vehicle 0 has no previous state. The velocity and acceleration values should be set to 0.0
    updateActorVelocityAndAcceleration(vehicle = vehicle1, previousActor = null, timeDelta = 0.0)
    assertEquals(0.0, vehicle1.velocity.x)
    assertEquals(0.0, vehicle1.velocity.y)
    assertEquals(0.0, vehicle1.velocity.z)
    assertEquals(0.0, vehicle1.acceleration.x)
    assertEquals(0.0, vehicle1.acceleration.y)
    assertEquals(0.0, vehicle1.acceleration.z)

    // The time difference between Vehicle0 and Vehicle1 is 0.0. Expect values of 0.0
    updateActorVelocityAndAcceleration(
        vehicle = vehicle2,
        previousActor = vehicle1,
        timeDelta = 0.0,
    )
    assertEquals(0.0, vehicle2.velocity.x)
    assertEquals(0.0, vehicle2.velocity.y)
    assertEquals(0.0, vehicle2.velocity.z)
    assertEquals(0.0, vehicle2.acceleration.x)
    assertEquals(0.0, vehicle2.acceleration.y)
    assertEquals(0.0, vehicle2.acceleration.z)

    // The time difference between Vehicle1 and Vehicle2 is 0.0. Expect values of 0.0
    updateActorVelocityAndAcceleration(
        vehicle = vehicle3,
        previousActor = vehicle2,
        timeDelta = 0.0,
    )
    assertEquals(0.0, vehicle3.velocity.x)
    assertEquals(0.0, vehicle3.velocity.y)
    assertEquals(0.0, vehicle3.velocity.z)
    assertEquals(0.0, vehicle3.acceleration.x)
    assertEquals(0.0, vehicle3.acceleration.y)
    assertEquals(0.0, vehicle3.acceleration.z)
  }

  /**
   * This test checks that negative time differences are handled correctly. If a "later" vehicle
   * state has a negative time difference to a "previous" vehicle state, an IllegalStateException
   * should be thrown.
   */
  @Test
  fun testNegativeTimeDifference() {
    val vehicle1 = Vehicle(isEgo = true, lane = lane)
    val vehicle2 = Vehicle(isEgo = true, lane = lane)

    // Create TickData for all vehicles.
    val tickData1 = TickData(currentTickUnit = TickDataUnitSeconds(0.0), entities = setOf(vehicle1))
    val tickData2 =
        TickData(currentTickUnit = TickDataUnitSeconds(-1.0), entities = setOf(vehicle2))

    // The time difference between Vehicle2 and Vehicle1 is -1.0. This should throw an
    // IllegalStateException
    assertFailsWith<IllegalStateException> {
      updateActorVelocityAndAcceleration(
          vehicle = vehicle2,
          previousActor = vehicle1,
          timeDelta = (tickData2.currentTickUnit - tickData1.currentTickUnit).differenceSeconds,
      )
    }
  }

  /**
   * This test checks that when there is no previous vehicle state that the velocity and
   * acceleration should be set to 0.0.
   */
  @Test
  fun testEmptyPreviousVehicle() {
    val vehicle = Vehicle(isEgo = true, lane = lane)

    // Vehicle has no previous state. The velocity and acceleration values should be set to 0.0
    updateActorVelocityAndAcceleration(vehicle = vehicle, previousActor = null, timeDelta = 0.0)
    assertEquals(0.0, vehicle.velocity.x)
    assertEquals(0.0, vehicle.velocity.y)
    assertEquals(0.0, vehicle.velocity.z)
    assertEquals(0.0, vehicle.acceleration.x)
    assertEquals(0.0, vehicle.acceleration.y)
    assertEquals(0.0, vehicle.acceleration.z)
  }

  /**
   * This test checks that when there is no previous vehicle state that the velocity and
   * acceleration should be set to 0.0.
   */
  @Test
  fun testWrongActorType() {
    val actor0 = Pedestrian(id = 0)
    val vehicle1 = Vehicle(id = 0)

    // It is expected that the actors of the current tick and the previous tick are both of type
    // 'Vehicle'. In this case the previous actor with the same id as a 'Pedestrian'. Therefore, an
    // IllegalStateException is expected.
    assertFailsWith<IllegalStateException> {
      updateActorVelocityAndAcceleration(
          vehicle = vehicle1,
          previousActor = actor0,
          timeDelta = 0.0,
      )
    }
  }
}
