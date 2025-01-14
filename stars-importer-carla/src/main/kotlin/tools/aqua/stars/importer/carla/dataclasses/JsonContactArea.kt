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
 * Json object for road touching points.
 *
 * @property id Identifier of the road touching point.
 * @property contactLocation The [JsonLocation] of the touching point.
 * @property lane1RoadId Road identifier of lane 1.
 * @property lane1Id Identifier of lane 1.
 * @property lane1StartPos Start position on lane 1.
 * @property lane1EndPos End position on lane 1.
 * @property lane2RoadId Road identifier of lane 2.
 * @property lane2Id Identifier of lane 2.
 * @property lane2StartPos Start position on lane 2.
 * @property lane2EndPos End position on lane 2.
 */
@Serializable
data class JsonContactArea(
    @SerialName("id") val id: String,
    @SerialName("contact_location") val contactLocation: JsonLocation,
    @SerialName("lane_1_road_id") val lane1RoadId: Int,
    @SerialName("lane_1_id") val lane1Id: Int,
    @SerialName("lane_1_start_pos") val lane1StartPos: Double,
    @SerialName("lane_1_end_pos") val lane1EndPos: Double,
    @SerialName("lane_2_road_id") val lane2RoadId: Int,
    @SerialName("lane_2_id") val lane2Id: Int,
    @SerialName("lane_2_start_pos") val lane2StartPos: Double,
    @SerialName("lane_2_end_pos") val lane2EndPos: Double,
)
