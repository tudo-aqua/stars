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
 * Lane type classification.
 *
 * The integer codes are aligned with the **CARLA Python API**.
 *
 * @property value CARLA's integer identifier for this lane type (as serialized in JSON).
 */
enum class LaneType(val value: Int) {
  /** Matches any lane type (wildcard). */
  Any(-2),
  /** Two-way lane allowing traffic in both directions. */
  Bidirectional(512),
  /** Bicycle lane. */
  Biking(16),
  /** Border / edge marking outside the drivable area. */
  Border(64),
  /** Regular drivable vehicle lane. */
  Driving(2),
  /** Entry lane to a controlled area (e.g., toll/gate). */
  Entry(131_072),
  /** Exit lane from a controlled area. */
  Exit(262_144),
  /** Median / separator between opposing directions. */
  Median(1024),
  /** No lane (placeholder / undefined). */
  NONE(1),
  /** Off-ramp lane leaving a highway. */
  OffRamp(524_288),
  /** On-ramp lane entering a highway. */
  OnRamp(1_048_576),
  /** Parking lane or marked parking area. */
  Parking(256),
  /** Rail track. */
  Rail(65_536),
  /** Restricted lane (e.g., bus/taxi/HOV depending on map rules). */
  Restricted(128),
  /** Road works / construction lane. */
  RoadWorks(16_384),
  /** Shoulder area. */
  Shoulder(8),
  /** Sidewalk. */
  Sidewalk(32),
  /** Map-specific special lane #1. */
  Special1(2048),
  /** Map-specific special lane #2. */
  Special2(4096),
  /** Map-specific special lane #3. */
  Special3(8192),
  /** Stop line / stop area lane. */
  Stop(4),
  /** Tram track. */
  Tram(32_768);

  /** Companion object for the [LaneType] class. */
  companion object {
    /**
     * Returns the [LaneType] that matches the given CARLA integer ID.
     *
     * @param value CARLA's integer identifier (as found in JSON).
     * @return The matching [LaneType].
     * @throws NoSuchElementException if no matching value exists.
     */
    fun getByValue(value: Int): LaneType = entries.first { it.value == value }
  }
}
