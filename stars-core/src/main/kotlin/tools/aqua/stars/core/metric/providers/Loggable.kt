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

package tools.aqua.stars.core.metric.providers

import java.io.File
import java.util.logging.*
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.activeLoggers

/** This interface can be implemented to be able to log data into the stdout and log files. */
@Suppress("unused")
interface Loggable {
  /** Holds the identifier (name) for this logger. */
  val loggerIdentifier: String

  /** Holds the [Logger] reference for this class. */
  val logger: Logger

  /**
   * Log the given [message] with Loglevel: [Level.SEVERE].
   *
   * @param message The message that should be logged.
   */
  fun logSevere(message: Any? = "") {
    logger.severe(message.toString())
    println("\u001B[31m$message\u001B[0m")
  }

  /**
   * Log the given [message] with Loglevel: [Level.SEVERE]. This function calls [logSevere].
   *
   * @param message The message that should be logged.
   */
  fun logError(message: Any? = "") {
    logSevere(message)
  }

  /**
   * Log the given [message] with Loglevel: [Level.WARNING].
   *
   * @param message The message that should be logged.
   */
  fun logWarning(message: Any? = "") {
    logger.warning(message.toString())
    println("\u001B[33m$message\u001B[0m")
  }

  /**
   * Log the given [message] with Loglevel: [Level.INFO].
   *
   * @param message The message that should be logged.
   */
  fun logInfo(message: Any? = "") {
    logger.info(message.toString())
    println(message)
  }

  /**
   * Log the given [message] with Loglevel: [Level.FINE].
   *
   * @param message The message that should be logged.
   */
  fun logFine(message: Any? = "") {
    logger.fine(message.toString())
  }

  /**
   * Log the given [message] with Loglevel: [Level.FINER].
   *
   * @param message The message that should be logged.
   */
  fun logFiner(message: Any? = "") {
    logger.finer(message.toString())
  }

  /**
   * Log the given [message] with Loglevel: [Level.FINEST].
   *
   * @param message The message that should be logged.
   */
  fun logFinest(message: Any? = "") {
    logger.finest(message.toString())
  }

  /** Close all [logger] [Handler]s to prevent ".lck" files to remain. */
  fun closeLogger() {
    logger.handlers.forEach { it.close() }
  }

  /** Provides static function for the creation of [Logger]s. */
  companion object {
    /**
     * Creates a [Logger] with a [FileHandler] each for [Level.SEVERE], [Level.WARNING],
     * [Level.INFO], [Level.FINE], [Level.FINER] and [Level.FINEST].
     *
     * @return A [Logger] with the predefined [FileHandler].
     */
    fun getLogger(name: String): Logger = run {
      // https://www.logicbig.com/tutorials/core-java-tutorial/logging/customizing-default-format.html
      System.setProperty("java.util.logging.SimpleFormatter.format", "%5\$s %n")

      val currentTimeAndDate = ApplicationConstantsHolder.applicationStartTimeString
      val logFolderFile =
          File("${ApplicationConstantsHolder.logFolder}/$currentTimeAndDate/metrics/$name").also {
            it.mkdirs()
          }
      val file = "$logFolderFile/$name-${currentTimeAndDate}"

      return@run Logger.getAnonymousLogger()
          .apply {
            useParentHandlers = false
            level = Level.FINEST

            addHandler(getLoggerHandler(file, Level.SEVERE))
            addHandler(getLoggerHandler(file, Level.WARNING))
            addHandler(getLoggerHandler(file, Level.INFO))
            addHandler(getLoggerHandler(file, Level.FINE))
            addHandler(getLoggerHandler(file, Level.FINER))
            addHandler(getLoggerHandler(file, Level.FINEST))
          }
          .also { activeLoggers.add(it) }
    }

    private fun getLoggerHandler(file: String, level: Level) =
        FileHandler("$file-${level.name.lowercase()}.txt").apply {
          this.level = level
          this.formatter = SimpleFormatter()
        }
  }
}
