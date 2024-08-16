/*
 * Copyright 2024 The STARS Project Authors
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

package tools.aqua.stars.core.metric.util

import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.groundTruthFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder

class SerializationHelpersTest {

  /** Clear all existing test folders for a clean setup for each test. */
  @BeforeTest
  fun `Clean up all existing test folders`() {
    File(serializedResultsFolder).deleteRecursively()
    File(comparedResultsFolder).deleteRecursively()
    File(groundTruthFolder).deleteRecursively()
  }

  @Test
  fun `Test correct saving of SerializableResult`() {
    var s = ""
  }
}
