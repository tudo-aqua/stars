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

package tools.aqua.stars.core.metric.metrics.postEvaluation

import java.util.logging.Logger
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerProjectionMetric
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.PostEvaluationMetricProvider
import tools.aqua.stars.core.tsc.TSCFailedMonitorInstance
import tools.aqua.stars.core.tsc.instance.*
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.*

/**
 * This metric implements the [PostEvaluationMetricProvider] and tracks the formulas specified as
 * [TSCNode.monitorFunction]s that evaluate to 'false'.
 *
 * This class implements the [Loggable] interface. It logs and prints the count and names of all
 * failing [TSCNode.monitorFunction]s for each [TSCProjection]. It logs the failing
 * [TSCFailedMonitorInstance]s for each [TSCProjection] and groups it by the [TSCInstanceNode].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param onlyLeafNodes (Default: false) Whether the monitor should only be triggered for leaf
 *   nodes.
 * @property dependsOn The instance of a [ValidTSCInstancesPerProjectionMetric] on which this metric
 *   depends on and needs for its calculation.
 * @property logger [Logger] instance.
 */
@Suppress("unused")
class FailedMonitorsGroupedByTSCNodeMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val dependsOn: ValidTSCInstancesPerProjectionMetric<E, T, S, U, D>,
    override val logger: Logger = Loggable.getLogger("failed-monitors-grouped-by-node"),
    private val onlyLeafNodes: Boolean = false
) : PostEvaluationMetricProvider<E, T, S, U, D>, Loggable {

  /**
   * Holds a [Map] from a [TSCProjection] to a [Map] from a node label (as [String], representing
   * the ID of the monitor) to a [Map] from a node label to a [List] of all occurring
   * [TSCInstanceNode]s including the node label.
   */
  private val failedMonitors:
      MutableMap<
          TSCProjection<E, T, S, U, D>,
          Map<String, Map<String, List<TSCInstance<E, T, S, U, D>>>>> =
      mutableMapOf()

  /**
   * Calculates a grouped [Map] of [TSCFailedMonitorInstance]s with grouped [TSCInstance]s for all
   * [TSCProjection]s by validating all monitors for all valid [TSCInstance]s.
   */
  override fun postEvaluate() {
    failedMonitors.clear()
    failedMonitors.putAll(
        dependsOn.getState().mapValues { (_, validInstancesMap) ->
          validInstancesMap.values
              .flatten()
              .flatMap { tscInstance ->
                tscInstance.rootNode.validateMonitors(tscInstance.sourceSegmentIdentifier).map {
                    tscFailedMonitorInstance ->
                  tscFailedMonitorInstance to tscInstance
                }
              }
              .groupBy({ it.first.nodeLabel }, { it.second })
              .mapValues {
                it.value
                    .map { t ->
                      t.rootNode.getAllEdges().mapNotNull { edge ->
                        if (onlyLeafNodes && edge.destination.edges.isNotEmpty()) null
                        else edge.destination.label to t
                      }
                    }
                    .flatten()
              }
              .mapValues { (_, values) ->
                values
                    .groupBy(keySelector = { it.first }, valueTransform = { it.second })
                    .toList()
                    .sortedByDescending { it.second.size }
                    .toMap()
              }
        })
  }

  /** Prints the count of failed monitors for each [TSCProjection]. */
  override fun printPostEvaluationResult() {
    failedMonitors.forEach { (projection, failedMonitors) ->
      if (failedMonitors.isEmpty()) return@forEach

      logInfo("Failed monitors for projection '$projection':")

      failedMonitors.forEach { monitor ->
        logInfo(
            "Monitor '${monitor.key}' failed ${monitor.value.values.sumOf { it.size }} times " +
                "in ${monitor.value.size} unique TSC nodes.")
        logFiner(
            "Count of grouped TSC nodes: ${monitor.value.values.map { it.size }.sortedDescending()}")

        monitor.value.forEach { (nodeLabel, tscInstances) ->
          logFine(
              "Failed ${"%04d".format(tscInstances.size)} times for the following TSC node: '$nodeLabel'")
          logFiner("Failed in:")
          tscInstances.forEach {
            logFiner()
            logFiner("'${it.sourceSegmentIdentifier}'")
            logFiner(it.rootNode)
          }
        }
        logFine()
      }
    }
  }
}
