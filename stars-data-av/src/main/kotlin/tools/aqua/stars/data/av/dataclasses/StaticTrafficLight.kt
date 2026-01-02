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

package tools.aqua.stars.data.av.dataclasses

/**
 * Data class for static traffic lights.
 *
 * @property id The identifier of the traffic light.
 * @property location The [Location] of the traffic light.
 * @property rotation The [Rotation] of the traffic light.
 * @property stopLocations List of stop locations as [Location]s.
 * @see TrafficLight
 */
data class StaticTrafficLight(
    var id: Int = 0,
    val location: Location = Location(),
    val rotation: Rotation = Rotation(),
    val stopLocations: List<Location> = emptyList(),
) {

  /** Returns [TrafficLightState] from [TickData]. */
  fun getStateInTick(tickData: TickData): TrafficLightState =
      tickData.trafficLights.firstOrNull { it.relatedOpenDriveId == this.id }?.state
          ?: TrafficLightState.Unknown

  override fun toString(): String = "StaticTrafficLight($id)"
}
