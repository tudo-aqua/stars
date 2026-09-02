/*
 * Copyright 2026 The STARS Project Authors
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
 * Json object describing a single lane marking that an [JsonActor]'s world-space bounding box is
 * currently touching (or has crossed).
 *
 * An empty `laneMarkingContacts` list on the actor means the bounding box lies within its lane's
 * markings.
 *
 * @property side Side of the reference lane, in driving direction (`"Left"` or `"Right"`).
 * @property roadId Identifier of the reference [JsonRoad].
 * @property laneId Identifier of the reference [JsonLane].
 * @property marking The touched [JsonLaneMarking], or 'null' if unavailable.
 * @property isCrossing Whether the bounding box reaches past the outer edge of the marking band.
 * @property penetration How far (in meters) the box extends past the inner edge of the marking
 *   band.
 */
@Serializable
data class JsonLaneMarkingContact(
    @SerialName("side") val side: String,
    @SerialName("road_id") val roadId: Int,
    @SerialName("lane_id") val laneId: Int,
    @SerialName("marking") val marking: JsonLaneMarking?,
    @SerialName("is_crossing") val isCrossing: Boolean,
    @SerialName("penetration") val penetration: Double,
)
