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
import tools.aqua.stars.core.metric.providers.Serializable
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.metric.providers.TSCAndTSCInstanceNodeMetricProvider
import tools.aqua.stars.core.metric.serialization.SerializableTSCOccurrenceResult
import tools.aqua.stars.core.metric.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.metric.serialization.tsc.SerializableTSCOccurrence
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_INDENT
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_SEPARATOR
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * This class implements the [TSCAndTSCInstanceNodeMetricProvider] interface and tracks the invalid
 * [TSCInstance]s for each [TSC].
 *
 * This class implements the [Stateful] interface. Its state contains the [Map] of [TSC]s to a
 * [List] of invalid [TSCInstance]s.
 *
 * This class implements the [Serializable] interface. It serializes all invalid [TSCInstance] for
 * their respective [TSC].
 *
 * This class implements [Loggable] and logs the final [Map] of invalid [TSCInstance]s for [TSC]s.
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
class InvalidTSCInstancesPerTSCMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val loggerIdentifier: String = "invalid-tsc-instances-per-tsc",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) : TSCAndTSCInstanceNodeMetricProvider<E, T, S, U, D>, Stateful, Serializable, Loggable {
  /** Map the [TSC] to a map in which the occurrences of invalid [TSCInstance]s are stored. */
  private val invalidInstancesMap:
      MutableMap<
          TSC<E, T, S, U, D>,
          MutableMap<TSCInstanceNode<E, T, S, U, D>, MutableList<TSCInstance<E, T, S, U, D>>>> =
      mutableMapOf()

  /**
   * Track the invalid [TSCInstance]s for each [TSC] in the [invalidInstancesMap]. If the current
   * [tscInstance] is valid it is skipped.
   *
   * @param tsc The current [TSC] for which the invalidity should be checked.
   * @param tscInstance The current [TSCInstance] which is checked for invalidity.
   */
  override fun evaluate(tsc: TSC<E, T, S, U, D>, tscInstance: TSCInstance<E, T, S, U, D>) {
    invalidInstancesMap.putIfAbsent(tsc, mutableMapOf())
    // Check if the given tscInstance is valid. If so, skip
    if (tsc.possibleTSCInstances.contains(tscInstance.rootNode)) return

    // Get already observed invalid instances for current tsc and add current instance
    invalidInstancesMap
        .getValue(tsc)
        .getOrPut(tscInstance.rootNode) { mutableListOf() }
        .add(tscInstance)
  }

  /**
   * Returns the full [invalidInstancesMap] containing the list of invalid [TSCInstance]s for each
   * [TSC].
   */
  override fun getState():
      MutableMap<
          TSC<E, T, S, U, D>,
          MutableMap<TSCInstanceNode<E, T, S, U, D>, MutableList<TSCInstance<E, T, S, U, D>>>> =
      invalidInstancesMap

  /**
   * Logs and prints the number of invalid [TSCInstance] for each [TSC]. Also logs count of invalid
   * classes and their reasons for invalidity.
   */
  override fun printState() {
    println(
        "\n$CONSOLE_SEPARATOR\n$CONSOLE_INDENT Invalid TSC Instances Per TSC \n$CONSOLE_SEPARATOR")
    invalidInstancesMap.forEach { (tsc, invalidInstancesMap) ->
      logInfo(
          "Count of unique invalid instances for tsc '${tsc.identifier}': ${invalidInstancesMap.size} (of " +
              "${tsc.possibleTSCInstances.size} possible instances).")

      logFine(
          "Count of unique invalid instances for tsc '${tsc.identifier}' per instance: " +
              invalidInstancesMap.map { it.value.size })

      invalidInstancesMap.forEach { (referenceInstance, invalidInstances) ->
        logFiner(
            "The following invalid class occurred ${
            when (invalidInstances.size) {
              1 -> "1 time."
              else -> "${invalidInstances.size} times."
            }
          }")
        logFiner(referenceInstance)
        logFiner("Reasons for invalidity:")
        referenceInstance.validate().forEach { validation -> logFiner("- ${validation.second}") }
        logFiner("Occurred in:")
        invalidInstances.forEachIndexed { index, instance ->
          logFiner("- $index -  ${instance.sourceSegmentIdentifier}")
        }
        logFiner()
      }
    }
  }

  override fun getSerializableResults(): List<SerializableTSCOccurrenceResult> =
      invalidInstancesMap.map { (tsc, invalidInstances) ->
        val resultList =
            invalidInstances.map { (tscInstanceNode, tscInstances) ->
              SerializableTSCOccurrence(
                  tscInstance = SerializableTSCNode(tscInstanceNode),
                  segmentIdentifiers = tscInstances.map { it.sourceSegmentIdentifier })
            }
        SerializableTSCOccurrenceResult(
            identifier = tsc.identifier,
            source = loggerIdentifier,
            count = resultList.size,
            value = resultList)
      }
}
