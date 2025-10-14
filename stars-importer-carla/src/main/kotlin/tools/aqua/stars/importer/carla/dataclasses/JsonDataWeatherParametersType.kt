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
 * JSON enum for CARLA weather presets. [Default] is equal to [ClearSunset].
 *
 * The numeric codes and names mirror the CARLA Python API.
 *
 * @property value Numeric code as it appears in CARLA JSON. Also duplicated in [SerialName] for
 *   stable I/O.
 */
@Serializable
enum class JsonDataWeatherParametersType(val value: Int) {
  /** Default preset (equivalent to [ClearSunset]). */
  @SerialName("0") Default(0),

  /** Clear sky at noon. */
  @SerialName("1") ClearNoon(1),

  /** Overcast/cloudy at noon. */
  @SerialName("2") CloudyNoon(2),

  /** Wet ground (no rainfall) at noon. */
  @SerialName("3") WetNoon(3),

  /** Wet ground with cloud cover at noon. */
  @SerialName("4") WetCloudyNoon(4),

  /** Light/soft rain at noon. */
  @SerialName("5") SoftRainNoon(5),

  /** Moderate rain at noon. */
  @SerialName("6") MidRainNoon(6),

  /** Heavy rain at noon. */
  @SerialName("7") HardRainNoon(7),

  /** Clear sky at sunset. */
  @SerialName("8") ClearSunset(8),

  /** Overcast/cloudy at sunset. */
  @SerialName("9") CloudySunset(9),

  /** Wet ground (no rainfall) at sunset. */
  @SerialName("10") WetSunset(10),

  /** Wet ground with cloud cover at sunset. */
  @SerialName("11") WetCloudySunset(11),

  /** Light/soft rain at sunset. */
  @SerialName("12") SoftRainSunset(12),

  /** Moderate rain at sunset. */
  @SerialName("13") MidRainSunset(13),

  /** Heavy rain at sunset. */
  @SerialName("14") HardRainSunset(14),
}
