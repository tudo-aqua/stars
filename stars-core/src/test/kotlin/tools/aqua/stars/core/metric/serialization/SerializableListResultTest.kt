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

package tools.aqua.stars.core.metric.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertDoesNotThrow
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.utils.writeSerializedResults

class SerializableListResultTest {

  // region Test of simple serializable list
  @Test
  fun `Test serialization of explicitly typed list of type 'Int' with values`() {
    val simpleObject = IntListClass(listOf(2, 3))
    val simpleObjectResult = simpleObject.getSerializableResults()

    assertEquals(2, simpleObject.stateList.size)
    assertDoesNotThrow { simpleObject.writeSerializedResults() }

    val serializedResult = simpleObjectResult.map { it.getJsonString() }
    val deserializedResult =
        serializedResult.map { SerializableResult.getJsonContentFromString(it) }

    assertEquals(simpleObjectResult, deserializedResult)
  }

  @Test
  fun `Test serialization of explicitly typed list of type 'Int' with no values`() {
    val simpleObject = IntListClass(emptyList())
    val simpleObjectResult = simpleObject.getSerializableResults()

    assertEquals(0, simpleObject.stateList.size)

    val serializedResult = simpleObjectResult.map { it.getJsonString() }
    val deserializedResult =
        serializedResult.map { SerializableResult.getJsonContentFromString(it) }

    assertEquals(simpleObjectResult, deserializedResult)
  }

  private inner class IntListClass(val stateList: List<Int>) : Serializable {
    override fun getSerializableResults(): List<SerializableIntListResult> =
        listOf(
            SerializableIntListResult(
                stateList, identifier = "IntListClass", source = "IntListClass"))
  }

  // endregion

  // region Test of simple nested kotlin datastructures in serializable list
  @Test
  fun `Test serialization of list of pairs of two Int values`() {
    val simpleObject = IntPairClass(listOf(2 to 3, 3 to 4))
    val simpleObjectResult = simpleObject.getSerializableResults()

    assertEquals(2, simpleObject.stateList.size)
    assertDoesNotThrow { simpleObject.writeSerializedResults() }

    val serializedResult = simpleObjectResult.map { it.getJsonString() }
    val deserializedResult =
        serializedResult.map { SerializableResult.getJsonContentFromString(it) }

    assertEquals(simpleObjectResult, deserializedResult)
  }

  private inner class IntPairClass(val stateList: List<Pair<Int, Int>>) : Serializable {
    override fun getSerializableResults(): List<SerializableIntPairListResult> =
        listOf(
            SerializableIntPairListResult(
                stateList, identifier = "IntPairClass", source = "IntPairClass"))
  }

  // endregion
}
