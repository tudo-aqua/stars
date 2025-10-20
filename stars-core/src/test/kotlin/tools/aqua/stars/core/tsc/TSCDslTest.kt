/*
 * Copyright 2025 The STARS Project Authors
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

package tools.aqua.stars.core.tsc

import kotlin.test.Test
import kotlin.test.assertEquals
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.metrics.E
import tools.aqua.stars.core.tsc.builder.tsc

typealias E = SimpleEntity

typealias T = SimpleTickData

typealias U = SimpleTickDataUnit

typealias D = SimpleTickDataDifference

/** Tests the DSL for the [TSC]. */
class TSCDslTest {

  /** Tests the correct creation of a [TSC] with no identifier. */
  @Test
  fun testNoIdentifier() {
    val tsc = tsc<E, T, U, D> { all("") }
    assertEquals("TSC", tsc.identifier)
  }

  /** Tests the correct creation of a [TSC] with an identifier. */
  @Test
  fun testIdentifier() {
    val identifier = "TSC Identifier"
    val tsc = tsc<E, T, U, D>(identifier = identifier) { all("") }
    assertEquals(identifier, tsc.identifier)
  }

  /** Tests the correct creation of a [TSC] with an empty identifier. */
  @Test
  fun testEmptyIdentifier() {
    val identifier = ""
    val tsc = tsc<E, T, U, D>(identifier = identifier) { all("") }
    assertEquals(identifier, tsc.identifier)
  }
}
