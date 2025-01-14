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
import tools.aqua.stars.data.av.dataclasses.Daytime
import tools.aqua.stars.data.av.dataclasses.WeatherType

/**
 * Json object for weather types. [Default] is equal to [ClearSunset].
 *
 * @property value Json value.
 */
@Serializable
enum class JsonDataWeatherParametersType(val value: Int) {
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
  @SerialName("14") HardRainSunset(14);

  /** Extracts [WeatherType] from [JsonDataWeatherParametersType]. */
  fun toWeatherType(): WeatherType =
      when (this) {
        ClearNoon,
        ClearSunset,
        Default -> WeatherType.Clear
        CloudyNoon,
        CloudySunset -> WeatherType.Cloudy
        WetNoon,
        WetSunset -> WeatherType.Wet
        WetCloudyNoon,
        WetCloudySunset -> WeatherType.WetCloudy
        SoftRainNoon,
        SoftRainSunset -> WeatherType.SoftRainy
        MidRainNoon,
        MidRainSunset -> WeatherType.MidRainy
        HardRainNoon,
        HardRainSunset -> WeatherType.HardRainy
      }

  /** Extracts [Daytime] from [JsonDataWeatherParametersType]. */
  fun toDaytime(): Daytime =
      when (this) {
        HardRainNoon,
        WetNoon,
        MidRainNoon,
        SoftRainNoon,
        CloudyNoon,
        WetCloudyNoon,
        ClearNoon -> Daytime.Noon
        HardRainSunset,
        SoftRainSunset,
        MidRainSunset,
        WetSunset,
        WetCloudySunset,
        CloudySunset,
        ClearSunset,
        Default -> Daytime.Sunset
      }
}
