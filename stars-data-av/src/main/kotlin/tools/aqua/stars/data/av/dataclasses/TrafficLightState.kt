/*
 * Copyright 2023-2024 The STARS Project Authors
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
 * Enum for traffic light states.
 *
 * @property value Internal json value.
 */
enum class TrafficLightState(val value: Int) {
  Red(0),
  Yellow(1),
  Green(2),
  Off(3),
  Unknown(4);

  companion object {
    /** Retrieves [TrafficLightState] by internal value. */
    fun getByValue(value: Int): TrafficLightState = entries.first { it.value == value }
  }
}
