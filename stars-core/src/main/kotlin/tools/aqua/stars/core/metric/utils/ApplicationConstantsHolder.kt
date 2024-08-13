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

  /** Holds the folder name for the logs. */
  val logFolder: String
    get() = if (isTestRun()) TEST_LOG_FOLDER else ANALYSIS_LOG_FOLDER

  init {
    Runtime.getRuntime()
        .addShutdownHook(
            Thread {
              LogManager.getLogManager().reset()
              File(TEST_LOG_FOLDER).deleteRecursively()
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
