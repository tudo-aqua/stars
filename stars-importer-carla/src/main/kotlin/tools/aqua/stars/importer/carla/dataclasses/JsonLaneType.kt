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
 * Json object for lane types.
 *
 * @property value Json value.
 */
@Suppress("unused")
@Serializable
enum class JsonLaneType(val value: Int) {
  @SerialName("-2") Any(-2),
  @SerialName("512") Bidirectional(512),
  @SerialName("16") Biking(16),
  @SerialName("64") Border(64),
  @SerialName("2") Driving(2),
  @SerialName("131072") Entry(131_072),
  @SerialName("262144") Exit(262_144),
  @SerialName("1024") Median(1024),
  @SerialName("1") NONE(1),
  @SerialName("524288") OffRamp(524_288),
  @SerialName("1048576") OnRamp(1_048_576),
  @SerialName("256") Parking(256),
  @SerialName("65536") Rail(65_536),
  @SerialName("128") Restricted(128),
  @SerialName("16384") RoadWorks(16_384),
  @SerialName("8") Shoulder(8),
  @SerialName("32") Sidewalk(32),
  @SerialName("2048") Special1(2048),
  @SerialName("4096") Special2(4096),
  @SerialName("8192") Special3(8192),
  @SerialName("4") Stop(4),
  @SerialName("32768") Tram(32_768),
}
