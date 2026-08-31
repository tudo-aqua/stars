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
import org.junit.jupiter.api.assertThrows
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.evaluation.TickSequence.Companion.asTickSequence
import tools.aqua.stars.core.evaluation.TickSequence.IterationMode.END_FILLED
import tools.aqua.stars.core.evaluation.TickSequence.IterationMode.FULL
import tools.aqua.stars.core.evaluation.TickSequence.IterationMode.FULL_FRAME
import tools.aqua.stars.core.evaluation.TickSequence.IterationMode.START_FILLED
import tools.aqua.stars.core.evaluation.TickSequence.IterationOrder.BACKWARD

/** Tests the unlimited buffer size (`bufferSize = -1`) for [TickSequence]s. */
class TickSequenceTestUnlimitedBuffer {

  /** A [bufferSize] of `-1` is accepted, other non-positive values are still rejected. */
  @Test
  fun `Test bufferSize of -1 is accepted`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    ticks.asTickSequence(bufferSize = -1)
  }

  /** A [bufferSize] of `0` or other negative values than `-1` are still rejected. */
  @Test
  fun `Test bufferSize other than -1 or greater than 0 is rejected`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    assertThrows<IllegalArgumentException> { ticks.asTickSequence(bufferSize = 0) }
    assertThrows<IllegalArgumentException> { ticks.asTickSequence(bufferSize = -2) }
  }

  /** With an unlimited buffer, [FULL_FRAME] mode yields exactly one tick with all ticks linked. */
  @Test
  fun `Test FullFrame with unlimited buffer loads all ticks into a single frame`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val result = ticks.asTickSequence(bufferSize = -1, iterationMode = FULL_FRAME).toList()

    assertEquals(1, result.size)
    assertEquals(5, result.single().sequenceLength)
  }

  /**
   * With an unlimited buffer and only a single tick, [FULL_FRAME] mode still yields that one tick.
   */
  @Test
  fun `Test FullFrame with unlimited buffer and a single tick`() {
    val ticks = listOf(SimpleTickData(0))
    val result = ticks.asTickSequence(bufferSize = -1, iterationMode = FULL_FRAME).toList()

    assertEquals(1, result.size)
    assertEquals(1, result.single().sequenceLength)
  }

  /** With an unlimited buffer, [FULL_FRAME] mode yields nothing if there are no ticks. */
  @Test
  fun `Test FullFrame with unlimited buffer and no ticks`() {
    val ticks = listOf<SimpleTickData>()
    val result = ticks.asTickSequence(bufferSize = -1, iterationMode = FULL_FRAME).toList()

    assertEquals(0, result.size)
  }

  /**
   * With an unlimited buffer, [FULL_FRAME] mode yields the same single, fully linked frame in
   * [BACKWARD] iteration order, just returning the newest tick instead of the oldest.
   */
  @Test
  fun `Test FullFrame with unlimited buffer loads all ticks into a single frame BACKWARD`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val result =
        ticks
            .asTickSequence(bufferSize = -1, iterationOrder = BACKWARD, iterationMode = FULL_FRAME)
            .toList()

    assertEquals(1, result.size)
    assertEquals(5, result.single().sequenceLength)
    assertEquals(4L, result.single().currentTickUnit.tickValue)
  }

  /**
   * With an unlimited buffer and only a single tick, [FULL_FRAME] mode still yields that one tick
   * in [BACKWARD] iteration order.
   */
  @Test
  fun `Test FullFrame with unlimited buffer and a single tick BACKWARD`() {
    val ticks = listOf(SimpleTickData(0))
    val result =
        ticks
            .asTickSequence(bufferSize = -1, iterationOrder = BACKWARD, iterationMode = FULL_FRAME)
            .toList()

    assertEquals(1, result.size)
    assertEquals(1, result.single().sequenceLength)
  }

  /**
   * With an unlimited buffer, [START_FILLED] mode yields every tick, each with full visibility into
   * all other ticks.
   */
  @Test
  fun `Test StartFilled with unlimited buffer loads all ticks and unrolls`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = -1, iterationMode = START_FILLED)

    // sequenceLength/previousTick/nextTick are only valid for the tick currently at the front of
    // the sliding window, so they must be checked while iterating, not on a collected list.
    var count = 0
    tickSequence.forEachIndexed { index, tick ->
      assertEquals(5 - index, tick.sequenceLength)
      assertEquals(index.toLong(), tick.currentTickUnit.tickValue)
      count++
    }
    assertEquals(5, count)
  }

  /**
   * With an unlimited buffer and only a single tick, [START_FILLED] mode still yields that one
   * tick.
   */
  @Test
  fun `Test StartFilled with unlimited buffer and a single tick`() {
    val ticks = listOf(SimpleTickData(0))
    val result = ticks.asTickSequence(bufferSize = -1, iterationMode = START_FILLED).toList()

    assertEquals(1, result.size)
    assertEquals(1, result.single().sequenceLength)
  }

  /**
   * With an unlimited buffer, [END_FILLED] mode presents ticks immediately and never evicts, so the
   * last tick has full visibility into all other ticks.
   */
  @Test
  fun `Test EndFilled with unlimited buffer never evicts`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = -1, iterationMode = END_FILLED)

    var count = 0
    tickSequence.forEachIndexed { index, tick ->
      assertEquals(index + 1, tick.sequenceLength)
      count++
    }
    assertEquals(5, count)
  }

  /**
   * With an unlimited buffer and only a single tick, [END_FILLED] mode still yields that one tick.
   */
  @Test
  fun `Test EndFilled with unlimited buffer and a single tick`() {
    val ticks = listOf(SimpleTickData(0))
    val result = ticks.asTickSequence(bufferSize = -1, iterationMode = END_FILLED).toList()

    assertEquals(1, result.size)
    assertEquals(1, result.single().sequenceLength)
  }

  /**
   * With an unlimited buffer, [END_FILLED] mode in [BACKWARD] iteration order never evicts either,
   * so each yielded (newest) tick has an ever-growing number of predecessors.
   */
  @Test
  fun `Test EndFilled with unlimited buffer never evicts BACKWARD`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence =
        ticks.asTickSequence(bufferSize = -1, iterationOrder = BACKWARD, iterationMode = END_FILLED)

    var count = 0
    tickSequence.forEachIndexed { index, tick ->
      assertEquals(index.toLong(), tick.currentTickUnit.tickValue)
      assertEquals(0, tick.numSuccessors)
      assertEquals(index, tick.numPredecessors)
      count++
    }
    assertEquals(5, count)
  }

  /**
   * With an unlimited buffer and only a single tick, [END_FILLED] mode still yields that one tick
   * in [BACKWARD] iteration order.
   */
  @Test
  fun `Test EndFilled with unlimited buffer and a single tick BACKWARD`() {
    val ticks = listOf(SimpleTickData(0))
    val result =
        ticks
            .asTickSequence(bufferSize = -1, iterationOrder = BACKWARD, iterationMode = END_FILLED)
            .toList()

    assertEquals(1, result.size)
    assertEquals(1, result.single().sequenceLength)
  }

  /**
   * With an unlimited buffer, [FULL] mode presents every tick, growing then unrolling, and never
   * evicts based on buffer size.
   */
  @Test
  fun `Test Full with unlimited buffer never evicts`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = -1, iterationMode = FULL)

    var count = 0
    tickSequence.forEachIndexed { index, tick ->
      val expected = if (index < 5) index + 1 else 9 - index
      assertEquals(expected, tick.sequenceLength)
      count++
    }
    assertEquals(9, count)
  }

  /** With an unlimited buffer and only a single tick, [FULL] mode still yields that one tick. */
  @Test
  fun `Test Full with unlimited buffer and a single tick`() {
    val ticks = listOf(SimpleTickData(0))
    val result = ticks.asTickSequence(bufferSize = -1, iterationMode = FULL).toList()

    assertEquals(1, result.size)
    assertEquals(1, result.single().sequenceLength)
  }
}
