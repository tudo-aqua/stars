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
 * JSON enum for the orientation of a landmark relative to lane direction.
 *
 * The numeric codes mirror the **CARLA Python API**.
 *
 * @property value Numeric code as present in CARLA JSON. Also encoded in [SerialName].
 */
@Suppress("unused")
@Serializable
enum class JsonLandmarkOrientation(val value: Int) {
  /** Oriented along the positive (forward) lane direction. */
  @SerialName("0") Positive(0),

  /** Oriented along the negative (opposite) lane direction. */
  @SerialName("1") Negative(1),

  /** Applies to both directions. */
  @SerialName("2") Both(2),
}
