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
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.evaluation.TickSequence.Companion.asTickSequence

/** Test for [TickSequence]. */
class TickSequenceTest {

  /** Test empty [TickSequence]. */
  @Test
  fun `Test empty TickSequence`() {
    val sequence = TickSequence { null }
    assertTrue(sequence.toList().isEmpty())
  }

  /** Test correct reset of linking. */
  @Test
  fun `Test correct reset of linking`() {
    val ticks = List(3) { SimpleTickData(SimpleTickDataUnit(it.toLong())) }
    ticks[0].nextTick = ticks[1]
    ticks[1].previousTick = ticks[0]

    ticks[1].nextTick = ticks[2]
    ticks[2].previousTick = ticks[1]

    val sequence =
        ticks.asTickSequence(bufferSize = 2, iterationMode = TickSequence.IterationMode.END_FILLED)

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
    assertEquals(2L, tick.nextTick?.currentTickUnit?.tickValue)
    assertEquals(tick, tick.nextTick?.previousTick)
    assertNull(tick.nextTick?.nextTick)
    assertNull(tick.previousTick)

    assertFalse(iterator.hasNext())
  }

  /** Test multiple calls to hasNext. */
  @Test
  fun `Test multiple calls to hasNext`() {
    var i = 0L
    val sequence =
        TickSequence(
            iterationOrder = TickSequence.IterationOrder.FORWARD,
            iterationMode = TickSequence.IterationMode.END_FILLED,
        ) {
          if (i < 5) SimpleTickData(SimpleTickDataUnit(i++)) else null
        }

    val iterator = sequence.iterator()

    assertTrue(iterator.hasNext())
    assertTrue(iterator.hasNext())
    assertTrue(iterator.hasNext())

    val tick = iterator.next()

    // Always return first tick
    assertEquals(0, tick.currentTickUnit.tickValue)

    // Successors increase with index
    assertEquals(0, tick.numSuccessors)

    // No predecessors in FORWARD iteration
    assertEquals(0, tick.numPredecessors)
  }

  /** Test [asTickSequence] extension function. */
  @Test
  fun `Test asTickSequence extension function`() {
    val ticks = List(5) { SimpleTickData(SimpleTickDataUnit(it.toLong())) }
    val sequence =
        ticks.asTickSequence(bufferSize = 5, iterationMode = TickSequence.IterationMode.END_FILLED)

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

  /** Test [asTickSequence] extension function on empty [List]. */
  @Test
  fun `Test asTickSequence extension function on empty List`() {
    val ticks = emptyList<SimpleTickData>()
    val sequence = ticks.asTickSequence()

    val iterator = sequence.iterator()

    assertFalse(iterator.hasNext())
  }

  /** Test once constraint. */
  @Test
  fun `Test once constraint`() {
    val sequence = TickSequence { null }
    sequence.iterator()

    assertFailsWith<IllegalStateException> { sequence.iterator() }
  }
}
