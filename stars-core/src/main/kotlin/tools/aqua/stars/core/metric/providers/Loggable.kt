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

package tools.aqua.stars.core.metric.providers

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.*

interface Loggable {
  /** Holds the [Logger] reference for this class */
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

  /** Close all [logger] [Handler]s to prevent ".lck" files to remain */
  fun closeLogger() {
    logger.handlers.forEach { it.close() }
  }

  companion object {
    /**
     * Creates a [Logger] with a [FileHandler] each for [Level.SEVERE], [Level.WARNING],
     * [Level.INFO], [Level.FINE], [Level.FINER] and [Level.FINEST].
     *
     * @return A [Logger] with the predefined [FileHandler].
     */
    fun getLogger(name: String): Logger = run {
      // https://www.logicbig.com/tutorials/core-java-tutorial/logging/customizing-default-format.html
      // System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\$tF %1\$tT] [%4$-7s]
      // %5\$s
      // %n")
      System.setProperty("java.util.logging.SimpleFormatter.format", "%5\$s %n")

      val logger = Logger.getAnonymousLogger()
      logger.useParentHandlers = false
      logger.level = Level.FINEST

      val currentTimeAndDate =
          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"))

      val logFolderFile = File("analysis-result-logs/$currentTimeAndDate/metrics/$name")
      logFolderFile.mkdirs()

      val severeFileHandler = FileHandler("$logFolderFile/$name-${currentTimeAndDate}-severe.txt")
      severeFileHandler.level = Level.SEVERE
      severeFileHandler.formatter = SimpleFormatter()
      logger.addHandler(severeFileHandler)

      val warningFileHandler = FileHandler("$logFolderFile/$name-${currentTimeAndDate}-warning.txt")
      warningFileHandler.level = Level.WARNING
      warningFileHandler.formatter = SimpleFormatter()
      logger.addHandler(warningFileHandler)

      val infoFileHandler = FileHandler("$logFolderFile/$name-${currentTimeAndDate}-info.txt")
      infoFileHandler.level = Level.INFO
      infoFileHandler.formatter = SimpleFormatter()
      logger.addHandler(infoFileHandler)

      val fineFileHandler = FileHandler("$logFolderFile/$name-${currentTimeAndDate}-fine.txt")
      fineFileHandler.level = Level.FINE
      fineFileHandler.formatter = SimpleFormatter()
      logger.addHandler(fineFileHandler)

      val finerFileHandler = FileHandler("$logFolderFile/$name-${currentTimeAndDate}-finer.txt")
      finerFileHandler.level = Level.FINER
      finerFileHandler.formatter = SimpleFormatter()
      logger.addHandler(finerFileHandler)

      val finestFileHandler = FileHandler("$logFolderFile/$name-${currentTimeAndDate}-finest.txt")
      finerFileHandler.level = Level.FINEST
      finerFileHandler.formatter = SimpleFormatter()
      logger.addHandler(finestFileHandler)

      logger
    }
  }
}
