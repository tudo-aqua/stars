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

package tools.aqua.stars.data.av.dataclasses

/**
 * Data class for weather parameters.
 *
 * @property type The [WeatherType] of the weather parameter.
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
data class WeatherParameters(
    val type: WeatherType = WeatherType.Clear,
    val dustStorm: Double = 0.0,
    val cloudiness: Double = 0.0,
    val precipitation: Double = 0.0,
    val precipitationDeposits: Double = 0.0,
    val windIntensity: Double = 0.0,
    val sunAzimuthAngle: Double = 0.0,
    val sunAltitudeAngle: Double = 0.0,
    val fogDensity: Double = 0.0,
    val fogDistance: Double = 0.0,
    val wetness: Double = 0.0,
    val fogFalloff: Double = 0.0,
    val scatteringIntensity: Double = 0.0,
    val mieScatteringScale: Double = 0.0,
    val rayleighScatteringScale: Double = 0.0,
)
