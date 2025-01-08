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
 * Data class for 3D vector.
 *
 * @property x The x ordinate.
 * @property y The y ordinate.
 * @property z The z ordinate.
 */
data class Vector3D(val x: Double, val y: Double, val z: Double) {
  constructor(vector: Vector3D) : this(vector.x, vector.y, vector.z)

  constructor(location: Location) : this(location.x, location.y, location.z)

  /** Addition operator. */
  operator fun plus(other: Vector3D): Vector3D =
      Vector3D(x = this.x + other.x, y = this.y + other.y, z = this.z + other.z)

  /** Subtraction operator. */
  operator fun minus(other: Vector3D): Vector3D =
      Vector3D(x = this.x - other.x, y = this.y - other.y, z = this.z - other.z)

  /** Negation operator. */
  operator fun unaryMinus(): Vector3D = Vector3D(x = -this.x, y = -this.y, z = -this.z)

  /** Multiplication with scalar operator. */
  operator fun times(scalar: Number): Vector3D =
      Vector3D(
          x = this.x * scalar.toDouble(),
          y = this.y * scalar.toDouble(),
          z = this.z * scalar.toDouble())

  /** Division with scalar operator. */
  operator fun div(scalar: Number): Vector3D =
      Vector3D(
          x = this.x / scalar.toDouble(),
          y = this.y / scalar.toDouble(),
          z = this.z / scalar.toDouble())

  fun dot(other: Vector3D): Double = x * other.x + y * other.y + z * other.z

  fun magnitude(): Double = sqrt(x * x + y * y + z * z)

  fun normalize(): Vector3D {
    val mag = magnitude()
    return Vector3D(x / mag, y / mag, z / mag)
  }
}
