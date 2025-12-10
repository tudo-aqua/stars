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

package tools.aqua.stars.core.metrics.evaluation

import java.util.logging.Logger
import kotlin.collections.component1
import kotlin.collections.component2
import tools.aqua.stars.core.metrics.providers.Loggable
import tools.aqua.stars.core.metrics.providers.SerializableMetric
import tools.aqua.stars.core.metrics.providers.Stateful
import tools.aqua.stars.core.metrics.providers.TSCAndTSCInstanceMetricProvider
import tools.aqua.stars.core.serialization.SerializableTSCResult
import tools.aqua.stars.core.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.types.*
import tools.aqua.stars.core.utils.ApplicationConstantsHolder.CONSOLE_INDENT
import tools.aqua.stars.core.utils.ApplicationConstantsHolder.CONSOLE_SEPARATOR

/**
 * This class implements the [TSCAndTSCInstanceMetricProvider] and tracks the missed [TSCInstance]s
 * for each [TSC].
 *
 * This class implements the [Stateful] interface. Its state contains the [Map] of [TSC]s to a
 * [List] of missed [TSCInstance]s.
 *
 * This class implements the [SerializableMetric] interface. It serializes all missed [TSCInstance]
 * for their respective [TSC].
 *
 * This class implements [Loggable] and logs the final [Map] of missed [TSCInstance]s for [TSC]s.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
@Suppress("unused")
class MissedTSCInstancesPerTSCMetric<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    override val loggerIdentifier: String = "missed-tsc-instances-per-tsc",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier),
) : TSCAndTSCInstanceMetricProvider<E, T, U, D>, Stateful, SerializableMetric, Loggable {
  /**
   * Map a [TSC] to a map in which the missed valid [TSCInstance]s are stored:
   * Map<tsc,List<referenceInstance>>.
   */
  private val missedInstances: MutableMap<TSC<E, T, U, D>, MutableList<TSCInstance<E, T, U, D>>> =
      mutableMapOf()

  /**
   * Track the missed [TSCInstance]s for each [TSC] in the [missedInstances]. If the current
   * [tscInstance] is invalid it is skipped.
   *
   * @param tsc The current [TSC] for which the [TSCInstance] should be set to false (not missed).
   * @param tscInstance The current [TSCInstance] which is removed from the [missedInstances] list.
   */
  override fun evaluate(tsc: TSC<E, T, U, D>, tscInstance: TSCInstance<E, T, U, D>) {
    missedInstances.putIfAbsent(tsc, tsc.possibleTSCInstances.toMutableList())

    // Check if the given tscInstance is invalid. If so, skip
    if (!tsc.possibleTSCInstances.contains(tscInstance)) return

    // Get the valid instances map for the current tsc and set state to "not missed"
    missedInstances.getValue(tsc).remove(tscInstance)
  }

  /** Returns a [Map] containing the list of missed [TSCInstance]s for each [TSC]. */
  override fun getState(): Map<TSC<E, T, U, D>, List<TSCInstance<E, T, U, D>>> = missedInstances

  /** Prints the count of missed [TSCInstance]s for each [TSC] using [println]. */
  override fun printState() {
    println(
        "\n$CONSOLE_SEPARATOR\n$CONSOLE_INDENT Missed TSC Instances Per TSC \n$CONSOLE_SEPARATOR"
    )
    getState().forEach { (tsc, missedInstances) ->
      logInfo(
          "Count of unique missed instances for tsc '${tsc.identifier}': ${missedInstances.size} (of ${tsc
          .possibleTSCInstances.size} possible instances)."
      )
      missedInstances.forEach { logFine(it) }
    }
  }

  override fun getSerializableResults(): List<SerializableTSCResult> =
      missedInstances.map { (tsc, missedInstances) ->
        val resultList = missedInstances.map { SerializableTSCNode(it.rootNode) }
        SerializableTSCResult(
            identifier = tsc.identifier,
            source = loggerIdentifier,
            count = resultList.size,
            value = resultList,
        )
      }
}
