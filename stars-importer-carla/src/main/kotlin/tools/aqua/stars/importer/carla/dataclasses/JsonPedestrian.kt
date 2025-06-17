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
 * Json object for pedestrians.
 *
 * @property id The identifier of the pedestrian.
 * @property typeId The type identifier.
 * @property location The [JsonLocation] of the traffic light
 * @property rotation The [JsonRotation] of the traffic light
 * @property attributes The additional attributes for the [JsonPedestrian] from the CARLA
 *   simulation.
 * @property isAlive Whether the [JsonPedestrian] is alive in the simulation.
 * @property isActive Whether the [JsonPedestrian] is active in the simulation.
 * @property isDormant Whether the [JsonPedestrian] is dormant in the simulation.
 * @property semanticTags The semantic tags of the [JsonPedestrian] from the CARLA simulation.
 * @property boundingBox The bounding box of the [JsonPedestrian].
 */
@Serializable
@SerialName("Pedestrian")
data class JsonPedestrian(
    @SerialName("id") override val id: Int = 0,
    @SerialName("type_id") val typeId: String = "",
    @SerialName("location") override val location: JsonLocation = JsonLocation(),
    @SerialName("rotation") override val rotation: JsonRotation = JsonRotation(),
    @SerialName("attributes") override val attributes: Map<String, String> = emptyMap(),
    @SerialName("is_alive") override val isAlive: Boolean = false,
    @SerialName("is_active") override val isActive: Boolean = false,
    @SerialName("is_dormant") override val isDormant: Boolean = false,
    @SerialName("semantic_tags") override val semanticTags: List<Int> = emptyList(),
    @SerialName("bounding_box") override val boundingBox: JsonBoundingBox? = JsonBoundingBox(),
) : JsonActor()
