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
import kotlin.test.Test
import kotlin.test.assertFailsWith
import tools.aqua.stars.data.av.*
import tools.aqua.stars.data.av.dataclasses.Location
import tools.aqua.stars.data.av.dataclasses.TickDataUnitSeconds

/** Tests the velocity and acceleration calculations. */
class VelocityAndAccelerationTest {

  /** Tests the velocity and acceleration calculations on pre-defined values. */
  @Test
  fun testVelocityCalculation() {
    val tick0 = emptyTickData(currentTick = TickDataUnitSeconds(0.0))
    val tick1 = emptyTickData(currentTick = TickDataUnitSeconds(1.0))
    val tick2 = emptyTickData(currentTick = TickDataUnitSeconds(2.0))

    val vehicle0 = emptyVehicle(id = 0, location = Location(0.0, 0.0, 0.0), tickData = tick0)
    val vehicle1 = emptyVehicle(id = 0, location = Location(3.0, 4.0, 5.0), tickData = tick1)
    val vehicle2 = emptyVehicle(id = 0, location = Location(8.0, 2.0, 10.0), tickData = tick2)

    // Vehicle 0 has no previous state. The velocity and acceleration values should be set to 0.0
    updateActorVelocityAndAcceleration(vehicle0, null)
    assert(vehicle0.velocity.x == 0.0)
    assert(vehicle0.velocity.y == 0.0)
    assert(vehicle0.velocity.z == 0.0)
    assert(vehicle0.acceleration.x == 0.0)
    assert(vehicle0.acceleration.y == 0.0)
    assert(vehicle0.acceleration.z == 0.0)

    // Vehicle 1 has Vehicle 1 as previous state.
    updateActorVelocityAndAcceleration(vehicle1, vehicle0)
    // Velocity = ((3, 4, 5) - (0, 0, 0)) / 1 = (3, 4, 5)
    assert(vehicle1.velocity.x == 3.0)
    assert(vehicle1.velocity.y == 4.0)
    assert(vehicle1.velocity.z == 5.0)
    // Acceleration = ((3, 4, 5) - (0, 0, 0)) / 1 = (3, 4, 5)
    assert(vehicle1.acceleration.x == 3.0)
    assert(vehicle1.acceleration.y == 4.0)
    assert(vehicle1.acceleration.z == 5.0)

    // Vehicle 2 has Vehicle 1 as previous state.
    updateActorVelocityAndAcceleration(vehicle2, vehicle1)
    // Velocity = ((8, 2, 10) - (3, 4, 5)) / 1 = (5, -2, 5)
    assert(vehicle2.velocity.x == 5.0)
    assert(vehicle2.velocity.y == -2.0)
    assert(vehicle2.velocity.z == 5.0)
    // Acceleration = ((5, -2, 5) - (3, 4, 5)) / 1 = (2, -6, 0)
    assert(vehicle2.acceleration.x == 2.0)
    assert(vehicle2.acceleration.y == -6.0)
    assert(vehicle2.acceleration.z == 0.0)
  }

  /**
   * Tests the velocity and acceleration calculations and check the case when there is no time
   * difference between two ticks (namely: time difference = 0.0).
   */
  @Test
  fun testZeroTimeDifference() {
    val tick0 = emptyTickData()
    val tick1 = emptyTickData()
    val tick2 = emptyTickData()

    val vehicle0 = emptyVehicle(id = 0, location = Location(0.0, 0.0, 0.0), tickData = tick0)
    val vehicle1 = emptyVehicle(id = 0, location = Location(0.0, 0.0, 0.0), tickData = tick1)
    val vehicle2 = emptyVehicle(id = 0, location = Location(0.0, 0.0, 0.0), tickData = tick2)

    // Vehicle 0 has no previous state. The velocity and acceleration values should be set to 0.0
    updateActorVelocityAndAcceleration(vehicle0, null)
    assert(vehicle0.velocity.x == 0.0)
    assert(vehicle0.velocity.y == 0.0)
    assert(vehicle0.velocity.z == 0.0)
    assert(vehicle0.acceleration.x == 0.0)
    assert(vehicle0.acceleration.y == 0.0)
    assert(vehicle0.acceleration.z == 0.0)

    // The time difference between Vehicle0 and Vehicle1 is 0.0. Expect values of 0.0
    updateActorVelocityAndAcceleration(vehicle1, vehicle0)
    assert(vehicle1.velocity.x == 0.0)
    assert(vehicle1.velocity.y == 0.0)
    assert(vehicle1.velocity.z == 0.0)
    assert(vehicle1.acceleration.x == 0.0)
    assert(vehicle1.acceleration.y == 0.0)
    assert(vehicle1.acceleration.z == 0.0)

    // The time difference between Vehicle1 and Vehicle2 is 0.0. Expect values of 0.0
    updateActorVelocityAndAcceleration(vehicle2, vehicle1)
    assert(vehicle2.velocity.x == 0.0)
    assert(vehicle2.velocity.y == 0.0)
    assert(vehicle2.velocity.z == 0.0)
    assert(vehicle2.acceleration.x == 0.0)
    assert(vehicle2.acceleration.y == 0.0)
    assert(vehicle2.acceleration.z == 0.0)
  }

  /**
   * This test checks that negative time differences are handled correctly. If a "later" vehicle
   * state has a negative time difference to a "previous" vehicle state, an IllegalStateException
   * should be thrown.
   */
  @Test
  fun testNegativeTimeDifference() {
    val tick1 = emptyTickData(currentTick = TickDataUnitSeconds(0.0))
    val tick2 = emptyTickData(currentTick = TickDataUnitSeconds(-1.0))

    val vehicle1 = emptyVehicle(id = 0, location = Location(0.0, 0.0, 0.0), tickData = tick1)
    val vehicle2 = emptyVehicle(id = 0, location = Location(0.0, 0.0, 0.0), tickData = tick2)

    // The time difference between Vehicle2 and Vehicle1 is -1.0. This should throw an
    // IllegalStateException
    assertFailsWith<IllegalStateException> {
      updateActorVelocityAndAcceleration(vehicle2, vehicle1)
    }
  }

  /**
   * This test checks that when there is no previous vehicle state that the velocity and
   * acceleration should be set to 0.0.
   */
  @Test
  fun testEmptyPreviousVehicle() {
    val tick0 = emptyTickData()
    val vehicle0 = emptyVehicle(id = 0, location = Location(0.0, 0.0, 0.0), tickData = tick0)

    // Vehicle 0 has no previous state. The velocity and acceleration values should be set to 0.0
    updateActorVelocityAndAcceleration(vehicle0, null)
    assert(vehicle0.velocity.x == 0.0)
    assert(vehicle0.velocity.y == 0.0)
    assert(vehicle0.velocity.z == 0.0)
    assert(vehicle0.acceleration.x == 0.0)
    assert(vehicle0.acceleration.y == 0.0)
    assert(vehicle0.acceleration.z == 0.0)
  }

  /**
   * This test checks that when there is no previous vehicle state that the velocity and
   * acceleration should be set to 0.0.
   */
  @Test
  fun testWrongActorType() {
    val actor0 = emptyPedestrian(id = 0)
    val vehicle1 = emptyVehicle(id = 0)

    // It is expected that the actors of the current tick and the previous tick are both of type
    // 'Vehicle'. In this case the previous actor with the same id as a 'Pedestrian'. Therefore, an
    // IllegalStateException is expected.
    assertFailsWith<IllegalStateException> { updateActorVelocityAndAcceleration(vehicle1, actor0) }
  }
}
