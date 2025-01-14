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
import tools.aqua.stars.data.av.dataclasses.WeatherParameters

/**
 * Json object for weather parameters.
 *
 * @property type The [JsonDataWeatherParametersType] of the weather parameter.
 * @property cloudiness The cloudiness.
 * @property precipitation The precipitation.
 * @property precipitationDeposits The deposits of precipitation.
 * @property windIntensity The intensity of the wind.
 * @property sunAzimuthAngle The azimuth of the sun.
 * @property sunAltitudeAngle The altitude of the sun.
 * @property fogDensity The density of the fog.
 * @property fogDistance The view distance due to fog.
 * @property wetness The wetness.
 * @property fogFalloff The fog falloff.
 * @property scatteringIntensity The intensity of light scattering.
 * @property mieScatteringScale The scale of mie scattering.
 * @property rayleighScatteringScale The scale of rayleigh scattering.
 */
@Serializable
data class JSonDataWeatherParameters(
    @SerialName("type") val type: JsonDataWeatherParametersType,
    @SerialName("cloudiness") val cloudiness: Double,
    @SerialName("precipitation") val precipitation: Double,
    @SerialName("precipitation_deposits") val precipitationDeposits: Double,
    @SerialName("wind_intensity") val windIntensity: Double,
    @SerialName("sun_azimuth_angle") val sunAzimuthAngle: Double,
    @SerialName("sun_altitude_angle") val sunAltitudeAngle: Double,
    @SerialName("fog_density") val fogDensity: Double,
    @SerialName("fog_distance") val fogDistance: Double,
    @SerialName("wetness") val wetness: Double,
    @SerialName("fog_falloff") val fogFalloff: Double,
    @SerialName("scattering_intensity") val scatteringIntensity: Double,
    @SerialName("mie_scattering_scale") val mieScatteringScale: Double,
    @SerialName("rayleigh_scattering_scale") val rayleighScatteringScale: Double,
) {

  /** Converts [JSonDataWeatherParameters] to [WeatherParameters]. */
  fun toWeatherParameters(): WeatherParameters =
      WeatherParameters(
          type = this.type.toWeatherType(),
          cloudiness = this.cloudiness,
          precipitation = this.precipitation,
          precipitationDeposits = this.precipitationDeposits,
          windIntensity = this.windIntensity,
          sunAzimuthAngle = this.sunAzimuthAngle,
          sunAltitudeAngle = this.sunAltitudeAngle,
          fogDensity = this.fogDensity,
          fogDistance = this.fogDistance,
          wetness = this.wetness,
          fogFalloff = this.fogFalloff,
          scatteringIntensity = this.scatteringIntensity,
          mieScatteringScale = this.mieScatteringScale,
          rayleighScatteringScale = this.rayleighScatteringScale)
}
