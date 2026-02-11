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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * JSON object for traffic lights.
 *
 * @property id The identifier of the traffic light.
 * @property typeId The type identifier.
 * @property attributes The additional attributes for the [JsonTrafficLight] from the CARLA
 *   simulation.
 * @property isAlive Whether the [JsonTrafficLight] is alive in the simulation.
 * @property isActive Whether the [JsonTrafficLight] is active in the simulation.
 * @property isDormant Whether the [JsonTrafficLight] is dormant in the simulation.
 * @property semanticTags The semantic tags of the [JsonTrafficLight] from the CARLA simulation.
 * @property boundingBox The bounding box of the [JsonTrafficLight].
 * @property location The [JsonLocation] of the traffic light
 * @property rotation The [JsonRotation] of the traffic light
 * @property collisions The list of actor IDs, this [JsonTrafficLight] is colliding with.
 * @property state The current state oif the traffic light.
 * @property relatedOpenDriveId The related open drive identifier.
 */
@Serializable
@SerialName("TrafficLight")
data class JsonTrafficLight(
    @SerialName("id") override val id: Int,
    @SerialName("type_id") override val typeId: String,
    @SerialName("attributes") override val attributes: Map<String, String> = mapOf(),
    @SerialName("is_alive") override val isAlive: Boolean = true,
    @SerialName("is_active") override val isActive: Boolean = true,
    @SerialName("is_dormant") override val isDormant: Boolean = true,
    @SerialName("semantic_tags") override val semanticTags: List<Int> = listOf(),
    @SerialName("bounding_box") override val boundingBox: JsonBoundingBox? = null,
    @SerialName("location") override val location: JsonLocation,
    @SerialName("rotation") override val rotation: JsonRotation,
    @SerialName("collisions") override val collisions: List<Int> = listOf(),
    @SerialName("state") var state: Int,
    @SerialName("related_open_drive_id") val relatedOpenDriveId: Int,
) : JsonActor()
