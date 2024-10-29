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

import kotlinx.serialization.json.Json
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.serialization.SerializableResultComparison
import tools.aqua.stars.core.metric.utils.baseline
import tools.aqua.stars.core.metric.utils.previousResults
import tools.aqua.stars.core.metric.utils.saveAsJsonFile

/**
 * Extension function of [List] of [Serializable] that compares it to the previous evaluation
 * results.
 *
 * @return Returns the [List] of [SerializableResultComparison]s that was created by comparing the
 *   [SerializableResult]s of the calling [Serializable] with the previous [SerializableResult]s.
 */
fun List<Serializable>.compareToPreviousResults(): List<SerializableResultComparison> =
    map { it.getSerializableResults() }.flatten().groupBy { it.source }.compareTo(previousResults)

/**
 * Extension function of [List] of [Serializable] that compares it to the baseline evaluation
 * results.
 *
 * @return Returns the [List] of [SerializableResultComparison]s that was created by comparing the
 *   [SerializableResult]s of the calling [Serializable] with the baseline [SerializableResult]s.
 */
fun List<Serializable>.compareToBaselineResults(): List<SerializableResultComparison> {
  check(baseline.isNotEmpty()) { "No baseline results found." }
  return map { it.getSerializableResults() }.flatten().groupBy { it.source }.compareTo(baseline)
}

/**
 * Extension function for [Serializable] that writes all its [SerializableResult]s as [Json] files
 * to the disc.
 */
fun Serializable.writeSerializedResults() {
  getSerializableResults().forEach { it.saveAsJsonFile() }
}
