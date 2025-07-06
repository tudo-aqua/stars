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

@Suppress("UNCHECKED_CAST")
class SegmentBuffer<T>(private val capacity: Int) : Iterable<T> {
  private val data = arrayOf<Any?>(capacity)
  private var start = 0
  private var end = 0
  private var full = false

  val size: Int
    get() = if (full) capacity else (end - start + capacity) % capacity

  operator fun get(index: Int): T = data[(start + index) % capacity] as T

  fun add(element: T) {
    data[end] = element
    end = (end + 1) % capacity

    if (full) start = (start + 1) % capacity

    if (end == start) full = true
  }

  override fun iterator(): Iterator<T> = SegmentBufferIterator(this)
}

class SegmentBufferIterator<T>(private val segmentBuffer: SegmentBuffer<T>) : Iterator<T> {
  var currentIndex = 0

  override fun next(): T = segmentBuffer[currentIndex++]

  override fun hasNext(): Boolean = currentIndex < segmentBuffer.size
}
