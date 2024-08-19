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

package tools.aqua.stars.core

import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.serialization.SerializableIntResult
import tools.aqua.stars.core.metric.serialization.SerializableResult
import tools.aqua.stars.core.metric.utils.compareToGroundTruthResults
import tools.aqua.stars.core.metric.utils.saveAsJsonFile
import tools.aqua.stars.core.metric.utils.writeSerializedResults

class MyMetric(val value: Int = 1, val source: String) : Serializable {
  override fun getSerializableResults(): List<SerializableResult> =
      listOf(
          SerializableIntResult(value, identifier = "MyMetric", source = source),
          SerializableIntResult(value + 1, identifier = "MyMetric2", source = source))
}

fun main() {
  val metric = MyMetric(source = "Metric1")
  metric.writeSerializedResults()

  val metric2 = MyMetric(source = "Metric2")
  metric2.writeSerializedResults()

  val t1 = listOf(metric, metric2).compareToGroundTruthResults()
  t1.forEach { t -> t.saveAsJsonFile(true) }
}
