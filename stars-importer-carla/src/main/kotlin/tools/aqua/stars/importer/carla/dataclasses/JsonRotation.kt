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
import tools.aqua.stars.data.av.dataclasses.Rotation

/**
 * Json object for rotations.
 *
 * @property pitch The pitch.
 * @property yaw The yaw.
 * @property roll The roll.
 */
@Serializable
data class JsonRotation(
    @SerialName("pitch") val pitch: Double = 0.0,
    @SerialName("yaw") val yaw: Double = 0.0,
    @SerialName("roll") val roll: Double = 0.0
) {

  /** Converts [JsonRotation] to [Rotation]. */
  fun toRotation(): Rotation = Rotation(pitch, yaw, roll)
}
