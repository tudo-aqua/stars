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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * This singleton holds the current date and time at the start of the application and the logging
 * policy. It is used to persist a consistent folder name for all exported files.
 */
object ApplicationConstantsHolder {
  /** Holds the [LocalDateTime] at the start of the application. */
  private val applicationStartTime: LocalDateTime = LocalDateTime.now()

  /** Holds the [LocalDateTime] at the start of the application in the yyyy-MM-dd-HH-mm format. */
  val applicationStartTimeString: String =
      applicationStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"))

  /**
   * Holds the logging policy. Only affects instances of classes implementing Loggable created after
   * setting the policy.
   */
  var logging: Boolean = true
}
