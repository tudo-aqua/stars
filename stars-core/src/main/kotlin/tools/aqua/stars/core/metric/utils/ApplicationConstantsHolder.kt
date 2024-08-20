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

package tools.aqua.stars.core.metric.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlinx.serialization.json.Json

/**
 * This singleton holds the current date and time at the start of the application and the log
 * folder. It is used to persist a consistent folder name for all exported files.
 */
object ApplicationConstantsHolder {
  /** Holds the [LocalDateTime] at the start of the application. */
  private val applicationStartTime: LocalDateTime = LocalDateTime.now()

  /** Holds the [LocalDateTime] at the start of the application in the yyyy-MM-dd-HH-mm format. */
  val applicationStartTimeString: String =
      applicationStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))

  /** Log folder directory for analysis result logs. */
  private const val ANALYSIS_LOG_FOLDER = "analysis-result-logs"

  /** Log folder directory for result logs produced in test runs. */
  private const val TEST_LOG_FOLDER = "test-result-logs"

  /** Folder directory for serialized metric results produced in evaluation. */
  private const val SERIALIZED_RESULTS_FOLDER = "serialized-results"

  /** Folder directory for serialized compared results produced in evaluation. */
  private const val COMPARED_RESULTS_FOLDER = "compared-results"

  /** Folder directory for serialized ground-truth result data set. */
  const val GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER = "ground-truth"

  /** Folder directory for serialized previous evaluation result. */
  const val PREVIOUS_EVALUATION_SERIALIZED_RESULT_IDENTIFIER = "previous-evaluation"

  /** Holds the folder name for the logs. */
  val logFolder: String
    get() = if (isTestRun()) TEST_LOG_FOLDER else ANALYSIS_LOG_FOLDER

  /** Holds the [MutableList] of all currently registered [Logger]s. */
  val activeLoggers: MutableList<Logger> = mutableListOf()

  /** Holds the folder name for the logs. */
  val serializedResultsFolder: String
    get() = if (isTestRun()) "test-$SERIALIZED_RESULTS_FOLDER" else SERIALIZED_RESULTS_FOLDER

  /** Holds the folder name for the logs. */
  val comparedResultsFolder: String
    get() = if (isTestRun()) "test-$COMPARED_RESULTS_FOLDER" else COMPARED_RESULTS_FOLDER

  /** Holds the [Json] configuration that is used throughout the project. */
  val jsonConfiguration = Json {
    prettyPrint = true
    isLenient = true
  }

  /**
   * Holds the aggregated [Boolean] verdict of all compared results with the ground-truth data.
   * Setting a new value will be conjugated with the old value such that a verdict 'false' may not
   * be changed to 'true' again.
   */
  var resultsReproducedFromGroundTruth: Boolean? = null
    set(value) {
      when {
        field == null -> field = value
        field != null && value != null -> field = field ?: false && value
      }
    }

  val resultsReproducedFromGroundTruthFile =
      File("$comparedResultsFolder/latest-run-reproduced-from-ground-truth.verdict")

  /**
   * Holds the aggregated [Boolean] verdict of all compared results with the previous evaluation
   * results. Setting a new value will be conjugated with the old value such that a verdict 'false'
   * may not be changed to 'true' again.
   */
  var resultsReproducedFromPreviousRun: Boolean? = null
    set(value) {
      when {
        field == null -> field = value
        field != null && value != null -> field = field ?: false && value
      }
    }

  val resultsReproducedFromPreviousRunVerdictFile =
      File("$comparedResultsFolder/latest-run-reproduced-from-previous-run.verdict")

  init {
    resultsReproducedFromGroundTruthFile.delete()
    resultsReproducedFromPreviousRunVerdictFile.delete()

    Runtime.getRuntime()
        .addShutdownHook(
            Thread {
              // Close loggers
              LogManager.getLogManager().reset()
              activeLoggers.forEach { it.handlers.forEach { handler -> handler.close() } }

              // Save comparison verdicts
              if (resultsReproducedFromGroundTruth != null) {
                resultsReproducedFromGroundTruthFile.writeText(
                    resultsReproducedFromGroundTruth.toString())
                File(
                        "$comparedResultsFolder/$applicationStartTimeString/verdicts/reproduced-from-ground-truth.verdict")
                    .writeText(resultsReproducedFromGroundTruth.toString())
              }

              if (resultsReproducedFromPreviousRun != null) {
                resultsReproducedFromPreviousRunVerdictFile.writeText(
                    resultsReproducedFromPreviousRun.toString())
                File(
                        "$comparedResultsFolder/$applicationStartTimeString/verdicts/reproduced-from-previous-run.verdict")
                    .writeText(resultsReproducedFromPreviousRun.toString())
              }

              // Delete test log folders
              File(TEST_LOG_FOLDER).deleteRecursively()
              File("test-$SERIALIZED_RESULTS_FOLDER").deleteRecursively()
              File("test-$COMPARED_RESULTS_FOLDER").deleteRecursively()
              File("test-$GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER").deleteRecursively()
            })
  }

  /** Indicates whether the application is running in test mode. */
  private fun isTestRun(): Boolean =
      try {
        Class.forName("org.junit.jupiter.api.Test")
        true
      } catch (e: ClassNotFoundException) {
        false
      }
}
