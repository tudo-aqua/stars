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

import kotlin.test.*
import tools.aqua.stars.core.metric.serialization.SerializableIntResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict
import tools.aqua.stars.core.metric.utils.compareTo

class SerializationResultExtensionsTest {

  // region compareTo with SerializationResult as parameter
  @Test
  fun `Test compareTo with SerializationResult that matches`() {
    val resultList = listOf(SerializableIntResult(1, "result 1", "Test case 1"))
    val compareToResult = SerializableIntResult(1, "result 1", "Test case 1")

    val compareResult = resultList.compareTo(compareToResult)

    assertNotNull(compareResult)
    assertEquals(SerializableResultComparisonVerdict.EQUAL_RESULTS, compareResult.verdict)
  }

  @Test
  fun `Test compareTo of list of two IntResults with SerializationResult that matches`() {
    val resultList =
        listOf(
            SerializableIntResult(1, "result 1", "Test case 1"),
            SerializableIntResult(2, "result 2", "Test case 1"))
    val compareToResult = SerializableIntResult(1, "result 1", "Test case 1")

    val compareResult = resultList.compareTo(compareToResult)

    assertNotNull(compareResult)
    assertEquals(SerializableResultComparisonVerdict.EQUAL_RESULTS, compareResult.verdict)
  }

