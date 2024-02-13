/*
 * Copyright 2023-2024 The STARS Project Authors
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

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.*
import tools.aqua.stars.core.*

/** Evaluation state tracker using [AtomicInteger] fields. */
object EvaluationState {
  /** totalSimulationRuns total number of simulation blocks. */
  val totalSimulationBlocks: AtomicInteger = AtomicInteger(0)

  /** Number of read simulation run .zip files. */
  val readSimulationRuns: AtomicInteger = AtomicInteger(0)

  /** Number of sliced simulation runs. */
  val slicedSimulationRuns: AtomicInteger = AtomicInteger(0)

  /** Number of simulation runs in the buffer. */
  val simulationRunsBuffer: AtomicInteger = AtomicInteger(0)

  /** Size of simulation runs buffer. */
  val simulationRunsBufferSize: AtomicInteger = AtomicInteger(0)

  /** Number of segments in the buffer. */
  val segmentsBuffer: AtomicInteger = AtomicInteger(0)

  /** Size of segments buffer. */
  val segmentsBufferSize: AtomicInteger = AtomicInteger(0)

  /** Number of finished segments. */
  val finishedSegments: AtomicInteger = AtomicInteger(0)

  /** Whether the calculation has finished. */
  val isFinished: AtomicBoolean = AtomicBoolean(false)

  private const val UPPER_QUANTILE = 0.75
  private const val LOWER_QUANTILE = 0.25

  /** Coroutine scope for simulation file IO. */
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

  private const val DELAY: Long = 100

  init {
    scope.launch {
      while (!isFinished.get()) {
        printState()
        delay(DELAY)
      }
    }
  }

  /**
   * Checks whether all data has been processed and all buffers are empty.
   *
   * @param print Whether to print final buffer states.
   * @throws IllegalStateException If data is left to process.
   */
  fun checkFinished(print: Boolean) {
    if (print) {
      println("Final BUFFER state: ")
      printState()
      println()
    }

    check(readSimulationRuns.get() == totalSimulationBlocks.get()) {
      "Not all Simulation runs have been read."
    }
    check(simulationRunsBuffer.get() == 0) { "Simulation runs left to be sliced." }

    check(slicedSimulationRuns.get() == totalSimulationBlocks.get()) {
      "Not all sliced simulation runs have been processed."
    }
    check(segmentsBuffer.get() == 0) { "Not all buffered segments have been processed." }
  }

  /** Prints current evaluation progress. */
  private fun printState() {
    val total = totalSimulationBlocks.get()

    val read = readSimulationRuns.get()
    val sliced = slicedSimulationRuns.get()
    val readFinished = total in 1..read
    val sliceFinished = total in 1..sliced

    val simBuff = simulationRunsBuffer.get()
    val simBuffSize = simulationRunsBufferSize.get()
    val segBuff = segmentsBuffer.get()
    val segBuffSize = segmentsBufferSize.get()
    val finished = finishedSegments.get()

    print(
        "\rTotal simulations runs read: ${if(readFinished) LIGHT_GREEN else ""}$read$RESET / $total; " +
            "Sliced: ${if(sliceFinished) LIGHT_GREEN else ""}$sliced$RESET / $total;   " +
            "BUFFER HEALTH: Reads: ${getColor(simBuff, simBuffSize)}$simBuff$RESET / $simBuffSize; " +
            "Slices: ${getColor(segBuff, segBuffSize)}$segBuff$RESET / $segBuffSize; " +
            "Finished: $finished                         ")
  }

  private fun getColor(value: Int, limit: Int): String =
      when (value) {
        limit -> LIGHT_GREEN
        0 -> RED
        in (UPPER_QUANTILE * limit).toInt()..limit -> GREEN
        in 0..(LOWER_QUANTILE * limit).toInt() -> ORANGE
        else -> YELLOW
      }
}
