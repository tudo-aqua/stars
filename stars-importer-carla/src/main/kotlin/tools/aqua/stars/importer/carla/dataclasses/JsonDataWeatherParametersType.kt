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
 * Json object for weather types. [Default] is equal to [ClearSunset].
 *
 * @property value Json value.
 */
@Serializable
enum class JsonDataWeatherParametersType(
    val value: Int
) { // TODO: Check whether to include dust storm
  @SerialName("0") Default(0),
  @SerialName("1") ClearNoon(1),
  @SerialName("2") CloudyNoon(2),
  @SerialName("3") WetNoon(3),
  @SerialName("4") WetCloudyNoon(4),
  @SerialName("5") SoftRainNoon(5),
  @SerialName("6") MidRainNoon(6),
  @SerialName("7") HardRainNoon(7),
  @SerialName("8") ClearSunset(8),
  @SerialName("9") CloudySunset(9),
  @SerialName("10") WetSunset(10),
  @SerialName("11") WetCloudySunset(11),
  @SerialName("12") SoftRainSunset(12),
  @SerialName("13") MidRainSunset(13),
  @SerialName("14") HardRainSunset(14)
}
