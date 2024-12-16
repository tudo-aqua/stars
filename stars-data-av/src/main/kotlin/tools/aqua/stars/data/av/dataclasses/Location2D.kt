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

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Data class for 2D locations.
 *
 * @property x The x ordinate.
 * @property y The y ordinate.
 * @see Vector2D
 */
data class Location2D(val x: Double, val y: Double) {

  constructor(location: Vector2D) : this(location.x, location.y)

  /** Converts this [Location2D] to a 2D [Vector2D]. */
  fun toVector2D() = Vector2D(x, y)

  /** Addition operator. */
  operator fun plus(other: Location2D): Vector2D =
      Vector2D(x = this.x + other.x, y = this.y + other.y)

  /** Addition operator. */
  operator fun plus(other: Vector2D): Vector2D =
      Vector2D(x = this.x + other.x, y = this.y + other.y)

  /** Subtraction operator. */
  operator fun minus(other: Location2D): Vector2D =
      Vector2D(x = this.x - other.x, y = this.y - other.y)

  /** Subtraction operator. */
  operator fun minus(other: Vector2D): Vector2D =
      Vector2D(x = this.x - other.x, y = this.y - other.y)

  companion object {
    /**
     * Calculates the Euclidean distance between two locations, i.e., the square root of the sum of
     * the squared ordinates.
     */
    fun euclideanDistance(loc1: Location2D, loc2: Location2D): Double =
        sqrt((loc1.x - loc2.x).pow(2) + (loc1.y - loc2.y).pow(2))
  }
}
