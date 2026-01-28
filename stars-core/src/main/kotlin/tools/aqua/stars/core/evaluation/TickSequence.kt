/*
 * Copyright 2025-2026 The STARS Project Authors
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

package tools.aqua.stars.core.evaluation

import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.utils.nextOrNull

/**
 * A sequence that returns ticks of type [T] in a doubly linked list structure. The [getNextValue]
 * function is used to lazily retrieve the next tick value. The iterator returns ticks one by one
 * and creates the doubly linked list structure. This list is cropped to a maximum size of
 * [bufferSize], meaning that the oldest ticks are removed when the size exceeds this limit and
 * their predecessors are set to `null`. Depending on the [iterationOrder], the iterator returns
 * either the oldest tick ([IterationOrder.FORWARD]) or the newest tick ([IterationOrder.BACKWARD])
 * of the sliding window.
 *
 * The sequence can only be consumed once.
 *
 * @param T [TickDataType].
 * @property bufferSize The maximum size of the buffer. If the size exceeds this limit, the oldest
 *   tick is removed.
 * @property iterationOrder The order in which ticks are returned.
 * @property iterationMode The mode in which the iteration is performed. See [IterationMode] for
 *   details.
 * @param getNextValue The generator function that lazily returns the next tick.
 */
class TickSequence<T : TickDataType<*, T, *, *>>(
    val bufferSize: Int = 100,
    val iterationOrder: IterationOrder = IterationOrder.FORWARD,
    val iterationMode: IterationMode = IterationMode.FULL_FRAME,
    private val getNextValue: () -> T?,
) : Sequence<T> {
  /** Constrains the sequence to be consumed only once. */
  private val onceConstraint = java.util.concurrent.atomic.AtomicBoolean(true)

  init {
    check(bufferSize > 0) { "Buffer size must be greater than 0" }
  }

  override fun iterator(): Iterator<T> {
    check(onceConstraint.getAndSet(false)) { "This TickSequence can only be consumed once." }

    return object : Iterator<T> {
      lateinit var firstItem: T
      lateinit var lastItem: T
      var nextItem: T? = null
      var size: Int = 0
      var initialized: Boolean = false
      var finished: Boolean = false

      override fun next(): T {
        // Call hasNext() to ensure we have a next item
        if (!hasNext()) throw NoSuchElementException("No more elements in the sequence")

        // We have a new item
        if (nextItem != null) linkNext()

        // If the buffer size exceeds the limit or the iteration is already at the end and
        // unrolling, remove the oldest tick
        if (size > bufferSize || finished) removeOldest()

        return checkNotNull(
            when (iterationOrder) {
              IterationOrder.FORWARD -> firstItem
              IterationOrder.BACKWARD -> lastItem
            }
        )
      }

      override fun hasNext(): Boolean {
        // If we already have a next item, return true
        if (nextItem != null) return true

        // If the sequence is finished. Return true only if we are unrolling in START_FILLED mode
        // and there are still items left.
        if (finished)
            return (iterationMode == IterationMode.START_FILLED ||
                iterationMode == IterationMode.FULL) && size > 1

        // Initialize on first call
        if (!initialized) return initialize()

        // Retrieve next item from the provided function
        retrieveNext()
        if (nextItem != null) return true

        // No new item available. Set finished to `true` to avoid calling retrieve function again.
        finished = true

        // Only return true if we are now in unrolling mode
        return (iterationMode == IterationMode.START_FILLED ||
            iterationMode == IterationMode.FULL) && size > 1
      }

      /**
       * Initially fills the buffer depending on the [iterationMode].
       *
       * @return `true` if initialization was successful and enoug h items were available, `false`
       *   otherwise.
       */
      private fun initialize(): Boolean {
        initialized = true

        // Retrieve first item. If no item is available, return false
        if (!retrieveNext()) return false

        if (
            iterationMode == IterationMode.START_FILLED || iterationMode == IterationMode.FULL_FRAME
        ) {
          val next = checkNotNull(nextItem)
          firstItem = next
          lastItem = next
          nextItem = null
          size = 1

          // Try to fill buffer completely
          while (size < bufferSize) {
            if (!retrieveNext()) return false

            linkNext()
          }

          size = bufferSize
        }

        return true
      }

      /**
       * Retrieves the next item from the provided function and sets [nextItem].
       *
       * @return `false` if no next item is available, `true` otherwise.
       */
      private fun retrieveNext(): Boolean {
        // Retrieve next item from the provided function
        nextItem =
            getNextValue()?.apply {
              previousTick = null
              nextTick = null
            } ?: return false

        return true
      }

      /**
       * Links the next item in [nextItem] to the doubly linked list, sets it to null and increases
       * [size] by one.
       */
      private fun linkNext() {
        val next = checkNotNull(nextItem)

        // Sequence needs initialization
        if (size == 0) {
          firstItem = next
          lastItem = next
          nextItem = null
          size = 1
          return
        }

        // Link new tick to the doubly linked list
        lastItem.nextTick = next
        next.previousTick = lastItem

        // Update last item
        lastItem = next
        nextItem = null

        // Increase size
        size++
      }

      /** Removes the oldest tick from the doubly linked list. Decreases [size] by one. */
      private fun removeOldest() {
        val currentFirst = firstItem

        // Move first pointer to the next item
        firstItem = checkNotNull(firstItem.nextTick)
        firstItem.previousTick = null

        // Clear the next reference of the last item to avoid strange behavior
        currentFirst.nextTick = null

        // Decrease size
        size--
      }
    }
  }

  /** Companion object for [TickSequence]. */
  companion object {
    /**
     * Creates a [TickSequence] from an [Iterable] of [TickDataType]s.
     *
     * @param T [TickDataType].
     * @param bufferSize The maximum size of the buffer. If the size exceeds this limit, the oldest
     *   tick is removed.
     * @param iterationOrder The order in which ticks are returned.
     * @return A [TickSequence] that iterates over the elements of the given [Iterable].
     */
    fun <T : TickDataType<*, T, *, *>> Iterable<T>.asTickSequence(
        bufferSize: Int = 100,
        iterationOrder: IterationOrder = IterationOrder.FORWARD,
        iterationMode: IterationMode = IterationMode.FULL_FRAME,
    ): TickSequence<T> =
        TickSequence(
            bufferSize = bufferSize,
            iterationOrder = iterationOrder,
            iterationMode = iterationMode,
            getNextValue = iterator()::nextOrNull,
        )
  }

  /** Enumeration for the iteration order of the [TickSequence]. */
  enum class IterationOrder {
    /** Forward iteration order. Always returns the oldest tick in the sequence. */
    FORWARD,

    /** Backward iteration order. Always returns the newest tick in the sequence. */
    BACKWARD,
  }

  /** Enumeration for the iteration mode of the [TickSequence]. */
  enum class IterationMode {
    /**
     * Full frame iteration mode. The buffer gets filled completely before the first tick is
     * returned and the iteration stops when no new tick can be appended to the [TickSequence]. This
     * results in a stable frame size buf the last (buffersize - 1) ticks (or first in case of
     * [IterationOrder] ``FORWARD``) are not returned directly and only observable via temporal
     * operators.
     */
    FULL_FRAME,

    /**
     * Start filled iteration mode. The buffer gets filled completely before the first tick is
     * returned. The iteration continues as long as new ticks can be appended to the [TickSequence].
     * When no new tick is available, the frame size decreases until the buffer is empty. This
     * results in a stable frame size at the beginning of the iteration and then presents the
     * remaining ticks until the buffer is empty. Useful in case of [IterationOrder] ``FORWARD``.
     * Here, all ticks get presented to the evaluation. A minimum remaining buffer size can be
     * controlled via [tools.aqua.stars.core.hooks.defaulthooks.MinTicksPerTickSequenceHook].
     */
    START_FILLED,

    /**
     * End filled iteration mode. The iteration starts immediately and presents all available ticks.
     * The iteration continues as long as new ticks can be appended to the [TickSequence]. When no
     * new tick is available, the iteration ends. Useful in case of [IterationOrder] ``BACKWARD``.
     * Here, all ticks get presented to the evaluation. A minimum initial buffer size can be
     * controlled via [tools.aqua.stars.core.hooks.defaulthooks.MinTicksPerTickSequenceHook].
     */
    END_FILLED,

    /**
     * Full iteration mode. The iteration starts immediately and presents all available ticks. The
     * iteration continues as long as new ticks can be appended to the [TickSequence]. When no new
     * tick is available, the frame size decreases until the buffer is empty. A minimum buffer size
     * can be controlled via [tools.aqua.stars.core.hooks.defaulthooks.MinTicksPerTickSequenceHook].
     */
    FULL,
  }
}
