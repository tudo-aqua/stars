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
 * JSON enum for lane marking types.
 *
 * Integer IDs mirror the CARLA Python API (`carla.LaneMarkingType`).
 *
 * @property value Numeric code from CARLA's JSON. Also provided via [SerialName] for stable I/O.
 */
@Suppress("unused")
@Serializable
enum class JsonLaneMarkingType(val value: Int) {
  /** Unspecified / other marking. */
  @SerialName("0") Other(0),
  /** Broken (dashed) line. */
  @SerialName("1") Broken(1),
  /** Solid line. */
  @SerialName("2") Solid(2),
  /** Two parallel solid lines. */
  @SerialName("3") SolidSolid(3),
  /** Inner solid, outer broken line. */
  @SerialName("4") SolidBroken(4),
  /** Inner broken, outer solid line. */
  @SerialName("5") BrokenSolid(5),
  /** Two parallel broken lines. */
  @SerialName("6") BrokenBroken(6),
  /** Botts' dots. */
  @SerialName("7") BottsDots(7),
  /** Grass edge. */
  @SerialName("8") Grass(8),
  /** Curb. */
  @SerialName("9") Curb(9),
  /** No marking on that side. */
  @SerialName("10") NONE(10),
}
