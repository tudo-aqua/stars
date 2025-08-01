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

package tools.aqua.stars.core.types

/**
 * Class storing data of the current tick. Forms a double-linked list with the previous and next tick.
 * Global/Static data should be stored directly in this class, dynamic entities are held in [entities].
 * The current timestamp is represented by [currentTickUnit].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 *
 * @property currentTickUnit The current [TickUnit].
 * @property entities List of [EntityType]s in tick data.
 */
abstract class TickDataType<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> (
      val currentTickUnit: U,
      val entities: Set<E> = LinkedHashSet()
    ) {
  /** The next [TickDataType] in the sequence. */
  var nextTick: T? = null

  /** The previous [TickDataType] in the sequence. */
  var previousTick: T? = null

  /**
   * The number of predecessors in the tick sequence.
   *
   * This is the number of ticks that precede this tick in the sequence, including the previous tick.
   * If there is no previous tick, this will return 0.
   */
  val numPredecessors: Int
    get() = previousTick?.numPredecessors?.plus(1) ?: 0

  /**
   * The number of successors in the tick sequence.
   *
   * This is the number of ticks that follow this tick in the sequence, including the next tick.
   * If there is no next tick, this will return 0.
   */
  val numSuccessors: Int
    get() = nextTick?.numSuccessors?.plus(1) ?: 0

  /**
   * The total length of the tick sequence.
   *
   * This is the total number of ticks in the sequence, including this tick.
   */
  val sequenceLength: Int
    get() = numPredecessors + numSuccessors + 1

  init {
    entities.forEach {
      @Suppress("UNCHECKED_CAST")
      it.currentTick = this as T
    }
  }
}