  @Test
  fun `Test compareTo with SerializationResult that has different value`() {
    val resultList = listOf(SerializableIntResult(1, "result 1", "Test case 1"))
    val compareToResult = SerializableIntResult(2, "result 1", "Test case 1")

    val compareResult = resultList.compareTo(compareToResult)

    assertNotNull(compareResult)
    assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, compareResult.verdict)
  }

  @Test
  fun `Test compareTo with SerializationResult that has different identifier`() {
    val resultList = listOf(SerializableIntResult(1, "result 1", "Test case 1"))
    val compareToResult = SerializableIntResult(1, "result 2", "Test case 1")

    val compareResult = resultList.compareTo(compareToResult)

    assertNull(compareResult)
  }

  @Test
  fun `Test compareTo with SerializationResult that has different source`() {
    val resultList = listOf(SerializableIntResult(1, "result 1", "Test case 1"))
    val compareToResult = SerializableIntResult(1, "result 1", "Test case 2")

    val compareResult = resultList.compareTo(compareToResult)

    assertNull(compareResult)
  }

  @Test
  fun `Test compareTo to emptyList with SerializationResult`() {
    val resultList = emptyList<SerializableIntResult>()
    val compareToResult = SerializableIntResult(1, "result 1", "Test case 2")

    val compareResult = resultList.compareTo(compareToResult)

    assertNull(compareResult)
  }

  // endregion

  // region compareTo with List<SerializationResult> as parameter
  //  @Test
  //  fun `Test compareTo with single element lists of SerializationResult that matches`() {
  //    val resultList = listOf(SerializableIntResult(1, "result 1", "Test case 1"))
  //    val compareToResults = listOf(SerializableIntResult(1, "result 1", "Test case 1"))
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(1, compareResults.size)
  //    compareResults.forEach {
  //      assertEquals(SerializableResultComparisonVerdict.EQUAL_RESULTS, it.verdict)
  //    }
  //  }
  //
  //  @Test
  //  fun `Test compareTo with multiple elements lists of SerializationResult that matches`() {
  //    val resultList =
  //        listOf(
  //            SerializableIntResult(1, "result 1", "Test case 1"),
  //            SerializableIntResult(1, "result 2", "Test case 1"))
  //    val compareToResults =
  //        listOf(
  //            SerializableIntResult(1, "result 1", "Test case 1"),
  //            SerializableIntResult(1, "result 2", "Test case 1"))
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(2, compareResults.size)
  //    compareResults.forEach {
  //      assertEquals(SerializableResultComparisonVerdict.EQUAL_RESULTS, it.verdict)
  //    }
  //  }
  //
  //  @Test
  //  fun `Test compareTo with multiple elements lists of different order of SerializationResult
  // that matches`() {
  //    val resultList =
  //        listOf(
  //            SerializableIntResult(1, "result 1", "Test case 1"),
  //            SerializableIntResult(1, "result 2", "Test case 1"))
  //    val compareToResults =
  //        listOf(
  //            SerializableIntResult(1, "result 2", "Test case 1"),
  //            SerializableIntResult(1, "result 1", "Test case 1"))
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(2, compareResults.size)
  //    compareResults.forEach {
  //      assertEquals(SerializableResultComparisonVerdict.EQUAL_RESULTS, it.verdict)
  //    }
  //  }
  //
  //  @Test
  //  fun `Test compareTo with multiple elements lists of different order of SerializationResult
  // where one element is missing`() {
  //    val resultList =
  //        listOf(
  //            SerializableIntResult(1, "result 1", "Test case 1"),
  //            SerializableIntResult(1, "result 2", "Test case 1"))
  //    val compareToResults = listOf(SerializableIntResult(1, "result 1", "Test case 1"))
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(2, compareResults.size)
  //
  //    val equalResults =
  //        compareResults.firstOrNull {
  //          it.verdict == SerializableResultComparisonVerdict.EQUAL_RESULTS
  //        }
  //    assertNotNull(equalResults)
  //    assertEquals("result 1", equalResults.identifier)
  //    assertEquals("Test case 1", equalResults.source)
  //
  //    val notMatchingResults =
  //        compareResults.first {
  //          it.verdict == SerializableResultComparisonVerdict.NO_MATCHING_RESULT
  //        }
  //    assertNotNull(notMatchingResults)
  //    assertEquals("result 2", notMatchingResults.identifier)
  //    assertEquals("Test case 1", notMatchingResults.source)
  //  }
  //
  //  @Test
  //  fun `Test compareTo with multiple elements lists of different order of SerializationResult
  // where one element is missing case 2`() {
  //    val resultList = listOf(SerializableIntResult(1, "result 1", "Test case 1"))
  //    val compareToResults =
  //        listOf(
  //            SerializableIntResult(1, "result 1", "Test case 1"),
  //            SerializableIntResult(1, "result 2", "Test case 1"))
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(2, compareResults.size)
  //
  //    val equalResults =
  //        compareResults.firstOrNull {
  //          it.verdict == SerializableResultComparisonVerdict.EQUAL_RESULTS
  //        }
  //    assertNotNull(equalResults)
  //    assertEquals("result 1", equalResults.identifier)
  //    assertEquals("Test case 1", equalResults.source)
  //
  //    val notMatchingResults =
  //        compareResults.first {
  //          it.verdict == SerializableResultComparisonVerdict.NO_MATCHING_RESULT
  //        }
  //    assertNotNull(notMatchingResults)
  //    assertEquals("result 2", notMatchingResults.identifier)
  //    assertEquals("Test case 1", notMatchingResults.source)
  //  }
  //
  //  @Test
  //  fun `Test compareTo with multiple elements lists of different order of SerializationResult
  // with empty calling list`() {
  //    val resultList: List<SerializableIntResult> = emptyList()
  //    val compareToResults = listOf(SerializableIntResult(1, "result 2", "Test case 1"))
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(1, compareResults.size)
  //
  //    val equalResults =
  //        compareResults.firstOrNull {
  //          it.verdict == SerializableResultComparisonVerdict.EQUAL_RESULTS
  //        }
  //    assertNull(equalResults)
  //
  //    val notMatchingResults =
  //        compareResults.first {
  //          it.verdict == SerializableResultComparisonVerdict.NO_MATCHING_RESULT
  //        }
  //    assertNotNull(notMatchingResults)
  //    assertEquals("result 2", notMatchingResults.identifier)
  //    assertEquals("Test case 1", notMatchingResults.source)
  //  }
  //
  //  @Test
  //  fun `Test compareTo with multiple elements lists of different order of SerializationResult
  // with empty argument list`() {
  //    val resultList = listOf(SerializableIntResult(1, "result 2", "Test case 1"))
  //    val compareToResults: List<SerializableIntResult> = emptyList()
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(1, compareResults.size)
  //
  //    val equalResults =
  //        compareResults.firstOrNull {
  //          it.verdict == SerializableResultComparisonVerdict.EQUAL_RESULTS
  //        }
  //    assertNull(equalResults)
  //
  //    val notMatchingResults =
  //        compareResults.first {
  //          it.verdict == SerializableResultComparisonVerdict.NO_MATCHING_RESULT
  //        }
  //    assertNotNull(notMatchingResults)
  //    assertEquals("result 2", notMatchingResults.identifier)
  //    assertEquals("Test case 1", notMatchingResults.source)
  //  }
  //
  //  @Test
  //  fun `Test compareTo with multiple elements lists of SerializationResult where one does not
  // match`() {
  //    val resultList =
  //        listOf(
  //            SerializableIntResult(1, "result 1", "Test case 1"),
  //            SerializableIntResult(1, "result 2", "Test case 1"))
  //    val compareToResults =
  //        listOf(
  //            SerializableIntResult(1, "result 1", "Test case 1"),
  //            SerializableIntResult(2, "result 2", "Test case 1"))
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(2, compareResults.size)
  //
  //    val equalResults =
  //        compareResults.firstOrNull {
  //          it.verdict == SerializableResultComparisonVerdict.EQUAL_RESULTS
  //        }
  //    assertNotNull(equalResults)
  //    assertEquals("result 1", equalResults.identifier)
  //    assertEquals("Test case 1", equalResults.source)
  //
  //    val notEqualResults =
  //        compareResults.first { it.verdict ==
  // SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS }
  //    assertNotNull(notEqualResults)
  //    assertEquals("result 2", notEqualResults.identifier)
  //    assertEquals("Test case 1", notEqualResults.source)
  //  }
  //
  //  @Test
  //  fun `Test compareTo with multiple elements lists of SerializationResult where both do not
  // match`() {
  //    val resultList =
  //        listOf(
  //            SerializableIntResult(1, "result 1", "Test case 1"),
  //            SerializableIntResult(1, "result 2", "Test case 1"))
  //    val compareToResults =
  //        listOf(
  //            SerializableIntResult(2, "result 1", "Test case 1"),
  //            SerializableIntResult(2, "result 2", "Test case 1"))
  //
  //    val compareResults = resultList.compareTo(compareToResults)
  //
  //    assertNotNull(compareResults)
  //    assertEquals(2, compareResults.size)
  //
  //    compareResults.forEach {
  //      assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, it.verdict)
  //    }
  //  }

  // endregion
}
