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

import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.ProjectionAndTSCInstanceNodeMetricProvider
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.tsc.TSCInstance
import tools.aqua.stars.core.tsc.TSCInstanceNode
import tools.aqua.stars.core.tsc.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType
import java.util.logging.Logger

/**
 * This class implements the [ProjectionAndTSCInstanceNodeMetricProvider] and tracks the missed
 * [TSCInstance]s for each [TSCProjection].
 */
class MissedTSCInstancesPerProjectionMetric<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(override val logger: Logger =
      Loggable.getLogger("missed-tsc-instances-per-projection")) :
    ProjectionAndTSCInstanceNodeMetricProvider<E, T, S>, Stateful, Loggable {
  /**
   * Map a [TSCProjection] to a map in which the missed valid [TSCInstanceNode]s are stored:
   * Map<projection,Map<referenceInstance,missed>>
   */
  private val missedInstancesMap:
      MutableMap<TSCProjection<E, T, S>, MutableMap<TSCInstanceNode<E, T, S>, Boolean>> =
      mutableMapOf()
  /**
   * Track the missed [TSCInstance]s for each [TSCProjection] in the [missedInstancesMap]. If the
   * current [tscInstance] is invalid it is skipped.
   *
   * @param projection The current [TSCProjection] for which the [TSCInstance] should be set to
   * false (not missed).
   * @param tscInstance The current [TSCInstance] which is removed from the [missedInstancesMap]
   * list.
   */
  override fun evaluate(projection: TSCProjection<E, T, S>, tscInstance: TSCInstance<E, T, S>) {
    // The current tscInstance is invalid: skip
    if (!projection.possibleTSCInstances.contains(tscInstance.rootNode)) {
      return
    }
    missedInstancesMap.putIfAbsent(projection, createDefaultMissedInstanceFlagMap(projection))
    // Get the valid instances map for the current projection
    val projectionMissedInstancesMap = missedInstancesMap.getValue(projection)
    // Set the state for the current tscInstance to: "not missed"
    projectionMissedInstancesMap[tscInstance.rootNode] = false
  }

  /**
   * Creates a [MutableMap] where each [TSCProjection.possibleTSCInstances] is mapped to true
   * (missed).
   *
   * @return the filled [MutableMap] mapping all [TSCProjection.possibleTSCInstances] to true
   * (missed).
   *
   * @throws IllegalStateException when the resulting size of the [MutableMap] is not equals to the
   * amount of [TSCInstanceNode]s in [TSCProjection.possibleTSCInstances].
   */
  private fun createDefaultMissedInstanceFlagMap(
      projection: TSCProjection<E, T, S>
  ): MutableMap<TSCInstanceNode<E, T, S>, Boolean> {
    val emptyMissedInstanceFlagMap = mutableMapOf<TSCInstanceNode<E, T, S>, Boolean>()
    projection.possibleTSCInstances.forEach { emptyMissedInstanceFlagMap.putIfAbsent(it, true) }
    check(emptyMissedInstanceFlagMap.size == projection.possibleTSCInstances.size)
    return emptyMissedInstanceFlagMap
  }

  /** Returns a [Map] containing the list of missed [TSCInstanceNode]s for each [TSCProjection]. */
  override fun getState(): Map<TSCProjection<E, T, S>, List<TSCInstanceNode<E, T, S>>> {
    return missedInstancesMap.mapValues { projection ->
      projection.value.filter { instance -> instance.value }.map { instance -> instance.key }
    }
  }

  /** Prints the count of missed [TSCInstance]s for each [TSCProjection] using [println]. */
  override fun printState() {
    getState().forEach { (projection, missedInstances) ->
      logInfo(
          "Count of unique missed instances for projection '$projection': ${missedInstances.size} (of ${projection
          .possibleTSCInstances.size} possible instances).")
      missedInstances.forEach {
        logFine(it)
      }
    }
  }
}
