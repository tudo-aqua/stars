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
 */
data class BoundingBox(
    val bottomLeftFront: Location,
    val bottomRightFront: Location,
    val bottomRightBack: Location,
    val bottomLeftBack: Location,
    val topLeftFront: Location,
    val topRightFront: Location,
    val topRightBack: Location,
    val topLeftBack: Location,
) {

  /** Converts this [BoundingBox] to a 2D [BoundingBox2D]. */
  fun toBoundingBox2D() =
      BoundingBox2D(
          leftFront = Location2D(x = bottomLeftFront.x, y = bottomLeftFront.y),
          rightFront = Location2D(x = bottomRightFront.x, y = bottomRightFront.y),
          rightBack = Location2D(x = bottomRightBack.x, y = bottomRightBack.y),
          leftBack = Location2D(x = bottomLeftBack.x, y = bottomLeftBack.y),
      )
}
