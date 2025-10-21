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
 * Data class for bounding boxes of [Actor]s.
 *
 * @property bottomLeftFront The bottom left front vertex of the [BoundingBox].
 * @property bottomRightFront The bottom right front vertex of the [BoundingBox].
 * @property bottomRightBack The bottom right back vertex of the [BoundingBox].
 * @property bottomLeftBack The bottom left back vertex of the [BoundingBox].
 * @property topLeftFront The top left front vertex of the [BoundingBox].
 * @property topRightFront The top right front vertex of the [BoundingBox].
 * @property topRightBack The top right back vertex of the [BoundingBox].
 * @property topLeftBack The top left back vertex of the [BoundingBox].
 * @see Vector3D
 */
data class BoundingBox(
    val bottomLeftFront: Location = Location(),
    val bottomRightFront: Location = Location(),
    val bottomRightBack: Location = Location(),
    val bottomLeftBack: Location = Location(),
    val topLeftFront: Location = Location(),
    val topRightFront: Location = Location(),
    val topRightBack: Location = Location(),
    val topLeftBack: Location = Location(),
) {

  /** Converts this [BoundingBox] to a 2D [BoundingBox2D]. */
  fun toBoundingBox2D(): BoundingBox2D =
      BoundingBox2D(
          leftFront = Location2D(x = bottomLeftFront.x, y = bottomLeftFront.y),
          rightFront = Location2D(x = bottomRightFront.x, y = bottomRightFront.y),
          rightBack = Location2D(x = bottomRightBack.x, y = bottomRightBack.y),
          leftBack = Location2D(x = bottomLeftBack.x, y = bottomLeftBack.y),
      )
}
