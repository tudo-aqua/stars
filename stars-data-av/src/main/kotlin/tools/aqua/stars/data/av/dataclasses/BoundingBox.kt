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
 * Data class for bounding boxes of [Actor]s.
 *
 * @property extent Vector from the center of the box to one vertex.
 * @property location: Location of the center of the bounding box.
 * @property rotation: Rotation of the bounding box.
 * @property vertices: Vertices of the bounding box.
 */
data class BoundingBox(
    val extent: Vector3D,
    val location: Location,
    val rotation: Rotation,
    val vertices: List<Location>
)
