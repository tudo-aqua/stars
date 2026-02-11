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

package tools.aqua.stars.importer.carla

import kotlin.io.path.Path
import kotlin.test.Test
import org.junit.jupiter.api.assertDoesNotThrow
import tools.aqua.stars.core.evaluation.TSCEvaluation
import tools.aqua.stars.core.metrics.evaluation.InvalidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metrics.evaluation.MissedTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metrics.evaluation.TickCountMetric
import tools.aqua.stars.core.metrics.evaluation.TotalTickDifferenceMetric
import tools.aqua.stars.core.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.tsc.builder.tsc
import tools.aqua.stars.data.av.dataclasses.Actor
import tools.aqua.stars.data.av.dataclasses.TickData
import tools.aqua.stars.data.av.dataclasses.TickDataDifferenceSeconds
import tools.aqua.stars.data.av.dataclasses.TickDataUnitSeconds

/** Tests the correct loading and evaluation of Carla recordings. */
class TestCarlaRecordings {

  /** Test the correct loading and evaluation of Town01. */
  @Test
  fun `Test correct loading and evaluation of Town01`() {
    val evaluation = setupEvaluation()
    val tickSequence =
        loadTicks(
            mapOf(
                Path("src/test/resources/Town01/static_data_Town01.zip") to
                    listOf(Path("src/test/resources/Town01/dynamic_data_recording_seed_2.zip"))
            ),
            useEveryVehicleAsEgo = true,
            bufferSize = 10,
        )
    assertDoesNotThrow { evaluation.runEvaluation(tickSequence) }
  }

  /** Test the correct loading and evaluation of Town02. */
  @Test
  fun `Test correct loading and evaluation of Town02`() {
    val evaluation = setupEvaluation()
    val tickSequence =
        loadTicks(
            mapOf(
                Path("src/test/resources/Town02/static_data_Town02.zip") to
                    listOf(Path("src/test/resources/Town02/dynamic_data_recording_seed_1.zip"))
            ),
            useEveryVehicleAsEgo = true,
            bufferSize = 10,
        )
    assertDoesNotThrow { evaluation.runEvaluation(tickSequence) }
  }

  /** Test the correct loading and evaluation of Town03. */
  @Test
  fun `Test correct loading and evaluation of Town03`() {
    val evaluation = setupEvaluation()
    val tickSequence =
        loadTicks(
            mapOf(
                Path("src/test/resources/Town03/static_data_Town03.zip") to
                    listOf(Path("src/test/resources/Town03/dynamic_data_recording_seed_7.zip"))
            ),
            useEveryVehicleAsEgo = true,
            bufferSize = 10,
        )
    assertDoesNotThrow { evaluation.runEvaluation(tickSequence) }
  }

  /** Test the correct loading and evaluation of Town05. */
  @Test
  fun `Test correct loading and evaluation of Town05`() {
    val evaluation = setupEvaluation()
    val tickSequence =
        loadTicks(
            mapOf(
                Path("src/test/resources/Town05/static_data_Town05.zip") to
                    listOf(Path("src/test/resources/Town05/dynamic_data_recording_seed_0.zip"))
            ),
            useEveryVehicleAsEgo = true,
            bufferSize = 10,
        )
    assertDoesNotThrow { evaluation.runEvaluation(tickSequence) }
  }

  /** Test the correct loading and evaluation of Town10HD. */
  @Test
  fun `Test correct loading and evaluation of Town10HD`() {
    val evaluation = setupEvaluation()
    val tickSequence =
        loadTicks(
            mapOf(
                Path("src/test/resources/Town10HD/static_data_Town10HD.zip") to
                    listOf(Path("src/test/resources/Town10HD/dynamic_data_recording_seed_5.zip"))
            ),
            useEveryVehicleAsEgo = true,
            bufferSize = 10,
        )
    assertDoesNotThrow { evaluation.runEvaluation(tickSequence) }
  }

  private fun setupEvaluation():
      TSCEvaluation<Actor, TickData, TickDataUnitSeconds, TickDataDifferenceSeconds> {
    val tsc =
        tsc<Actor, TickData, TickDataUnitSeconds, TickDataDifferenceSeconds> {
          all("TSCRoot") {
            exclusive("Weather") {
              leaf("Clear")
              leaf("Cloudy")
              leaf("Rainy")
            }
          }
        }

    return TSCEvaluation(
            writePlots = false,
            writePlotDataCSV = false,
            writeSerializedResults = false,
            compareToPreviousRun = false,
            tsc = tsc,
        )
        .apply {
          registerMetricProviders(
              ValidTSCInstancesPerTSCMetric(),
              InvalidTSCInstancesPerTSCMetric(),
              MissedTSCInstancesPerTSCMetric(),
              TotalTickDifferenceMetric(),
              TickCountMetric(),
          )
        }
  }
}
