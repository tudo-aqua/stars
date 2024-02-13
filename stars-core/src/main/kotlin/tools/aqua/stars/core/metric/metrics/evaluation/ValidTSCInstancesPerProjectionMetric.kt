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

@file:Suppress("StringLiteralDuplication")

package tools.aqua.stars.core.metric.metrics.evaluation

import java.util.logging.Logger
import tools.aqua.stars.core.metric.providers.*
import tools.aqua.stars.core.metric.utils.getCSVString
import tools.aqua.stars.core.metric.utils.getPlot
import tools.aqua.stars.core.metric.utils.plotDataAsBarChart
import tools.aqua.stars.core.metric.utils.plotDataAsLineChart
import tools.aqua.stars.core.metric.utils.saveAsCSVFile
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/** Valid instances projection name. */
private const val VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME: String =
    "valid-tsc-instances-per-projection"

/** Valid instances occurrences name. */
private const val VALID_TSC_INSTANCES_OCCURRENCES_PER_PROJECTION_METRIC_NAME: String =
    "valid-tsc-instances-occurrences-per-projection"

/**
 * This class implements the [ProjectionAndTSCInstanceNodeMetricProvider] and tracks the occurred
 * valid [TSCInstance] for each [TSCProjection].
 */
class ValidTSCInstancesPerProjectionMetric<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    override val logger: Logger = Loggable.getLogger(VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME)
) :
    ProjectionAndTSCInstanceNodeMetricProvider<E, T, S>,
    PostEvaluationMetricProvider<E, T, S>,
    Stateful,
    Loggable,
    Plottable {
  /**
   * Map a [TSCProjection] to a map in which the occurrences of valid [TSCInstanceNode]s are stored:
   * Map<projection,Map<referenceInstance,List<TSCInstance>>>.
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

  /** Maps the name of a [TSCProjection] the a list of timed [TSCInstance] occurrences. */
  private val combinedProjectionToOccurredInstancesMap =
      mutableMapOf<String, Pair<List<Int>, List<Int>>>()

  /**
   * Maps the name of a [TSCProjection] the a list of timed [TSCInstance] occurrences in relation to
   * the count of possible [TSCInstance]s.
   */
  private val combinedProjectionToOccurredInstancesPercentagesMap =
      mutableMapOf<String, Pair<List<Int>, List<Float>>>()

  /** This metric does not depend on another metric. */
  override val dependsOn: Any? = null

  private val xAxisName = "unique and valid TSC instances"
  private val yAxisName = "instance count"
  private val yAxisNamePercentage = "$yAxisName (in %)"
  private val legendHeader = "Projection"
  private val plotFileName = "validTSCInstancesProgressPerProjection"
  private val plotFileNameCombined = "${plotFileName}_combined"
  private val plotFileNameCombinedPercentage = "${plotFileNameCombined}_percentage"

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
          MutableMap<TSCInstanceNode<E, T, S>, MutableList<TSCInstance<E, T, S>>>> =
      validInstancesMap

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
   * Calculates the combined [Map]s that contain the occurrences and their percentages combined for
   * all projections.
   *
   * As these maps are required for both [plotData] and [writePlotData] they have to be calculated
   * beforehand.
   */
  override fun evaluate() {
    uniqueTimedInstances.forEach { (projection, instances) ->
      // Get the count of all possible TSC instances for the current projection
      val possibleTscInstancesForProjection = projection.possibleTSCInstances.size

      // Get the count of occurred TSC instances
      val lastYValue = instances.last()

      val legendEntry = "$projection ($lastYValue/$possibleTscInstancesForProjection)"

      // Calculate the percentage of the occurred instances against the count of possible instances
      val yValuesPercentage: List<Float> =
          instances.map { (it.toFloat() / possibleTscInstancesForProjection) * 100 }

      // Save values of current projection to combined map
      combinedProjectionToOccurredInstancesMap[legendEntry] =
          List(instances.size) { it } to instances
      combinedProjectionToOccurredInstancesPercentagesMap[legendEntry] =
          List(yValuesPercentage.size) { it } to yValuesPercentage
    }
  }

  /**
   * Prints the collected data in the [combinedProjectionToOccurredInstancesMap] and
   * [combinedProjectionToOccurredInstancesPercentagesMap].
   */
  override fun print() {
    logFine("Combined projections to occurred instances: $combinedProjectionToOccurredInstancesMap")
    logFine(
        "Combined projections to occurred instances in percentages: $combinedProjectionToOccurredInstancesPercentagesMap")
  }

  // region Plot

  /**
   * Plot the data collected with this metric.
   *
   * For each analyzed [TSCProjection] there will be two plots that are generated.
   * 1. A progress graph which shows the count of unique [TSCInstance]s depicted as a line chart
   * 2. A bar chart showing the distribution of occurrences of the [TSCInstance]s
   *
   * A combined line chart is generated that depicts the combined [TSCInstance] progress for all
   * projections in one chart.
   *
   * For each generated chart a CSV file with the same data and additional sliced values are
   * exported as well.
   */
  override fun plotData() {
    plotOccurrencesProgressionPerProjection()
    plotOccurrencesPerProjection()
    plotCombinedOccurrencesProgression()
  }

  /** Plots the occurrence progression of [TSCInstance]s for each [TSCProjection]. */
  private fun plotOccurrencesProgressionPerProjection() {
    uniqueTimedInstances.forEach { (projection, instances) ->
      // Get the count of all possible TSC instances for the current projection
      val possibleTscInstancesForProjection = projection.possibleTSCInstances.size

      // Get the count of occurred TSC instances
      val lastYValue = instances.last()

      val legendEntry = "$projection ($lastYValue/$possibleTscInstancesForProjection)"

      // Build the plot with the values for the current projection
      val uniqueInstancesPlot =
          getPlot(
              legendEntry = legendEntry, yValues = instances, xAxisName, yAxisName, legendHeader)

      val fileName = "${plotFileName}_${projection}"
      val fileNamePercentage = "${fileName}_percentage"

      // Plot the timed absolute count of unique TSC instances for the current projection, where the
      // y-axis is scaled to the possible instances
      plotDataAsLineChart(
          plot = uniqueInstancesPlot,
          yAxisScaleMaxValue = possibleTscInstancesForProjection,
          folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          fileName = "${fileName}_scaled",
          subFolder = projection.toString())

      // Plot the timed absolute count of unique TSC instances for the current projection, where the
      // y-axis is scaled to the occurred instances
      plotDataAsLineChart(
          plot = uniqueInstancesPlot,
          folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          fileName = fileName,
          subFolder = projection.toString())

      // Calculate the percentage of the occurred instances against the count of possible instances
      val yValuesPercentage: List<Float> =
          instances.map { (it.toFloat() / possibleTscInstancesForProjection) * 100 }

      val percentagePlot =
          getPlot(
              legendEntry = legendEntry,
              yValues = yValuesPercentage,
              xAxisName = xAxisName,
              yAxisName = yAxisNamePercentage,
              legendHeader = legendHeader)

      // Plot the timed percentage count of unique TSC instances for the current projection
      plotDataAsLineChart(
          plot = percentagePlot,
          folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          fileName = fileNamePercentage,
          subFolder = projection.toString())

      // Plot the timed percentage count of unique TSC instances for the current projection
      plotDataAsLineChart(
          plot = percentagePlot,
          folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          fileName = "${fileNamePercentage}_scaled",
          yAxisScaleMaxValue = 100,
          subFolder = projection.toString())
    }
  }

  /** Plots the occurrence counts of [TSCInstance]s for each [TSCProjection]. */
  private fun plotOccurrencesPerProjection() {
    validInstancesMap.forEach { (projection, instances) ->
      // Get the count of instances for the current projection ordered by occurrence
      val instanceCounts = instances.values.map { it.size }.sortedDescending()

      val barPlotName = "validTSCInstanceOccurrencesPerProjection"

      // Get the count of occurred TSC instances
      val lastYValue = instanceCounts.size

      val legendEntry = "$projection ($lastYValue/${projection.possibleTSCInstances.size})"

      val plot =
          getPlot(
              legendEntry = legendEntry,
              yValues = instanceCounts,
              xAxisName = "instance index",
              yAxisName = "instance count",
              legendHeader = legendHeader)

      val fileName = "${barPlotName}_${projection}"

      // Plot the occurrences of unique TSC instances for the current projection
      plotDataAsBarChart(
          plot = plot,
          fileName = fileName,
          folder = VALID_TSC_INSTANCES_OCCURRENCES_PER_PROJECTION_METRIC_NAME,
          subFolder = projection.toString())

      // Plot the occurrences of unique TSC instances for the current projection in which the x-axis
      // is scale to the amount of all possible TSC instances
      plotDataAsBarChart(
          plot = plot,
          fileName = "${fileName}_scaled",
          folder = VALID_TSC_INSTANCES_OCCURRENCES_PER_PROJECTION_METRIC_NAME,
          xAxisScaleMaxValue = projection.possibleTSCInstances.size,
          subFolder = projection.toString())
    }
  }

  /** Plots the combined occurrence progression of [TSCInstance]s of all [TSCProjection]s. */
  private fun plotCombinedOccurrencesProgression() {
    val combinedTotalPlot =
        getPlot(
            nameToValuesMap = combinedProjectionToOccurredInstancesMap,
            xAxisName = xAxisName,
            yAxisName = yAxisName,
            legendHeader = legendHeader)

    val combinedPercentagePlot =
        getPlot(
            nameToValuesMap = combinedProjectionToOccurredInstancesPercentagesMap,
            xAxisName = xAxisName,
            yAxisName = yAxisNamePercentage,
            legendHeader = legendHeader)

    // Plot the timed total count of unique TSC instances for all projections combined
    plotDataAsLineChart(
        plot = combinedTotalPlot,
        folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
        fileName = plotFileNameCombined)

    // Plot the timed percentage count of unique TSC instances for all projections combined
    plotDataAsLineChart(
        plot = combinedPercentagePlot,
        folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
        fileName = plotFileNameCombinedPercentage)

    // Plot the timed percentage count of unique TSC instances for all projections combined and a
    // y-axis scaled to 100%
    plotDataAsLineChart(
        plot = combinedPercentagePlot,
        folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
        yAxisScaleMaxValue = 100,
        fileName = "${plotFileNameCombinedPercentage}_scaled")
  }
  // endregion

  // region CSV files

  /**
   * Save the data collected with this metric.
   *
   * For each analyzed [TSCProjection] there will be two files that are generated.
   * 1. A progress file which contains the count of unique [TSCInstance]s
   * 2. A file containing the distribution of occurrences of the [TSCInstance]s
   *
   * A combined file is generated that contains the combined [TSCInstance] progress for all
   * projections in one file.
   *
   * For each generated file additional sliced values are exported as well.
   */
  override fun writePlotData() {
    saveOccurrencesProgressionPerProjection()
    saveOccurrencesPerProjection()
    saveCombinedOccurrencesProgression()
  }

  /** Saves CSV files for the occurrence progression of [TSCInstance]s for each [TSCProjection]. */
  private fun saveOccurrencesProgressionPerProjection() {
    uniqueTimedInstances.forEach { (projection, instances) ->
      // Get the count of all possible TSC instances for the current projection
      val possibleTscInstancesForProjection = projection.possibleTSCInstances.size

      // Get the count of occurred TSC instances
      val lastYValue = instances.last()

      val legendEntry = "$projection ($lastYValue/$possibleTscInstancesForProjection)"

      val fileName = "${plotFileName}_${projection}"
      val fileNamePercentage = "${fileName}_percentage"

      // Save the timed absolute count of unique TSC instances data as CSV file
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, instances),
          fileName = fileName,
          folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          subFolder = projection.toString())

      // Save the timed absolute count of unique TSC instances data as CSV file, but only every
      // 100th entry is taken
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, instances, 100),
          fileName = "${fileName}_slice100",
          folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          subFolder = projection.toString())

      // Calculate the percentage of the occurred instances against the count of possible instances
      val yValuesPercentage: List<Float> =
          instances.map { (it.toFloat() / possibleTscInstancesForProjection) * 100 }

      // Save the timed percentage count of unique TSC instances data as CSV file
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, yValuesPercentage),
          fileName = fileNamePercentage,
          folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          subFolder = projection.toString())

      // Save the timed percentage count of unique TSC instances data as CSV file but only take
      // every 100th entry
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, yValuesPercentage, 100),
          fileName = "${fileNamePercentage}_slice100",
          folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME,
          subFolder = projection.toString())
    }
  }

  /** Saves CSV files for the occurrence counts of [TSCInstance]s for each [TSCProjection]. */
  private fun saveOccurrencesPerProjection() {
    validInstancesMap.forEach { (projection, instances) ->
      // Get the count of instances for the current projection ordered by occurrence
      val instanceCounts = instances.values.map { it.size }.sortedDescending()

      val barPlotName = "validTSCInstanceOccurrencesPerProjection"

      // Get the count of occurred TSC instances
      val lastYValue = instanceCounts.size

      val legendEntry = "$projection ($lastYValue/${projection.possibleTSCInstances.size})"

      val fileName = "${barPlotName}_${projection}"

      // Save the occurrences of unique TSC instances data as CSV file
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, instanceCounts),
          fileName = fileName,
          folder = VALID_TSC_INSTANCES_OCCURRENCES_PER_PROJECTION_METRIC_NAME,
          subFolder = projection.toString())
    }
  }

  /**
   * Saves CSV files for the combined occurrence progression of [TSCInstance]s of all
   * [TSCProjection]s.
   */
  private fun saveCombinedOccurrencesProgression() {
    // Save the timed total count of unique TSC instances for all projections combined as a CSV file
    saveAsCSVFile(
        csvString = getCSVString(combinedProjectionToOccurredInstancesMap),
        fileName = plotFileNameCombined,
        folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME)

    // Save the timed total count of unique TSC instances for all projections combined as a CSV file
    // but only take every 100th entry
    saveAsCSVFile(
        csvString = getCSVString(combinedProjectionToOccurredInstancesMap, sliceValue = 100),
        fileName = "${plotFileNameCombined}_slice100",
        folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME)

    // Save the timed percentage count of unique TSC instances for all projections combined as a CSV
    // file
    saveAsCSVFile(
        csvString = getCSVString(combinedProjectionToOccurredInstancesPercentagesMap),
        fileName = plotFileNameCombinedPercentage,
        folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME)

    // Save the timed percentage count of unique TSC instances for all projections combined as a CSV
    // file but only take every 100th entry
    saveAsCSVFile(
        csvString = getCSVString(combinedProjectionToOccurredInstancesPercentagesMap, 100),
        fileName = "${plotFileNameCombinedPercentage}_slice100",
        folder = VALID_TSC_INSTANCES_PER_PROJECTION_METRIC_NAME)
  }
  // endregion
}
