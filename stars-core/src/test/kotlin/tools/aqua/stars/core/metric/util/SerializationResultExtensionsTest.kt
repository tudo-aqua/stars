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
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict.*
import tools.aqua.stars.core.metric.serialization.extensions.compareTo

/** Tests the functionality of the function [compareTo] of [List] of [SerializableResult]s. */
class SerializationResultExtensionsTest {
  /** Tests the correct functionality of [compareTo] where the current result list is empty. */
  @Test
  fun `Test compareTo to emptyList with SerializationResult`() {
    val currentResults = emptyList<SerializableIntResult>()
    val compareToResult =
        listOf(SerializableIntResult(identifier = "result 1", source = "Test case 2", value = 1))

    val compareResult = currentResults.compareTo(compareToResult)

    assertEquals(1, compareResult.size)
    assertEquals(MISSING_METRIC_SOURCE, compareResult.first { it.source == "Test case 2" }.verdict)
  }

  /** Tests the correct functionality of [compareTo] where the previous result list is empty. */
  @Test
  fun `Test compareTo with multiple elements lists of SerializationResults with empty calling list`() {
    val currentResults =
        listOf(SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1))
    val previousResults: List<SerializableIntResult> = emptyList()

    val compareResults = currentResults.compareTo(previousResults)

    assertNotNull(compareResults)
    assertEquals(1, compareResults.size)

    val equalResults = compareResults.firstOrNull { it.verdict == EQUAL_RESULTS }
    assertNull(equalResults)

    val notMatchingResults = compareResults.first { it.verdict == NEW_METRIC_SOURCE }
    assertNotNull(notMatchingResults)
    assertEquals("result 2", notMatchingResults.identifier)
    assertEquals("Test case 1", notMatchingResults.source)
  }

  /** Tests the correct functionality of [compareTo] where both [SerializableResult]s are equal. */
  @Test
  fun `Test compareTo with single element lists of SerializationResult that matches`() {
    val currentResults =
        listOf(SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1))
    val previousResults =
        listOf(SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1))

    val compareResults = currentResults.compareTo(previousResults)

    assertNotNull(compareResults)
    assertEquals(1, compareResults.size)
    compareResults.forEach { assertEquals(EQUAL_RESULTS, it.verdict) }
  }

  /**
   * Tests the correct functionality of [compareTo] where all [SerializableResult]s are have a match
   * and are equal.
   */
  @Test
  fun `Test compareTo with multiple elements lists of SerializationResult that matches`() {
    val currentResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1))
    val previousResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1))

    val compareResults = currentResults.compareTo(previousResults)

    assertNotNull(compareResults)
    assertEquals(2, compareResults.size)
    compareResults.forEach { assertEquals(EQUAL_RESULTS, it.verdict) }
  }

  /**
   * Tests the correct functionality of [compareTo] where all [SerializableResult]s are have a match
   * and are equal but are differently sorted.
   */
  @Test
  fun `Test compareTo with multiple elements lists of different order of SerializationResult that matches`() {
    val currentResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1))
    val previousResults =
        listOf(
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1))

    val compareResults = currentResults.compareTo(previousResults)

    assertNotNull(compareResults)
    assertEquals(2, compareResults.size)
    compareResults.forEach { assertEquals(EQUAL_RESULTS, it.verdict) }
  }

  /**
   * Tests the correct functionality of [compareTo] where one [SerializableResult] has a match and
   * is equal and one [SerializableResult] is new.
   */
  @Test
  fun `Test compareTo with multiple elements lists of different order of SerializationResult where one element is missing`() {
    val currentResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1))
    val previousResults =
        listOf(SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1))

    val compareResults = currentResults.compareTo(previousResults)

    assertNotNull(compareResults)
    assertEquals(2, compareResults.size)

    val equalResults = compareResults.firstOrNull { it.verdict == EQUAL_RESULTS }
    assertNotNull(equalResults)
    assertEquals("result 1", equalResults.identifier)
    assertEquals("Test case 1", equalResults.source)

    val notMatchingResults = compareResults.first { it.verdict == NEW_IDENTIFIER }
    assertNotNull(notMatchingResults)
    assertEquals("result 2", notMatchingResults.identifier)
    assertEquals("Test case 1", notMatchingResults.source)
  }

  /**
   * Tests the correct functionality of [compareTo] where one [SerializableResult] has a match and
   * is equal and one [SerializableResult] is missing.
   */
  @Test
  fun `Test compareTo with multiple elements lists of different order of SerializationResult where one element is missing case 2`() {
    val currentResults =
        listOf(SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1))
    val previousResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1))

    val compareResults = currentResults.compareTo(previousResults)

    assertNotNull(compareResults)
    assertEquals(2, compareResults.size)

    val equalResults = compareResults.firstOrNull { it.verdict == EQUAL_RESULTS }
    assertNotNull(equalResults)
    assertEquals("result 1", equalResults.identifier)
    assertEquals("Test case 1", equalResults.source)

    val notMatchingResults = compareResults.first { it.verdict == MISSING_IDENTIFIER }
    assertNotNull(notMatchingResults)
    assertEquals("result 2", notMatchingResults.identifier)
    assertEquals("Test case 1", notMatchingResults.source)
  }

  /**
   * Tests the correct functionality of [compareTo] where both [SerializableResult]s have a match
   * and only one has the same value.
   */
  @Test
  fun `Test compareTo with multiple elements lists of SerializationResult where one does not match`() {
    val currentResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1))
    val previousResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 2))

    val compareResults = currentResults.compareTo(previousResults)

    assertNotNull(compareResults)
    assertEquals(2, compareResults.size)

    val equalResults = compareResults.firstOrNull { it.verdict == EQUAL_RESULTS }
    assertNotNull(equalResults)
    assertEquals("result 1", equalResults.identifier)
    assertEquals("Test case 1", equalResults.source)

    val notEqualResults = compareResults.first { it.verdict == NOT_EQUAL_RESULTS }
    assertNotNull(notEqualResults)
    assertEquals("result 2", notEqualResults.identifier)
    assertEquals("Test case 1", notEqualResults.source)
  }

  /**
   * Tests the correct functionality of [compareTo] where both [SerializableResult] have a match and
   * have different values.
   */
  @Test
  fun `Test compareTo with multiple elements lists of SerializationResult where both do not match`() {
    val currentResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 1),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 1))
    val previousResults =
        listOf(
            SerializableIntResult(identifier = "result 1", source = "Test case 1", value = 2),
            SerializableIntResult(identifier = "result 2", source = "Test case 1", value = 2))

    val compareResults = currentResults.compareTo(previousResults)

    assertNotNull(compareResults)
    assertEquals(2, compareResults.size)

    compareResults.forEach { assertEquals(NOT_EQUAL_RESULTS, it.verdict) }
  }
}
