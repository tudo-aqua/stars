/*
 * Copyright 2023-2025 The STARS Project Authors
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
import tools.aqua.stars.core.metric.metrics.providers.Loggable
import tools.aqua.stars.core.metric.metrics.providers.Serializable
import tools.aqua.stars.core.metric.metrics.providers.Stateful
import tools.aqua.stars.core.metric.metrics.providers.TickMetricProvider
import tools.aqua.stars.core.metric.serialization.SerializableIntResult
import tools.aqua.stars.core.types.*

/**
 * This class is an implementation of [TickMetricProvider] which provides the count of evaluated
 * ticks. This Metric is stateful as it has to track the count of observed [TickDataType]s.
 *
 * This class implements the [Stateful] interface. Its state contains the [tickCount].
 *
 * This class implements [Serializable] and stores, and compares its evaluation results.
 *
 * This class implements [Loggable] and logs the final [tickCount].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
class TickCountMetric<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val loggerIdentifier: String = "tick-count",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) : TickMetricProvider<E, T, U, D>, Stateful, Serializable, Loggable {
  /** Holds the count of [TickDataType]s that are analyzed. */
  private var tickCount: Int = 0

  /**
   * Increases the count of evaluated [TickDataType]s.
   *
   * @param tick The current [TickDataType] that is evaluated.
   * @return The number of analyzed [TickDataType]s so far.
   */
  override fun evaluate(tick: T): Int =
      (++tickCount).also { logFiner("==== Tick $tickCount: $tick ====") }

  /**
   * Returns the current [tickCount].
   *
   * @return Returns the current [tickCount].
   */
  override fun getState(): Int = tickCount

  /** Prints the current state using [println]. */
  override fun printState() {
    logInfo("Analyzed $tickCount ticks.")
  }

  override fun getSerializableResults(): List<SerializableIntResult> =
      listOf(
          SerializableIntResult(
              identifier = loggerIdentifier, source = loggerIdentifier, value = tickCount))
}
