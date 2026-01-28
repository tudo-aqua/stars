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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import tools.aqua.stars.core.SimpleTickData

/** Test for [TickSequence] in [TickSequence.IterationOrder.FORWARD]. */
class TickSequenceTestForward {
  /** Test correct iteration order FORWARD. */
  @Test
  fun `Test correct iteration order FORWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    for (index in 0 until 5) {
      assertTrue(iterator.hasNext())

      val tick = iterator.next()

      // Always return first tick
      assertEquals(0, tick.currentTickUnit.tickValue)

      // Successors increase with index
      assertEquals(index, tick.numSuccessors)

      // No predecessors in FORWARD iteration
      assertEquals(0, tick.numPredecessors)
    }

    assertFalse(iterator.hasNext())
  }

  /** Test correct linking FORWARD. */
  @Test
  fun `Test correct linking FORWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    var tick = iterator.next()

    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(tick, tick.nextTick?.previousTick)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertEquals(tick, tick.nextTick?.previousTick)
    assertEquals(tick, tick.nextTick?.nextTick?.previousTick?.previousTick)
    assertEquals(tick.nextTick, tick.nextTick?.nextTick?.previousTick)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick?.nextTick?.nextTick)

    assertFalse(iterator.hasNext())
  }

  /** Test iterationMode END_FILLED on iterationOrder FORWARD. */
  @Test
  fun `Test iterationMode END FILLED on iterationOrder FORWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(3L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(4L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }

  /** Test iterationMode FULL_FRAME on iterationOrder FORWARD. */
  @Test
  fun `Test iterationMode FULL FRAME on iterationOrder FORWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.FULL_FRAME,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(3L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(4L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }

  /** Test iterationMode START_FILLED on iterationOrder FORWARD. */
  @Test
  fun `Test iterationMode START FILLED on iterationOrder FORWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.START_FILLED,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(3L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(4L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(3L, tick.currentTickUnit.tickValue)
    assertEquals(4L, tick.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }

  /** Test iterationMode FULL on iterationOrder FORWARD. */
  @Test
  fun `Test iterationMode FULL on iterationOrder FORWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.FULL,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(3L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(4L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(3L, tick.currentTickUnit.tickValue)
    assertEquals(4L, tick.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode END_FILLED on iterationOrder FORWARD with too few elements. The iteration
   * does not require a minimum number of elements to fill the buffer, so the iteration must
   * succeed.
   */
  @Test
  fun `Test iterationMode END FILLED on iterationOrder FORWARD with too few elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 5,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode FULL_FRAME on iterationOrder FORWARD with too few elements. The iteration
   * requires a filled frame of size bufferSize, so hasNext must return false on the first call.
   */
  @Test
  fun `Test iterationMode FULL FRAME on iterationOrder FORWARD with too few elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 5,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.FULL_FRAME,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode FULL_FRAME on iterationOrder FORWARD with the exact number of elements. The
   * iteration requires a filled frame of size bufferSize, so it must return only one frame.
   */
  @Test
  fun `Test iterationMode FULL FRAME on iterationOrder FORWARD with the exact number of elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.FULL_FRAME,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    assertTrue(iterator.hasNext())
    assertTrue(iterator.hasNext())

    val tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode START_FILLED on iterationOrder FORWARD with too few elements. The iteration
   * requires a filled frame of size bufferSize, so hasNext must return false on the first call.
   */
  @Test
  fun `Test iterationMode START FILLED on iterationOrder FORWARD with too few elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 5,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.START_FILLED,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode START_FILLED on iterationOrder FORWARD with the exact number of elements.
   * The iteration requires a filled frame of size bufferSize, so it must return only one frame.
   */
  @Test
  fun `Test iterationMode START FILLED on iterationOrder FORWARD with the exact number of elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.START_FILLED,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    assertTrue(iterator.hasNext())

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode FULL on iterationOrder FORWARD with too few elements. The iteration does not
   * require a minimum number of elements to fill the buffer, so the iteration must succeed.
   */
  @Test
  fun `Test iterationMode FULL on iterationOrder FORWARD with too few elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            bufferSize = 5,
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.FULL,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.nextTick?.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.nextTick?.currentTickUnit?.tickValue)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }
}
