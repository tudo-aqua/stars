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
import kotlin.collections.component1
import kotlin.collections.component2
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.metric.providers.TSCAndTSCInstanceNodeMetricProvider
import tools.aqua.stars.core.metric.serialization.SerializableTSCResult
import tools.aqua.stars.core.metric.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_INDENT
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_SEPARATOR
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * This class implements the [TSCAndTSCInstanceNodeMetricProvider] and tracks the missed
 * [TSCInstance]s for each [TSC].
 *
 * This class implements the [Stateful] interface. Its state contains the [Map] of [TSC]s to a
 * [List] of missed [TSCInstance]s.
 *
 * This class implements the [Serializable] interface. It serializes all missed [TSCInstance] for
 * their respective [TSC].
 *
 * This class implements [Loggable] and logs the final [Map] of missed [TSCInstance]s for [TSC]s.
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
class MissedTSCInstancesPerTSCMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val loggerIdentifier: String = "missed-tsc-instances-per-tsc",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) : TSCAndTSCInstanceNodeMetricProvider<E, T, S, U, D>, Stateful, Serializable, Loggable {
  /**
   * Map a [TSC] to a map in which the missed valid [TSCInstanceNode]s are stored:
   * Map<tsc,Map<referenceInstance,missed>>.
   */
  private val missedInstancesMap:
      MutableMap<TSC<E, T, S, U, D>, MutableMap<TSCInstanceNode<E, T, S, U, D>, Boolean>> =
      mutableMapOf()

  /**
   * Track the missed [TSCInstance]s for each [TSC] in the [missedInstancesMap]. If the current
   * [tscInstance] is invalid it is skipped.
   *
   * @param tsc The current [TSC] for which the [TSCInstance] should be set to false (not missed).
   * @param tscInstance The current [TSCInstance] which is removed from the [missedInstancesMap]
   *   list.
   */
  override fun evaluate(tsc: TSC<E, T, S, U, D>, tscInstance: TSCInstance<E, T, S, U, D>) {
    missedInstancesMap.putIfAbsent(tsc, createDefaultMissedInstanceFlagMap(tsc))
    // Check if the given tscInstance is invalid. If so, skip
    if (!tsc.possibleTSCInstances.contains(tscInstance.rootNode)) return

    // Get the valid instances map for the current tsc and set state to "not missed"
    missedInstancesMap.getValue(tsc)[tscInstance.rootNode] = false
  }

  /**
   * Creates a [MutableMap] where each [TSC.possibleTSCInstances] is mapped to true (missed).
   *
   * @return the filled [MutableMap] mapping all [TSC.possibleTSCInstances] to true (missed).
   * @throws IllegalStateException when the resulting size of the [MutableMap] is not equals to the
   *   amount of [TSCInstanceNode]s in [TSC.possibleTSCInstances].
   */
  private fun createDefaultMissedInstanceFlagMap(
      tsc: TSC<E, T, S, U, D>
  ): MutableMap<TSCInstanceNode<E, T, S, U, D>, Boolean> =
      mutableMapOf<TSCInstanceNode<E, T, S, U, D>, Boolean>().apply {
        tsc.possibleTSCInstances.forEach { putIfAbsent(it, true) }
        check(size == tsc.possibleTSCInstances.size)
      }

  /** Returns a [Map] containing the list of missed [TSCInstanceNode]s for each [TSC]. */
  override fun getState(): Map<TSC<E, T, S, U, D>, List<TSCInstanceNode<E, T, S, U, D>>> =
      missedInstancesMap.mapValues { (_, nodes) ->
        nodes.filter { instance -> instance.value }.map { instance -> instance.key }
      }

  /** Prints the count of missed [TSCInstance]s for each [TSC] using [println]. */
  override fun printState() {
    println(
        "\n$CONSOLE_SEPARATOR\n$CONSOLE_INDENT Missed TSC Instances Per TSC \n$CONSOLE_SEPARATOR")
    getState().forEach { (tsc, missedInstances) ->
      logInfo(
          "Count of unique missed instances for tsc '${tsc.identifier}': ${missedInstances.size} (of ${tsc
          .possibleTSCInstances.size} possible instances).")
      missedInstances.forEach { logFine(it) }
    }
  }

  override fun getSerializableResults(): List<SerializableTSCResult> =
      missedInstancesMap.map { (tsc, missedInstances) ->
        val resultList =
            missedInstances
                .filter { it.value }
                .map { (tscInstanceNode, _) -> SerializableTSCNode(tscInstanceNode) }
        SerializableTSCResult(
            identifier = tsc.identifier,
            source = loggerIdentifier,
            count = resultList.size,
            value = resultList)
      }
}
