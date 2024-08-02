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
 * [TSCFailedMonitorInstance]s for each [TSCProjection].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property dependsOn The instance of a [ValidTSCInstancesPerProjectionMetric] on which this metric
 *   depends on and needs for its calculation.
 * @property logger [Logger] instance.
 */
@Suppress("unused")
class FailedMonitorsMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val dependsOn: ValidTSCInstancesPerProjectionMetric<E, T, S, U, D>,
    override val logger: Logger = Loggable.getLogger("failed-monitors")
) : PostEvaluationMetricProvider<E, T, S, U, D>, Loggable {

  /** Holds all failed monitors after calling [postEvaluate]. */
  val failedMonitors:
      MutableMap<TSCProjection<E, T, S, U, D>, List<TSCFailedMonitorInstance<E, T, S, U, D>>> =
      mutableMapOf()
  /**
   * Calculates a [Map] of [TSCFailedMonitorInstance]s for all [TSCProjection]s by validating all
   * monitors for all valid [TSCInstance]s.
   */
  override fun postEvaluate() {
    failedMonitors.clear()
    failedMonitors.putAll(
        dependsOn.getState().mapValues { (_, validInstancesMap) ->
          validInstancesMap.flatMap { (_, validInstances) ->
            validInstances.flatMap { validInstance ->
              validInstance.rootNode.validateMonitors(validInstance.sourceSegmentIdentifier)
            }
          }
        })
  }

  /** Prints the count of failed monitors for each [TSCProjection]. */
  override fun printPostEvaluationResult() {
    failedMonitors.forEach { (projection, failedMonitors) ->
      logInfo("Count of failed monitors for projection '$projection': ${failedMonitors.size}")

      if (failedMonitors.isEmpty()) return@forEach

      logFine("Failed monitors for projection '$projection':")
      failedMonitors.forEach { failedMonitor ->
        logFine(
            "Monitors ${failedMonitor.monitorLabel} failed in: ${failedMonitor.segmentIdentifier}")
        logFine("Monitor failed at: ${failedMonitor.nodeLabel}")
        logFiner("Failed in TSC instance:\n${failedMonitor.tscInstance}")
      }
      logFine()
    }
  }
}
