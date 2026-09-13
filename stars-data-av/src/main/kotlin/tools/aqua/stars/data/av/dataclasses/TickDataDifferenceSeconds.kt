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

import tools.aqua.stars.core.types.TickDifference

/**
 * Implementation of the [TickDifference] interface for 'seconds' units.
 *
 * @property differenceSeconds Difference in seconds.
 */
data class TickDataDifferenceSeconds(val differenceSeconds: Double) :
    TickDifference<TickDataDifferenceSeconds>() {
  /**
   * Adds a [TickDataDifferenceSeconds] to this [TickDataDifferenceSeconds].
   *
   * @param other The [TickDataDifferenceSeconds] to add.
   * @return A new [TickDataDifferenceSeconds] object.
   */
  override operator fun plus(other: TickDataDifferenceSeconds): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(this.differenceSeconds + other.differenceSeconds)

  /**
   * Adds a [Number] considered as seconds to this [TickDataDifferenceSeconds].
   *
   * @param seconds The [Number] to add.
   * @return A new [TickDataDifferenceSeconds] object.
   */
  operator fun plus(seconds: Number): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(this.differenceSeconds + seconds.toDouble())

  /**
   * Subtracts a [TickDataDifferenceSeconds] from this [TickDataDifferenceSeconds].
   *
   * @param other The [TickDataDifferenceSeconds] to subtract.
   * @return A new [TickDataDifferenceSeconds] object.
   */
  override operator fun minus(other: TickDataDifferenceSeconds): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(this.differenceSeconds - other.differenceSeconds)

  /**
   * Subtracts a [Number] considered as seconds from this [TickDataDifferenceSeconds].
   *
   * @param seconds The [Number] to subtract.
   * @return A new [TickDataDifferenceSeconds] object.
   */
  operator fun minus(seconds: Number): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(this.differenceSeconds - seconds.toDouble())

  /**
   * Compares this object with the specified object for order. Returns zero if this object is equal
   * to the specified [other] object, a negative number if it's less than [other], or a positive
   * number if it's greater than [other].
   */
  override operator fun compareTo(other: TickDataDifferenceSeconds): Int =
      this.differenceSeconds.compareTo(other.differenceSeconds)

  /**
   * Compares this object with the specified [Number] treated as seconds for order. Returns zero if
   * this object is equal to the specified [seconds], a negative number if it's less than [seconds],
   * or a positive number if it's greater than [seconds].
   */
  operator fun compareTo(seconds: Number): Int =
      this.differenceSeconds.compareTo(seconds.toDouble())

  /**
   * Serializes this [TickDataDifferenceSeconds] to a [String].
   *
   * @return A [String] representation of this [TickDataDifferenceSeconds].
   */
  override fun serialize(): String = this.differenceSeconds.toString()

  /**
   * Deserializes a [String] to a [TickDataDifferenceSeconds].
   *
   * @param str The [String] to deserialize.
   * @return A new [TickDataDifferenceSeconds] object.
   */
  override fun deserialize(str: String): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(str.toDouble())

  /**
   * Returns a string representation of this [TickDataDifferenceSeconds].
   *
   * @return A string representation of this [TickDataDifferenceSeconds].
   */
  override fun toString(): String = "${this.differenceSeconds}s"
}
