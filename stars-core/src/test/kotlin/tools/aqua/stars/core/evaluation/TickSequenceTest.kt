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

  /** Test correct iteration order. */
  @Test
  fun `Test correct iteration order`() {
    var i = 0L
    val sequence = TickSequence { if (i < 5) SimpleTickData(SimpleTickDataUnit(i++)) else null }

    sequence.forEachIndexed { index, tick ->
      assertEquals(index.toLong(), tick.currentTickUnit.tickValue)
    }
  }

  /** Test correct linking. */
  @Test
  fun `Test correct linking`() {
    var i = 0L
    val sequence = TickSequence { if (i < 3) SimpleTickData(SimpleTickDataUnit(i++)) else null }

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
