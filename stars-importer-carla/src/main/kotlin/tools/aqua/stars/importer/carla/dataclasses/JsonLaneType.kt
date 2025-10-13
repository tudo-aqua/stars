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
 * JSON enum for lane classification.
 *
 * Codes mirror the CARLA Python API lane type bitmasks/IDs.
 *
 * @property value Numeric code used in CARLA JSON (also duplicated in [SerialName]).
 */
@Suppress("unused")
@Serializable
enum class JsonLaneType(val value: Int) {
  /** Matches any lane type (wildcard). */
  @SerialName("-2") Any(-2),
  /** Two-way lane allowing traffic in both directions. */
  @SerialName("512") Bidirectional(512),
  /** Bicycle lane. */
  @SerialName("16") Biking(16),
  /** Border/edge marking outside the drivable area. */
  @SerialName("64") Border(64),
  /** Regular drivable vehicle lane. */
  @SerialName("2") Driving(2),
  /** Entry lane to a controlled area (e.g., toll/gate). */
  @SerialName("131072") Entry(131_072),
  /** Exit lane from a controlled area. */
  @SerialName("262144") Exit(262_144),
  /** Median/separator between opposing directions. */
  @SerialName("1024") Median(1024),
  /** No lane (placeholder/undefined). */
  @SerialName("1") NONE(1),
  /** Off-ramp leaving a highway. */
  @SerialName("524288") OffRamp(524_288),
  /** On-ramp entering a highway. */
  @SerialName("1048576") OnRamp(1_048_576),
  /** Parking lane or marked parking area. */
  @SerialName("256") Parking(256),
  /** Rail track. */
  @SerialName("65536") Rail(65_536),
  /** Restricted lane (e.g., bus/taxi/HOV depending on map rules). */
  @SerialName("128") Restricted(128),
  /** Road works/construction lane. */
  @SerialName("16384") RoadWorks(16_384),
  /** Paved shoulder area. */
  @SerialName("8") Shoulder(8),
  /** Sidewalk/pedestrian path. */
  @SerialName("32") Sidewalk(32),
  /** Map-specific special lane #1. */
  @SerialName("2048") Special1(2048),
  /** Map-specific special lane #2. */
  @SerialName("4096") Special2(4096),
  /** Map-specific special lane #3. */
  @SerialName("8192") Special3(8192),
  /** Stop line / stop area lane. */
  @SerialName("4") Stop(4),
  /** Tram track. */
  @SerialName("32768") Tram(32_768),
}
