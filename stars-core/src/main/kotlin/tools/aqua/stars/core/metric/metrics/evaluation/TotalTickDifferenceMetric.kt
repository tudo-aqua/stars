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

import java.util.Optional
import java.util.logging.Logger
import tools.aqua.stars.core.metric.metrics.providers.Loggable
import tools.aqua.stars.core.metric.metrics.providers.Serializable
import tools.aqua.stars.core.metric.metrics.providers.Stateful
import tools.aqua.stars.core.metric.metrics.providers.TickMetricProvider
import tools.aqua.stars.core.metric.serialization.SerializableTickDifferenceResult
import tools.aqua.stars.core.types.*

/**
 * This class implements the [TickMetricProvider] and tracks the total [TickDifference] of all
 * analyzed ticks.
 *
 * This class implements the [SerializableMetric] interface. It serializes the [totalTickDifference]
 * for all analyzed ticks.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
@Suppress("unused")
class TotalTickDifferenceMetric<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    override val loggerIdentifier: String = "total-tick-difference",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier),
) : TickMetricProvider<E, T, U, D>, Stateful, SerializableMetric, Loggable {
  /** Holds the current [TickDifference] for all already analyzed ticks. */
  private var totalTickDifference: D? = null

  /**
   * Add the given [tick] to the total [TickDifference].
   *
   * @param tick The [TickDataType] for which the total [TickDifference] should be tracked.
   * @return The current total [TickDifference] of all analyzed ticks.
   * @throws IllegalStateException If the [TickDifference] between the previous and the current
   *   [TickDataType] is not positive.
   */
  override fun evaluate(tick: T): Optional<D> {
    // The tick has at least one previous TickData object from which a TickDifference can be
    // calculated.
    tick.previousTick?.let { previousTick ->

      // Calculate the TickDifference between the previous and the current tick.
      val currentTickDifference = tick.currentTickUnit - previousTick.currentTickUnit

      check(tick.currentTickUnit > previousTick.currentTickUnit) {
        "The difference between the previous and the current tick should be positive! " +
            "Actual difference: $currentTickDifference."
      }

      // Update total
      var newTotal = currentTickDifference
      totalTickDifference?.let { newTotal = it + currentTickDifference }
      totalTickDifference = newTotal
    }

    return Optional.ofNullable(totalTickDifference)
  }

  /**
   * Returns the current [totalTickDifference] as Optional. Returns [Optional.empty] if no ticks
   * have been analyzed yet.
   *
   * @return The current [totalTickDifference] for all already analyzed ticks.
   */
  override fun getState(): Optional<D> = Optional.ofNullable(totalTickDifference)

  /** Prints the current [totalTickDifference]. */
  override fun printState() {
    logInfo("The analyzed ticks yielded a total tick difference of $totalTickDifference.")
  }

  override fun getSerializableResults(): List<SerializableTickDifferenceResult> =
      totalTickDifference?.let {
        listOf(
            SerializableTickDifferenceResult(
                identifier = loggerIdentifier,
                source = loggerIdentifier,
                value = it.serialize(),
            )
        )
      } ?: emptyList()
}
