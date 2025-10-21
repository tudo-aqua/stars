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
 * Class storing data of the current tick. Forms a double-linked list with the previous and next
 * tick. Global/Static data should be stored directly in this class, dynamic entities are held in
 * [entities]. The current timestamp is represented by [currentTickUnit].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property currentTickUnit The current [TickUnit].
 * @property entities List of [EntityType]s in tick data.
 */
abstract class TickDataType<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(val currentTickUnit: U, val entities: Set<E> = LinkedHashSet()) {
  /** The next [TickDataType] in the sequence. */
  var nextTick: T? = null

  /** The previous [TickDataType] in the sequence. */
  var previousTick: T? = null

  /** Identifier for [TickDataType]. */
  abstract val identifier: String

  /**
   * The number of predecessors in the tick sequence.
   *
   * This is the number of ticks that precede this tick in the sequence, including the previous
   * tick. If there is no previous tick, this will return 0.
   */
  val numPredecessors: Int
    get() = previousTick?.numPredecessors?.plus(1) ?: 0

  /**
   * The number of successors in the tick sequence.
   *
   * This is the number of ticks that follow this tick in the sequence, including the next tick. If
   * there is no next tick, this will return 0.
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

  /** The ego [EntityType]. */
  abstract val ego: E

  init {
    entities.forEach {
      @Suppress("UNCHECKED_CAST")
      it.currentTick = this as T
    }
  }

  /**
   * Returns a forward iterator over the ticks in the sequence.
   *
   * @return A [TickDataIterator] that iterates over the ticks in the sequence in forward order.
   */
  @Suppress("UNCHECKED_CAST")
  fun forward(): TickDataIterator<T> = TickDataIterator(this as T) { it.nextTick }

  /**
   * Returns a backward iterator over the ticks in the sequence.
   *
   * @return A [TickDataIterator] that iterates over the ticks in the sequence in backward order.
   */
  @Suppress("UNCHECKED_CAST")
  fun backward(): TickDataIterator<T> = TickDataIterator(this as T) { it.previousTick }

  override fun toString(): String =
      "Tick ($identifier) @$currentTickUnit with ${entities.size} entities."

  /**
   * Iterator implementation for [TickDataType]. Instanced may be retrieved using [forward] or
   * [backward] methods.
   *
   * @param T [TickDataType].
   * @param tick The initial tick to start the iteration from.
   * @param iter The function to get the next tick from the current tick.
   * @return An [Iterable] that allows iteration over the ticks in the sequence.
   */
  class TickDataIterator<T> internal constructor(private val tick: T, private val iter: (T) -> T?) :
      Iterable<T> {
    override fun iterator(): Iterator<T> =
        object : Iterator<T> {
          private var current: T? = tick

          override fun hasNext(): Boolean = current != null

          override fun next(): T {
            val nextTick = current ?: throw NoSuchElementException("No more ticks in the sequence")
            current = iter(nextTick)
            return nextTick
          }
        }
  }
}
