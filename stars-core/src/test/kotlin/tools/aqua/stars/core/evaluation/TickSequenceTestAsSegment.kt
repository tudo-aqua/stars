/*
 * Copyright 2026 The STARS Project Authors
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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.evaluation.TickSequence.Companion.asSegment

/** Tests the [asSegment] extension function for [TickSequence]s. */
class TickSequenceTestAsSegment {

  /** [asSegment] uses an unlimited buffer, so it is accepted regardless of the tick count. */
  @Test
  fun `Test asSegment is accepted`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    ticks.asSegment()
  }

  /** [asSegment] sets the [TickSequence.bufferSize] to `-1`. */
  @Test
  fun `Test asSegment sets bufferSize to -1`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    assertEquals(-1, ticks.asSegment().bufferSize)
  }

  /**
   * [asSegment] sets the [TickSequence.iterationOrder] to [TickSequence.IterationOrder.FORWARD].
   */
  @Test
  fun `Test asSegment sets iterationOrder to FORWARD`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    assertEquals(TickSequence.IterationOrder.FORWARD, ticks.asSegment().iterationOrder)
  }

  /**
   * [asSegment] sets the [TickSequence.iterationMode] to [TickSequence.IterationMode.FULL_FRAME].
   */
  @Test
  fun `Test asSegment sets iterationMode to FULL_FRAME`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    assertEquals(TickSequence.IterationMode.FULL_FRAME, ticks.asSegment().iterationMode)
  }

  /** [asSegment] forwards the given [name] to the created [TickSequence]. */
  @Test
  fun `Test asSegment forwards name`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    assertEquals("mySegment", ticks.asSegment(name = "mySegment").name)
  }

  /** [asSegment] defaults to an empty [name] when none is given. */
  @Test
  fun `Test asSegment defaults to empty name`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    assertEquals("", ticks.asSegment().name)
  }

  /** [asSegment] loads all ticks into a single frame and yields exactly the first tick. */
  @Test
  fun `Test asSegment loads all ticks into a single frame`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val result = ticks.asSegment().toList()

    assertEquals(1, result.size)
    assertEquals(5, result.single().sequenceLength)
    assertEquals(0L, result.single().currentTickUnit.tickValue)
  }

  /** [asSegment] links every subsequent tick to the yielded first tick. */
  @Test
  fun `Test asSegment links all subsequent ticks`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val head = ticks.asSegment().single()

    var tick = head
    var count = 1
    while (tick.nextTick != null) {
      tick = checkNotNull(tick.nextTick)
      count++
    }

    assertNull(head.previousTick)
    assertNull(tick.nextTick)
    assertEquals(5, count)
    assertEquals(4L, tick.currentTickUnit.tickValue)
  }

  /** [asSegment] with only a single tick still yields that one tick. */
  @Test
  fun `Test asSegment with a single tick`() {
    val ticks = listOf(SimpleTickData(0))
    val result = ticks.asSegment().toList()

    assertEquals(1, result.size)
    assertEquals(1, result.single().sequenceLength)
    assertNull(result.single().previousTick)
    assertNull(result.single().nextTick)
  }

  /** [asSegment] yields nothing if there are no ticks. */
  @Test
  fun `Test asSegment with no ticks`() {
    val ticks = listOf<SimpleTickData>()
    val result = ticks.asSegment().toList()

    assertEquals(0, result.size)
  }
}
