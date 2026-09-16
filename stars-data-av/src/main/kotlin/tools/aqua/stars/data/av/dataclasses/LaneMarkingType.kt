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
 * Type of a [LaneMarking].
 *
 * The integer codes mirror the **CARLA Python API** (`carla.LaneMarkingType`).
 *
 * @property value CARLA's integer identifier for this marking type (as serialized in JSON).
 */
enum class LaneMarkingType(val value: Int) {
  /** Unspecified / other marking. */
  Other(0),
  /** Broken (dashed) line. */
  Broken(1),
  /** Solid line. */
  Solid(2),
  /** Two parallel solid lines. */
  SolidSolid(3),
  /** Inner solid, outer broken line. */
  SolidBroken(4),
  /** Inner broken, outer solid line. */
  BrokenSolid(5),
  /** Two parallel broken lines. */
  BrokenBroken(6),
  /** Botts' dots. */
  BottsDots(7),
  /** Grass edge. */
  Grass(8),
  /** Curb. */
  Curb(9),
  /** No marking on that side. */
  NONE(10);

  /** Companion object for the [LaneMarkingType] class. */
  companion object {
    /**
     * Returns the [LaneMarkingType] that matches the given CARLA integer ID.
     *
     * @param value CARLA's integer identifier (as found in JSON).
     * @return The matching [LaneMarkingType].
     * @throws NoSuchElementException if no matching value exists.
     */
    fun getByValue(value: Int): LaneMarkingType = entries.first { it.value == value }
  }
}
