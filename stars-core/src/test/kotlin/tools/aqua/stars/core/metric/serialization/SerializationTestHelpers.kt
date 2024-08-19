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

import tools.aqua.stars.core.*
import tools.aqua.stars.core.metric.metrics.evaluation.SegmentCountMetric
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder
import tools.aqua.stars.core.metric.utils.compareToGroundTruthResults
import tools.aqua.stars.core.metric.utils.compareToLatestResults
import tools.aqua.stars.core.metric.utils.getJsonStrings

fun serializeAndDeserialize(serializable: Serializable): List<SerializableResult> =
    serializable.getJsonStrings().map { SerializableResult.getJsonContentFromString(it) }


fun main() {
    val t1 = SegmentCountMetric<SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference>().compareToLatestResults()
    val t2 = SegmentCountMetric<SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference>().compareToGroundTruthResults()
    println(ApplicationConstantsHolder.logFolder)
    t1
}
