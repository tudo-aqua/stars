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
import kotlin.io.path.exists
import kotlin.test.*
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metric.serialization.SerializableIntResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict
import tools.aqua.stars.core.metric.utils.*
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.applicationStartTimeString
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.comparedResultsFolder
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.serializedResultsFolder

class SerializationHelpersTest {

  //  /** Clear all existing test folders for a clean setup for each test. */
  //  @BeforeTest
  //  fun `Clean up all existing test folders`() {
  //    File(serializedResultsFolder).deleteRecursively()
  //    File(comparedResultsFolder).deleteRecursively()
  //    File(GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER).deleteRecursively()
  //  }

  // region Tests for saveAsJsonFile(String, String)
  @Test
  fun `Test correct saving of SerializableResult with saveAsJsonFile()`() {
    val actualFilePath = "$serializedResultsFolder/testFile.json"
    val actualFileContent = "{}"
    val actualFile = File(actualFilePath)

    val resultPath = actualFileContent.saveAsJsonFile(actualFilePath)

    assertTrue(actualFile.exists())
    assertTrue(resultPath.exists())
    assertEquals(actualFileContent, resultPath.readText())
    assertEquals(actualFile, resultPath)
  }

  @Test
  fun `Test correct saving of SerializableResult with saveAsJsonFile() without json file extension`() {
    val actualFilePath = "$serializedResultsFolder/testFile"
    val actualFileContent = "{}"
    val actualFile = File("$actualFilePath.json")

    val resultPath = actualFileContent.saveAsJsonFile(actualFilePath)

    assertTrue(actualFile.exists())
    assertTrue(resultPath.exists())
    assertEquals(actualFileContent, actualFile.readText())
    assertEquals(actualFile, resultPath)
  }

  @Test
  fun `Test error when wanting to override SerializableResult with saveAsJsonFile()`() {
    val actualFilePath = "$serializedResultsFolder/testFile.json"
    val actualFileContent = "{}"
    actualFileContent.saveAsJsonFile(actualFilePath)

    assertTrue(File(actualFilePath).exists())
    assertThrows<IllegalStateException> { actualFileContent.saveAsJsonFile(actualFilePath) }
  }

  // endregion

