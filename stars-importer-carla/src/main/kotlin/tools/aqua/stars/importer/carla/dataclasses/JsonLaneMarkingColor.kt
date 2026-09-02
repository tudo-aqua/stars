/*
 * Copyright 2026 The STARS Project Authors
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
 * JSON enum for lane marking colors.
 *
 * Integer IDs mirror the CARLA Python API (`carla.LaneMarkingColor`). CARLA's `Standard` and
 * `White` share the code `0`; this enum keeps the single canonical [Standard] entry.
 *
 * @property value Numeric code from CARLA's JSON. Also provided via [SerialName] for stable I/O.
 */
@Suppress("unused")
@Serializable
enum class JsonLaneMarkingColor(val value: Int) {
  /** Standard marking color (white). */
  @SerialName("0") Standard(0),
  /** Blue marking. */
  @SerialName("1") Blue(1),
  /** Green marking. */
  @SerialName("2") Green(2),
  /** Red marking. */
  @SerialName("3") Red(3),
  /** Yellow marking. */
  @SerialName("4") Yellow(4),
  /** Any other marking color. */
  @SerialName("5") Other(5),
}
