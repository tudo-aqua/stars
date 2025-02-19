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

package tools.aqua.stars.data.av.dataclasses

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.data.av.emptyTickData
import tools.aqua.stars.data.av.emptyVehicle

/**
 * This class tests the correctness of the [Segment.primaryEntityId] for [Segment]s. The primary
 * entity has constraints, such as 'in each tick there should be exactly one primary entity' and
 * 'the primary entity should be consistent in the whole segment'.
 */
class SegmentPrimaryEntityTest {
  /** Test [TickData] throwing [IllegalStateException] when there is no ego vehicle. */
  @Test
  fun `Test TickData throwing IllegalStateException when there is no ego vehicle`() {
    val vehicle1 = emptyVehicle(id = 0, isEgo = false)
    val vehicle2 = emptyVehicle(id = 1, isEgo = false)
    assertFailsWith<IllegalStateException> { emptyTickData(actors = listOf(vehicle1, vehicle2)) }
  }

  /** Test [TickData] with exactly one ego vehicle. */
  @Test
  fun `Test TickData with exactly one ego vehicle`() {
    val vehicle1 = emptyVehicle(id = 0, isEgo = true)
    val vehicle2 = emptyVehicle(id = 1, isEgo = false)
    val tickData = emptyTickData(actors = listOf(vehicle1, vehicle2))
    val segment = Segment(tickData = listOf(tickData), segmentSource = "")

    assertEquals(segment.primaryEntityId, vehicle1.id)
  }

  /** Test [TickData] throwing [IllegalStateException] when there are multiple ego vehicles. */
  @Test
  fun `Test TickData throwing IllegalStateException when there are multiple ego vehicles`() {
    val vehicle1 = emptyVehicle(id = 0, isEgo = true)
    val vehicle2 = emptyVehicle(id = 1, isEgo = true)
    assertFailsWith<IllegalStateException> { emptyTickData(actors = listOf(vehicle1, vehicle2)) }
  }

  /** Test [TickData] throwing [IllegalStateException] when ego vehicle changes between ticks. */
  @Test
  fun `Test TickData throwing IllegalStateException when ego vehicle changes between ticks`() {
    val vehicle1 = emptyVehicle(id = 0, isEgo = true)
    val vehicle2 = emptyVehicle(id = 1, isEgo = false)
    val tickData = emptyTickData(actors = listOf(vehicle1, vehicle2))

    // Change egoVehicle flag
    val changedVehicle1 = emptyVehicle(id = 0, isEgo = false)
    val changedVehicle2 = emptyVehicle(id = 1, isEgo = true)
    val tickData2 = emptyTickData(actors = listOf(changedVehicle1, changedVehicle2))

    assertFailsWith<IllegalStateException> {
      Segment(tickData = listOf(tickData, tickData2), segmentSource = "")
    }
  }
}