  // region Tests for saveAsJsonFile(SerializableResult)
  @Test
  fun `Test correct saving of SerializableResult object`() {
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult = SerializableIntResult(2, actualIdentifier, actualSource)
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

  @Test
  fun `Test correct saving of SerializableResultComparison object compared with last evaluation`() {
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResultComparison =
        SerializableResultComparison(
            SerializableResultComparisonVerdict.EQUAL_RESULTS,
            actualSource,
            actualIdentifier,
            "oldValue",
            "newValue")
    val resultPath = actualSerializableResultComparison.saveAsJsonFile(false)

    // Check that the returned path really exists and contains the necessary keywords
    assertTrue(resultPath.exists())
    assertTrue(resultPath.isFile)
    assertTrue(resultPath.absolutePath.contains(actualIdentifier))
    assertTrue(resultPath.absolutePath.contains(actualSource))

    // Check that the returned path is equal to the expected path
    val actualFile =
        File(
            "$comparedResultsFolder/$applicationStartTimeString/latest-evaluation/$actualSource/comparison_$actualIdentifier.json")
    assertEquals(actualFile, resultPath)
    assertTrue(actualFile.exists())
    assertTrue(actualFile.isFile())

    // Check that the content of the file is actually the Json string of the SerializableResult
    assertEquals(actualSerializableResultComparison.getJsonString(), resultPath.readText())
  }

  @Test
  fun `Test correct saving of SerializableResultComparison object compared with ground truth`() {
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResultComparison =
        SerializableResultComparison(
            SerializableResultComparisonVerdict.EQUAL_RESULTS,
            actualSource,
            actualIdentifier,
            "oldValue",
            "newValue")
    val resultPath = actualSerializableResultComparison.saveAsJsonFile(true)

    // Check that the returned path really exists and contains the necessary keywords
    assertTrue(resultPath.exists())
    assertTrue(resultPath.isFile)
    assertTrue(resultPath.absolutePath.contains(actualIdentifier))
    assertTrue(resultPath.absolutePath.contains(actualSource))

    // Check that the returned path is equal to the expected path
    val actualFile =
        File(
            "$comparedResultsFolder/$applicationStartTimeString/ground-truth/$actualSource/comparison_$actualIdentifier.json")
    assertEquals(actualFile, resultPath)
    assertTrue(actualFile.exists())
    assertTrue(actualFile.isFile())

    // Check that the content of the file is actually the Json string of the SerializableResult
    assertEquals(actualSerializableResultComparison.getJsonString(), resultPath.readText())
  }

  // endregion

  // region Tests for getLatestSerializationResultPath()

  @Test
  fun `Test correct getting of latest SerializationResult`() {
    // Setup of previous SerializationResult for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val timeOfLatestState =
        LocalDateTime.now()
            .minusMinutes(2)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
    val actualSerializableResult = SerializableIntResult(2, actualIdentifier, actualSource)
    val actualFileContent = actualSerializableResult.getJsonString()
    val actualFilePath =
        "${serializedResultsFolder}/${timeOfLatestState}/${actualSource}/${actualIdentifier}.json"
    val resultPath = actualFileContent.saveAsJsonFile(actualFilePath)

    // Get Path to latest SerializationResult
    val latestResult = getLatestSerializationResultDirectory()

    assertNotNull(latestResult)
    assertEquals(resultPath.parentFile.parentFile, latestResult)
  }

  @Test
  fun `Test correct getting of latest SerializationResult with multiple last results`() {
    // Setup of previous SerializationResults for testing
    lateinit var lastActualSavedPath: File
    for (i in 3 downTo 0) {
      val actualIdentifier = "actualIdentifier"
      val actualSource = "actualSource"
      val timeOfLatestState =
          LocalDateTime.now()
              .minusMinutes(i.toLong())
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
      val actualSerializableResult = SerializableIntResult(i, actualIdentifier, actualSource)
      val actualFilePath =
          "${serializedResultsFolder}/${timeOfLatestState}/${actualSource}/${actualIdentifier}.json"
      // The element i == 0 should be not considered, but the one before.
      // Therefore, save the previous to last result path.
      if (i == 1) {
        lastActualSavedPath =
            actualSerializableResult.getJsonString().saveAsJsonFile(actualFilePath)
      }
    }

    // Get Path to latest SerializationResult
    val latestResult = getLatestSerializationResultDirectory()

    assertNotNull(latestResult)
    assertEquals(lastActualSavedPath.parentFile.parentFile, latestResult)
  }

  @Test
  fun `Test getting of latest SerializationResult with no results`() {
    // Setup of previous SerializationResults for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult = SerializableIntResult(2, actualIdentifier, actualSource)
    val actualFilePath =
        "$serializedResultsFolder/$GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER/${actualSource}/${actualIdentifier}.json"
    actualSerializableResult.getJsonString().saveAsJsonFile(actualFilePath)

    // Get Path to latest SerializationResult (ground truth SerializationResult should be ignored)
    val latestResult = getLatestSerializationResultDirectory()

    assertNull(latestResult)
  }

  // endregion

  // region Tests for getGroundTruthSerializationResultPath()

  @Test
  fun `Test correct getting of ground truth SerializationResult`() {
    // Setup of ground truth SerializationResults for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult = SerializableIntResult(2, actualIdentifier, actualSource)
    val actualFile =
        "$serializedResultsFolder/$GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER/${actualSource}/${actualIdentifier}.json"
    val lastActualSavedPath = actualSerializableResult.getJsonString().saveAsJsonFile(actualFile)

    // Get Path to latest SerializationResult
    val latestResult = getGroundTruthSerializationResultDirectory()

    assertNotNull(latestResult)
    assertEquals(lastActualSavedPath.parentFile.parentFile, latestResult)
  }

  @Test
  fun `Test getting of ground truth SerializationResult with no results`() {
    // Setup of previous SerializationResult for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult = SerializableIntResult(2, actualIdentifier, actualSource)
    val actualFileContent = actualSerializableResult.getJsonString()
    val timeOfLatestState =
        LocalDateTime.now()
            .minusMinutes(2)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
    val actualFilePath =
        "${serializedResultsFolder}/${timeOfLatestState}/${actualSource}/${actualIdentifier}.json"
    actualFileContent.saveAsJsonFile(actualFilePath)

    // Get Path to ground truth SerializationResult (latest SerializationResult should be ignored)
    val latestResult = getGroundTruthSerializationResultDirectory()

    assertNull(latestResult)
  }
  // endregion

  // region Tests for getSerializedResultsFromFolder(Path,SerializableResult)

  @Test
  fun `Test correct deserialization of SerializableResult with latest results`() {
    // Setup of previous SerializationResult for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val timeOfLatestState =
        LocalDateTime.now()
            .minusMinutes(2)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
    val actualSerializableResult = SerializableIntResult(2, actualIdentifier, actualSource)
    val actualFileContent = actualSerializableResult.getJsonString()
    val actualFilePath =
        "${serializedResultsFolder}/${timeOfLatestState}/${actualSource}/${actualIdentifier}.json"
    actualFileContent.saveAsJsonFile(actualFilePath)

    val latestResultPath = getLatestSerializationResultDirectory()
    assertNotNull(latestResultPath)

    val deserializedIntResultList = assertDoesNotThrow {
      getSerializedResultsFromSourceFolder(latestResultPath, actualSerializableResult)
    }

    assertEquals(actualSerializableResult.value, deserializedIntResultList[0].value)
    assertEquals(actualSerializableResult.source, deserializedIntResultList[0].source)
    assertEquals(actualSerializableResult.identifier, deserializedIntResultList[0].identifier)
  }

  @Test
  fun `Test correct deserialization of SerializableResult with ground truth results`() {
    // Setup of ground truth SerializationResults for testing
    val actualIdentifier = "actualIdentifier"
    val actualSource = "actualSource"
    val actualSerializableResult = SerializableIntResult(2, actualIdentifier, actualSource)
    val actualFilePath =
        "$serializedResultsFolder/$GROUND_TRUTH_SERIALIZED_RESULT_IDENTIFIER/${actualSource}/${actualIdentifier}.json"
    actualSerializableResult.getJsonString().saveAsJsonFile(actualFilePath)

    val latestResultPath = getGroundTruthSerializationResultDirectory()
    assertNotNull(latestResultPath)

    // Create dummy result to get correct result from latestResultPath
    val serializableIntResult = SerializableIntResult(2, actualIdentifier, actualSource)

    val deserializedIntResultList = assertDoesNotThrow {
      getSerializedResultsFromSourceFolder(latestResultPath, serializableIntResult)
    }

    assertEquals(actualSerializableResult.value, deserializedIntResultList[0].value)
    assertEquals(actualSerializableResult.source, deserializedIntResultList[0].source)
    assertEquals(actualSerializableResult.identifier, deserializedIntResultList[0].identifier)
  }
  // endregion
}
