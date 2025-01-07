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
 * Data class for landmarks.
 *
 * @property id The identifier of the landmark.
 * @property name The name of the landmark.
 * @property distance The distance.
 * @property s S.
 * @property country The country.
 * @property type The [LandmarkType] of the landmark.
 * @property value The value.
 * @property unit The unit.
 * @property text The text.
 * @property location The [Location] of the landmark.
 * @property rotation The [Rotation] of the landmark.
 */
data class Landmark(
    val id: Int,
    val name: String,
    val distance: Double,
    val s: Double,
    val country: String,
    val type: LandmarkType,
    val value: Double,
    val unit: String,
    val text: String,
    val location: Location,
    val rotation: Rotation
)
