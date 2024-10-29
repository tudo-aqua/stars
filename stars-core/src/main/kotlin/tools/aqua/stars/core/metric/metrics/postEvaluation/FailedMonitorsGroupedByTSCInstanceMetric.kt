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
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.PostEvaluationMetricProvider
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_INDENT
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_SEPARATOR
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.TSCFailedMonitorInstance
import tools.aqua.stars.core.tsc.instance.*
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * This metric implements the [PostEvaluationMetricProvider] and tracks the formulas specified as
 * [TSCNode.monitors] that evaluate to 'false'.
 *
 * This class implements the [Loggable] interface. It logs and prints the count and names of all
 * failing [TSCNode.monitors] for each [TSC]. It logs the failing [TSCFailedMonitorInstance]s for
 * each [TSC] and groups it by the [TSCInstance].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property dependsOn The instance of a [ValidTSCInstancesPerTSCMetric] on which this metric
 *   depends on and needs for its calculation.
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
@Suppress("unused")
class FailedMonitorsGroupedByTSCInstanceMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val dependsOn: ValidTSCInstancesPerTSCMetric<E, T, S, U, D>,
    override val loggerIdentifier: String = "failed-monitors-grouped-by-tsc-instance",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) : PostEvaluationMetricProvider<E, T, S, U, D>, Loggable {

  /**
   * Holds a [Map] from a [TSC] to a [Map] from a node label (as [String], representing the ID of
   * the monitor) to a [Map] from a [TSCInstanceNode] to a [List] of all occurring
   * [TSCInstanceNode]s.
   */
  private val failedMonitors:
      MutableMap<
          TSC<E, T, S, U, D>,
          Map<String, Map<TSCInstanceNode<E, T, S, U, D>, List<TSCInstance<E, T, S, U, D>>>>> =
      mutableMapOf()

  /**
   * Calculates a grouped [Map] of [TSCFailedMonitorInstance]s with grouped [TSCInstance]s for all
   * [TSC]s by validating all monitors for all valid [TSCInstance]s.
   */
  override fun postEvaluate() {
    failedMonitors.clear()
    failedMonitors.putAll(
        dependsOn.getState().mapValues { (_, validInstancesMap) ->
          validInstancesMap.values
              .flatten()
              .flatMap { tscInstance ->
                tscInstance.rootNode.validateMonitors(tscInstance.sourceSegmentIdentifier).map {
                    failedMonitor ->
                  failedMonitor to tscInstance
                }
              }
              .groupBy({ it.first.nodeLabel }, { it.second })
              .mapValues { it.value.groupBy { t -> t.rootNode } }
        })
  }

  /** Prints the count of failed monitors for each [TSC]. */
  override fun printPostEvaluationResult() {
    println(
        "\n$CONSOLE_SEPARATOR\n$CONSOLE_INDENT Failed Monitors Grouped By TSC \n$CONSOLE_SEPARATOR")
    failedMonitors.forEach { (tsc, failedMonitors) ->
      if (failedMonitors.isEmpty()) return@forEach

      logInfo("Failed monitors for tsc '${tsc.identifier}':")

      failedMonitors.forEach { monitor ->
        logInfo(
            "Monitor '${monitor.key}' failed ${monitor.value.values.sumOf { it.size }} times " +
                "in ${monitor.value.size} unique TSC instances.")
        logFine(
            "Count of grouped TSC instances: ${monitor.value.values.map { it.size }.sortedDescending()}")

        monitor.value.forEach { (tscInstanceNode, tscInstances) ->
          logFine("Failed ${tscInstances.size} times for the following tsc instance:")
          tscInstances.forEach {
            logFiner()
            logFiner(it.sourceSegmentIdentifier)
          }
          logFine(tscInstanceNode)
        }
      }
    }
  }
}
