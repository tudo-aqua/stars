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
 * @param currentTickUnit Current tick value.
 * @param entities Set of all [Actor]s in the current [TickData].
 * @param identifier The identifier of the [TickData].
 * @property trafficLights List of all [TrafficLight]s in this [TickData].
 * @property weather The current [WeatherParameters] in this [TickData].
 * @property daytime The current [Daytime] in this [TickData].
 * @property world The [World] for this [TickData].
 */
class TickData(
    currentTickUnit: TickDataUnitSeconds = TickDataUnitSeconds(0.0),
    entities: Set<Actor>,
    identifier: String,
    val trafficLights: List<TrafficLight> = emptyList(),
    val weather: WeatherParameters = WeatherParameters(),
    val daytime: Daytime = Daytime.Noon,
    val world: World = World(straights = listOf(Road(id = 0, lanes = listOf(Lane(laneId = 0))))),
) :
    TickDataType<Actor, TickData, TickDataUnitSeconds, TickDataDifferenceSeconds>(
        currentTickUnit,
        entities,
        identifier,
    ) {

  /** All pedestrians. */
  val pedestrians: List<Pedestrian>
    get() = entities.filterIsInstance<Pedestrian>()

  /** All vehicles. */
  val vehicles: List<Vehicle>
    get() = entities.filterIsInstance<Vehicle>()

  /** The ego vehicle. */
  override val ego: Vehicle = vehicles.first { it.isEgo }
}
