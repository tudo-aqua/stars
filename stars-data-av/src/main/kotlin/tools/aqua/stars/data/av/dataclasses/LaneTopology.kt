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

package tools.aqua.stars.data.av.dataclasses

/**
 * Directional relationship between a [Lane] and its [Lane.overlappingLanes].
 *
 * @property value The raw string as serialized by the CARLA exporter.
 */
enum class LaneTopology(val value: String) {
  /** No physical overlap detected. */
  None(""),
  /** This lane's centerline converges into an overlapping lane toward its end. */
  Merging("Merging"),
  /** This lane's centerline splits away from an overlapping lane after its start. */
  Diverging("Diverging"),
  /** Both merging and diverging, via different overlap partners. */
  MergingAndDiverging("Merging & Diverging"),
  /** Physical overlap without a clear directional trend. */
  Overlapping("Overlapping");

  /** Companion object for the [LaneTopology] class. */
  companion object {
    /**
     * Returns the [LaneTopology] matching the given CARLA string.
     *
     * @param value The raw string as serialized by the CARLA exporter.
     * @return The matching [LaneTopology], or [None] if the string is empty or unknown.
     */
    fun getByValue(value: String): LaneTopology = entries.firstOrNull { it.value == value } ?: None
  }
}
