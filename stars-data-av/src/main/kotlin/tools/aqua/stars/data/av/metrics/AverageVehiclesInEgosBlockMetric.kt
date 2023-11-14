/*
 * Copyright 2023 The STARS Project Authors
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

import java.util.logging.Logger
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.SegmentMetricProvider
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.data.av.Actor
import tools.aqua.stars.data.av.Block
import tools.aqua.stars.data.av.Segment
import tools.aqua.stars.data.av.TickData

/**
 * This class is an implementation of [SegmentMetricProvider] which provides the metric "average
 * vehicles in ego's block"
 */
class AverageVehiclesInEgosBlockMetric(
    override val logger: Logger = Loggable.getLogger("average-vehicles-in-egos-block")
) : SegmentMetricProvider<Actor, TickData, Segment>, Loggable {
  /**
   * Evaluates the average count of [Actor]s in the [Block] of the ego vehicle.
   *
   * @param segment The Segment on which the average count should be calculated
   * @return The average count of [Actor]s in the [Block] of the ego vehicle
   */
  override fun evaluate(segment: SegmentType<Actor, TickData, Segment>): Double {
    val averageVehiclesInEgosBlock =
        segment.tickData.map { it.vehiclesInBlock(it.egoVehicle.lane.road.block).size }.average()
    logFiner(
        "The average count of vehicles in Segment '$segment' for ego's block is: $averageVehiclesInEgosBlock")
    return averageVehiclesInEgosBlock
  }
}
