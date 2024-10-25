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
 * This metric implements the [SegmentMetricProvider] and tracks the total [TickDifference] of all
 * [SegmentType]s for each existing [SegmentType.segmentSource]. Normally each
 * [SegmentType.segmentSource] defines the source of the [SegmentType] (e.g. a map name, analysis
 * name).
 *
 * This class implements the [Serializable] interface. It serializes the total tick difference for
 * all analyzed [SegmentType]s grouped by the segment.
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
class TotalSegmentTickDifferencePerIdentifierMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val loggerIdentifier: String = "total-segment-tick-difference-per-identifier",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) : SegmentMetricProvider<E, T, S, U, D>, Stateful, Serializable, Loggable {
  /**
   * Holds the Map of [SegmentType.segmentSource] to total [TickDifference] of all [SegmentType]s
   * for the [SegmentType.segmentSource].
   */
  private val segmentIdentifierToTotalSegmentDurationMap: MutableMap<String, D> = mutableMapOf()

  /**
   * Track the current length of the [SegmentType] in the
   * [segmentIdentifierToTotalSegmentDurationMap]. If the [SegmentType.segmentSource] is currently
   * not in the map it is added as a new key.
   *
   * @param segment The [SegmentType] for which the total [TickDifference] should be tracked.
   * @return The current total [TickDifference] of all [SegmentType] with identifier
   *   [SegmentType.segmentSource] of the given [segment].
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

      // Add segmentTimeLength to the map with key "segment.segmentSource"
      segmentIdentifierToTotalSegmentDurationMap[segment.segmentSource] =
          if (segmentIdentifierToTotalSegmentDurationMap.containsKey(segment.segmentSource))
              segmentTickDifference +
                  segmentIdentifierToTotalSegmentDurationMap.getValue(segment.segmentSource)
          else segmentTickDifference
    }

    return Optional.ofNullable(segmentIdentifierToTotalSegmentDurationMap[segment.segmentSource])
  }

  /**
   * Returns the Map of [SegmentType.segmentSource] to the total [TickDifference] for this
   * [SegmentType.segmentSource].
   *
   * @return The Map of [SegmentType.segmentSource] to the total [TickDifference].
   */
  override fun getState(): Map<String, D> = segmentIdentifierToTotalSegmentDurationMap

  /**
   * Prints one line for each [SegmentType.segmentSource] with the related total [TickDifference].
   */
  override fun printState() {
    segmentIdentifierToTotalSegmentDurationMap.forEach { (identifier, totalDifference) ->
      logInfo(
          "The analyzed segments with source '$identifier' yielded a total tick difference of $totalDifference.")
    }
  }

  override fun getSerializableResults(): List<SerializableTickDifferenceResult> =
      segmentIdentifierToTotalSegmentDurationMap.map { (identifier, tickDifference) ->
        SerializableTickDifferenceResult(
            identifier = identifier, source = loggerIdentifier, value = tickDifference.serialize())
      }
}
