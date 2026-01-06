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
import kotlin.test.assertEquals
import tools.aqua.stars.data.av.dataclasses.Lane
import tools.aqua.stars.data.av.dataclasses.Road
import tools.aqua.stars.data.av.dataclasses.TickData
import tools.aqua.stars.data.av.dataclasses.TickDataUnitSeconds
import tools.aqua.stars.data.av.dataclasses.Vehicle

/** This class tests the [AverageVehiclesInEgoRoadMetric.evaluate] function. */
class AverageVehiclesInEgosRoadMetricTest {

  /** [Road] for testing. */
  lateinit var road: Road

  /** [Lane] for testing. */
  lateinit var lane: Lane

  /** Test setup. */
  @BeforeTest
  fun setup() {
    lane = Lane()
    road = Road(lanes = listOf(lane)).also { lane.road = it }
  }

  /**
   * This test checks that the returned value of the metric is equal to 1, as there is only the ego
   * in the block.
   */
  @Test
  fun testOnlyEgoInRoad() {
    val actor = Vehicle(isEgo = true, lane = lane)
    val tick = TickData(entities = setOf(actor), identifier = "TickData")

    val averageVehiclesInEgosRoadMetric = AverageVehiclesInEgoRoadMetric()
    val evaluationResult = averageVehiclesInEgosRoadMetric.evaluate(tick)

    assertEquals(1, evaluationResult)
    assertEquals(1.0, averageVehiclesInEgosRoadMetric.getState())
  }

  /** This test checks that there are (on average) 2 vehicles. */
  @Test
  fun testOneVehicleBesidesEgoInRoad() {
    val ego = Vehicle(id = 0, isEgo = true, lane = lane)
    val other = Vehicle(id = 1, isEgo = false, lane = lane)

    val tick1 =
        TickData(
            currentTickUnit = TickDataUnitSeconds(0.0),
            entities = setOf(ego, other),
            identifier = "TickData",
        )
    val tick2 =
        TickData(
            currentTickUnit = TickDataUnitSeconds(1.0),
            entities = setOf(ego, other),
            identifier = "TickData",
        )
    val tick3 =
        TickData(
            currentTickUnit = TickDataUnitSeconds(2.0),
            entities = setOf(ego, other),
            identifier = "TickData",
        )

    var evaluationResult: Int
    val averageVehiclesInEgosRoadMetric = AverageVehiclesInEgoRoadMetric()

    evaluationResult = averageVehiclesInEgosRoadMetric.evaluate(tick1)
    assertEquals(2, evaluationResult)
    assertEquals(2.0, averageVehiclesInEgosRoadMetric.getState())

    evaluationResult = averageVehiclesInEgosRoadMetric.evaluate(tick2)
    assertEquals(2, evaluationResult)
    assertEquals(2.0, averageVehiclesInEgosRoadMetric.getState())

    evaluationResult = averageVehiclesInEgosRoadMetric.evaluate(tick3)
    assertEquals(2, evaluationResult)
    assertEquals(2.0, averageVehiclesInEgosRoadMetric.getState())
  }

  /** This test checks that there are (on average) over 1.5 vehicles. */
  @Test
  fun testMostlyOneVehicleBesidesEgoInRoad() {
    val ego = Vehicle(id = 0, isEgo = true, lane = lane)
    val other = Vehicle(id = 1, isEgo = false, lane = lane)

    val tick1 =
        TickData(
            currentTickUnit = TickDataUnitSeconds(0.0),
            entities = setOf(ego, other),
            identifier = "TickData",
        )
    val tick2 =
        TickData(
            currentTickUnit = TickDataUnitSeconds(1.0),
            entities = setOf(ego),
            identifier = "TickData",
        )

    var evaluationResult: Int
    val averageVehiclesInEgosRoadMetric = AverageVehiclesInEgoRoadMetric()

    evaluationResult = averageVehiclesInEgosRoadMetric.evaluate(tick1)
    assertEquals(2, evaluationResult)
    assertEquals(2.0, averageVehiclesInEgosRoadMetric.getState())

    evaluationResult = averageVehiclesInEgosRoadMetric.evaluate(tick2)
    assertEquals(1, evaluationResult)
    assertEquals(1.5, averageVehiclesInEgosRoadMetric.getState())
  }
}
