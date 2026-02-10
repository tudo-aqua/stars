/*
 * Copyright 2023-2026 The STARS Project Authors
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

package tools.aqua.stars.core.metrics.evaluation

import java.util.Optional
import java.util.logging.Logger
import tools.aqua.stars.core.evaluation.TickSequence
import tools.aqua.stars.core.metrics.providers.Loggable
import tools.aqua.stars.core.metrics.providers.SerializableMetric
import tools.aqua.stars.core.metrics.providers.Stateful
import tools.aqua.stars.core.metrics.providers.TickAndTickSequenceMetricProvider
import tools.aqua.stars.core.metrics.providers.TickMetricProvider
import tools.aqua.stars.core.serialization.SerializableTickDifferenceResult
import tools.aqua.stars.core.types.*
import tools.aqua.stars.core.types.TickDifference.Companion.sum
import tools.aqua.stars.core.utils.ApplicationConstantsHolder.CONSOLE_INDENT
import tools.aqua.stars.core.utils.ApplicationConstantsHolder.CONSOLE_SEPARATOR

/**
 * This class implements the [TickMetricProvider] and tracks the total [TickDifference] of all
 * analyzed [TickDataType]s.
 *
 * This class implements the [SerializableMetric] interface. It serializes the [totalTickDifference]
 * for all analyzed [TickDataType]s.
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
) : TickAndTickSequenceMetricProvider<E, T, U, D>, Stateful, SerializableMetric, Loggable {
  /** Holds the current [TickDifference] for all already analyzed [TickDataType]s. */
  private var totalTickDifference: MutableList<Pair<String, D?>> = mutableListOf()

  /** Holds the last processed [TickDataType]. */
  private var lastTick: T? = null
  private var lastTickSequence: TickSequence<T>? = null

  /**
   * Add the given [tick] to the total [TickDifference].
   *
   * @param tick The [TickDataType] for which the total [TickDifference] should be tracked.
   * @param tickSequence The [TickSequence] of ticks that the tick belongs to.
   * @return The current total [TickDifference] of all analyzed ticks.
   * @throws IllegalStateException If the [TickDifference] between the previous and the current
   *   [TickDataType] is not positive.
   */
  override fun evaluate(tick: T, tickSequence: TickSequence<T>): List<Pair<String, D?>> {
    if (tick == lastTick) return totalTickDifference

    if (lastTickSequence != tickSequence) {
      lastTick = null
      lastTickSequence = tickSequence
      totalTickDifference.add(tickSequence.name to null)
    }

    lastTick?.let { previousTick ->

      // Calculate the TickDifference between the previous and the current tick.
      val currentTickDifference = tick.currentTickUnit - previousTick.currentTickUnit

      check(tick.currentTickUnit > previousTick.currentTickUnit) {
        "The difference between the previous and the current tick should be positive! " +
            "Actual difference: $currentTickDifference."
      }

      // Update total
      var newTotal = currentTickDifference
      totalTickDifference.removeLast().second?.let { newTotal = it + currentTickDifference }
      totalTickDifference.add(tickSequence.name to newTotal)
    }

    lastTick = tick

    return totalTickDifference
  }

  /**
   * Returns the current [totalTickDifference] as Optional. Returns [Optional.empty] if no
   * [TickDataType]s have been analyzed yet.
   *
   * @return The current [totalTickDifference] for all already analyzed [TickDataType]s.
   */
  override fun getState(): List<Pair<String, D?>> = totalTickDifference

  /** Prints the current [totalTickDifference]. */
  override fun printState() {
    println(
        "\n$CONSOLE_SEPARATOR\n$CONSOLE_INDENT Total Tick Difference per Tick Sequence \n$CONSOLE_SEPARATOR"
    )
    totalTickDifference.forEach { (tickSequenceName, tickDifference) ->
      logFine(
          "The analyzed ticks from $tickSequenceName yielded a total tick difference of $tickDifference."
      )
    }
    logFine()
    logInfo(
        "The analyzed ticks yielded a total tick difference of ${totalTickDifference.mapNotNull { it.second }.sum()}."
    )
    logInfo()
  }

  override fun getSerializableResults(): List<SerializableTickDifferenceResult> {
    val result = mutableListOf<SerializableTickDifferenceResult>()
    result.addAll(
        totalTickDifference.map { (tickSequenceIdentifier, tickSequenceTickDifference) ->
          SerializableTickDifferenceResult(
              identifier = loggerIdentifier,
              source = tickSequenceIdentifier,
              value = tickSequenceTickDifference.toString(),
          )
        }
    )
    result.add(
        SerializableTickDifferenceResult(
            identifier = loggerIdentifier,
            source = "Total tick difference over all sequences",
            value = totalTickDifference.mapNotNull { it.second }.sum().toString(),
        )
    )
    return result
  }
}
