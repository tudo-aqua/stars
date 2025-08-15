package tools.aqua.stars.core.evaluation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataUnit
import kotlin.test.assertFalse
import kotlin.test.assertNull

class TickSequenceTest {

  @Test
  fun `Test empty TickSequence`() {
    val sequence = TickSequence { null }
    assertTrue(sequence.toList().isEmpty())
  }

  @Test
  fun `Test correct iteration order`() {
    var i = 0L
    val sequence = TickSequence {
      if (i < 5) SimpleTickData(SimpleTickDataUnit(i++)) else null
    }

    sequence.forEachIndexed { index, tick ->
      assertEquals(index.toLong(), tick.currentTickUnit.tickValue)
    }
  }

  @Test
  fun `Test correct linking`() {
    var i = 0L
    val sequence = TickSequence {
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