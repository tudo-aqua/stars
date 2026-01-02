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
    @SerialName("id") var id: Int = 0,
    @SerialName("road_id") val roadId: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("distance") val distance: Double = 0.0,
    @SerialName("s") var s: Double = 0.0,
    @SerialName("is_dynamic") val isDynamic: Boolean = false,
    @SerialName("orientation")
    val orientation: JsonLandmarkOrientation = JsonLandmarkOrientation.Both,
    @SerialName("z_offset") val zOffset: Double = 0.0,
    @SerialName("country") val country: String = "",
    @SerialName("type") var type: JsonLandmarkType = JsonLandmarkType.LightPost,
    @SerialName("sub_type") val subType: String = "",
    @SerialName("value") var value: Double = 0.0,
    @SerialName("unit") var unit: String = "",
    @SerialName("height") val height: Double = 0.0,
    @SerialName("width") val width: Double = 0.0,
    @SerialName("text") val text: String = "",
    @SerialName("h_offset") val hOffset: Double = 0.0,
    @SerialName("pitch") val pitch: Double = 0.0,
    @SerialName("roll") val roll: Double = 0.0,
    @SerialName("location") val location: JsonLocation = JsonLocation(),
    @SerialName("rotation") val rotation: JsonRotation = JsonRotation(),
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
          rotation = this.rotation.toRotation(),
      )
}
