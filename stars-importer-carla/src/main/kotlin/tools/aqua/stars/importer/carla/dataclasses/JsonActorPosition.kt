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

/**
 * Json object for actor positions.
 *
 * @property positionOnLane Current position on the lane.
 * @property laneId The lane's identifier.
 * @property roadId The road's identifier.
 * @property actor The [JsonActor].
 */
@Serializable
data class JsonActorPosition(
    @SerialName("position_on_lane") val positionOnLane: Double,
    @SerialName("lane_id") var laneId: Int,
    @SerialName("road_id") var roadId: Int,
    @SerialName("actor") var actor: JsonActor,
)
