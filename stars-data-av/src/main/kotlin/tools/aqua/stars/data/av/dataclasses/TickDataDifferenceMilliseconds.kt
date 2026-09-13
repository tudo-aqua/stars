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
 * Implementation of the [TickDifference] interface for 'milliseconds' units.
 *
 * @property differenceMillis Difference in milliseconds.
 */
data class TickDataDifferenceMilliseconds(val differenceMillis: Long) :
    TickDifference<TickDataDifferenceMilliseconds>() {
  /**
   * Adds a [TickDataDifferenceMilliseconds] to this [TickDataDifferenceMilliseconds].
   *
   * @param other The [TickDataDifferenceMilliseconds] to add.
   * @return A new [TickDataDifferenceMilliseconds] object.
   */
  override operator fun plus(
      other: TickDataDifferenceMilliseconds
  ): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(this.differenceMillis + other.differenceMillis)

  /**
   * Adds a [Number] considered as milliseconds to this [TickDataDifferenceMilliseconds].
   *
   * @param millis The [Number] to add.
   * @return A new [TickDataDifferenceMilliseconds] object.
   */
  operator fun plus(millis: Number): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(this.differenceMillis + millis.toLong())

  /**
   * Subtracts a [TickDataDifferenceMilliseconds] from this [TickDataDifferenceMilliseconds].
   *
   * @param other The [TickDataDifferenceMilliseconds] to subtract.
   * @return A new [TickDataDifferenceMilliseconds] object.
   */
  override operator fun minus(
      other: TickDataDifferenceMilliseconds
  ): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(this.differenceMillis - other.differenceMillis)

  /**
   * Subtracts a [Number] considered as milliseconds from this [TickDataDifferenceMilliseconds].
   *
   * @param millis The [Number] to subtract.
   * @return A new [TickDataDifferenceMilliseconds] object.
   */
  operator fun minus(millis: Number): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(this.differenceMillis - millis.toLong())

  /**
   * Compares this object with the specified object for order. Returns zero if this object is equal
   * to the specified [other] object, a negative number if it's less than [other], or a positive
   * number if it's greater than [other].
   */
  override operator fun compareTo(other: TickDataDifferenceMilliseconds): Int =
      this.differenceMillis.compareTo(other.differenceMillis)

  /**
   * Compares this object with the specified [Number] treated as milliseconds for order. Returns
   * zero if this object is equal to the specified [millis], a negative number if it's less than
   * [millis], or a positive number if it's greater than [millis].
   */
  operator fun compareTo(millis: Number): Int = this.differenceMillis.compareTo(millis.toLong())

  /**
   * Serializes this [TickDataDifferenceMilliseconds] to a [String].
   *
   * @return A [String] representation of this [TickDataDifferenceMilliseconds].
   */
  override fun serialize(): String = this.differenceMillis.toString()

  /**
   * Deserializes a [String] to a [TickDataDifferenceMilliseconds].
   *
   * @param str The [String] to deserialize.
   * @return A new [TickDataDifferenceMilliseconds] object.
   */
  override fun deserialize(str: String): TickDataDifferenceMilliseconds =
      TickDataDifferenceMilliseconds(str.toLong())

  /**
   * Returns a string representation of this [TickDataDifferenceMilliseconds].
   *
   * @return A string representation of this [TickDataDifferenceMilliseconds].
   */
  override fun toString(): String = "${this.differenceMillis}ms"
}
