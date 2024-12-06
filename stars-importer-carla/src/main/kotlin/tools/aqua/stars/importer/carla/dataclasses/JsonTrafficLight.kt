/*
 * Copyright 2023-2024 The STARS Project Authors
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
import tools.aqua.stars.data.av.dataclasses.TrafficLight
import tools.aqua.stars.data.av.dataclasses.TrafficLightState

/**
 * Json object for traffic lights.
 *
 * @property id The identifier of the traffic light.
 * @property typeId The type identifier.
 * @property state The current state oif the traffic light.
 * @property location The [JsonLocation] of the traffic light
 * @property rotation The [JsonRotation] of the traffic light
 * @property relatedOpenDriveId The related open drive identifier.
 */
@Serializable
@SerialName("TrafficLight")
data class JsonTrafficLight(
    @SerialName("id") override val id: Int,
    @SerialName("type_id") override val typeId: String,
    @SerialName("attributes") override val attributes: Map<String, String>,
    @SerialName("is_alive") override val isAlive: Boolean,
    @SerialName("is_active") override val isActive: Boolean,
    @SerialName("is_dormant") override val isDormant: Boolean,
    @SerialName("semantic_tags") override val semanticTags: List<Int>,
    @SerialName("bounding_box") override val boundingBox: JsonBoundingBox,
    @SerialName("location") override val location: JsonLocation,
    @SerialName("rotation") override val rotation: JsonRotation,
    @SerialName("state") val state: Int,
    @SerialName("related_open_drive_id") val relatedOpenDriveId: Int,
) : JsonActor() {

  /** Converts [JsonTrafficLight] to [TrafficLight]. */
  fun toTrafficLight(): TrafficLight =
      TrafficLight(
          id = this.id,
          state = TrafficLightState.getByValue(this.state),
          relatedOpenDriveId = this.relatedOpenDriveId)
}
