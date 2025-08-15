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
 * @property currentTickUnit Current tick value.
 * @property trafficLights List of all [TrafficLight]s.
 * @property blocks ist of all [World]s.
 * @property weather The current [WeatherParameters].
 * @property daytime The current [Daytime].
 */
class TickData(
  currentTickUnit: TickDataUnitSeconds = TickDataUnitSeconds(0.0),
  entities: Set<Actor>,
  val trafficLights: List<TrafficLight> = emptyList(),
  val blocks: List<World> = emptyList(),
  val weather: WeatherParameters = WeatherParameters(),
  val daytime: Daytime = Daytime.Noon
) :
    TickDataType<Actor, TickData, TickDataUnitSeconds, TickDataDifferenceSeconds>(
        currentTickUnit, entities) {

  /** All pedestrians. */
  val pedestrians: Map<Int, Pedestrian>
    get() = TODO() // entities.filterIsInstanceTo<Int, Pedestrian>(mutableMapOf<Int, Pedestrian>())

  /** All vehicles. */
  val vehicles: List<Vehicle> = listOf() // TODO

  /** The ego vehicle. */
  val ego: Vehicle

  init {
    vehicles
        .filter { it.isEgo }
        .let {
          check(it.size == 1) {
            "There must be exactly one ego vehicle in the tick data. Was ${it.size} of ${vehicles.size}$ vehicles."
          }
          ego = it.first()
        }
  }

  //  public inline fun <K, V> Map<*, *>.filterValuesIsInstance(): Map<K, V> =
  //    filterValuesIsInstanceTo(mutableMapOf())

  //  public inline fun <reified K, reified V, C : MutableMap<in K, in V>> Map<*,
  // *>.filterValuesIsInstanceTo(destination: C): C {
  //    for ((k, v) in this) if (k is K && v is V) destination.put(k, v)
  //    return destination
  //  }

  /** Returns all [Vehicle]s in given [World]. */
  fun vehiclesInBlock(block: World): List<Vehicle> = vehicles.filter { it.lane.road.block == block }

  /** Clones current [TickData]. */
  fun clone(): TickData =
      TickData(currentTickUnit, entities, trafficLights, blocks, weather, daytime)

  override fun toString(): String = "$currentTickUnit"
}
