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
import tools.aqua.stars.data.av.dataclasses.Location

/**
 * Json object for 3D locations.
 *
 * @property x The x ordinate.
 * @property y The y ordinate.
 * @property z The z ordinate.
 */
@Serializable
data class JsonLocation(
    @SerialName("x") val x: Double,
    @SerialName("y") val y: Double,
    @SerialName("z") val z: Double
) {

  /** Converts [JsonLocation] to [Location]. */
  fun toLocation(): Location = Location(x, y, z)

  override fun toString(): String = "($x,$y,$z)"
}
