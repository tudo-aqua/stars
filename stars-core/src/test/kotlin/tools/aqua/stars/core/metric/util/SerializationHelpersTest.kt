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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.*
import tools.aqua.stars.core.metric.serialization.SerializableIntResult
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict.EQUAL_RESULTS
import tools.aqua.stars.core.metric.serialization.extensions.getJsonString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.PREVIOUS_EVALUATION_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.baselineDirectory
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder
import tools.aqua.stars.core.metric.utils.getBaselineSerializationResultDirectory
import tools.aqua.stars.core.metric.utils.getPreviousSerializationResultDirectory
import tools.aqua.stars.core.metric.utils.saveAsJsonFile

/** Contains test functions for the SerializationHelpers.kt file. */
class SerializationHelpersTest {

  /** Clear all existing test folders for a clean setup for each test. */
  @BeforeTest
  fun `Clean up all existing test folders`() {
    File(serializedResultsFolder).deleteRecursively()
    File(comparedResultsFolder).deleteRecursively()
    File(baselineDirectory).deleteRecursively()
  }

  // region Tests for saveAsJsonFile(String, String)
  /** Tests the correct saving of a simple Json string with the [saveAsJsonFile] function. */
  @Test
  fun `Test correct saving of a simple Json string with saveAsJsonFile()`() {
    val actualFilePath = "$serializedResultsFolder/testFile.json"
    val actualFileContent = "{}"
    val actualFile = File(actualFilePath)

    val resultPath = actualFileContent.saveAsJsonFile(actualFilePath)

    assertTrue(actualFile.exists())
    assertTrue(resultPath.exists())
    assertEquals(actualFileContent, resultPath.readText())
    assertEquals(actualFile, resultPath)
  }

  /**
   * Tests the correct saving of a simple Json string with the [saveAsJsonFile] function without
   * explicitly adding the `.json` file extension.
   */
  @Test
  fun `Test correct saving of a simple Json string with saveAsJsonFile() without json file extension`() {
    val actualFilePath = "$serializedResultsFolder/testFile"
    val actualFileContent = "{}"
    val actualFile = File("$actualFilePath.json")

    val resultPath = actualFileContent.saveAsJsonFile(actualFilePath)

    assertTrue(actualFile.exists())
    assertTrue(resultPath.exists())
    assertEquals(actualFileContent, actualFile.readText())
    assertEquals(actualFile, resultPath)
  }

  // endregion

  // region Tests for saveAsJsonFile(SerializableResult)
  /** Tests the correct saving of a simple [SerializableResult] with [saveAsJsonFile]. */
  @Test
  fun `Test correct saving of simple SerializableResult object`() {
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult =
        SerializableIntResult(identifier = actualIdentifier, source = actualSource, value = 2)
    val resultPath = actualSerializableResult.saveAsJsonFile()

    // Check that the returned path really exists and contains the necessary keywords
    assertTrue(resultPath.exists())
    assertTrue(resultPath.isFile)
    assertTrue(resultPath.absolutePath.contains(actualIdentifier))
    assertTrue(resultPath.absolutePath.contains(actualSource))

    // Check that the returned path is equal to the expected path
    val actualFile =
        File(
            "$serializedResultsFolder/$applicationStartTimeString/${actualSource}/${actualIdentifier}.json")
    assertEquals(actualFile, resultPath)
    assertTrue(actualFile.exists())
    assertTrue(actualFile.isFile())

    // Check that the content of the file is actually the Json string of the SerializableResult
    assertEquals(actualSerializableResult.getJsonString(), resultPath.readText())
  }

  // endregion

  // region Tests for saveAsJsonFile(SerializableResultComparison,Boolean)
  /**
   * Tests the correct saving of a [SerializableResultComparison] that was created by comparing a
   * current result with the latest evaluation.
   */
  @Test
  fun `Test correct saving of SerializableResultComparison object compared with latest evaluation`() {
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResultComparison =
        SerializableResultComparison(
            EQUAL_RESULTS, actualSource, actualIdentifier, "oldValue", "newValue")
    val resultPath = actualSerializableResultComparison.saveAsJsonFile(false)

    // Check that the returned path really exists and contains the necessary keywords
    assertTrue(resultPath.exists())
    assertTrue(resultPath.isFile)
    assertTrue(resultPath.absolutePath.contains(actualIdentifier))
    assertTrue(resultPath.absolutePath.contains(actualSource))

    // Check that the returned path is equal to the expected path
    val actualFile =
        File(
            "$comparedResultsFolder/" +
                "$applicationStartTimeString/" +
                "$PREVIOUS_EVALUATION_SERIALIZED_RESULT_IDENTIFIER/" +
                "$actualSource/" +
                "[${EQUAL_RESULTS.shortString}]_comparison_$actualIdentifier.json")
    assertTrue(actualFile.exists())
    assertTrue(actualFile.isFile())

    assertEquals(actualFile, resultPath)
    // Check that the content of the file is actually the Json string of the SerializableResult
    assertEquals(actualSerializableResultComparison.getJsonString(), resultPath.readText())
  }

