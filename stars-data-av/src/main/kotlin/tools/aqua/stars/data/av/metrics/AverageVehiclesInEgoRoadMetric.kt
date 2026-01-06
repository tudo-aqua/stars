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

import java.util.logging.Logger
import tools.aqua.stars.core.metrics.providers.Loggable
import tools.aqua.stars.core.metrics.providers.SerializableMetric
import tools.aqua.stars.core.metrics.providers.Stateful
import tools.aqua.stars.core.metrics.providers.TickMetricProvider
import tools.aqua.stars.core.serialization.SerializableIntResult
import tools.aqua.stars.data.av.dataclasses.*

/**
 * This class is an implementation of [TickMetricProvider] which provides the metric "average
 * vehicles in ego's road".
 *
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
class AverageVehiclesInEgoRoadMetric(
    override val loggerIdentifier: String = "average-vehicles-in-egos-road",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier),
) :
    TickMetricProvider<Actor, TickData, TickDataUnitSeconds, TickDataDifferenceSeconds>,
    Stateful,
    SerializableMetric,
    Loggable {

  private val vehicleCounts = mutableListOf<Int>()

  /**
   * Evaluates the average count of [Actor]s in the [Road] of the ego vehicle.
   *
   * @param tick The [TickData] on which the average count should be calculated.
   * @return The current count of [Actor]s in the [Road] of the ego vehicle.
   */
  override fun evaluate(tick: TickData): Int {
    val road = tick.ego.lane.road

    val vehicleCount = tick.vehicles.count { it.lane.road == road }
    vehicleCounts += vehicleCount

    logFiner(
        "The count of vehicles in Tick '$tick' for ego's road is: ${vehicleCount}. " +
            "Rolling average is: ${getState()}."
    )

    return vehicleCount
  }

  override fun getState(): Double = vehicleCounts.average()

  override fun printState() {
    logInfo("Average vehicles in ego's road: ${getState()}")
  }

  override fun getSerializableResults(): List<SerializableIntResult> =
      vehicleCounts.map {
        SerializableIntResult(identifier = loggerIdentifier, source = loggerIdentifier, value = it)
      }
}
