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
 * Data class for lane types.
 *
 * @property value Internal json value.
 */
enum class LaneType(val value: Int) {
  Any(-2),
  Bidirectional(512),
  Biking(16),
  Border(64),
  Driving(2),
  Entry(131_072),
  Exit(262_144),
  Median(1024),
  NONE(1),
  OffRamp(524_288),
  OnRamp(1_048_576),
  Parking(256),
  Rail(65_536),
  Restricted(128),
  RoadWorks(16_384),
  Shoulder(8),
  Sidewalk(32),
  Special1(2048),
  Special2(4096),
  Special3(8192),
  Stop(4),
  Tram(32_768);

  companion object {
    /** Retrieves [LaneType] by internal value. */
    fun getByValue(value: Int): LaneType = entries.first { it.value == value }
  }
}
