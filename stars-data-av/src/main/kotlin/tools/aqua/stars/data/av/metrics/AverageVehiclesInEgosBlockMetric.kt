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

package tools.aqua.stars.data.av.metrics

import java.util.logging.Logger
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.MetricProvider
import tools.aqua.stars.core.metric.providers.SegmentMetricProvider
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.requireIsInstance
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.data.av.dataclasses.Actor
import tools.aqua.stars.data.av.dataclasses.Block
import tools.aqua.stars.data.av.dataclasses.Segment
import tools.aqua.stars.data.av.dataclasses.TickData

/**
 * This class is an implementation of [SegmentMetricProvider] which provides the metric "average
 * vehicles in ego's block".
 */
class AverageVehiclesInEgosBlockMetric(
    override val logger: Logger = Loggable.getLogger("average-vehicles-in-egos-block")
) : SegmentMetricProvider<Actor, TickData, Segment>(), Stateful, Loggable {

  /** Holds the number of analyzed ticks. */
  private var totalTicks: Int = 0

  /** Holds the number of encountered vehicles. */
  private var totalVehicles: Int = 0

  /**
   * Evaluates the average count of [Actor]s in the [Block] of the ego vehicle.
   *
   * @param segment The Segment on which the average count should be calculated
   * @return The average count of [Actor]s in the [Block] of the ego vehicle
   */
  override fun evaluate(segment: SegmentType<Actor, TickData, Segment>): Double {
    val ticks: Int = segment.tickData.size
    val vehiclesInEgosBlock: Int =
        segment.tickData.sumOf { it.vehiclesInBlock(it.egoVehicle.lane.road.block).size }
    val avg: Double = vehiclesInEgosBlock.toDouble() / ticks.toDouble()

    totalTicks += ticks
    totalVehicles += vehiclesInEgosBlock

    logFiner("The average count of vehicles in Segment '$segment' for ego's block is: $avg")

    return avg
  }

  override fun getState(): Double = totalVehicles.toDouble() / totalTicks.toDouble()

  override fun printState() {
    logInfo("=== Average vehicles in ego block ===")
    logInfo(" ${getState()}\n")
  }

  override fun copy(): AverageVehiclesInEgosBlockMetric = AverageVehiclesInEgosBlockMetric(logger)

  override fun merge(other: MetricProvider<Actor, TickData, Segment>) {
    requireIsInstance<AverageVehiclesInEgosBlockMetric>(other) {
      "Trying to merge different metrics."
    }

    this.totalVehicles += other.totalVehicles
    this.totalTicks += other.totalTicks
  }
}
