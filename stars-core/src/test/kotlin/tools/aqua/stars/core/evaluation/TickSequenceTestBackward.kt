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

/** Test for [TickSequence]. */
class TickSequenceTestBackward {
  /** Test correct iteration order BACKWARD. */
  @Test
  fun `Test correct iteration order BACKWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    sequence.forEachIndexed { index, tick ->
      // Always return newest tick
      assertEquals(index.toLong(), tick.currentTickUnit.tickValue)

      // No successors in BACKWARD iteration
      assertEquals(0, tick.numSuccessors)

      // Predecessors increase with index
      assertEquals(index, tick.numPredecessors)
    }
  }

  /** Test correct linking BACKWARD. */
  @Test
  fun `Test correct linking BACKWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    var tick = iterator.next()

    assertNull(tick.nextTick)
    assertNull(tick.previousTick)

    tick = iterator.next()
    assertEquals(0L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(tick, tick.previousTick?.nextTick)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertEquals(tick, tick.previousTick?.nextTick)
    assertEquals(tick, tick.previousTick?.previousTick?.nextTick?.nextTick)
    assertEquals(tick.previousTick, tick.previousTick?.previousTick?.nextTick)
    assertNull(tick.nextTick)
    assertNull(tick.previousTick?.previousTick?.previousTick)

    assertFalse(iterator.hasNext())
  }

  /** Test iterationMode END_FILLED on iterationOrder BACKWARD. */
  @Test
  fun `Test iterationMode END FILLED on iterationOrder BACKWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(0L, tick.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(3L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(1L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    assertFalse(iterator.hasNext())
  }

  /** Test iterationMode FULL_FRAME on iterationOrder BACKWARD. */
  @Test
  fun `Test iterationMode FULL FRAME on iterationOrder BACKWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.FULL_FRAME,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(3L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(1L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    assertFalse(iterator.hasNext())
  }

  /** Test iterationMode START_FILLED on iterationOrder BACKWARD. */
  @Test
  fun `Test iterationMode START FILLED on iterationOrder BACKWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.START_FILLED,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(3L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(1L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick)

    assertFalse(iterator.hasNext())
  }

  /** Test iterationMode FULL on iterationOrder BACKWARD. */
  @Test
  fun `Test iterationMode FULL on iterationOrder BACKWARD`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.FULL,
        ) {
          if (i < 5) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(0L, tick.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(3L, tick.currentTickUnit.tickValue)
    assertEquals(2L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(1L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(2L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertEquals(3L, tick.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(4L, tick.currentTickUnit.tickValue)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick)

    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode END_FILLED on iterationOrder BACKWARD with too few elements. The iteration
   * does not require a minimum number of elements to fill the buffer, so the iteration must
   * succeed.
   */
  @Test
  fun `Test iterationMode END FILLED on iterationOrder BACKWARD with too few elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 5,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(0L, tick.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode FULL_FRAME on iterationOrder BACKWARD with too few elements. The iteration
   * requires a filled frame of size bufferSize, so hasNext must return false on the first call.
   */
  @Test
  fun `Test iterationMode FULL FRAME on iterationOrder BACKWARD with too few elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 5,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.FULL_FRAME,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode FULL_FRAME on iterationOrder BACKWARD with the exact number of elements. The
   * iteration requires a filled frame of size bufferSize, so it must return only one frame.
   */
  @Test
  fun `Test iterationMode FULL FRAME on iterationOrder BACKWARD with the exact number of elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.FULL_FRAME,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    assertTrue(iterator.hasNext())
    assertTrue(iterator.hasNext())

    val tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode START_FILLED on iterationOrder BACKWARD with too few elements. The iteration
   * requires a filled frame of size bufferSize, so hasNext must return false on the first call.
   */
  @Test
  fun `Test iterationMode START FILLED on iterationOrder BACKWARD with too few elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 5,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.START_FILLED,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode START_FILLED on iterationOrder BACKWARD with the exact number of elements.
   * The iteration requires a filled frame of size bufferSize, so it must return only one frame.
   */
  @Test
  fun `Test iterationMode START FILLED on iterationOrder BACKWARD with the exact number of elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 3,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.START_FILLED,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()
    assertTrue(iterator.hasNext())

    var tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick)

    assertFalse(iterator.hasNext())
  }

  /**
   * Test iterationMode FULL on iterationOrder BACKWARD with too few elements. The iteration does
   * not require a minimum number of elements to fill the buffer, so the iteration must succeed.
   */
  @Test
  fun `Test iterationMode FULL on iterationOrder BACKWARD with too few elements`() {
    var i = 0L
    val sequence =
        TickSequence(
            name = "",
            bufferSize = 5,
            iterationOrder = TickSequence.IterationOrder.BACKWARD,
            iterationMode = TickSequence.IterationMode.FULL,
        ) {
          if (i < 3) SimpleTickData(i++) else null
        }

    val iterator = sequence.iterator()

    var tick = iterator.next()
    assertEquals(0L, tick.currentTickUnit.tickValue)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(1L, tick.currentTickUnit.tickValue)
    assertEquals(0L, tick.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertEquals(0L, tick.previousTick?.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertEquals(1L, tick.previousTick?.currentTickUnit?.tickValue)
    assertNull(tick.previousTick?.previousTick)
    assertNull(tick.nextTick)

    tick = iterator.next()
    assertEquals(2L, tick.currentTickUnit.tickValue)
    assertNull(tick.previousTick)
    assertNull(tick.nextTick)

    assertFalse(iterator.hasNext())
  }
}
