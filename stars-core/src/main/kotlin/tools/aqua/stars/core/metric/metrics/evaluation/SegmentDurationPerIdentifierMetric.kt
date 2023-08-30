/*
 * Copyright 2023 The STARS Project Authors
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
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * This metric implements the [SegmentMetricProvider] and tracks the total analysis time of all
 * [SegmentType]s for each existing [SegmentType.segmentSource]. Normally each
 * [SegmentType.segmentSource] defines the source of the [SegmentType] (e.g. a map name, analysis
 * name).
 */
class SegmentDurationPerIdentifierMetric<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    override val logger: Logger = Loggable.getLogger("segment-duration-per-identifier")
) : SegmentMetricProvider<E, T, S>, Stateful, Loggable {
  /**
   * Holds the Map of [SegmentType.segmentSource] to total time length of all [SegmentType]s for the
   * [SegmentType.segmentSource] in seconds ([Double]).
   */
  private var segmentIdentifierToTotalSegmentDurationMap: MutableMap<String, Double> =
      mutableMapOf()

  /**
   * Track the current length of the [SegmentType] in the
   * [segmentIdentifierToTotalSegmentDurationMap]. If the [SegmentType.segmentSource] is currently
   * not in the map it is added as a new key.
   *
   * @param segment The [SegmentType] for which the total time length should be tracked
   * @return The current total time length of all [SegmentType] with identifier
   * [SegmentType.segmentSource] of the given [segment]
   */
  override fun evaluate(segment: SegmentType<E, T, S>): Double {
    segmentIdentifierToTotalSegmentDurationMap.putIfAbsent(segment.segmentSource, 0.0)

    // The Segment does not have any analyzable time duration
    if (segment.tickData.isEmpty()) {
      return 0.0
    }
    // The Segment has only one TickData from which no time duration can be calculated.
    if (segment.tickData.size == 1) {
      return 0.0
    }

    // Calculate the time difference between the last and the first tick in the given segment.
    val segmentTimeLength =
        segment.tickData.last().currentTick - segment.tickData.first().currentTick
    check(segmentTimeLength >= 0.0) {
      "The duration between the first and last tick of segment '$segment' should be positive! " +
          "Actual duration: $segmentTimeLength."
    }

    // Add segmentTimeLength to the map with key "segment.segmentSource"
    segmentIdentifierToTotalSegmentDurationMap[segment.segmentSource] =
        segmentIdentifierToTotalSegmentDurationMap.getValue(segment.segmentSource) +
            segmentTimeLength
    return segmentIdentifierToTotalSegmentDurationMap.getValue(segment.segmentSource)
  }

  /**
   * Returns the Map of [SegmentType.segmentSource] to total time length of segments for the
   * [SegmentType.segmentSource] in seconds ([Double])
   */
  override fun getState(): Map<String, Double> {
    return segmentIdentifierToTotalSegmentDurationMap
  }

  /**
   * Prints a line for each [SegmentType.segmentSource] with the related time length of segments in
   * seconds ([Double]).
   */
  override fun printState() {
    segmentIdentifierToTotalSegmentDurationMap.forEach { (identifier, totalTimeLength) ->
      logInfo(
          "The analyzed segments with source '$identifier' yielded a total of $totalTimeLength seconds of analysis data.")
    }
  }
}
