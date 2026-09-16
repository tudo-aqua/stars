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
 * Json object for lanes.
 *
 * @property roadId The identifier of the road.
 * @property laneId The identifier of the lane.
 * @property laneType The [JsonLaneType] of the lane.
 * @property laneWidth The width of the lane.
 * @property laneLength The length of the lane.
 * @property s S.
 * @property predecessorLanes List of [JsonContactLaneInfo]s for preceding lanes.
 * @property successorLanes List of [JsonContactLaneInfo]s for successive lanes.
 * @property intersectingLanes List of [JsonContactLaneInfo]s for intersecting lanes.
 * @property laneMidpoints List of [JsonLaneMidpoint]s for lane midpoints.
 * @property speedLimits List of [JsonSpeedLimit]s for the speed limits.
 * @property landmarks List of [JsonLandmark]s on list lane.
 * @property contactAreas List of [JsonContactArea]s on this lane.
 * @property trafficLights List of [JsonStaticTrafficLight]s on this lane.
 * @property leftLaneMarking The [JsonLaneMarking] on the left edge of this lane, if any.
 * @property rightLaneMarking The [JsonLaneMarking] on the right edge of this lane, if any.
 * @property leftLane The adjacent lane to the left, if any.
 * @property rightLane The adjacent lane to the right, if any.
 * @property overlappingLanes List of [JsonContactLaneInfo]s for lanes that physically share the
 *   same road surface with this lane for a stretch.
 * @property laneTopology Raw topology string describing how this lane relates to its
 *   [overlappingLanes] (e.g. "Merging", "Diverging", "Merging & Diverging", "Overlapping", "").
 */
@Serializable
data class JsonLane(
    @SerialName("road_id") val roadId: Int,
    @SerialName("lane_id") val laneId: Int,
    @SerialName("lane_type") val laneType: JsonLaneType = JsonLaneType.Driving,
    @SerialName("lane_width") val laneWidth: Double,
    @SerialName("lane_length") var laneLength: Double,
    @SerialName("s") val s: Double,
    @SerialName("predecessor_lanes") val predecessorLanes: List<JsonContactLaneInfo>,
    @SerialName("successor_lanes") val successorLanes: List<JsonContactLaneInfo>,
    @SerialName("intersecting_lanes") val intersectingLanes: List<JsonContactLaneInfo>,
    @SerialName("lane_midpoints") val laneMidpoints: List<JsonLaneMidpoint>,
    @SerialName("speed_limits") val speedLimits: List<JsonSpeedLimit>,
    @SerialName("landmarks") var landmarks: List<JsonLandmark>,
    @SerialName("contact_areas") val contactAreas: List<JsonContactArea>,
    @SerialName("traffic_lights") var trafficLights: List<JsonStaticTrafficLight>,
    @SerialName("left_lane_marking") val leftLaneMarking: JsonLaneMarking? = null,
    @SerialName("right_lane_marking") val rightLaneMarking: JsonLaneMarking? = null,
    @SerialName("left_lane") val leftLane: JsonContactLaneInfo? = null,
    @SerialName("right_lane") val rightLane: JsonContactLaneInfo? = null,
    @SerialName("overlapping_lanes") val overlappingLanes: List<JsonContactLaneInfo> = emptyList(),
    @SerialName("lane_topology") val laneTopology: String = "",
)
