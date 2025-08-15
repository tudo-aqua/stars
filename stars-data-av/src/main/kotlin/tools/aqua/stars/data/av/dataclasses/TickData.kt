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
) : TickDataType<Actor, TickData, TickDataUnitSeconds, TickDataDifferenceSeconds>(currentTickUnit, entities) {

  /** All pedestrians. */
  val pedestrians: List<Pedestrian>
    get() = entities.filterIsInstance<Pedestrian>()

  /** All vehicles. */
  val vehicles: List<Vehicle>
    get() = entities.filterIsInstance<Vehicle>()

  /** The ego vehicle. */
  override val ego: Vehicle = vehicles.first { it.isEgo }
}
