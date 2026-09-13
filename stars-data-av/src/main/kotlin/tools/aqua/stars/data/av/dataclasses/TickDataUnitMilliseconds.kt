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
 * Implementation of the [TickUnit] interface for 'milliseconds' units.
 *
 * @property tickMillis Current tick value in milliseconds.
 */
data class TickDataUnitMilliseconds(val tickMillis: Long) :
    TickUnit<TickDataUnitMilliseconds, TickDataDifferenceMilliseconds>() {
  /**
   * Adds a [TickDataDifferenceMilliseconds] to this [TickDataUnitMilliseconds].
   *
   * @param other The [TickDataDifferenceMilliseconds] to add.
   * @return A new [TickDataUnitMilliseconds] object.
   */
  override operator fun plus(other: TickDataDifferenceMilliseconds): TickDataUnitMilliseconds =
      TickDataUnitMilliseconds(this.tickMillis + other.differenceMillis)

  /**
   * Adds a [Number] considered as milliseconds to this [TickDataUnitMilliseconds].
   *
   * @param millis The [Number] to add.
   * @return A new [TickDataUnitMilliseconds] object.
   */
  operator fun plus(millis: Number): TickDataUnitMilliseconds =
      TickDataUnitMilliseconds(this.tickMillis + millis.toLong())

  /**
   * Subtracts a [TickDataUnitMilliseconds] from this [TickDataUnitMilliseconds].
   *
   * @param other The [TickDataUnitMilliseconds] to subtract.
   * @return A new [TickDataDifferenceMilliseconds] object.
   */
  override operator fun minus(other: TickDataUnitMilliseconds): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(this.tickMillis - other.tickMillis)

  /**
   * Subtracts a [TickDataDifferenceMilliseconds] from this [TickDataUnitMilliseconds].
   *
   * @param other The [TickDataDifferenceMilliseconds] to subtract.
   * @return A new [TickDataUnitMilliseconds] object.
   */
  override operator fun minus(other: TickDataDifferenceMilliseconds): TickDataUnitMilliseconds =
      TickDataUnitMilliseconds(this.tickMillis - other.differenceMillis)

  /**
   * Subtracts a [Number] considered as milliseconds from this [TickDataUnitMilliseconds].
   *
   * @param millis The [Number] to subtract.
   * @return A new [TickDataUnitMilliseconds] object.
   */
  operator fun minus(millis: Number): TickDataUnitMilliseconds =
      TickDataUnitMilliseconds(this.tickMillis - millis.toLong())

  /**
   * Compares this object with the specified object for order. Returns zero if this object is equal
   * to the specified [other] object, a negative number if it's less than [other], or a positive
   * number if it's greater than [other].
   */
  override operator fun compareTo(other: TickDataUnitMilliseconds): Int =
      this.tickMillis.compareTo(other.tickMillis)

  /**
   * Compares this object with the specified [Number] treated as milliseconds for order. Returns
   * zero if this object is equal to the specified [millis], a negative number if it's less than
   * [millis], or a positive number if it's greater than [millis].
   */
  operator fun compareTo(millis: Number): Int = this.tickMillis.compareTo(millis.toLong())

  /**
   * Serializes this [TickDataUnitMilliseconds] to a [String].
   *
   * @return A [String] representation of this [TickDataUnitMilliseconds].
   */
  override fun serialize(): String = this.tickMillis.toString()

  /**
   * Deserializes a [String] to a [TickDataUnitMilliseconds].
   *
   * @param str The [String] to deserialize.
   * @return A new [TickDataUnitMilliseconds] object.
   */
  override fun deserialize(str: String): TickDataUnitMilliseconds =
      TickDataUnitMilliseconds(str.toLong())

  /**
   * Returns a string representation of this [TickDataUnitMilliseconds].
   *
   * @return A string representation of this [TickDataUnitMilliseconds].
   */
  override fun toString(): String = "${this.tickMillis}ms"
}
