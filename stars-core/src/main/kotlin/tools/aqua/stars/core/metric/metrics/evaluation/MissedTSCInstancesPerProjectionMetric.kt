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
import tools.aqua.stars.core.types.*

/**
 * This class implements the [ProjectionAndTSCInstanceNodeMetricProvider] and tracks the missed
 * [TSCInstance]s for each [TSCProjection].
 *
 * This class implements the [Stateful] interface. Its state contains the [Map] of [TSCProjection]s
 * to a [List] of missed [TSCInstance]s.
 *
 * This class implements [Loggable] and logs the final [Map] of missed [TSCInstance]s for
 * [TSCProjection]s.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property logger [Logger] instance.
 */
@Suppress("unused")
class MissedTSCInstancesPerProjectionMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val logger: Logger = Loggable.getLogger("missed-tsc-instances-per-projection")
) : ProjectionAndTSCInstanceNodeMetricProvider<E, T, S, U, D>, Stateful, Loggable {
  /**
   * Map a [TSCProjection] to a map in which the missed valid [TSCInstanceNode]s are stored:
   * Map<projection,Map<referenceInstance,missed>>.
   */
  private val missedInstancesMap:
      MutableMap<
          TSCProjection<E, T, S, U, D>, MutableMap<TSCInstanceNode<E, T, S, U, D>, Boolean>> =
      mutableMapOf()

  /**
   * Track the missed [TSCInstance]s for each [TSCProjection] in the [missedInstancesMap]. If the
   * current [tscInstance] is invalid it is skipped.
   *
   * @param projection The current [TSCProjection] for which the [TSCInstance] should be set to
   *   false (not missed).
   * @param tscInstance The current [TSCInstance] which is removed from the [missedInstancesMap]
   *   list.
   */
  override fun evaluate(
      projection: TSCProjection<E, T, S, U, D>,
      tscInstance: TSCInstance<E, T, S, U, D>
  ) {
    missedInstancesMap.putIfAbsent(projection, createDefaultMissedInstanceFlagMap(projection))
    // Check if the given tscInstance is invalid. If so, skip
    if (!projection.possibleTSCInstances.contains(tscInstance.rootNode)) return

    // Get the valid instances map for the current projection and set state to "not missed"
    missedInstancesMap.getValue(projection)[tscInstance.rootNode] = false
  }

  /**
   * Creates a [MutableMap] where each [TSCProjection.possibleTSCInstances] is mapped to true
   * (missed).
   *
   * @return the filled [MutableMap] mapping all [TSCProjection.possibleTSCInstances] to true
   *   (missed).
   * @throws IllegalStateException when the resulting size of the [MutableMap] is not equals to the
   *   amount of [TSCInstanceNode]s in [TSCProjection.possibleTSCInstances].
   */
  private fun createDefaultMissedInstanceFlagMap(
      projection: TSCProjection<E, T, S, U, D>
  ): MutableMap<TSCInstanceNode<E, T, S, U, D>, Boolean> =
      mutableMapOf<TSCInstanceNode<E, T, S, U, D>, Boolean>().apply {
        projection.possibleTSCInstances.forEach { putIfAbsent(it, true) }
        check(size == projection.possibleTSCInstances.size)
      }

  /** Returns a [Map] containing the list of missed [TSCInstanceNode]s for each [TSCProjection]. */
  override fun getState(): Map<TSCProjection<E, T, S, U, D>, List<TSCInstanceNode<E, T, S, U, D>>> =
      missedInstancesMap.mapValues { projection ->
        projection.value.filter { instance -> instance.value }.map { instance -> instance.key }
      }

  /** Prints the count of missed [TSCInstance]s for each [TSCProjection] using [println]. */
  override fun printState() {
    getState().forEach { (projection, missedInstances) ->
      logInfo(
          "Count of unique missed instances for projection '$projection': ${missedInstances.size} (of ${projection
          .possibleTSCInstances.size} possible instances).")
      missedInstances.forEach { logFine(it) }
    }
  }
}
