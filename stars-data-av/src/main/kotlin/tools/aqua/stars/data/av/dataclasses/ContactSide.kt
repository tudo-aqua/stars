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

/** Side of a reference [Lane], in driving direction. */
enum class ContactSide {
  /** Left side of the reference lane. */
  LEFT,
  /** Right side of the reference lane. */
  RIGHT;

  /** Companion object for the [ContactSide] class. */
  companion object {
    /**
     * Returns the [ContactSide] matching the given CARLA string (`"Left"` / `"Right"`).
     *
     * @param value CARLA's string identifier (as found in JSON).
     * @return The matching [ContactSide].
     * @throws NoSuchElementException if no matching value exists.
     */
    fun getByValue(value: String): ContactSide = entries.first { it.name.equals(value, true) }
  }
}
