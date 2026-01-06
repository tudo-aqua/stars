/*
 * Copyright 2023-2026 The STARS Project Authors
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
 * @see Vector2D
 */
data class BoundingBox2D(
    val leftFront: Location2D,
    val rightFront: Location2D,
    val rightBack: Location2D,
    val leftBack: Location2D,
) {

  /** Normalized [Vector2D] to the left of the [BoundingBox2D]. */
  val vectorLeft: Vector2D = (leftFront.toVector2D() - rightFront.toVector2D()).normalize()

  /** Normalized [Vector2D] to the right of the [BoundingBox2D]. */
  val vectorRight: Vector2D = (rightFront.toVector2D() - leftFront.toVector2D()).normalize()

  /** Normalized [Vector2D] to the front of the [BoundingBox2D]. */
  val vectorFront: Vector2D = (leftFront.toVector2D() - leftBack.toVector2D()).normalize()

  /** Normalized [Vector2D] to the back of the [BoundingBox2D]. */
  val vectorBack: Vector2D = (leftBack.toVector2D() - leftFront.toVector2D()).normalize()

  /**
   * Returns the vertices of this [BoundingBox2D] in the order leftFront, rightFront, rightBack,
   * leftBack.
   */
  fun getVertices(): List<Location2D> = listOf(leftFront, rightFront, rightBack, leftBack)

  /**
   * Extends this [BoundingBox2D] to the left by the given [amount].
   *
   * @param amount The amount to extend the bounding box to the left.
   * @return A new [BoundingBox2D] that is extended to the left by the given [amount].
   */
  fun extendLeft(amount: Double): BoundingBox2D =
      BoundingBox2D(
          leftFront = leftFront + vectorLeft * amount,
          rightFront = rightFront,
          rightBack = rightBack,
          leftBack = leftBack + vectorLeft * amount,
      )

  /**
   * Extends this [BoundingBox2D] to the right by the given [amount].
   *
   * @param amount The amount to extend the bounding box to the right.
   * @return A new [BoundingBox2D] that is extended to the right by the given [amount].
   */
  fun extendRight(amount: Double): BoundingBox2D =
      BoundingBox2D(
          leftFront = leftFront,
          rightFront = rightFront + vectorRight * amount,
          rightBack = rightBack + vectorRight * amount,
          leftBack = leftBack,
      )

  /**
   * Extends this [BoundingBox2D] to the front by the given [amount].
   *
   * @param amount The amount to extend the bounding box to the front.
   * @return A new [BoundingBox2D] that is extended to the front by the given [amount].
   */
  fun extendFront(amount: Double): BoundingBox2D =
      BoundingBox2D(
          leftFront = leftFront + vectorLeft * amount,
          rightFront = rightFront + vectorRight * amount,
          rightBack = rightBack,
          leftBack = leftBack,
      )

  /**
   * Extends this [BoundingBox2D] to the back by the given [amount].
   *
   * @param amount The amount to extend the bounding box to the back.
   * @return A new [BoundingBox2D] that is extended to the back by the given [amount].
   */
  fun extendBack(amount: Double): BoundingBox2D =
      BoundingBox2D(
          leftFront = leftFront,
          rightFront = rightFront,
          rightBack = rightBack + vectorBack * amount,
          leftBack = leftBack + vectorLeft * amount,
      )

  /**
   * Extends this [BoundingBox2D] in all directions by the given amount.
   *
   * @param amount The amount to extend the bounding box in all directions.
   * @return A new [BoundingBox2D] that is extended in all directions by the given [amount].
   */
  fun extend(amount: Double): BoundingBox2D =
      BoundingBox2D(
          leftFront = leftFront + vectorLeft * amount + vectorFront * amount,
          rightFront = rightFront + vectorRight * amount + vectorFront * amount,
          rightBack = rightBack + vectorRight * amount + vectorBack * amount,
          leftBack = leftBack + vectorLeft * amount + vectorBack * amount,
      )

  /**
   * Checks if this [BoundingBox2D] contains a given [Location2D]. Points on the edge or on a vertex
   * are considered inside.
   */
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

  /**
   * Checks if this [BoundingBox2D] collides with another [BoundingBox2D]. Touching at an edge or
   * point is considered a collision.
   */
  fun collidesWith(other: BoundingBox2D): Boolean {
    listOf(vectorLeft, vectorFront, other.vectorLeft, other.vectorFront).forEach { axis ->
      val vertices1 = this.getVertices().map { axis.dot(it.toVector2D()) }
      val vertices2 = other.getVertices().map { axis.dot(it.toVector2D()) }

      if (vertices1.max() < vertices2.min() || vertices2.max() < vertices1.min()) {
        return false
      }
    }
    return true
  }
}
