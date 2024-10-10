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

package tools.aqua.stars.core.types

/**
 * Interface for the relative difference between ticks. Implements the [Comparable] interface.
 *
 * @param D [TickDifference].
 */
interface TickDifference<D : TickDifference<D>> : Comparable<D> {

  /**
   * Adds a [TickDifference] to this [TickDifference].
   *
   * @param other The [TickDifference] to add.
   * @return A new [TickDifference] object.
   */
  operator fun plus(other: D): D

  /**
   * Subtracts a [TickDifference] from this [TickDifference].
   *
   * @param other The [TickDifference] to subtract.
   * @return A new [TickDifference] object.
   */
  operator fun minus(other: D): D

  /** Serializes the [TickDifference] to a [String]. */
  fun serialize(): String

  /** Deserializes the [TickDifference] from a [String]. */
  fun deserialize(str: String): D
}
