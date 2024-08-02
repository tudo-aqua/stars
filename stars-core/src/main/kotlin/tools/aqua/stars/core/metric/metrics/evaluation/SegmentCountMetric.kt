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
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.types.*

/**
 * This class is an implementation of [SegmentMetricProvider] which provides the count of evaluated
 * segments. This Metric is stateful as it has to track the count of observed [SegmentType]s.
 *
 * This class implements the [Stateful] interface. Its state contains the [segmentCount].
 *
 * This class implements [Loggable] and logs the final [segmentCount].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property logger [Logger] instance.
 */
class SegmentCountMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(override val logger: Logger = Loggable.getLogger("segment-count")) :
    SegmentMetricProvider<E, T, S, U, D>, Stateful, Loggable {
  /** Holds the count of [SegmentType]s that are analyzed. */
  private var segmentCount: Int = 0

  /**
   * Increases the count of evaluated [SegmentType]s.
   *
   * @param segment The current [SegmentType] that is evaluated.
   * @return The number of analyzed [SegmentType]s so far.
   */
  override fun evaluate(segment: SegmentType<E, T, S, U, D>): Int =
      (++segmentCount).also { logFiner("==== Segment $segmentCount: $segment ====") }

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
}
