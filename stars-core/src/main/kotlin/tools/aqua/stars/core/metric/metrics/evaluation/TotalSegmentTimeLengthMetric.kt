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
import tools.aqua.stars.core.metric.providers.MetricProvider
import tools.aqua.stars.core.metric.providers.SegmentMetricProvider
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.requireIsInstance
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * This class implements the [SegmentMetricProvider] and calculates the total time duration of all
 * analyzed [SegmentType]s. This metric is [Stateful] and tracks the total time duration.
 */
class TotalSegmentTimeLengthMetric<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    override val logger: Logger = Loggable.getLogger("total-segment-time-length")
) : SegmentMetricProvider<E, T, S>(), Stateful, Loggable {
  /** Holds the current time duration for all already analyzed [SegmentType]s. */
  private var totalTimeDuration: Double = 0.0

  /**
   * Add the duration of the given [segment] to the total duration. [SegmentType] with no, or only
   * one [TickDataType] are ignores.
   *
   * @param segment The [SegmentType] from which the duration should by tracked
   * @return The current time duration of all analyzed [SegmentType]s
   *
   * @throws IllegalStateException When the time duration between the first and last [TickDataType]
   * of the [SegmentType] is negative.
   */
  override fun evaluate(segment: SegmentType<E, T, S>): Double {
    // The Segment has no or only one TickData from which no time duration can be calculated.
    if (segment.tickData.size <= 1) return totalTimeDuration

    // Calculate the time difference between the last and the first tick in the given segment.
    val segmentTimeDuration =
        segment.tickData.last().currentTick - segment.tickData.first().currentTick

    check(segmentTimeDuration >= 0.0) {
      "The duration between the first and last tick of segment '$segment' should be positive! " +
          "Actual duration: $segmentTimeDuration."
    }

    totalTimeDuration += segmentTimeDuration
    return totalTimeDuration
  }

  /**
   * Returns the current [totalTimeDuration] as [Double].
   *
   * @return The current [totalTimeDuration] for all already analyzed [SegmentType]s as [Double].
   */
  override fun getState(): Double = totalTimeDuration

  /** Prints the current [totalTimeDuration]. */
  override fun printState() {
    logInfo("=== Total analysis data ===")
    logInfo(" $totalTimeDuration seconds\n")
  }

  override fun copy(): TotalSegmentTimeLengthMetric<E, T, S> = TotalSegmentTimeLengthMetric(logger)

  override fun merge(other: MetricProvider<E, T, S>) {
    requireIsInstance<TotalSegmentTimeLengthMetric<E, T, S>>(other) {
      "Trying to merge different metrics."
    }

    this.totalTimeDuration += other.totalTimeDuration
  }
}
