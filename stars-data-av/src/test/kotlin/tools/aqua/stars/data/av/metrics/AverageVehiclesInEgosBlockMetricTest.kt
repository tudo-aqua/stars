/*
 * Copyright 2023-2026 The STARS Project Authors
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

package tools.aqua.stars.data.av.metrics

import kotlin.test.BeforeTest
import kotlin.test.Test
import tools.aqua.stars.data.av.dataclasses.Block
import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Road
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.data.av.dataclasses.TickData
import tools.aqua.stars.data.av.dataclasses.TickDataUnitSeconds
import tools.aqua.stars.data.av.dataclasses.Vehicle

/** This class tests the [AverageVehiclesInEgosBlockMetric.evaluate] function. */
class AverageVehiclesInEgosBlockMetricTest {

  /** Lane for testing. */
  lateinit var lane: Lane

  /** Test setup. */
  @BeforeTest
  fun setup() {
    lane = Lane().also { Block(roads = listOf(Road(lanes = listOf(it)))) }
  }

  /**
   * This test checks that the returned value of the metric is equal to 1, as there is only the ego
   * in the block.
   */
  @Test
  fun testOnlyEgoInBlock() {
    val actor = Vehicle(isEgo = true, lane = lane)
    val tickData = TickData(entities = listOf(actor))
    val segment = Segment(listOf(tickData), "", "")

    val averageVehiclesInEgosBlockMetric = AverageVehiclesInEgosBlockMetric()
    val evaluationResult = averageVehiclesInEgosBlockMetric.evaluate(segment)

    assert(evaluationResult == 1.0)
  }

  /** This test checks that there are (on average) 2 vehicles. */
  @Test
  fun testOneVehicleBesidesEgoInBlock() {
    val entities = listOf(Vehicle(isEgo = true, lane = lane), Vehicle(isEgo = false, lane = lane))

    val tickData = TickData(currentTick = TickDataUnitSeconds(0.0), entities = entities)
    val tickData2 = TickData(currentTick = TickDataUnitSeconds(1.0), entities = entities)
    val tickData3 = TickData(currentTick = TickDataUnitSeconds(2.0), entities = entities)

    val segment = Segment(listOf(tickData, tickData2, tickData3), "", "")

    val averageVehiclesInEgosBlockMetric = AverageVehiclesInEgosBlockMetric()
    val evaluationResult = averageVehiclesInEgosBlockMetric.evaluate(segment)

    assert(evaluationResult == 2.0)
  }

  /** This test checks that there are (on average) over 1.5 vehicles. */
  @Test
  fun testMostlyOneVehicleBesidesEgoInBlock() {
    val actor = Vehicle(isEgo = true, lane = lane)
    val actor2 = Vehicle(isEgo = false, lane = lane)
    val tickData =
        TickData(currentTick = TickDataUnitSeconds(0.0), entities = listOf(actor, actor2))
    val tickData2 =
        TickData(currentTick = TickDataUnitSeconds(1.0), entities = listOf(actor, actor2))
    val tickData3 = TickData(currentTick = TickDataUnitSeconds(2.0), entities = listOf(actor))
    val segment = Segment(listOf(tickData, tickData2, tickData3), "", "")

    val averageVehiclesInEgosBlockMetric = AverageVehiclesInEgosBlockMetric()
    val evaluationResult = averageVehiclesInEgosBlockMetric.evaluate(segment)

    assert(evaluationResult > 1.5)
  }
}
