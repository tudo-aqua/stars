/*
 * Copyright 2023 The STARS Project Authors
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

package tools.aqua.stars.importer.carla

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Evaluation state tracker using [AtomicInteger] fields.
 *
 * @property totalSimulationRuns Total number of simulation run .zip files.
 */
data class EvaluationState(val totalSimulationRuns: Int) {
  /** Number of read simulation run .zip files. */
  val readSimulationRuns: AtomicInteger = AtomicInteger(0)

  /** Number of sliced simulation runs. */
  val slicedSimulationRuns: AtomicInteger = AtomicInteger(0)

  /** Number of simulation runs in the buffer. */
  val simulationRunsBuffer: AtomicInteger = AtomicInteger(0)

  /** Number of segments in the buffer. */
  val segmentsBuffer: AtomicInteger = AtomicInteger(0)

  /** Whether the calculation has finished. */
  val isFinished: AtomicBoolean = AtomicBoolean(false)

  /** Prints current evaluation progress. */
  fun print() {
    println(
        "Total simulations runs read: ${readSimulationRuns.get()} / $totalSimulationRuns; " +
            "Sliced: ${slicedSimulationRuns.get()} / $totalSimulationRuns; " +
            "Buffer Health: ${simulationRunsBuffer.get()} / ${segmentsBuffer.get()}")
  }
}
