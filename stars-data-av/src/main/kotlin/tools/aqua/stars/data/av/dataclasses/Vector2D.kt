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

import kotlin.math.sqrt

/**
 * Data class for 2D vectors.
 *
 * @property x The x ordinate of the [Vector2D].
 * @property y The y ordinate of the [Vector2D].
 */
data class Vector2D(val x: Double, val y: Double) {
  constructor(vector: Vector2D) : this(vector.x, vector.y)

  constructor(location: Location) : this(location.x, location.y)

  /** Addition operator. */
  operator fun plus(other: Vector2D): Vector2D =
      Vector2D(x = this.x + other.x, y = this.y + other.y)

  /** Subtraction operator. */
  operator fun minus(other: Vector2D): Vector2D =
      Vector2D(x = this.x - other.x, y = this.y - other.y)

  /** Negation operator. */
  operator fun unaryMinus(): Vector2D = Vector2D(x = -this.x, y = -this.y)

  /** Multiplication with scalar operator. */
  operator fun times(scalar: Number): Vector2D =
      Vector2D(x = this.x * scalar.toDouble(), y = this.y * scalar.toDouble())

  /** Division with scalar operator. */
  operator fun div(scalar: Number): Vector2D =
      Vector2D(x = this.x / scalar.toDouble(), y = this.y / scalar.toDouble())

  /** Dot product with another [Vector2D]. */
  fun dot(other: Vector2D): Double = x * other.x + y * other.y

  /** Cross (Vector) product with another [Vector2D] (in 2D resulting in a scalar determinant). */
  fun cross(other: Vector2D): Double = x * other.y - y * other.x

  /** Length of the vector. */
  fun magnitude(): Double = sqrt(x * x + y * y)

  /** Normalized vector with the same direction and length 1. */
  fun normalize(): Vector2D = magnitude().let { Vector2D(x / it, y / it) }
}
