/*
 * Copyright 2025 The STARS Project Authors
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

class TickSequence<T : TickDataType<*, T, *, *>>(
    val bufferSize: Int = 100,
    private val getNextValue: () -> T?
) : Sequence<T> {
  /** Constrains the sequence to be consumed only once. */
  private val onceConstraint = java.util.concurrent.atomic.AtomicBoolean(false)

  init {
    check(bufferSize > 0) { "Buffer size must be greater than 0" }
  }

  override fun iterator(): Iterator<T> {
    if (!onceConstraint.getAndSet(true)) {
      throw IllegalStateException("This TickSequence can only be consumed once.")
    }

    return object : Iterator<T> {
      var firstItem: T? = null
      var currentItem: T? = null
      var nextItem: T? = null
      var size: Int = 0
      var finished: Boolean = false

      override fun next(): T {
        if (!hasNext()) throw NoSuchElementException("No more elements in the sequence")

        val first = checkNotNull(firstItem)
        val current = checkNotNull(currentItem)
        val next = checkNotNull(nextItem)

        // Link new tick to the doubly linked list
        current.nextTick = next
        next.previousTick = current

        // Update current buffer size
        size++

        // If the buffer size exceeds the limit, remove the oldest tick
        if (size > bufferSize) {
          firstItem = first.nextTick
          firstItem?.previousTick = null
          first.nextTick =
              null // Clear the next reference of the last item to avoid strange behavior
          size--
        }

        return next
      }

      override fun hasNext(): Boolean {
        if (finished) return false

        nextItem = getNextValue()

        if (firstItem == null) {
          firstItem = nextItem
          currentItem = nextItem
        }

        if (nextItem == null) finished = true

        return nextItem != null
      }
    }
  }
}
