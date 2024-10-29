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

package tools.aqua.stars.core.metric.serialization.extensions

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict.*
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.jsonConfiguration

/** Returns a [Json] [String] of a [SerializableResult] using the project [jsonConfiguration]. */
fun SerializableResult.getJsonString(): String = jsonConfiguration.encodeToString(this)

/**
 * Compares the [List] of [SerializableResult]s with the given [otherResults] [List]. See
 * [compareTo] for implementation details.
 *
 * @see [compareTo]
 */
fun List<SerializableResult>.compareTo(
    otherResults: List<SerializableResult>
): List<SerializableResultComparison> =
    groupBy { it.source }.compareTo(otherResults.groupBy { it.source })

/**
 * Extension function for [Map] of [String] to [List] of [SerializableResult] that builds a
 * difference [List] of [SerializableResultComparison] with the given [otherResultsMap].
 *
 * To create the difference [List] multiple checks are done:
 * 1. We check which [SerializableResult.source]s are either NEW, MISSING, EQUAL, or NOT_EQUAL. With
 *    this, it is possible to check that a source was either added, or removed. When the source
 *    still exists, its values are compared with the verdicts EQUAL oder NOT_EQUAL.
 * 2. For all [SerializableResult.source]s that do match, the [SerializableResult.identifier]s are
 *    then checked for the same differences. So they are either NEW, MISSING, EQUAL, or NOT_EQUAL.
 *
 * With this distinction it is possible to conclude which source/identifier was newly added, or was
 * removed and when they are still there, whether their values have changed.
 */
fun Map<String, List<SerializableResult>>.compareTo(
    otherResultsMap: Map<String, List<SerializableResult>>
): List<SerializableResultComparison> {
  val serializableResultComparisons = mutableListOf<SerializableResultComparison>()

  val theseSources = this.keys
  val otherSources = otherResultsMap.keys

  val newSources = theseSources - otherSources
  val removedSources = otherSources - theseSources
  val commonSources = theseSources.intersect(otherSources)

  // Add new keys to results
  serializableResultComparisons.addAll(
      newSources
          .map { source ->
            checkNotNull(this[source]).map { result ->
              SerializableResultComparison(
                  verdict = NEW_METRIC_SOURCE,
                  source = result.source,
                  identifier = result.identifier,
                  newValue = result.value.toString(),
                  oldValue = "None")
            }
          }
          .flatten())

  // Add removed keys to results
  serializableResultComparisons.addAll(
      removedSources
          .map { source ->
            checkNotNull(otherResultsMap[source]).map { result ->
              SerializableResultComparison(
                  verdict = MISSING_METRIC_SOURCE,
                  source = result.source,
                  identifier = result.identifier,
                  newValue = "None",
                  oldValue = result.value.toString())
            }
          }
          .flatten())

  // Compare common keys and their identifiers
  serializableResultComparisons.addAll(
      commonSources
          .map { source ->
            val identifierComparisons = mutableListOf<SerializableResultComparison>()

            val theseResults = checkNotNull(this[source])
            val otherResults = checkNotNull(otherResultsMap[source])

            val theseIdentifiers = theseResults.map { it.identifier }.toSet()
            val otherIdentifiers = otherResults.map { it.identifier }.toSet()

            check(theseIdentifiers.size == theseResults.size) {
              "Duplicate identifiers in source: $source"
            }
            check(otherIdentifiers.size == otherResults.size) {
              "Duplicate identifiers in source: $source"
            }

            val newIdentifiers = theseIdentifiers - otherIdentifiers
            val removedIdentifiers = otherIdentifiers - theseIdentifiers
            val commonIdentifiers = theseIdentifiers.intersect(otherIdentifiers)

            // Add new identifiers to results
            identifierComparisons.addAll(
                newIdentifiers.map { identifier ->
                  theseResults
                      .first { it.identifier == identifier }
                      .let { result ->
                        SerializableResultComparison(
                            verdict = NEW_IDENTIFIER,
                            source = result.source,
                            identifier = result.identifier,
                            newValue = result.value.toString(),
                            oldValue = "None")
                      }
                })

            // Add removed identifiers to results
            identifierComparisons.addAll(
                removedIdentifiers.map { identifier ->
                  otherResults
                      .first { it.identifier == identifier }
                      .let { result ->
                        SerializableResultComparison(
                            verdict = MISSING_IDENTIFIER,
                            source = result.source,
                            identifier = result.identifier,
                            newValue = "None",
                            oldValue = result.value.toString())
                      }
                })

            // Compare common identifiers
            identifierComparisons.addAll(
                commonIdentifiers.mapNotNull { identifier ->
                  val thisResult = theseResults.first { it.identifier == identifier }
                  val otherResult = otherResults.first { it.identifier == identifier }
                  thisResult.compareTo(otherResult)
                })

            return@map identifierComparisons
          }
          .flatten())

  return serializableResultComparisons
}
