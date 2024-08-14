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
import org.junit.jupiter.api.assertDoesNotThrow
import tools.aqua.stars.core.metric.providers.Stateful

class SerializableListResultTest {

  @Test
  fun `Test serialization of simple typed list of type 'Int'`() {
    val simpleObject = TypedClass<Int>()
    assertDoesNotThrow { simpleObject.getSerializableResults().getJsonString() }
  }

  private inner class TypedClass<T> : Stateful {
    val stateList: List<T> = emptyList()

    override fun getState(): List<T> = stateList

    override fun printState() {
      println("${getState()}")
    }

    override fun getSerializableResults(): SerializableListResult<T> =
        SerializableListResult(getState())
  }
}
