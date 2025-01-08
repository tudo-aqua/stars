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
import tools.aqua.stars.data.av.dataclasses.LaneMidpoint

/**
 * Json object for lane midpoints.
 *
 * @property laneId The identifier of the lane.
 * @property roadId The identifier of the road.
 * @property distanceToStart The distance to the starting point.
 * @property location The [JsonLocation] of the midpoint.
 * @property rotation The [JsonRotation] of the midpoint.
 */
@Serializable
data class JsonLaneMidpoint(
    @SerialName("lane_id") val laneId: Int,
    @SerialName("road_id") val roadId: Int,
    @SerialName("distance_to_start") val distanceToStart: Double,
    @SerialName("location") val location: JsonLocation,
    @SerialName("rotation") val rotation: JsonRotation,
) {
  /** Converts [JsonLaneMidpoint] to [LaneMidpoint]. */
  fun toLaneMidpoint(): LaneMidpoint =
      LaneMidpoint(
          distanceToStart = this.distanceToStart,
          location = this.location.toLocation(),
          rotation = this.rotation.toRotation())
}
