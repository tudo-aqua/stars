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
import kotlin.time.Duration
import kotlinx.serialization.json.Json

/**
 * This singleton holds the current date and time at the start of the application and the log
 * folder. It is used to persist a consistent folder name for all exported files.
 */
object ApplicationConstantsHolder {
  /** Holds the [LocalDateTime] at the start of the application. */
  val applicationStartTime: LocalDateTime = LocalDateTime.now()

  /** Holds the [LocalDateTime] at the end of the experiment. */
  var experimentEndTime: LocalDateTime? = null

  /** Holds the total time spent on evaluation. */
  var totalEvaluationTime: Duration = Duration.ZERO

  /** Holds the total time spent on segment evaluation. */
  var totalSegmentEvaluationTime: Duration = Duration.ZERO

  /** Holds the [LocalDateTime] at the start of the application in the yyyy-MM-dd-HH-mm format. */
  val applicationStartTimeString: String =
      applicationStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))

  /** Separator for console output. */
  const val CONSOLE_SEPARATOR =
      "===================================================================================================="
  /** Indent for console output. */
  const val CONSOLE_INDENT = "                         "

  /** Log folder directory for analysis result logs. */
  private const val ANALYSIS_LOG_FOLDER = "analysis-result-logs"

  /** Log folder directory for result logs produced in test runs. */
  private const val TEST_LOG_FOLDER = "test-result-logs"

  /** Folder directory for serialized metric results produced in evaluation. */
  private const val SERIALIZED_RESULTS_FOLDER = "serialized-results"

  /** Folder directory for serialized compared results produced in evaluation. */
  private const val COMPARED_RESULTS_FOLDER = "compared-results"

  /** File name for the metadata file. */
  private const val METADATA_FILE_NAME = "experiment_run_metadata"

  /** Folder directory for serialized baseline result data set. */
  var baselineDirectory = "baseline"

  /** May hold the command that was used to execute the application. */
  var executionCommand: String = ""

  /** Folder directory for serialized previous evaluation result. */
  const val PREVIOUS_EVALUATION_SERIALIZED_RESULT_IDENTIFIER = "previous-evaluation"

  /** Indicates whether the application is running in test mode. */
  val isTestRun: Boolean
    get() =
        try {
          Class.forName("org.junit.jupiter.api.Test")
          true
        } catch (_: ClassNotFoundException) {
          false
        }

  /** Holds the folder name for the logs. */
  val logFolder: String = if (isTestRun) TEST_LOG_FOLDER else ANALYSIS_LOG_FOLDER

  /** Holds the [MutableList] of all currently registered [Logger]s. */
  val activeLoggers: MutableList<Logger> = mutableListOf()

  /** Holds the folder name for the logs. */
  val serializedResultsFolder: String =
      if (isTestRun) "test-$SERIALIZED_RESULTS_FOLDER" else SERIALIZED_RESULTS_FOLDER

  /** Holds the folder name for the logs. */
  val comparedResultsFolder: String =
      if (isTestRun) "test-$COMPARED_RESULTS_FOLDER" else COMPARED_RESULTS_FOLDER

  /** Holds the [Json] configuration that is used throughout the project. */
  val jsonConfiguration = Json {
    prettyPrint = true
    isLenient = true
  }

  init {
    Runtime.getRuntime()
        .addShutdownHook(
            Thread {
              // Close loggers
              LogManager.getLogManager().reset()
              activeLoggers.forEach { it.handlers.forEach { handler -> handler.close() } }

              // Delete test log folders
              File(TEST_LOG_FOLDER).deleteRecursively()
              File("test-$SERIALIZED_RESULTS_FOLDER").deleteRecursively()
              File("test-$COMPARED_RESULTS_FOLDER").deleteRecursively()
              File("test-$baselineDirectory").deleteRecursively()
            })
  }

  /**
   * Writes the experiment run metadata into the file [METADATA_FILE_NAME] in the serialization
   * folders.
   */
  fun writeMetaInfo(directory: String): File =
      File("$directory$METADATA_FILE_NAME.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText(
            """
        ====================================================================================================
        Experiment run metadata
        ====================================================================================================
        Experiment start              : ${applicationStartTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}
        Experiment end                : ${experimentEndTime?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) ?: "Not finished"}
        Total evaluation time         : $totalEvaluationTime
        Total segment evaluation time : $totalSegmentEvaluationTime
        Execution command             : ${executionCommand
          .prependIndent("                                        ").trimStart()}
                                                  
        
        ====================================================================================================
        System information
        ====================================================================================================
        User                          : ${System.getProperty("user.name")}
        Hostname                      : ${java.net.InetAddress.getLocalHost().hostName}
        OS                            : ${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}
        CPU                           : ${Runtime.getRuntime().availableProcessors()} cores
        Memory                        : ${Runtime.getRuntime().maxMemory() / 1024 / 1024} MB
        Java version                  : ${System.getProperty("java.version")}
        Java vendor                   : ${System.getProperty("java.vendor")}
      """
                .trimIndent())
      }
}
