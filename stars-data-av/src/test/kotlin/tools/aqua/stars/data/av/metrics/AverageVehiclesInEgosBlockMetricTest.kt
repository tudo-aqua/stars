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

package tools.aqua.stars.data.av.metrics

import kotlin.test.Test
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.data.av.dataclasses.TickDataUnitSeconds
import tools.aqua.stars.data.av.emptyBlock
import tools.aqua.stars.data.av.emptyTickData
import tools.aqua.stars.data.av.emptyVehicle

/** This class tests the [AverageVehiclesInEgosBlockMetric.evaluate] function. */
class AverageVehiclesInEgosBlockMetricTest {

  /**
   * This test checks that the returned value of the metric is equal to 1, as there is only the ego
   * in the block.
   */
  @Test
  fun testOnlyEgoInBlock() {
    val block = emptyBlock()
    val actor = emptyVehicle(egoVehicle = true)
    val tickData = emptyTickData(blocks = listOf(block), actors = listOf(actor))
    val segment = Segment(listOf(tickData), "", "")

    val averageVehiclesInEgosBlockMetric = AverageVehiclesInEgosBlockMetric()
    val evaluationResult = averageVehiclesInEgosBlockMetric.evaluate(segment)

    assert(evaluationResult == 1.0)
  }

  /** This test checks that there are (on average) 2 vehicles. */
  @Test
  fun testOneVehicleBesidesEgoInBlock() {
    val block = emptyBlock()
    val actor = emptyVehicle(egoVehicle = true)
    val actor2 = emptyVehicle(egoVehicle = false)
    val tickData =
        emptyTickData(
            blocks = listOf(block),
            currentTick = TickDataUnitSeconds(0.0),
            actors = listOf(actor, actor2))
    val tickData2 =
        emptyTickData(
            blocks = listOf(block),
            currentTick = TickDataUnitSeconds(1.0),
            actors = listOf(actor, actor2))
    val tickData3 =
        emptyTickData(
            blocks = listOf(block),
            currentTick = TickDataUnitSeconds(2.0),
            actors = listOf(actor, actor2))
    val segment = Segment(listOf(tickData, tickData2, tickData3), "", "")

    val averageVehiclesInEgosBlockMetric = AverageVehiclesInEgosBlockMetric()
    val evaluationResult = averageVehiclesInEgosBlockMetric.evaluate(segment)

    assert(evaluationResult == 2.0)
  }

  /** This test checks that there are (on average) over 1.5 vehicles. */
  @Test
  fun testMostlyOneVehicleBesidesEgoInBlock() {
    val block = emptyBlock()
    val actor = emptyVehicle(egoVehicle = true)
    val actor2 = emptyVehicle(egoVehicle = false)
    val tickData =
        emptyTickData(
            blocks = listOf(block),
            currentTick = TickDataUnitSeconds(0.0),
            actors = listOf(actor, actor2))
    val tickData2 =
        emptyTickData(
            blocks = listOf(block),
            currentTick = TickDataUnitSeconds(1.0),
            actors = listOf(actor, actor2))
    val tickData3 =
        emptyTickData(
            blocks = listOf(block), currentTick = TickDataUnitSeconds(2.0), actors = listOf(actor))
    val segment = Segment(listOf(tickData, tickData2, tickData3), "", "")

    val averageVehiclesInEgosBlockMetric = AverageVehiclesInEgosBlockMetric()
    val evaluationResult = averageVehiclesInEgosBlockMetric.evaluate(segment)

    assert(evaluationResult > 1.5)
  }
}
