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
 * Color of a [LaneMarking].
 *
 * The integer codes mirror the **CARLA Python API** (`carla.LaneMarkingColor`). CARLA's `Standard`
 * and `White` share the code `0`; this enum keeps the single canonical [Standard] entry.
 *
 * @property value CARLA's integer identifier for this marking color (as serialized in JSON).
 */
enum class LaneMarkingColor(val value: Int) {
  /** Standard marking color (white). */
  Standard(0),
  /** Blue marking. */
  Blue(1),
  /** Green marking. */
  Green(2),
  /** Red marking. */
  Red(3),
  /** Yellow marking. */
  Yellow(4),
  /** Any other marking color. */
  Other(5);

  /** Companion object for the [LaneMarkingColor] class. */
  companion object {
    /**
     * Returns the [LaneMarkingColor] that matches the given CARLA integer ID.
     *
     * @param value CARLA's integer identifier (as found in JSON).
     * @return The matching [LaneMarkingColor].
     * @throws NoSuchElementException if no matching value exists.
     */
    fun getByValue(value: Int): LaneMarkingColor = entries.first { it.value == value }
  }
}
