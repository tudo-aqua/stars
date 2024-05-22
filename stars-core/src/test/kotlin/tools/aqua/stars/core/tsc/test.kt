package tools.aqua.stars.core.tsc

import kotlin.test.Test

val TRUE = { true }

class test {
  @Test
  fun test1() {
    println( TRUE == TRUE )
    println( TRUE === TRUE )
  }
}