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

import tools.aqua.stars.core.types.TickUnit

/**
 * Implementation of the [TickUnit] interface for 'seconds' units.
 *
 * @property tickSeconds Current tick value in seconds.
 */
data class TickDataUnitSeconds(val tickSeconds: Double) :
    TickUnit<TickDataUnitSeconds, TickDataDifferenceSeconds>() {
  /**
   * Adds a [TickDataDifferenceSeconds] to this [TickDataUnitSeconds].
   *
   * @param other The [TickDataDifferenceSeconds] to add.
   * @return A new [TickDataUnitSeconds] object.
   */
  override operator fun plus(other: TickDataDifferenceSeconds): TickDataUnitSeconds =
      TickDataUnitSeconds(this.tickSeconds + other.differenceSeconds)

  /**
   * Adds a [Number] considered as seconds to this [TickDataUnitSeconds].
   *
   * @param seconds The [Number] to add.
   * @return A new [TickDataUnitSeconds] object.
   */
  operator fun plus(seconds: Number): TickDataUnitSeconds =
      TickDataUnitSeconds(this.tickSeconds + seconds.toDouble())

  /**
   * Subtracts a [TickDataDifferenceSeconds] from this [TickDataUnitSeconds].
   *
   * @param other The [TickDataDifferenceSeconds] to subtract.
   * @return A new [TickDataUnitSeconds] object.
   */
  override operator fun minus(other: TickDataDifferenceSeconds): TickDataUnitSeconds =
      TickDataUnitSeconds(this.tickSeconds - other.differenceSeconds)

  /**
   * Subtracts a [TickDataUnitSeconds] from this [TickDataUnitSeconds].
   *
   * @param other The [TickDataUnitSeconds] to subtract.
   * @return A new [TickDataDifferenceSeconds] object.
   */
  override operator fun minus(other: TickDataUnitSeconds): TickDataDifferenceSeconds =
      TickDataDifferenceSeconds(this.tickSeconds - other.tickSeconds)

  /**
   * Subtracts a [Number] considered as seconds from this [TickDataUnitSeconds].
   *
   * @param seconds The [Number] to subtract.
   * @return A new [TickDataUnitSeconds] object.
   */
  operator fun minus(seconds: Number): TickDataUnitSeconds =
      TickDataUnitSeconds(this.tickSeconds - seconds.toDouble())

  /**
   * Compares this object with the specified object for order. Returns zero if this object is equal
   * to the specified [other] object, a negative number if it's less than [other], or a positive
   * number if it's greater than [other].
   */
  override operator fun compareTo(other: TickDataUnitSeconds): Int =
      this.tickSeconds.compareTo(other.tickSeconds)

  /**
   * Compares this object with the specified [Number] treated as seconds for order. Returns zero if
   * this object is equal to the specified [seconds], a negative number if it's less than [seconds],
   * or a positive number if it's greater than [seconds].
   */
  operator fun compareTo(seconds: Number): Int = this.tickSeconds.compareTo(seconds.toDouble())

  /**
   * Serializes this [TickDataUnitSeconds] to a [String].
   *
   * @return A [String] representation of this [TickDataUnitSeconds].
   */
  override fun serialize(): String = this.tickSeconds.toString()

  /**
   * Deserializes a [String] to a [TickDataUnitSeconds].
   *
   * @param str The [String] to deserialize.
   * @return A new [TickDataUnitSeconds] object.
   */
  override fun deserialize(str: String): TickDataUnitSeconds = TickDataUnitSeconds(str.toDouble())

  /**
   * Returns a string representation of this [TickDataUnitSeconds].
   *
   * @return A string representation of this [TickDataUnitSeconds].
   */
  override fun toString(): String = "${this.tickSeconds}s"
}
