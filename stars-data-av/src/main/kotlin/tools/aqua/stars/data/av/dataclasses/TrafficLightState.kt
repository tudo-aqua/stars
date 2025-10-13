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
 * Traffic light states.
 *
 * The integer codes mirror the **CARLA Python API**.
 *
 * @property value CARLA's integer identifier for this state (as serialized in JSON).
 */
enum class TrafficLightState(val value: Int) {
  /** Red light: stop. */
  Red(0),
  /** Yellow/amber: prepare to stop. */
  Yellow(1),
  /** Green: proceed if safe. */
  Green(2),
  /** Signal off / unlit. */
  Off(3),
  /** Unknown or not detected. */
  Unknown(4);

  /** Companion object for the [TrafficLightState] class. */
  companion object {
    /**
     * Returns the [TrafficLightState] that matches the given CARLA integer ID.
     *
     * @param value CARLA's integer identifier (as found in JSON).
     * @return The matching [TrafficLightState].
     * @throws NoSuchElementException if no matching value exists.
     */
    fun getByValue(value: Int): TrafficLightState = entries.first { it.value == value }
  }
}
