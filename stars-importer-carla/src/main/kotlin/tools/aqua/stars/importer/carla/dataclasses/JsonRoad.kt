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

package tools.aqua.stars.importer.carla.dataclasses

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

/**
 * Json object for roads.
 *
 * @property roadId The identifier of the road.
 * @property lanes List of [JsonLane]s on this road.
 * @property junctionId The identifier of the junction this road belongs to, if any.
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys // ignore is_junction key in JSON file
data class JsonRoad(
    @SerialName("road_id") val roadId: Int,
    @SerialName("lanes") val lanes: List<JsonLane>,
    @SerialName("junction_id") val junctionId: Int?,
)
