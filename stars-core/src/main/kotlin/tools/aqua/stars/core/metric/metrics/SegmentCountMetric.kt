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

package tools.aqua.stars.core.metric.metrics

import tools.aqua.stars.core.metric.providers.SegmentMetricProvider
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * This class is an implementation of [SegmentMetricProvider] which provides the count of evaluated
 * segments. This Metric is stateful as it has to track the count of observed [SegmentType]s.
 */
class SegmentCountMetric<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> :
    SegmentMetricProvider<E, T, S>, Stateful {
  /** Holds the count of [SegmentType]s that are analyzed */
  private var segmentCount: Int = 0

  /**
   * Increases the count of evaluated [SegmentType]s.
   *
   * @param segment The current [SegmentType] that is evaluated
   * @return The number of analyzed [SegmentType]s so far
   */
  override fun evaluate(segment: SegmentType<E, T, S>): Int {
    segmentCount++
    return segmentCount
  }

  /**
   * Returns the current [segmentCount].
   *
   * @return Returns the current [segmentCount]
   */
  override fun getState(): Int {
    return segmentCount
  }

  /** Prints the current state using [println]. */
  override fun printState() {
    println("Analyzed $segmentCount Segments!")
  }
}