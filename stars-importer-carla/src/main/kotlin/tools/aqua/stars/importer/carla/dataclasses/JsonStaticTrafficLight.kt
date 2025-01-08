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

package tools.aqua.stars.importer.carla.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tools.aqua.stars.data.av.dataclasses.StaticTrafficLight

/**
 * Json object for static traffic lights.
 *
 * @property id The identifier of the traffic light.
 * @property positionDistance The position distance.
 * @property location The [JsonLocation] of the traffic light.
 * @property rotation The [JsonRotation] of the traffic light.
 * @property stopLocations List of stop locations as [JsonLocation]s.
 */
@Serializable
data class JsonStaticTrafficLight(
    @SerialName("open_drive_id") var id: Int,
    @SerialName("position_distance") val positionDistance: Float,
    @SerialName("location") var location: JsonLocation,
    @SerialName("rotation") var rotation: JsonRotation,
    @SerialName("stop_locations") var stopLocations: List<JsonLocation>,
) {

  /** Converts [JsonStaticTrafficLight] to [StaticTrafficLight]. */
  fun toStaticTrafficLight(): StaticTrafficLight =
      StaticTrafficLight(
          id = this.id,
          location = this.location.toLocation(),
          rotation = this.rotation.toRotation(),
          stopLocations = this.stopLocations.map { it.toLocation() })
}
