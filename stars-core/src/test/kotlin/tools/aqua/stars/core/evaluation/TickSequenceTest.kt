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

import kotlin.test.assertFalse
import kotlin.test.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataUnit

/** Test for [TickSequence]. */
class TickSequenceTest {

  /** Test empty [TickSequence]. */
  @Test
  fun `Test empty TickSequence`() {
    val sequence = TickSequence { null }
    assertTrue(sequence.toList().isEmpty())
  }

  /** Test correct iteration order FORWARD. */
  @Test
  fun `Test correct iteration order FORWARD`() {
    var i = 0L
    val sequence =
        TickSequence(iterationOrder = TickSequence.IterationOrder.FORWARD) {
          if (i < 5) SimpleTickData(SimpleTickDataUnit(i++)) else null
        }

    sequence.forEachIndexed { index, tick ->
      // Always return first tick
      assertEquals(0, tick.currentTickUnit.tickValue)

      // Predecessors increase with index
      assertEquals(index, tick.numSuccessors)

      // No predecessors in FORWARD iteration
      assertEquals(0, tick.numPredecessors)
    }
  }

  /** Test correct iteration order BACKWARD. */
  @Test
  fun `Test correct iteration order BACKWARD`() {
    var i = 0L
    val sequence =
        TickSequence(iterationOrder = TickSequence.IterationOrder.BACKWARD) {
          if (i < 5) SimpleTickData(SimpleTickDataUnit(i++)) else null
        }

    sequence.forEachIndexed { index, tick ->
      // Always return last tick
      assertEquals(index.toLong(), tick.currentTickUnit.tickValue)

      // No successors in BACKWARD iteration
      assertEquals(0, tick.numSuccessors)

      // Predecessors increase with index
      assertEquals(index, tick.numPredecessors)
    }
  }

  /** Test correct linking FORWARD. */
  @Test
  fun `Test correct linking FORWARD`() {
    var i = 0L
    val sequence =
        TickSequence(iterationOrder = TickSequence.IterationOrder.FORWARD) {
          if (i < 3) SimpleTickData(SimpleTickDataUnit(i++)) else null
        }

    val iterator = sequence.iterator()
    var tick = iterator.next()

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

  /** Test correct linking BACKWARD. */
  @Test
  fun `Test correct linking BACKWARD`() {
    var i = 0L
    val sequence =
        TickSequence(iterationOrder = TickSequence.IterationOrder.BACKWARD) {
          if (i < 3) SimpleTickData(SimpleTickDataUnit(i++)) else null
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
}
