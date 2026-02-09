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

/** Tests the [END_FILLED] iteration mode for [TickSequence]s. */
class TickSequenceTestEndFilled {

  /**
   * Tests the [END_FILLED] iteration mode where the list of ticks is exactly the size of the
   * desired buffer size.
   */
  @Test
  fun `Test EndFilled with sequence exactly the size of the frame`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = 5, iterationMode = END_FILLED)
    assertEquals(ticks.size, tickSequence.toList().size)
  }

  /**
   * Tests the [END_FILLED] iteration mode where the list of ticks is exactly one tick larger than
   * the size of desired buffer size.
   */
  @Test
  fun `Test EndFilled with sequence exactly one tick larger than the size of the frame`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = 4, iterationMode = END_FILLED)
    var counter = 0
    tickSequence.forEachIndexed { i, tickSequence ->
      when (i) {
        0 -> assertEquals(1, tickSequence.sequenceLength)
        1 -> assertEquals(2, tickSequence.sequenceLength)
        2 -> assertEquals(3, tickSequence.sequenceLength)
        3 -> assertEquals(4, tickSequence.sequenceLength)
        4 -> assertEquals(4, tickSequence.sequenceLength)
      }
      counter++
    }
    assertEquals(5, counter)
  }

  /**
   * Tests the [END_FILLED] iteration mode where the list of ticks is exactly one tick smaller than
   * the size of the desired buffer size.
   */
  @Test
  fun `Test EndFilled with sequence exactly one tick less than the size of the frame`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = 6, iterationMode = END_FILLED)
    assertEquals(5, tickSequence.toList().size)
  }

  /** Tests the [END_FILLED] iteration mode where no ticks are given to the sequence. */
  @Test
  fun `Test EndFilled with no ticks`() {
    val ticks = listOf<SimpleTickData>()
    val tickSequence = ticks.asTickSequence(bufferSize = 5, iterationMode = END_FILLED)
    assertEquals(0, tickSequence.toList().size)
  }

  /**
   * Tests the [END_FILLED] iteration mode where a list of ticks is given, but the desired buffer
   * size is equal to 0.
   */
  @Test
  fun `Test EndFilled with a list of ticks but no desired bufferSize`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    assertThrows<IllegalArgumentException> {
      ticks.asTickSequence(bufferSize = 0, iterationMode = END_FILLED)
    }
  }
}
