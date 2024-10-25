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

import java.util.Optional
import java.util.logging.Logger
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.SegmentMetricProvider
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.metric.serialization.SerializableTickDifferenceResult
import tools.aqua.stars.core.types.*

/**
 * This class implements the [SegmentMetricProvider] and tracks the total [TickDifference] of all
 * [SegmentType]s.
 *
 * This class implements the [Serializable] interface. It serializes the [totalTickDifference] for
 * all analyzed [SegmentType]s.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
@Suppress("unused")
class TotalSegmentTickDifferenceMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val loggerIdentifier: String = "total-segment-tick-difference",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) : SegmentMetricProvider<E, T, S, U, D>, Stateful, Serializable, Loggable {
  /** Holds the current [TickDifference] for all already analyzed [SegmentType]s. */
  private var totalTickDifference: D? = null

  /**
   * Add the given [segment] to the total [TickDifference].
   *
   * @param segment The [SegmentType] for which the total [TickDifference] should be tracked.
   * @return The current total [TickDifference] of all analyzed [SegmentType]s.
   * @throws IllegalStateException If the [TickDifference] between the first and last [TickDataType]
   *   of the [SegmentType] is negative.
   */
  override fun evaluate(segment: SegmentType<E, T, S, U, D>): Optional<D> {
    // The Segment has at least two TickData objects from which a TickDifference can be calculated.
    if (segment.tickData.size >= 2) {
      // Calculate the TickDifference between the last and the first tick in the given segment.
      val segmentTickDifference = segment.tickData.run { last().currentTick - first().currentTick }

      check(segment.tickData.run { last().currentTick > first().currentTick }) {
        "The difference between the first and last tick of segment '$segment' should be positive! " +
            "Actual difference: $segmentTickDifference."
      }

      // Update total
      var newTotal = segmentTickDifference
      totalTickDifference?.let { newTotal = it + segmentTickDifference }
      totalTickDifference = newTotal
    }

    return Optional.ofNullable(totalTickDifference)
  }

  /**
   * Returns the current [totalTickDifference] as Optional. Returns [Optional.empty] if no
   * [SegmentType]s have been analyzed yet.
   *
   * @return The current [totalTickDifference] for all already analyzed [SegmentType]s.
   */
  override fun getState(): Optional<D> = Optional.ofNullable(totalTickDifference)

  /** Prints the current [totalTickDifference]. */
  override fun printState() {
    logInfo("The analyzed segments yielded a total tick difference of $totalTickDifference.")
  }

  override fun getSerializableResults(): List<SerializableTickDifferenceResult> =
      totalTickDifference?.let {
        listOf(
            SerializableTickDifferenceResult(
                identifier = loggerIdentifier, source = loggerIdentifier, value = it.serialize()))
      } ?: emptyList()
}
