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

package tools.aqua.stars.core.metric.utils

import kotlinx.serialization.json.Json
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict.*
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.resultsReproducedFromGroundTruth
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.resultsReproducedFromPreviousRun

/**
 * Extension function of [List] of [Serializable] that compares it to the previous evaluation
 * results.
 *
 * @param saveVerdict (Default: false) Whether the verdict of the comparison should be saved in
 *   [ApplicationConstantsHolder.resultsReproducedFromPreviousRun].
 * @return Returns the [List] of [SerializableResultComparison]s that was created by comparing the
 *   [SerializableResult]s of the calling [Serializable] with the previous [SerializableResult]s.
 */
fun List<Serializable>.compareToPreviousResults(
    saveVerdict: Boolean = false
): List<SerializableResultComparison> =
    map { it.getSerializableResults() }
        .flatten()
        .groupBy { it.source }
        .compareTo(previousResults)
        .also {
          if (saveVerdict)
              resultsReproducedFromPreviousRun =
                  it.all { t ->
                    t.verdict in listOf(EQUAL_RESULTS, NEW_METRIC_SOURCE, NEW_IDENTIFIER)
                  }
        }

/**
 * Extension function of [List] of [Serializable] that compares it to the ground-truth evaluation
 * results.
 *
 * @param saveVerdict (Default: false) Whether the verdict of the comparison should be saved in
 *   [ApplicationConstantsHolder.resultsReproducedFromGroundTruth].
 * @return Returns the [List] of [SerializableResultComparison]s that was created by comparing the
 *   [SerializableResult]s of the calling [Serializable] with the ground-truth
 *   [SerializableResult]s.
 */
fun List<Serializable>.compareToGroundTruthResults(
    saveVerdict: Boolean = false
): List<SerializableResultComparison> =
    map { it.getSerializableResults() }
        .flatten()
        .groupBy { it.source }
        .compareTo(groundTruth)
        .also {
          if (saveVerdict)
              resultsReproducedFromGroundTruth =
                  it.all { t ->
                    t.verdict in listOf(EQUAL_RESULTS, NEW_METRIC_SOURCE, NEW_IDENTIFIER)
                  }
        }

/**
 * Extension function for [Serializable] that writes all its [SerializableResult]s as [Json] files
 * to the disc.
 */
fun Serializable.writeSerializedResults() {
  getSerializableResults().forEach { it.saveAsJsonFile() }
}
