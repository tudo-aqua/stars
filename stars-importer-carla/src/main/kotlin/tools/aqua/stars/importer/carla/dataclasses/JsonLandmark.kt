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
import tools.aqua.stars.data.av.dataclasses.Landmark
import tools.aqua.stars.data.av.dataclasses.LandmarkType

/**
 * Json object for landmarks.
 *
 * @property id The identifier of the landmark.
 * @property roadId The identifier of the road.
 * @property name The name of the landmark.
 * @property distance The distance.
 * @property s S.
 * @property isDynamic Whether the landmark is dynamic.
 * @property orientation The [JsonLandmarkOrientation] of teh landmark.
 * @property zOffset The z offset.
 * @property country The country.
 * @property type The [JsonLandmarkType] of the landmark.
 * @property subType The subtype.
 * @property value The value.
 * @property unit The unit.
 * @property height The height of the landmark.
 * @property width The width of the landmark.
 * @property text The text.
 * @property hOffset The h offset.
 * @property pitch The pitch of the landmark.
 * @property roll The roll of the landmark.
 * @property location The [JsonLocation] of the landmark.
 * @property rotation The [JsonRotation] of the landmark.
 */
@Serializable
data class JsonLandmark(
    @SerialName("id") var id: Int,
    @SerialName("road_id") val roadId: Int,
    @SerialName("name") val name: String,
    @SerialName("distance") val distance: Double,
    @SerialName("s") var s: Double,
    @SerialName("is_dynamic") val isDynamic: Boolean,
    @SerialName("orientation") val orientation: JsonLandmarkOrientation,
    @SerialName("z_offset") val zOffset: Double,
    @SerialName("country") val country: String,
    @SerialName("type") var type: JsonLandmarkType,
    @SerialName("sub_type") val subType: String,
    @SerialName("value") var value: Double,
    @SerialName("unit") var unit: String,
    @SerialName("height") val height: Double,
    @SerialName("width") val width: Double,
    @SerialName("text") val text: String,
    @SerialName("h_offset") val hOffset: Double,
    @SerialName("pitch") val pitch: Double,
    @SerialName("roll") val roll: Double,
    @SerialName("location") val location: JsonLocation,
    @SerialName("rotation") val rotation: JsonRotation,
) {
  /** Converts [JsonLandmark] to [Landmark]. */
  fun toLandmark(): Landmark =
      Landmark(
          id = this.id,
          name = this.name,
          distance = this.distance,
          s = this.s,
          country = this.country,
          type = LandmarkType.getByValue(this.type.value),
          value = this.value,
          unit = this.unit,
          text = this.text,
          location = this.location.toLocation(),
          rotation = this.rotation.toRotation())
}
