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
import tools.aqua.stars.core.metric.providers.ProjectionAndTSCInstanceNodeMetricProvider
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * This class implements the [ProjectionAndTSCInstanceNodeMetricProvider] interface and tracks the
 * invalid [TSCInstance]s for each [TSCProjection].
 *
 * This class implements the [Stateful] interface. Its state contains the [Map] of [TSCProjection]s
 * to a [List] of invalid [TSCInstance]s.
 *
 * This class implements [Loggable] and logs the final [Map] of invalid [TSCInstance]s for
 * [TSCProjection]s.
 */
@Suppress("unused")
class InvalidTSCInstancesPerProjectionMetric<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    override val logger: Logger = Loggable.getLogger("invalid-tsc-instances-per-projection")
) : ProjectionAndTSCInstanceNodeMetricProvider<E, T, S>, Stateful, Loggable {
  /**
   * Map the [TSCProjection] to a map in which the occurrences of invalid [TSCInstance]s are stored.
   */
  private val invalidInstancesMap:
      MutableMap<
          TSCProjection<E, T, S>,
          MutableMap<TSCInstanceNode<E, T, S>, MutableList<TSCInstance<E, T, S>>>> =
      mutableMapOf()

  /**
   * Track the invalid [TSCInstance]s for each [TSCProjection] in the [invalidInstancesMap]. If the
   * current [tscInstance] is valid it is skipped.
   *
   * @param projection The current [TSCProjection] for which the invalidity should be checked.
   * @param tscInstance The current [TSCInstance] which is checked for invalidity.
   */
  override fun evaluate(projection: TSCProjection<E, T, S>, tscInstance: TSCInstance<E, T, S>) {
    invalidInstancesMap.putIfAbsent(projection, mutableMapOf())
    // Check if the given tscInstance is valid. If so, skip
    if (projection.possibleTSCInstances.contains(tscInstance.rootNode)) return

    // Get already observed invalid instances for current projection and add current instance
    invalidInstancesMap
        .getValue(projection)
        .getOrPut(tscInstance.rootNode) { mutableListOf() }
        .add(tscInstance)
  }

  /**
   * Returns the full [invalidInstancesMap] containing the list of invalid [TSCInstance]s for each
   * [TSCProjection].
   */
  override fun getState():
      MutableMap<
          TSCProjection<E, T, S>,
          MutableMap<TSCInstanceNode<E, T, S>, MutableList<TSCInstance<E, T, S>>>> =
      invalidInstancesMap

  /**
   * Logs and prints the number of invalid [TSCInstance] for each [TSCProjection]. Also logs count
   * of invalid classes and their reasons for invalidity.
   */
  override fun printState() {
    invalidInstancesMap.forEach { (projection, invalidInstancesMap) ->
      logInfo(
          "Count of unique invalid instances for projection '$projection': ${invalidInstancesMap.size} (of " +
              "${projection.possibleTSCInstances.size} possible instances).")

      logFine(
          "Count of unique invalid instances for projection '$projection' per instance: " +
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
}
