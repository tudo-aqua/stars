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

import tools.aqua.stars.core.types.TickDataType

/**
 * Data class for tick data.
 *
 * @property currentTick Current tick value.
 * @property entities List of all [Actor]s.
 * @property trafficLights List of all [TrafficLight]s.
 * @property blocks ist of all [Block]s.
 * @property weather The current [WeatherParameters].
 * @property daytime The current [Daytime].
 */
data class TickData(
    override val currentTick: TickDataUnitSeconds = TickDataUnitSeconds(0.0),
    override var entities: List<Actor>,
    val trafficLights: List<TrafficLight> = emptyList(),
    val blocks: List<Block> = emptyList(),
    val weather: WeatherParameters = WeatherParameters(),
    val daytime: Daytime = Daytime.Noon
) : TickDataType<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds> {

  override lateinit var segment: Segment

  init {
    entities.onEach { it.tickData = this }

    vehicles
        .filter { it.isEgo }
        .let {
          check(it.size == 1) {
            "There must be exactly one ego vehicle in the tick data. Was ${it.size} of ${vehicles.size}$ vehicles."
          }
          ego = it.first()
        }
  }

  /** All pedestrians. */
  val pedestrians: List<Pedestrian>
    get() = entities.filterIsInstance<Pedestrian>()

  /** All vehicles. */
  val vehicles: List<Vehicle>
    get() = entities.filterIsInstance<Vehicle>()

  /** The ego vehicle. */
  val ego: Vehicle

  /** Returns all [Vehicle]s in given [Block]. */
  fun vehiclesInBlock(block: Block): List<Vehicle> = vehicles.filter { it.lane.road.block == block }

  /** Clones current [TickData]. */
  fun clone(): TickData =
      TickData(currentTick, entities, trafficLights, blocks, weather, daytime).also {
        it.entities = entities.map { t -> t.clone(it) }
      }

  override fun toString(): String = "$currentTick"
}
