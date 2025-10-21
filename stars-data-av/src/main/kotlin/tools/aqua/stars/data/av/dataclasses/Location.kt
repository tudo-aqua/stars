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

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Data class for 3D locations.
 *
 * @property x The x ordinate of the [Location].
 * @property y The y ordinate of the [Location].
 * @property z The z ordinate of the [Location].
 * @see Vector3D
 */
data class Location(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0) {
  /** Converts this [Location] to a [Vector3D]. */
  fun toVector3D(): Vector3D = Vector3D(x, y, z)

  /** Addition operator. */
  operator fun plus(other: Location): Vector3D =
      Vector3D(x = this.x + other.x, y = this.y + other.y, z = this.z + other.z)

  /** Addition operator. */
  operator fun plus(other: Vector3D): Location =
      Location(x = this.x + other.x, y = this.y + other.y, z = this.z + other.z)

  /** Subtraction operator. */
  operator fun minus(other: Location): Vector3D =
      Vector3D(x = this.x - other.x, y = this.y - other.y, z = this.z - other.z)

  /** Subtraction operator. */
  operator fun minus(other: Vector3D): Location =
      Location(x = this.x - other.x, y = this.y - other.y, z = this.z - other.z)

  /** Companion object for [Location]. */
  companion object {
    /**
     * Calculates the Euclidean distance between two locations, i.e., the square root of the sum of
     * the squared ordinates.
     */
    fun euclideanDistance(loc1: Location, loc2: Location): Double =
        sqrt((loc1.x - loc2.x).pow(2) + (loc1.y - loc2.y).pow(2) + (loc1.z - loc2.z).pow(2))
  }
}
