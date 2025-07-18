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
 * Json object for speed limits.
 *
 * @property speedLimit The speed limit.
 * @property fromDistanceFromStart Distance from the start of speed limit start.
 * @property toDistanceFromStart Distance from the start of speed limit end.
 */
@Serializable
data class JsonSpeedLimit(
    @SerialName("speed_limit") val speedLimit: Double = 0.0,
    @SerialName("from_distance") val fromDistanceFromStart: Double = 0.0,
    @SerialName("to_distance") val toDistanceFromStart: Double = 0.0,
)
