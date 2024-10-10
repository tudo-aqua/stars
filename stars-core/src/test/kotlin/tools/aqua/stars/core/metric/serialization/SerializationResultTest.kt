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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Tests the functionality for the [SerializableResult] class. */
class SerializationResultTest {

  /**
   * Tests the correct functionality of [compareTo] of [SerializableResult]s where the values match.
   */
  @Test
  fun `Test compareTo with SerializationResult that matches`() {
    val result = SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1)
    val compareToResult =
        SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1)

    val compareResult = result.compareTo(compareToResult)

    assertNotNull(compareResult)
    assertEquals(SerializableResultComparisonVerdict.EQUAL_RESULTS, compareResult.verdict)
    assertEquals(result.value.toString(), compareResult.newValue)
    assertEquals(compareToResult.value.toString(), compareResult.oldValue)
    assertEquals(result.identifier, compareResult.identifier)
    assertEquals(result.source, compareResult.source)
  }

  /**
   * Tests the correct functionality of [compareTo] of [SerializableResult]s where the values are
   * different.
   */
  @Test
  fun `Test compareTo with SerializationResult that has different value`() {
    val result = SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1)
    val compareToResult =
        SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 2)

    val compareResult = result.compareTo(compareToResult)

    assertNotNull(compareResult)
    assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, compareResult.verdict)
    assertEquals(result.value.toString(), compareResult.newValue)
    assertEquals(compareToResult.value.toString(), compareResult.oldValue)
    assertEquals(result.identifier, compareResult.identifier)
    assertEquals(result.source, compareResult.source)
  }

  /**
   * Tests the correct functionality of [compareTo] of [SerializableResult]s where the identifiers
   * are different.
   */
  @Test
  fun `Test compareTo with SerializationResult that has different identifier`() {
    val result = SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1)
    val compareToResult =
        SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1)

    val compareResult = result.compareTo(compareToResult)

    assertNull(compareResult)
  }

  /**
   * Tests the correct functionality of [compareTo] of [SerializableResult]s where the sources are
   * different.
   */
  @Test
  fun `Test compareTo with SerializationResult that has different source`() {
    val result = SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1)
    val compareToResult =
        SerializableIntResult(identifier = "result 1", source = "Test case 2", value = 1)

    val compareResult = result.compareTo(compareToResult)

    assertNull(compareResult)
  }
}
