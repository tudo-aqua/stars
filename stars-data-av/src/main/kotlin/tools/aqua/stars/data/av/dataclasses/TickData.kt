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

package tools.aqua.stars.data.av.dataclasses

import tools.aqua.stars.core.types.TickDataType

data class TickData(
    override val currentTick: Double,
    override var entities: List<Actor>,
    val trafficLights: List<TrafficLight>,
    val blocks: List<Block>,
    val weather: WeatherParameters,
    val daytime: Daytime
) : TickDataType<Actor, TickData, Segment> {
  override lateinit var segment: Segment
  val actors: List<Actor>
    get() = entities

  fun actor(actorID: Int): Actor? = actors.firstOrNull { it.id == actorID }

  val egoVehicle: Vehicle
    get() = actors.firstOrNull { it is Vehicle && it.egoVehicle } as Vehicle

  val vehicles: List<Vehicle>
    get() = actors.filterIsInstance<Vehicle>()

  fun vehiclesInBlock(block: Block): List<Vehicle> = vehicles.filter { it.lane.road.block == block }

  val pedestrians: List<Pedestrian>
    get() = actors.filterIsInstance<Pedestrian>()

  override fun toString() = "$currentTick"

  fun clone(): TickData {
    val newTickData = TickData(currentTick, emptyList(), trafficLights, blocks, weather, daytime)
    newTickData.entities = actors.map { it.clone(newTickData) }
    return newTickData
  }
}
