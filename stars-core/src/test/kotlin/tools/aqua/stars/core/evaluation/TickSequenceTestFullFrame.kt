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
import tools.aqua.stars.core.evaluation.TickSequence.IterationMode.FULL_FRAME

/** Tests the [FULL_FRAME] iteration mode for [TickSequence]s. */
class TickSequenceTestFullFrame {

  /**
   * Tests the [FULL_FRAME] iteration mode where the list of ticks is exactly the size of the
   * desired buffer size.
   */
  @Test
  fun `Test FullFrame with sequence exactly the size of the frame`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = 5, iterationMode = FULL_FRAME)
    assertEquals(1, tickSequence.toList().size)
  }

  /**
   * Tests the [FULL_FRAME] iteration mode where the list of ticks is exactly one tick larger than
   * the size of desired buffer size.
   */
  @Test
  fun `Test FullFrame with sequence exactly one tick larger than the size of the frame`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = 4, iterationMode = FULL_FRAME)
    var count = 0
    tickSequence.forEach {
      assertEquals(4, it.sequenceLength)
      count++
    }
    assertEquals(2, count)
  }

  /**
   * Tests the [FULL_FRAME] iteration mode where the list of ticks is exactly one tick smaller than
   * the size of the desired buffer size.
   */
  @Test
  fun `Test FullFrame with sequence exactly one tick less than the size of the frame`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    val tickSequence = ticks.asTickSequence(bufferSize = 6, iterationMode = FULL_FRAME)
    assertEquals(0, tickSequence.toList().size)
  }

  /** Tests the [FULL_FRAME] iteration mode where no ticks are given to the sequence. */
  @Test
  fun `Test FullFrame with no ticks`() {
    val ticks = listOf<SimpleTickData>()
    val tickSequence = ticks.asTickSequence(bufferSize = 5, iterationMode = FULL_FRAME)
    assertEquals(0, tickSequence.toList().size)
  }

  /**
   * Tests the [FULL_FRAME] iteration mode where a list of ticks ist given, but the desired buffer
   * size is equal to 0.
   */
  @Test
  fun `Test FullFrame with a list of ticks but no desired bufferSize`() {
    val ticks = List(5) { SimpleTickData(it.toLong()) }
    assertThrows<IllegalArgumentException> {
      ticks.asTickSequence(bufferSize = 0, iterationMode = FULL_FRAME)
    }
  }
}
