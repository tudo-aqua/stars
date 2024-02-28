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
import tools.aqua.stars.core.tsc.TSCMonitorResult
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.*

/**
 * This metric implements the [PostEvaluationMetricProvider] and tracks the formulas specified as
 * [TSCNode.monitorFunction]s that evaluate to 'false'.
 *
 * This class implements the [Loggable] interface. It logs and prints the count and names of all
 * failing [TSCNode.monitorFunction]s for each [TSCProjection]. It logs the failing
 * [TSCMonitorResult]s for each [TSCProjection].
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

  /** Returns a [Map] of [TSCMonitorResult]s for all [TSCProjection]s. */
  override fun evaluate(): Map<TSCProjection<E, T, S, U, D>, List<TSCMonitorResult>> =
      dependsOn.getState().mapValues { (_, validInstancesMap) ->
        validInstancesMap.flatMap { (_, validInstances) ->
          validInstances.map { validInstance ->
            validInstance.rootNode.validateMonitors(validInstance.sourceSegmentIdentifier)
          }
        }
      }

  /** Prints the count of filed monitors for each [TSCProjection]. */
  override fun print() {
    var evaluationResult = this.evaluate()
    // Filter the result, so that only failed monitors are left
    evaluationResult =
        evaluationResult.mapValues { (_, monitors) -> monitors.filter { !it.monitorsValid } }
    // Create output for failed monitors
    evaluationResult.forEach { (projection, failedMonitors) ->
      logInfo("Count of failed monitors for projection '$projection': ${failedMonitors.size}")
      if (failedMonitors.isEmpty()) {
        return@forEach
      }
      logFine("Failed monitors for projection '$projection':")
      failedMonitors.forEach { failedMonitor ->
        logFine("Monitor failed in: ${failedMonitor.segmentIdentifier}")
        logFine("List of edges leading to failed monitor: ${failedMonitor.edgeList}")
      }
      logFine()
    }
  }
}
