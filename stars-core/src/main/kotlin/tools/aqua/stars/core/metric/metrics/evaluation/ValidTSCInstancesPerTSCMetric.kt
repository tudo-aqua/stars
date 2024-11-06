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
import tools.aqua.stars.core.metric.serialization.SerializableTSCOccurrenceResult
import tools.aqua.stars.core.metric.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.metric.serialization.tsc.SerializableTSCOccurrence
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_INDENT
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.CONSOLE_SEPARATOR
import tools.aqua.stars.core.metric.utils.getCSVString
import tools.aqua.stars.core.metric.utils.getPlot
import tools.aqua.stars.core.metric.utils.plotDataAsBarChart
import tools.aqua.stars.core.metric.utils.plotDataAsLineChart
import tools.aqua.stars.core.metric.utils.saveAsCSVFile
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * This class implements the [TSCAndTSCInstanceNodeMetricProvider] and tracks the occurred valid
 * [TSCInstance] for each [TSC].
 *
 * This class implements the [PostEvaluationMetricProvider] which evaluates the combined results of
 * valid [TSCInstance]s for all [TSC]s.
 *
 * This class implements the [Stateful] interface. Its state contains the [Map] of [TSC]s to a
 * [List] of valid [TSCInstance]s.
 *
 * This class implements the [Serializable] interface. It serializes all valid [TSCInstance] for
 * their respective [TSC].
 *
 * This class implements [Loggable] and logs the final [Map] of invalid [TSCInstance]s for [TSC]s.
 *
 * This class implements [Plottable] and plots the distribution and temporal change of valid
 * [TSCInstance]s.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [Logger] instance.
 */
class ValidTSCInstancesPerTSCMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    override val loggerIdentifier: String = "valid-tsc-instances-per-tsc",
    override val logger: Logger = Loggable.getLogger(loggerIdentifier)
) :
    TSCAndTSCInstanceNodeMetricProvider<E, T, S, U, D>,
    PostEvaluationMetricProvider<E, T, S, U, D>,
    Stateful,
    Serializable,
    Loggable,
    Plottable {
  /**
   * Map a [TSC] to a map in which the occurrences of valid [TSCInstanceNode]s are stored:
   * - Map<tsc,Map<referenceInstance,List<TSCInstance>>>.
   */
  private val validInstancesMap:
      MutableMap<
          TSC<E, T, S, U, D>,
          MutableMap<TSCInstanceNode<E, T, S, U, D>, MutableList<TSCInstance<E, T, S, U, D>>>> =
      mutableMapOf()

  /**
   * Maps [TSC] to a list of increasing counts of occurrences of valid [TSCInstanceNode]s:
   * - Map<tsc,List<increasing count>>.
   */
  private val uniqueTimedInstances: MutableMap<TSC<E, T, S, U, D>, MutableList<Int>> =
      mutableMapOf()

  /** Maps the name of a [TSC] the a list of timed [TSCInstance] occurrences. */
  private val combinedTSCToOccurredInstancesMap = mutableMapOf<String, Pair<List<Int>, List<Int>>>()

  /**
   * Maps the name of a [TSC] the a list of timed [TSCInstance] occurrences in relation to the count
   * of possible [TSCInstance]s.
   */
  private val combinedTSCToOccurredInstancesPercentagesMap =
      mutableMapOf<String, Pair<List<Int>, List<Float>>>()

  /** This metric does not depend on another metric. */
  override val dependsOn: Any? = null

  private val xAxisName = "unique and valid TSC instances"
  private val yAxisName = "instance count"
  private val yAxisNamePercentage = "$yAxisName (in %)"
  private val legendHeader = "TSC"
  private val plotFileName = "validTSCInstancesProgressPerTSC"
  private val plotFileNameCombined = "${plotFileName}_combined"
  private val plotFileNameCombinedPercentage = "${plotFileNameCombined}_percentage"

  /**
   * Track the valid [TSCInstance]s for each [TSC] in the [validInstancesMap]. If the current
   * [tscInstance] is invalid it is skipped.
   *
   * @param tsc The current [TSC] for which the validity should be checked.
   * @param tscInstance The current [TSCInstance] which is checked for validity.
   */
  override fun evaluate(tsc: TSC<E, T, S, U, D>, tscInstance: TSCInstance<E, T, S, U, D>) {
    // Get current count of unique and valid TSC instance for the current TSC
    val validInstances = validInstancesMap.getOrPut(tsc) { mutableMapOf() }

    // Track current TSC
    val validInstancesCount = uniqueTimedInstances.getOrPut(tsc) { mutableListOf() }

    // Check if given tscInstance is valid
    if (!tsc.possibleTSCInstances.contains(tscInstance.rootNode)) {
      // Add current count of observed instances to list of timed instance counts
      validInstancesCount.add(validInstances.size)
      return
    }

    // Get already observed instances for current TSC
    val validInstanceList = validInstances.getOrPut(tscInstance.rootNode) { mutableListOf() }

    // Add current instance to list of observed instances
    validInstanceList.add(tscInstance)

    // Add current count of observed instances to list of timed instance counts
    validInstancesCount.add(validInstances.size)
  }

  /**
   * Returns the full [validInstancesMap] containing the [List] of valid [TSCInstance]s for each
   * [TSC].
   *
   * @return A [Map] containing the [List] of valid [TSCInstance]s for each [TSC].
   */
  override fun getState():
      MutableMap<
          TSC<E, T, S, U, D>,
          MutableMap<TSCInstanceNode<E, T, S, U, D>, MutableList<TSCInstance<E, T, S, U, D>>>> =
      validInstancesMap

  /** Prints the number of valid [TSCInstance] for each [TSC] using [println]. */
  override fun printState() {
    println(
        "\n$CONSOLE_SEPARATOR\n$CONSOLE_INDENT Valid TSC Instances Per TSC \n$CONSOLE_SEPARATOR")
    validInstancesMap.forEach { (tsc, validInstancesMap) ->
      logInfo(
          "Count of unique valid instances for tsc '${tsc.identifier}' is: ${validInstancesMap.size} (of " +
              "${tsc.possibleTSCInstances.size} possible instances)")

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
   * all TSCs.
   *
   * As these maps are required for both [writePlots] and [writePlotDataCSV] they have to be
   * calculated beforehand.
   */
  override fun postEvaluate() {
    uniqueTimedInstances.forEach { (tsc, instances) ->
      // Get the count of all possible TSC instances for the current tsc
      val possibleTscInstancesForTSC = tsc.possibleTSCInstances.size

      // Get the count of occurred TSC instances
      val lastYValue = instances.last()

      val legendEntry = "${tsc.identifier} ($lastYValue/$possibleTscInstancesForTSC)"

      // Calculate the percentage of the occurred instances against the count of possible instances
      val yValuesPercentage: List<Float> =
          instances.map { (it.toFloat() / possibleTscInstancesForTSC) * 100 }

      // Save values of current tsc to combined map
      combinedTSCToOccurredInstancesMap[legendEntry] = List(instances.size) { it } to instances
      combinedTSCToOccurredInstancesPercentagesMap[legendEntry] =
          List(yValuesPercentage.size) { it } to yValuesPercentage
    }
  }

  /**
   * Prints the collected data in the [combinedTSCToOccurredInstancesMap] and
   * [combinedTSCToOccurredInstancesPercentagesMap].
   */
  override fun printPostEvaluationResult() {
    logFine("Combined TSCs to occurred instances: $combinedTSCToOccurredInstancesMap")
    logFine(
        "Combined TSCs to occurred instances in percentages: $combinedTSCToOccurredInstancesPercentagesMap")
  }

  override fun getSerializableResults(): List<SerializableTSCOccurrenceResult> =
      validInstancesMap.map { (tsc, validInstances) ->
        val resultList =
            validInstances.map { (tscInstanceNode, tscInstances) ->
              SerializableTSCOccurrence(
                  tscInstance = SerializableTSCNode(tscInstanceNode),
                  segmentIdentifiers = tscInstances.map { it.sourceSegmentIdentifier })
            }
        SerializableTSCOccurrenceResult(
            identifier = tsc.identifier,
            source = this@ValidTSCInstancesPerTSCMetric.loggerIdentifier,
            count = resultList.size,
            value = resultList)
      }

  // region Plot

  /**
   * Plot the data collected with this metric.
   *
   * For each analyzed [TSC] there will be two plots that are generated.
   * 1. A progress graph which shows the count of unique [TSCInstance]s depicted as a line chart.
   * 2. A bar chart showing the distribution of occurrences of the [TSCInstance]s.
   *
   * A combined line chart is generated that depicts the combined [TSCInstance] progress for all
   * TSCs in one chart.
   *
   * For each generated chart a CSV file with the same data and additional sliced values are
   * exported as well.
   */
  override fun writePlots() {
    plotOccurrencesProgressionPerTSC()
    plotOccurrencesPerTSC()
    plotCombinedOccurrencesProgression()
  }

  /** Plots the occurrence progression of [TSCInstance]s for each [TSC]. */
  private fun plotOccurrencesProgressionPerTSC() {
    uniqueTimedInstances.forEach { (tsc, instances) ->
      // Get the count of all possible TSC instances for the current tsc
      val possibleTscInstancesForTSC = tsc.possibleTSCInstances.size

      // Get the count of occurred TSC instances
      val lastYValue = instances.last()

      val legendEntry = "${tsc.identifier} ($lastYValue/$possibleTscInstancesForTSC)"

      // Build the plot with the values for the current tsc
      val uniqueInstancesPlot =
          getPlot(
              legendEntry = legendEntry, yValues = instances, xAxisName, yAxisName, legendHeader)

      val fileName = "${plotFileName}_${tsc.identifier}"
      val fileNamePercentage = "${fileName}_percentage"

      // Plot the timed absolute count of unique TSC instances for the current tsc, where the
      // y-axis is scaled to the possible instances
      plotDataAsLineChart(
          plot = uniqueInstancesPlot,
          yAxisScaleMaxValue = possibleTscInstancesForTSC,
          folder = loggerIdentifier,
          fileName = "${fileName}_scaled",
          subFolder = tsc.identifier)

      // Plot the timed absolute count of unique TSC instances for the current tsc, where the
      // y-axis is scaled to the occurred instances
      plotDataAsLineChart(
          plot = uniqueInstancesPlot,
          folder = loggerIdentifier,
          fileName = fileName,
          subFolder = tsc.identifier)

      // Calculate the percentage of the occurred instances against the count of possible instances
      val yValuesPercentage: List<Float> =
          instances.map { (it.toFloat() / possibleTscInstancesForTSC) * 100 }

      val percentagePlot =
          getPlot(
              legendEntry = legendEntry,
              yValues = yValuesPercentage,
              xAxisName = xAxisName,
              yAxisName = yAxisNamePercentage,
              legendHeader = legendHeader)

      // Plot the timed percentage count of unique TSC instances for the current tsc
      plotDataAsLineChart(
          plot = percentagePlot,
          folder = loggerIdentifier,
          fileName = fileNamePercentage,
          subFolder = tsc.identifier)

      // Plot the timed percentage count of unique TSC instances for the current tsc
      plotDataAsLineChart(
          plot = percentagePlot,
          folder = loggerIdentifier,
          fileName = "${fileNamePercentage}_scaled",
          yAxisScaleMaxValue = 100,
          subFolder = tsc.identifier)
    }
  }

  /** Plots the occurrence counts of [TSCInstance]s for each [TSC]. */
  private fun plotOccurrencesPerTSC() {
    validInstancesMap.forEach { (tsc, instances) ->
      // Get the count of instances for the current tsc ordered by occurrence
      val instanceCounts = instances.values.map { it.size }.sortedDescending()

      val barPlotName = "validTSCInstanceOccurrencesPerTSC"

      // Get the count of occurred TSC instances
      val lastYValue = instanceCounts.size

      val legendEntry = "${tsc.identifier} ($lastYValue/${tsc.possibleTSCInstances.size})"

      val plot =
          getPlot(
              legendEntry = legendEntry,
              yValues = instanceCounts,
              xAxisName = "instance index",
              yAxisName = "instance count",
              legendHeader = legendHeader)

      val fileName = "${barPlotName}_${tsc.identifier}"

      // Plot the occurrences of unique TSC instances for the current tsc
      plotDataAsBarChart(
          plot = plot,
          fileName = fileName,
          folder = "$loggerIdentifier-occurrences",
          subFolder = tsc.identifier)

      // Plot the occurrences of unique TSC instances for the current tsc in which the x-axis
      // is scale to the amount of all possible TSC instances
      plotDataAsBarChart(
          plot = plot,
          fileName = "${fileName}_scaled",
          folder = "$loggerIdentifier-occurrences",
          xAxisScaleMaxValue = tsc.possibleTSCInstances.size,
          subFolder = tsc.identifier)
    }
  }

  /** Plots the combined occurrence progression of [TSCInstance]s of all [TSC]s. */
  private fun plotCombinedOccurrencesProgression() {
    val combinedTotalPlot =
        getPlot(
            nameToValuesMap = combinedTSCToOccurredInstancesMap,
            xAxisName = xAxisName,
            yAxisName = yAxisName,
            legendHeader = legendHeader)

    val combinedPercentagePlot =
        getPlot(
            nameToValuesMap = combinedTSCToOccurredInstancesPercentagesMap,
            xAxisName = xAxisName,
            yAxisName = yAxisNamePercentage,
            legendHeader = legendHeader)

    // Plot the timed total count of unique TSC instances for all TSCs combined
    plotDataAsLineChart(
        plot = combinedTotalPlot, folder = loggerIdentifier, fileName = plotFileNameCombined)

    // Plot the timed percentage count of unique TSC instances for all TSCs combined
    plotDataAsLineChart(
        plot = combinedPercentagePlot,
        folder = loggerIdentifier,
        fileName = plotFileNameCombinedPercentage)

    // Plot the timed percentage count of unique TSC instances for all TSCs combined and a
    // y-axis scaled to 100%
    plotDataAsLineChart(
        plot = combinedPercentagePlot,
        folder = loggerIdentifier,
        yAxisScaleMaxValue = 100,
        fileName = "${plotFileNameCombinedPercentage}_scaled")
  }

  // endregion

  // region CSV files

  /**
   * Save the data collected with this metric.
   *
   * For each analyzed [TSC] there will be two files that are generated.
   * 1. A progress file which contains the count of unique [TSCInstance]s.
   * 2. A file containing the distribution of occurrences of the [TSCInstance]s.
   *
   * A combined file is generated that contains the combined [TSCInstance] progress for all TSCs in
   * one file.
   *
   * For each generated file additional sliced values are exported as well.
   */
  override fun writePlotDataCSV() {
    saveOccurrencesProgressionPerTSC()
    saveOccurrencesPerTSC()
    saveCombinedOccurrencesProgression()
  }

  /** Saves CSV files for the occurrence progression of [TSCInstance]s for each [TSC]. */
  private fun saveOccurrencesProgressionPerTSC() {
    uniqueTimedInstances.forEach { (tsc, instances) ->
      // Get the count of all possible TSC instances for the current tsc
      val possibleTscInstancesForTSC = tsc.possibleTSCInstances.size

      // Get the count of occurred TSC instances
      val lastYValue = instances.last()

      val legendEntry = "${tsc.identifier} ($lastYValue/$possibleTscInstancesForTSC)"

      val fileName = "${plotFileName}_${tsc.identifier}"
      val fileNamePercentage = "${fileName}_percentage"

      // Save the timed absolute count of unique TSC instances data as CSV file
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, instances),
          fileName = fileName,
          folder = loggerIdentifier,
          subFolder = tsc.identifier)

      // Save the timed absolute count of unique TSC instances data as CSV file, but only every
      // 100th entry is taken
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, instances, 100),
          fileName = "${fileName}_slice100",
          folder = loggerIdentifier,
          subFolder = tsc.identifier)

      // Calculate the percentage of the occurred instances against the count of possible instances
      val yValuesPercentage: List<Float> =
          instances.map { (it.toFloat() / possibleTscInstancesForTSC) * 100 }

      // Save the timed percentage count of unique TSC instances data as CSV file
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, yValuesPercentage),
          fileName = fileNamePercentage,
          folder = loggerIdentifier,
          subFolder = tsc.identifier)

      // Save the timed percentage count of unique TSC instances data as CSV file but only take
      // every 100th entry
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, yValuesPercentage, 100),
          fileName = "${fileNamePercentage}_slice100",
          folder = loggerIdentifier,
          subFolder = tsc.identifier)
    }
  }

  /** Saves CSV files for the occurrence counts of [TSCInstance]s for each [TSC]. */
  private fun saveOccurrencesPerTSC() {
    validInstancesMap.forEach { (tsc, instances) ->
      // Get the count of instances for the current tsc ordered by occurrence
      val instanceCounts = instances.values.map { it.size }.sortedDescending()

      val barPlotName = "validTSCInstanceOccurrencesPerTSC"

      // Get the count of occurred TSC instances
      val lastYValue = instanceCounts.size

      val legendEntry = "${tsc.identifier} ($lastYValue/${tsc.possibleTSCInstances.size})"

      val fileName = "${barPlotName}_${tsc.identifier}"

      // Save the occurrences of unique TSC instances data as CSV file
      saveAsCSVFile(
          csvString = getCSVString(legendEntry, instanceCounts),
          fileName = fileName,
          folder = "$loggerIdentifier-occurrences",
          subFolder = tsc.identifier)
    }
  }

  /** Saves CSV files for the combined occurrence progression of [TSCInstance]s of all [TSC]s. */
  private fun saveCombinedOccurrencesProgression() {
    // Save the timed total count of unique TSC instances for all TSCs combined as a CSV file
    saveAsCSVFile(
        csvString = getCSVString(combinedTSCToOccurredInstancesMap),
        fileName = plotFileNameCombined,
        folder = loggerIdentifier)

    // Save the timed total count of unique TSC instances for all TSCs combined as a CSV file
    // but only take every 100th entry
    saveAsCSVFile(
        csvString = getCSVString(combinedTSCToOccurredInstancesMap, sliceValue = 100),
        fileName = "${plotFileNameCombined}_slice100",
        folder = loggerIdentifier)

    // Save the timed percentage count of unique TSC instances for all TSCs combined as a CSV
    // file
    saveAsCSVFile(
        csvString = getCSVString(combinedTSCToOccurredInstancesPercentagesMap),
        fileName = plotFileNameCombinedPercentage,
        folder = loggerIdentifier)

    // Save the timed percentage count of unique TSC instances for all TSCs combined as a CSV
    // file but only take every 100th entry
    saveAsCSVFile(
        csvString = getCSVString(combinedTSCToOccurredInstancesPercentagesMap, sliceValue = 100),
        fileName = "${plotFileNameCombinedPercentage}_slice100",
        folder = loggerIdentifier)
  }
  // endregion
}
