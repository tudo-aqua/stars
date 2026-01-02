/*
 * Copyright 2023-2026 The STARS Project Authors
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

package tools.aqua.stars.core.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metrics.evaluation.TickCountMetric
import tools.aqua.stars.core.serialization.extensions.getJsonString
import tools.aqua.stars.core.serialization.extensions.writeSerializedResults
import tools.aqua.stars.core.utils.*

/** Tests the [SerializableResult] sealed class implementation for the [SerializableLongResult]. */
class SerializableLongResultTest {

  /** Tests that the [SerializableIntResult] is correctly (de)serialized. */
  @Test
  fun `Test simple serialization`() {
    val simpleTick = SimpleTickData()

    val tickCountMetric =
        TickCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertEquals(tickCountMetric.evaluate(simpleTick), 1)
    assertEquals(tickCountMetric.evaluate(simpleTick), 2)
    tickCountMetric.writeSerializedResults()
  }

  /** Tests that the change of the [SerializableIntResult] is correctly (de)serialized. */
  @Test
  fun `Test changed result value`() {
    val simpleTick = SimpleTickData()

    val tickCountMetric =
        TickCountMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleTickDataUnit,
            SimpleTickDataDifference,
        >()

    assertEquals(tickCountMetric.evaluate(simpleTick), 1)
    val serializedResultBaseline = tickCountMetric.getSerializableResults()
    val deserializedResultBaseline =
        serializedResultBaseline.map { getJsonContentFromString(it.getJsonString()) }

    assertEquals(tickCountMetric.evaluate(simpleTick), 2)
    val serializedResultCompare = tickCountMetric.getSerializableResults()
    val deserializedResultCompare =
        serializedResultCompare.map { getJsonContentFromString(it.getJsonString()) }

    assertNotEquals(deserializedResultBaseline, deserializedResultCompare)
  }
}
