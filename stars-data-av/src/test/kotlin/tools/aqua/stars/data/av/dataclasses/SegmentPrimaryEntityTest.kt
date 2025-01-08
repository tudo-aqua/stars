/*
 * Copyright 2024-2025 The STARS Project Authors
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
  /**
   * This test checks that an exception is thrown, when there is no primary entity in a [Segment].
   */
  @Test
  fun testNoEgoVehicle() {
    val vehicle1 = emptyVehicle(id = 0, egoVehicle = false)
    val vehicle2 = emptyVehicle(id = 1, egoVehicle = false)
    val tickData = emptyTickData(actors = listOf(vehicle1, vehicle2))
    val segment =
        Segment(segmentSource = "", mainInitList = listOf(tickData), simulationRunId = "1")
    assertFailsWith<IllegalStateException> { segment.primaryEntityId }
  }

  /**
   * This test checks that there is no exception when exactly one primary entity is present in a
   * [Segment].
   */
  @Test
  fun testHasEgoVehicle() {
    val vehicle1 = emptyVehicle(id = 0, egoVehicle = true)
    val vehicle2 = emptyVehicle(id = 1, egoVehicle = false)
    val tickData = emptyTickData(actors = listOf(vehicle1, vehicle2))
    val segment =
        Segment(segmentSource = "", mainInitList = listOf(tickData), simulationRunId = "1")
    assertEquals(segment.primaryEntityId, vehicle1.id)
  }

  /**
   * This test checks that an exception is thrown when there are multiple primary entities in a
   * [Segment].
   */
  @Test
  fun testHasMultipleEgoVehicles() {
    val vehicle1 = emptyVehicle(id = 0, egoVehicle = true)
    val vehicle2 = emptyVehicle(id = 1, egoVehicle = true)
    val tickData = emptyTickData(actors = listOf(vehicle1, vehicle2))
    val segment =
        Segment(segmentSource = "", mainInitList = listOf(tickData), simulationRunId = "1")
    assertFailsWith<IllegalStateException> { segment.primaryEntityId }
  }

  /**
   * This test checks that an exception is thrown when the primary entity changed during a [Segment]
   * .
   */
  @Test
  fun testChangingEgoVehicles() {
    val vehicle1 = emptyVehicle(id = 0, egoVehicle = true)
    val vehicle2 = emptyVehicle(id = 1, egoVehicle = false)
    val tickData = emptyTickData(actors = listOf(vehicle1, vehicle2))

    // Change egoVehicle flag
    val changedVehicle1 = emptyVehicle(id = 0, egoVehicle = false)
    val changedVehicle2 = emptyVehicle(id = 1, egoVehicle = true)
    val tickData2 = emptyTickData(actors = listOf(changedVehicle1, changedVehicle2))

    val segment =
        Segment(
            segmentSource = "", mainInitList = listOf(tickData, tickData2), simulationRunId = "1")

    assertFailsWith<IllegalStateException> { segment.primaryEntityId }
  }
}