  /**
   * Tests the correct saving of a [SerializableResultComparison] that was created by comparing a
   * current result with the baseline result set.
   */
  @Test
  fun `Test correct saving of SerializableResultComparison object compared with baseline`() {
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResultComparison =
        SerializableResultComparison(
            EQUAL_RESULTS, actualSource, actualIdentifier, "oldValue", "newValue")
    val resultPath = actualSerializableResultComparison.saveAsJsonFile(true)

    // Check that the returned path really exists and contains the necessary keywords
    assertTrue(resultPath.exists())
    assertTrue(resultPath.isFile)
    assertTrue(resultPath.absolutePath.contains(actualIdentifier))
    assertTrue(resultPath.absolutePath.contains(actualSource))

    // Check that the returned path is equal to the expected path
    val actualFile =
        File(
            "$comparedResultsFolder/" +
                "$applicationStartTimeString/" +
                "$baselineDirectory/" +
                "$actualSource/" +
                "[${EQUAL_RESULTS.shortString}]_comparison_$actualIdentifier.json")
    assertTrue(actualFile.exists())
    assertTrue(actualFile.isFile())

    assertEquals(actualFile, resultPath)
    // Check that the content of the file is actually the Json string of the SerializableResult
    assertEquals(actualSerializableResultComparison.getJsonString(), resultPath.readText())
  }

  // endregion

  // region Tests for getLatestSerializationResultPath()
  /**
   * Tests that the [getPreviousSerializationResultDirectory] function returns the correct
   * directory, when exactly one previous run exists.
   */
  @Test
  fun `Test correct getting of latest SerializationResult`() {
    // Setup of previous SerializationResult for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val timeOfLatestState =
        LocalDateTime.now()
            .minusMinutes(2)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
    val actualSerializableResult =
        SerializableIntResult(identifier = actualIdentifier, source = actualSource, value = 2)
    val actualFileContent = actualSerializableResult.getJsonString()
    val actualFilePath =
        "${serializedResultsFolder}/${timeOfLatestState}/${actualSource}/${actualIdentifier}.json"
    val resultPath = actualFileContent.saveAsJsonFile(actualFilePath)

    // Get Path to latest SerializationResult
    val latestResult = getPreviousSerializationResultDirectory()

    assertNotNull(latestResult)
    assertEquals(resultPath.parentFile.parentFile, latestResult)
  }

  /**
   * Tests that the [getPreviousSerializationResultDirectory] function returns the correct
   * directory, when multiple previous runs exists.
   */
  @Test
  fun `Test correct getting of latest SerializationResult with multiple latest results`() {
    // Setup of previous SerializationResults for testing
    lateinit var latestActualSavedPath: File
    for (i in 3 downTo 0) {
      val actualIdentifier = "actualIdentifier"
      val actualSource = "actualSource"
      val timeOfLatestState =
          LocalDateTime.now()
              .minusMinutes(i.toLong())
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
      val actualSerializableResult =
          SerializableIntResult(identifier = actualIdentifier, source = actualSource, i)
      val actualFilePath =
          "${serializedResultsFolder}/${timeOfLatestState}/${actualSource}/${actualIdentifier}.json"
      // The element i == 0 should be not considered, but the one before.
      // Therefore, save the previous to latest result path.
      if (i == 1) {
        latestActualSavedPath =
            actualSerializableResult.getJsonString().saveAsJsonFile(actualFilePath)
      }
    }

    // Get Path to latest SerializationResult
    val latestResult = getPreviousSerializationResultDirectory()

    assertNotNull(latestResult)
    assertEquals(latestActualSavedPath.parentFile.parentFile, latestResult)
  }

  /**
   * Tests that the [getPreviousSerializationResultDirectory] function returns no directory, when no
   * previous run exists.
   */
  @Test
  fun `Test getting of latest SerializationResult with no results`() {
    // Setup of previous SerializationResults for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult =
        SerializableIntResult(identifier = actualIdentifier, source = actualSource, value = 2)
    val actualFilePath =
        "$serializedResultsFolder/$baselineDirectory/${actualSource}/${actualIdentifier}.json"
    actualSerializableResult.getJsonString().saveAsJsonFile(actualFilePath)

    // Get Path to latest SerializationResult (baseline SerializationResult should be ignored)
    val latestResult = getPreviousSerializationResultDirectory()

    assertNull(latestResult)
  }

  // endregion

  // region Tests for getBaselineSerializationResultPath()
  /**
   * Tests that the [getBaselineSerializationResultDirectory] function returns the correct
   * directory, when the baseline result set exists.
   */
  @Test
  fun `Test correct getting of baseline SerializationResult`() {
    // Setup of baseline SerializationResults for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult =
        SerializableIntResult(identifier = actualIdentifier, source = actualSource, value = 2)
    val actualFile =
        "$serializedResultsFolder/" +
            "$baselineDirectory/" +
            "${actualSource}/" +
            "${actualIdentifier}.json"
    val latestActualSavedPath = actualSerializableResult.getJsonString().saveAsJsonFile(actualFile)

    // Get Path to latest SerializationResult
    val latestResult = getBaselineSerializationResultDirectory()

    assertNotNull(latestResult)
    assertEquals(latestActualSavedPath.parentFile.parentFile, latestResult)
  }

  /**
   * Tests that the [getBaselineSerializationResultDirectory] function returns no directory, when
   * the baseline result set does not exist.
   */
  @Test
  fun `Test getting of baseline SerializationResult with no results`() {
    // Setup of previous SerializationResult for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult =
        SerializableIntResult(identifier = actualIdentifier, source = actualSource, value = 2)
    val actualFileContent = actualSerializableResult.getJsonString()
    val timeOfLatestState =
        LocalDateTime.now()
            .minusMinutes(2)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
    val actualFilePath =
        "${serializedResultsFolder}/${timeOfLatestState}/${actualSource}/${actualIdentifier}.json"
    actualFileContent.saveAsJsonFile(actualFilePath)

    // Get Path to baseline SerializationResult (latest SerializationResult should be ignored)
    val latestResult = getBaselineSerializationResultDirectory()

    assertNull(latestResult)
  }
  // endregion
}
