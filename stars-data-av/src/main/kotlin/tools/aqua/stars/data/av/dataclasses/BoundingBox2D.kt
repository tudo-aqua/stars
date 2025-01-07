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
 * Data class for 2D bounding boxes of [Actor]s.
 *
 * @property leftFront The left front vertex of the [BoundingBox2D].
 * @property rightFront The right front vertex of the [BoundingBox2D].
 * @property rightBack The right back vertex of the [BoundingBox2D].
 * @property leftBack The left back vertex of the [BoundingBox2D].
 */
data class BoundingBox2D(
    val leftFront: Location2D,
    val rightFront: Location2D,
    val rightBack: Location2D,
    val leftBack: Location2D,
) {

  val vectorLeft = (leftFront - rightFront).normalize()
  val vectorRight = (rightFront - leftFront).normalize()
  val vectorFront = (leftFront - leftBack).normalize()
  val vectorBack = (leftBack - leftFront).normalize()

  /**
   * Returns the vertices of this [BoundingBox2D] in the order leftFront, rightFront, rightBack,
   * leftBack.
   */
  fun getVertices() = listOf(leftFront, rightFront, rightBack, leftBack)

  /** Extends this [BoundingBox2D] to the left by the given amount. */
  fun extendLeft(amount: Double) =
      BoundingBox2D(
          leftFront = Location2D(leftFront + vectorLeft * amount),
          rightFront = rightFront,
          rightBack = rightBack,
          leftBack = Location2D(leftBack + vectorLeft * amount))

  /** Extends this [BoundingBox2D] to the right by the given amount. */
  fun extendRight(amount: Double) =
      BoundingBox2D(
          leftFront = leftFront,
          rightFront = Location2D(rightFront + vectorRight * amount),
          rightBack = Location2D(rightBack + vectorRight * amount),
          leftBack = leftBack)

  /** Extends this [BoundingBox2D] to the front by the given amount. */
  fun extendFront(amount: Double) =
      BoundingBox2D(
          leftFront = Location2D(leftFront + vectorLeft * amount),
          rightFront = Location2D(rightFront + vectorRight * amount),
          rightBack = rightBack,
          leftBack = leftBack)

  /** Extends this [BoundingBox2D] to the back by the given amount. */
  fun extendBack(amount: Double) =
      BoundingBox2D(
          leftFront = leftFront,
          rightFront = rightFront,
          rightBack = Location2D(rightBack + vectorBack * amount),
          leftBack = Location2D(leftBack + vectorLeft * amount))

  /** Extends this [BoundingBox2D] in all directions by the given amount. */
  fun extend(amount: Double) =
      BoundingBox2D(
          leftFront = Location2D(leftFront + vectorLeft * amount + vectorFront * amount),
          rightFront = Location2D(rightFront + vectorRight * amount + vectorFront * amount),
          rightBack = Location2D(rightBack + vectorRight * amount + vectorBack * amount),
          leftBack = Location2D(leftBack + vectorLeft * amount + vectorBack * amount))

  /** Checks if this [BoundingBox2D] collides with another [BoundingBox2D]. */
  fun collidesWith(other: BoundingBox2D): Boolean =
      other.getVertices().any { containsPoint(it) } || getVertices().any { other.containsPoint(it) }

  fun containsPoint(location: Location2D): Boolean {
    val vertices = getVertices()

    for (i in 0 until vertices.size) {
      val vertex = vertices[i]
      val nextVertex = vertices[(i + 1) % vertices.size]
      val edge = nextVertex - vertex
      val pointToVertex = vertex - location

      if (edge.cross(pointToVertex) < 0.0) {
        return false
      }
    }
    return true
  }
}
