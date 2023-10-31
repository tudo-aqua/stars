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

package tools.aqua.stars.core.metric.metrics.evaluation

import java.util.logging.Logger
import tools.aqua.stars.core.metric.providers.Loggable
import tools.aqua.stars.core.metric.providers.Plottable
import tools.aqua.stars.core.metric.providers.ProjectionAndTSCInstanceNodeMetricProvider
import tools.aqua.stars.core.metric.providers.Stateful
import tools.aqua.stars.core.metric.utils.getNTimes
import tools.aqua.stars.core.metric.utils.getPlot
import tools.aqua.stars.core.metric.utils.plotDataAsBarChart
import tools.aqua.stars.core.metric.utils.plotDataAsLineChart
import tools.aqua.stars.core.tsc.TSCInstance
import tools.aqua.stars.core.tsc.TSCInstanceNode
import tools.aqua.stars.core.tsc.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

const val VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME = "valid-tsc-instances-per-projection"

/**
 * This class implements the [ProjectionAndTSCInstanceNodeMetricProvider] and tracks the occurred
 * valid [TSCInstance] for each [TSCProjection].
 */
class ValidTSCInstancesPerProjectionMetric<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    override val logger: Logger = Loggable.getLogger(VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME)
) : ProjectionAndTSCInstanceNodeMetricProvider<E, T, S>, Stateful, Loggable, Plottable {
  /**
   * Map a [TSCProjection] to a map in which the occurrences of valid [TSCInstanceNode]s are stored:
   * Map<projection,Map<referenceInstance,List<TSCInstance>>>
   */
  private val validInstancesMap:
      MutableMap<
          TSCProjection<E, T, S>,
          MutableMap<TSCInstanceNode<E, T, S>, MutableList<TSCInstance<E, T, S>>>> =
      mutableMapOf()

  /**
   * Map a [TSCProjection] to a list of increasing counts of occurrences of valid [TSCInstanceNode]
   * s. Map<projection,List<increasing count>>
   */
  private val uniqueTimedInstances: MutableMap<TSCProjection<E, T, S>, MutableList<Int>> =
      mutableMapOf()

  /**
   * Track the valid [TSCInstance]s for each [TSCProjection] in the [validInstancesMap]. If the
   * current [tscInstance] is invalid it is skipped.
   *
   * @param projection The current [TSCProjection] for which the validity should be checked
   * @param tscInstance The current [TSCInstance] which is checked for validity
   */
  override fun evaluate(projection: TSCProjection<E, T, S>, tscInstance: TSCInstance<E, T, S>) {
    validInstancesMap.putIfAbsent(projection, mutableMapOf())
    // Get current count of unique and valid TSC instance for the current projection
    val projectionValidInstances = validInstancesMap.getValue(projection)

    // Track current TSC projection
    uniqueTimedInstances.putIfAbsent(projection, mutableListOf())
    val projectionValidInstancesCount = uniqueTimedInstances.getValue(projection)

    // Check if given tscInstance is valid
    if (!projection.possibleTSCInstances.contains(tscInstance.rootNode)) {
      // Add current count of observed instances to list of timed instance counts
      projectionValidInstancesCount.add(projectionValidInstances.size)
      return
    }
    projectionValidInstances.putIfAbsent(tscInstance.rootNode, mutableListOf())
    // Get already observed instances for current projection
    val projectionValidInstanceList = projectionValidInstances.getValue(tscInstance.rootNode)
    // Add current instance to list of observed instances
    projectionValidInstanceList.add(tscInstance)
    // Add current count of observed instances to list of timed instance counts
    projectionValidInstancesCount.add(projectionValidInstances.size)
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
      logInfo(
          "Count of unique valid instances for projection '$projection' is: ${validInstancesMap.size} (of " +
              "${projection.possibleTSCInstances.size} possible instances)")
      logFine("Count of valid instances per instance: " + validInstancesMap.map { it.value.size })
      logFine()
      logFine("Valid instances:")
      validInstancesMap.forEach { (key, values) ->
        logFine("The following instance occurred ${values.size} times:")
        logFine(key)
        logFiner("Occurred in:")
        values.forEach { logFiner(it.sourceSegmentIdentifier) }
        logFine("----------------")
      }
    }
  }

  /**
   * Plot the data collected with this metric.
   *
   * For each analyzed [TSCProjection] there will be two plots that are generated.
   * 1. A progress graph which shows the count of unique [TSCInstance]s depicted as a line chart
   * 2. A bar chart showing the distribution of occurrences of the [TSCInstance]s
   */
  override fun plotData() {
    val xAxisName = "unique and valid TSC instances"
    val yAxisName = "instance count"
    val yAxisNamePercentage = "$yAxisName (in %)"
    val plotFileName = "validTSCInstancesPerProjection"

    val combinedXValues = mutableListOf<Int>()
    val combinedYValues = mutableListOf<Int>()
    val combinedYPercentageValues = mutableListOf<Float>()
    val combinedLegendEntries = mutableListOf<String>()

    val projectionToValuesMap: MutableMap<String, List<Int>> = mutableMapOf()

    uniqueTimedInstances.keys.forEach { projection ->
      // Get list of timed instances for current projection. If not existing: return
      val projectionTimedInstances = uniqueTimedInstances[projection] ?: return@forEach
      val xValues: List<Int> = List(projectionTimedInstances.size) { it }
      val possibleTscInstancesForProjection = projection.possibleTSCInstances.size
      val lastYValue = projectionTimedInstances.last()
      val legendEntry = "${projection.id} ($lastYValue/$possibleTscInstancesForProjection)"

      val uniqueInstancesPlot = getPlot(legendEntry, xValues, xAxisName, yAxisName, "Projection")

      // Plot the timed absolute count of unique TSC instances for the current projection
      plotDataAsLineChart(
          plot = uniqueInstancesPlot,
          xValues = xValues,
          yValues = projectionTimedInstances,
          metricName = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          plotFileName = "${plotFileName}_${projection.id}",
          plotFileSubFolder = projection.id.toString())

      //      // Plot the timed percentage count of unique TSC instances for the current projection
      //      val yValuesPercentage: List<Float> =
      //          projectionTimedInstances.map { (it.toFloat() / possibleTscInstancesForProjection)
      // * 100 }
      //      plotDataAsLineChart(
      //          xValues = xValues,
      //          xAxisName = xAxisName,
      //          yValues = yValuesPercentage,
      //          yAxisName = yAxisNamePercentage,
      //          legendEntries = legendEntries,
      //          legendHeader = "Projection",
      //          metricName = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
      //          plotFileName = "${plotFileName}_${projection.id}_percentages",
      //          plotFileSubFolder = projection.id.toString())

      projectionToValuesMap.putIfAbsent(legendEntry, xValues)

      combinedXValues += xValues
      combinedYValues += projectionTimedInstances
      //      combinedYPercentageValues += yValuesPercentage
    }

    //    // Plot the timed absolute count of unique TSC instances for all projections combined
    //    plotDataAsLineChart(
    //        xValues = combinedXValues,
    //        xAxisName = xAxisName,
    //        yValues = combinedYValues,
    //        yAxisName = yAxisName,
    //        legendEntries = combinedLegendEntries,
    //        legendHeader = "Projection",
    //        metricName = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
    //        plotFileName = "${plotFileName}_combined")
    //
    //    // Plot the timed percentage count of unique TSC instances for all projections combined
    //    plotDataAsLineChart(
    //        xValues = combinedXValues,
    //        xAxisName = xAxisName,
    //        yValues = combinedYPercentageValues,
    //        yAxisName = yAxisNamePercentage,
    //        legendEntries = combinedLegendEntries,
    //        legendHeader = "Projection",
    //        metricName = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
    //        plotFileName = "${plotFileName}_combined_percentage")

    validInstancesMap.forEach { (projection, validInstanceMap) ->
      // Get the count of instances for the current projection ordered by occurrence
      val instanceCounts = validInstanceMap.values.map { it.size }.sortedDescending()
      plotDataAsBarChart(
          xValues = List(instanceCounts.size) { it },
          xAxisName = "instance index",
          yValues = instanceCounts,
          yAxisName = "instance count",
          legendEntries = getNTimes(projection.id.toString(), instanceCounts.size),
          legendHeader = "Projection",
          metricName = "unique-instance-occurrences",
          plotFileSubFolder = projection.id.toString(),
          plotFileName = "tscInstanceOccurrences_${projection.id}")
    }
  }
}
