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

package tools.aqua.stars.importer.carla.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Json object for roads.
 *
 * @property roadId The identifier of the road.
 * @property isJunction Whether this is a junction.
 * @property lanes List of [JsonLane]s on this road.
 */
@Serializable
data class JsonRoad(
    @SerialName("road_id") val roadId: Int,
    @SerialName("is_junction") val isJunction: Boolean,
    @SerialName("lanes") val lanes: List<JsonLane>,
)
