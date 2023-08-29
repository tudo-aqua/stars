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

import tools.aqua.stars.core.metric.providers.ProjectionAndTSCInstanceNodeMetricProvider
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.tsc.TSCInstance
import tools.aqua.stars.core.tsc.TSCInstanceNode
import tools.aqua.stars.core.tsc.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * This class implements the [ProjectionAndTSCInstanceNodeMetricProvider] and tracks the occurred
 * valid [TSCInstance] for each [TSCProjection].
 */
class ValidTSCInstancePerProjectionMetric<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>> :
    ProjectionAndTSCInstanceNodeMetricProvider<E, T, S>, Stateful {
  /**
   * Map a [TSCProjection] to a map in which the occurrences of valid [TSCInstanceNode]s are stored:
   * Map<projection,Map<referenceInstance,List<TSCInstance>>>
   */
  val validInstancesMap:
      MutableMap<
          TSCProjection<E, T, S>,
          MutableMap<TSCInstanceNode<E, T, S>, MutableList<TSCInstance<E, T, S>>>> =
      mutableMapOf()

  /**
   * Track the valid [TSCInstance]s for each [TSCProjection] in the [validInstancesMap]. If the
   * current [tscInstance] is invalid it is skipped.
   *
   * @param projection The current [TSCProjection] for which the validity should be checked
   * @param tscInstance The current [TSCInstance] which is checked for validity
   */
  override fun evaluate(projection: TSCProjection<E, T, S>, tscInstance: TSCInstance<E, T, S>) {
    // Check if given tscInstance is valid
    if (!projection.possibleTSCInstances.contains(tscInstance.rootNode)) {
      return
    }
    validInstancesMap.putIfAbsent(projection, mutableMapOf())
    val projectionValidInstances = validInstancesMap.getValue(projection)
    projectionValidInstances.putIfAbsent(tscInstance.rootNode, mutableListOf())
    // Get already observed instances for current projection
    val projectionValidInstanceList = projectionValidInstances.getValue(tscInstance.rootNode)
    // Add current instance to list of observed instances
    projectionValidInstanceList.add(tscInstance)
  }

  /**
   * Returns the full [validInstancesMap] containing the list of valid [TSCInstance]s for each
   * [TSCProjection].
   */
  override fun getState():
      MutableMap<
          TSCProjection<E, T, S>,
          MutableMap<TSCInstanceNode<E, T, S>, MutableList<TSCInstance<E, T, S>>>> {
    return validInstancesMap
  }

  /** Prints the number of valid [TSCInstance] for each [TSCProjection] using [println]. */
  override fun printState() {
    validInstancesMap.forEach { (projection, validInstancesMap) ->
      println(
          "For projection '$projection', there are ${validInstancesMap.size} unique valid instances (of ${projection
            .possibleTSCInstances.size} possible instances).")
      println(validInstancesMap.map { it.value.size })
    }
  }
}
