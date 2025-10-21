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
 * JSON object for traffic signs.
 *
 * @property id The identifier of the traffic light.
 * @property trafficSignType The [JsonTrafficSignType] of the traffic sign.
 * @property speedLimit The speed limit of the traffic sign, 'null' if not applicable.
 * @property typeId List type identifier.
 * @property location The [JsonLocation] of the traffic light
 * @property rotation The [JsonRotation] of the traffic light
 * @property attributes The additional attributes for the [JsonTrafficSign] from the CARLA
 *   simulation.
 * @property isAlive Whether the [JsonTrafficSign] is alive in the simulation.
 * @property isActive Whether the [JsonTrafficSign] is active in the simulation.
 * @property isDormant Whether the [JsonTrafficSign] is dormant in the simulation.
 * @property semanticTags The semantic tags of the [JsonTrafficSign] from the CARLA simulation.
 * @property boundingBox The bounding box of the [JsonTrafficSign].
 * @property collisions The list of actor IDs, this [JsonTrafficSign] is colliding with.
 */
@Serializable
@SerialName("TrafficSign")
data class JsonTrafficSign(
    @SerialName("id") override val id: Int = 0,
    @SerialName("traffic_sign_type") val trafficSignType: JsonTrafficSignType,
    @SerialName("speed_limit") val speedLimit: Double?,
    @SerialName("type_id") override val typeId: String,
    @SerialName("location") override val location: JsonLocation,
    @SerialName("rotation") override val rotation: JsonRotation,
    @SerialName("attributes") override val attributes: Map<String, String>,
    @SerialName("is_alive") override val isAlive: Boolean,
    @SerialName("is_active") override val isActive: Boolean,
    @SerialName("is_dormant") override val isDormant: Boolean,
    @SerialName("semantic_tags") override val semanticTags: List<Int>,
    @SerialName("bounding_box") override val boundingBox: JsonBoundingBox?,
    @SerialName("collisions") override val collisions: List<Int>,
) : JsonActor()
