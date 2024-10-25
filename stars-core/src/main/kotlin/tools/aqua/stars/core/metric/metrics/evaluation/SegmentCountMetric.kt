/*
 * Copyright 2023-2024 The STARS Project Authors
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

package tools.aqua.stars.core.metric.metrics.evaluation

import java.util.logging.Logger
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.SegmentMetricProvider
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.metric.serialization.SerializableIntResult
import tools.aqua.stars.core.types.*

/**
 * This class is an implementation of [SegmentMetricProvider] which provides the count of evaluated
 * segments. This Metric is stateful as it has to track the count of observed [SegmentType]s.
 *
 * This class implements the [Stateful] interface. Its state contains the [segmentCount].
 *
 * This class implements [Serializable] and stores, and compares its evaluation results.
 *
 * This class implements [Loggable] and logs the final [segmentCount].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
class SegmentCountMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val loggerIdentifier: String = "segment-count",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) : SegmentMetricProvider<E, T, S, U, D>, Stateful, Serializable, Loggable {
  /** Holds the count of [SegmentType]s that are analyzed. */
  private var segmentCount: Int = 0

  /** Holds the count of [SegmentType]s grouped by [SegmentType.segmentIdentifier]. */
  private val segmentIdentifierToSegmentCountMap: MutableMap<String, Int> = mutableMapOf()

  /**
   * Increases the count of evaluated [SegmentType]s.
   *
   * @param segment The current [SegmentType] that is evaluated.
   * @return The number of analyzed [SegmentType]s so far.
   */
  override fun evaluate(segment: SegmentType<E, T, S, U, D>): Int {
    ++segmentCount
    logFiner("==== Segment $segmentCount: $segment ====")
    val segmentSource = segment.segmentSource
    segmentIdentifierToSegmentCountMap[segmentSource] =
        segmentIdentifierToSegmentCountMap.getOrPut(segmentSource) { 0 } + 1
    return segmentCount
  }

  /**
   * Returns the current [segmentCount].
   *
   * @return Returns the current [segmentCount].
   */
  override fun getState(): Int = segmentCount

  /** Prints the current state using [println]. */
  override fun printState() {
    logInfo("Analyzed $segmentCount Segments.")
  }

  override fun getSerializableResults(): List<SerializableIntResult> =
      segmentIdentifierToSegmentCountMap.map { (segmentSource, segmentCount) ->
        SerializableIntResult(
            identifier = segmentSource, source = loggerIdentifier, value = segmentCount)
      } +
          listOf(
              SerializableIntResult(
                  identifier = loggerIdentifier, source = loggerIdentifier, value = segmentCount))
}
