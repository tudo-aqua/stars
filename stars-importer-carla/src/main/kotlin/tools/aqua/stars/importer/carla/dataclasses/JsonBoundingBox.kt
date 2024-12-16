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

package tools.aqua.stars.importer.carla.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tools.aqua.stars.data.av.dataclasses.BoundingBox

/**
 * Json object for bounding boxes.
 *
 * @property extent: Vector from the center of the box to one vertex.
 * @property location: Location of the center of the bounding box.
 * @property rotation: Rotation of the bounding box.
 * @property vertices: Vertices of the bounding box.
 */
@Serializable
@SerialName("Actor")
data class JsonBoundingBox(
    @SerialName("extent") val extent: JsonVector3D,
    @SerialName("location") val location: JsonLocation,
    @SerialName("rotation") val rotation: JsonRotation,
    @SerialName("vertices") val vertices: List<JsonLocation>,
) {
  /** Converts [JsonBoundingBox] to [BoundingBox]. */
  fun toBoundingBox(): BoundingBox =
      BoundingBox(
          bottomLeftBack = vertices[0].toLocation(),
          topLeftBack = vertices[1].toLocation(),
          bottomRightBack = vertices[2].toLocation(),
          topRightBack = vertices[3].toLocation(),
          bottomLeftFront = vertices[4].toLocation(),
          topLeftFront = vertices[5].toLocation(),
          bottomRightFront = vertices[6].toLocation(),
          topRightFront = vertices[7].toLocation())
}
