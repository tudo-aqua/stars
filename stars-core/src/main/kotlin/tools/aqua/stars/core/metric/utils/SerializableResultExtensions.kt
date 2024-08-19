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

import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict.MISSING_IDENTIFIER
import tools.aqua.stars.core.metric.serialization.SerializableResultComparisonVerdict.NEW_IDENTIFIER

fun List<SerializableResult>.compareTo(
    otherResult: SerializableResult
): SerializableResultComparison? = this.firstNotNullOfOrNull { it.compareTo(otherResult) }

fun Map<String, List<SerializableResult>>.compareTo(
    otherResults: Map<String, List<SerializableResult>>
): List<SerializableResultComparison> {
  val result = mutableListOf<SerializableResultComparison>()

  val theseSources = this.keys
  val otherSources = otherResults.keys

  val newSources = theseSources - otherSources
  val removedSources = otherSources - theseSources
  val commonSources = theseSources.intersect(otherSources)

  // Add new keys to results
  result.addAll(
      newSources
          .map { key ->
            checkNotNull(this[key]).map { res ->
              SerializableResultComparison(
                  verdict = NEW_IDENTIFIER,
                  source = res.source,
                  identifier = res.identifier,
                  newValue = res.value.toString(),
                  oldValue = "None")
            }
          }
          .flatten())

  // Add removed keys to results
  result.addAll(
      removedSources
          .map { key ->
            checkNotNull(otherResults[key]).map { res ->
              SerializableResultComparison(
                  verdict = MISSING_IDENTIFIER,
                  source = res.source,
                  identifier = res.identifier,
                  newValue = "None",
                  oldValue = res.value.toString())
            }
          }
          .flatten())

  // Compare common keys
  result.addAll(
      commonSources
          .map { key ->
            val identifierComparisons = mutableListOf<SerializableResultComparison>()

            val t1 = checkNotNull(this[key])
            val t2 = checkNotNull(otherResults[key])

            val theseIdentifiers = t1.map { it.identifier }.toSet()
            val otherIdentifiers = t2.map { it.identifier }.toSet()

            check(theseIdentifiers.size == t1.size) { "Duplicate identifiers in source: $key" }
            check(otherIdentifiers.size == t2.size) { "Duplicate identifiers in source: $key" }

            val newIdentifiers = theseIdentifiers - otherIdentifiers
            val removedIdentifiers = otherIdentifiers - theseIdentifiers
            val commonIdentifiers = theseIdentifiers.intersect(otherIdentifiers)

            // Add new identifiers to results
            identifierComparisons.addAll(
                newIdentifiers.map { identifier ->
                  t1.first { it.identifier == identifier }
                      .let { res ->
                        SerializableResultComparison(
                            verdict = NEW_IDENTIFIER,
                            source = res.source,
                            identifier = res.identifier,
                            newValue = res.value.toString(),
                            oldValue = "None")
                      }
                })

            // Add removed identifiers to results
            identifierComparisons.addAll(
                removedIdentifiers.map { identifier ->
                  t2.first { it.identifier == identifier }
                      .let { res ->
                        SerializableResultComparison(
                            verdict = MISSING_IDENTIFIER,
                            source = res.source,
                            identifier = res.identifier,
                            newValue = "None",
                            oldValue = res.value.toString())
                      }
                })

            // Compare common identifiers
            identifierComparisons.addAll(
                commonIdentifiers.mapNotNull { identifier ->
                  val r1 = t1.first { it.identifier == identifier }
                  val r2 = t2.first { it.identifier == identifier }
                  r1.compareTo(r2)
                })

            identifierComparisons
          }
          .flatten())

  return result
}

  //  val comparedThisToOther: List<SerializableResultComparison> =
  //      this.map { thisResult ->
  //        otherResults.firstNotNullOfOrNull { otherResult -> thisResult.compareTo(otherResult) }
  //            ?: SerializableResultComparison(
  //                verdict = NO_MATCHING_RESULT,
  //                source = thisResult.source,
  //                identifier = thisResult.identifier ?: DEFAULT_SERIALIZED_RESULT_IDENTIFIER,
  //                newValue = thisResult.value.toString(),
  //                oldValue = "None")
  //      }
  //  val comparedOtherToThis =
  //      otherResults.map { otherResult ->
  //        this.firstNotNullOfOrNull { thisResult -> thisResult.compareTo(otherResult) }
  //            ?: SerializableResultComparison(
  //                verdict = NO_MATCHING_RESULT,
  //                source = otherResult.source,
  //                identifier = otherResult.identifier ?: DEFAULT_SERIALIZED_RESULT_IDENTIFIER,
  //                newValue = otherResult.value.toString(),
  //                oldValue = "None")
  //      }
  // return (comparedThisToOther + comparedOtherToThis).distinct() // TODO: Performance
