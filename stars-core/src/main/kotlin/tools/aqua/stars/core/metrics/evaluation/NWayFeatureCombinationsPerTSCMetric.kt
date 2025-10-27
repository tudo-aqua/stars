/*
 * Copyright 2025 The STARS Project Authors
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

package tools.aqua.stars.core.metrics.evaluation

import java.util.logging.Logger
import kotlin.collections.plusAssign
import tools.aqua.stars.core.evaluation.NWayPredicateCombination
import tools.aqua.stars.core.metrics.providers.Loggable
import tools.aqua.stars.core.metrics.providers.Plottable
import tools.aqua.stars.core.metrics.providers.SerializableMetric
import tools.aqua.stars.core.metrics.providers.Stateful
import tools.aqua.stars.core.metrics.providers.TSCAndTSCInstanceMetricProvider
import tools.aqua.stars.core.serialization.SerializableNWayFeatureCombinationsResult
import tools.aqua.stars.core.serialization.tsc.SerializableTSCNode
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.utils.combinations
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit
import tools.aqua.stars.core.utils.ApplicationConstantsHolder
import tools.aqua.stars.core.utils.getCSVString
import tools.aqua.stars.core.utils.getPlot
import tools.aqua.stars.core.utils.plotDataAsLineChart
import tools.aqua.stars.core.utils.saveAsCSVFile

/**
 * N-way metric over [tools.aqua.stars.core.tsc.TSC] features (i.e.,
 * [tools.aqua.stars.core.tsc.node.TSCLeafNode]s).
 *
 * For a given n, this metric tracks (per [tools.aqua.stars.core.tsc.TSC]) how many unique
 * n-combinations of leaf nodes (i.e., features) have been observed **across valid instances only**.
 * The denominator is the total number of such combinations that are *possible* based on all
 * possible TSC instances.
 *
 * @param E [tools.aqua.stars.core.types.EntityType].
 * @param T [tools.aqua.stars.core.types.TickDataType].
 * @param S [tools.aqua.stars.core.types.SegmentType].
 * @param U [tools.aqua.stars.core.types.TickUnit].
 * @param D [tools.aqua.stars.core.types.TickDifference].
 * @param n The n-way combination size. Must be >= 1.
 * @property identifier identifier (name).
 * @property loggerIdentifier identifier (name) for the logger.
 * @property logger [java.util.logging.Logger] instance.
 */
class NWayFeatureCombinationsPerTSCMetric<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    private val n: Int,
    override val identifier: String = "$n-way-feature-combinations-per-tsc",
    override val loggerIdentifier: String = identifier,
    override val logger: Logger = Loggable.getLogger(loggerIdentifier),
) :
    TSCAndTSCInstanceMetricProvider<E, T, S, U, D>,
    Stateful,
    SerializableMetric,
    Loggable,
    Plottable {

  init {
    require(n >= 1) { "n must be >= 1" }
  }

  /** Observed combinations over time (for plotting) and as a set (for results). */
  private val observedPerTSC: MutableMap<TSC<E, T, S, U, D>, MutableSet<NWayPredicateCombination>> =
      mutableMapOf()
  private val timedCountsPerTSC: MutableMap<TSC<E, T, S, U, D>, MutableList<Int>> = mutableMapOf()

  /** Cached map of all possible n-way combinations per [TSC]. */
  private val possiblePerTSC: MutableMap<TSC<E, T, S, U, D>, Set<NWayPredicateCombination>> =
      mutableMapOf()

  /**
   * Combined (legendEntry -> (x-values, y-values)) maps for plotting/CSV (absolute and percentage).
   */
  private val combinedAbsolute: MutableMap<String, Pair<List<Int>, List<Int>>> = mutableMapOf()
  private val combinedPercentage: MutableMap<String, Pair<List<Int>, List<Float>>> = mutableMapOf()

  // region Metric core

  /**
   * Evaluates a given [tools.aqua.stars.core.tsc.instance.TSCInstance] and updates the observed
   * combinations, timed counts, and possible combinations for the respective TSC based on the valid
   * state of the instance and n-way predicate combinations.
   *
   * @param tsc The [TSC] graph used for evaluation.
   * @param tscInstance The specific instance of the [TSC] being evaluated, including its root node
   *   and source segment identifier.
   */
  override fun evaluate(tsc: TSC<E, T, S, U, D>, tscInstance: TSCInstance<E, T, S, U, D>) {
    val observedSet = observedPerTSC.getOrPut(tsc) { mutableSetOf() }
    val timedCounts = timedCountsPerTSC.getOrPut(tsc) { mutableListOf() }
    possiblePerTSC.getOrPut(tsc) { tsc.getAllPossibleNWayPredicateCombinations(n) }

    // Only consider instances that are valid, according to the same rule as the dependency metric
    val isValid = tsc.possibleTSCInstances.contains(tscInstance.rootNode)
    if (isValid) {
      // Extract leaf labels of the *evaluated* valid instance
      val instanceLeafLabels = tscInstance.rootNode.extractLeafLabels()

      // Add all n-combinations from this instance
      combinations(instanceLeafLabels, n).forEach { combo ->
        observedSet += NWayPredicateCombination(combo.sorted())
      }
    }

    // Track time-series (cumulative size of observed unique combinations) at every step
    timedCounts.add(observedSet.size)
  }

  override fun getState() = observedPerTSC to possiblePerTSC

  override fun printState() {
    println(
        "\n${ApplicationConstantsHolder.CONSOLE_SEPARATOR}\n${ApplicationConstantsHolder.CONSOLE_INDENT} ($n)-Way Leaf Combinations Seen Per TSC \n${ApplicationConstantsHolder.CONSOLE_SEPARATOR}"
    )
    observedPerTSC.forEach { (tsc, seenSet) ->
      val allPossible =
          possiblePerTSC.getOrPut(tsc) { tsc.getAllPossibleNWayPredicateCombinations(n) }
      logInfo(
          "TSC '${tsc.identifier}': ${seenSet.size}/${allPossible.size} unique $n-way combinations seen."
      )
    }
  }

  override fun getSerializableResults(): List<SerializableNWayFeatureCombinationsResult> {
    val results = mutableListOf<SerializableNWayFeatureCombinationsResult>()

    observedPerTSC.forEach { (tsc, observed) ->
      val observedSorted: List<List<String>> =
          observed.map { it.elements }.sortedBy { it.joinToString("\u0001") }

      val possible =
          possiblePerTSC.getOrPut(tsc) { tsc.getAllPossibleNWayPredicateCombinations(n) }.size
      val serTsc = SerializableTSCNode(tsc.rootNode)

      results +=
          SerializableNWayFeatureCombinationsResult(
              identifier = tsc.identifier,
              source = loggerIdentifier,
              n = n,
              tsc = serTsc,
              seenCombinations = observed.size,
              possibleCombinations = possible,
              value = observedSorted,
          )
    }

    return results
  }

  // endregion

  // region Plot + CSV
  override fun writePlots() {
    plotProgressionPerTSC()
    plotCombinedProgression()
  }

  override fun writePlotDataCSV() {
    saveProgressionPerTSC()
    saveCombinedProgression()
  }

  private fun plotProgressionPerTSC() {
    observedPerTSC.forEach { (tsc, seenSet) ->
      val yAbs = timedCountsPerTSC[tsc].orEmpty()
      if (yAbs.isEmpty()) return@forEach

      val possible =
          possiblePerTSC.getOrPut(tsc) { tsc.getAllPossibleNWayPredicateCombinations(n) }.size
      val legend = "${tsc.identifier} (${seenSet.size}/$possible)"
      val xName = "seen instances"
      val yName = "unique n-combinations"
      val legendHeader = "TSC"
      val baseName = "nWayLeafCombinationsProgressPerTSC_${tsc.identifier}_n$n"

      val plotAbs =
          getPlot(
              legendEntry = legend,
              yValues = yAbs,
              xAxisName = xName,
              yAxisName = yName,
              legendHeader = legendHeader,
          )
      plotDataAsLineChart(
          plot = plotAbs,
          yAxisScaleMaxValue = possible,
          folder = loggerIdentifier,
          fileName = "${baseName}_scaled",
          subFolder = tsc.identifier,
      )
      plotDataAsLineChart(
          plot = plotAbs,
          folder = loggerIdentifier,
          fileName = baseName,
          subFolder = tsc.identifier,
      )

      val yPct = yAbs.map { if (possible == 0) 0f else (it.toFloat() / possible) * 100f }
      val plotPct =
          getPlot(
              legendEntry = legend,
              yValues = yPct,
              xAxisName = xName,
              yAxisName = "$yName (in %)",
              legendHeader = legendHeader,
          )
      plotDataAsLineChart(
          plot = plotPct,
          folder = loggerIdentifier,
          fileName = "${baseName}_percentage",
          subFolder = tsc.identifier,
      )
      plotDataAsLineChart(
          plot = plotPct,
          folder = loggerIdentifier,
          fileName = "${baseName}_percentage_scaled",
          yAxisScaleMaxValue = 100,
          subFolder = tsc.identifier,
      )
    }
  }

  private fun plotCombinedProgression() {
    // Build combined maps on demand
    combinedAbsolute.clear()
    combinedPercentage.clear()

    observedPerTSC.forEach { (tsc, _) ->
      val yAbs = timedCountsPerTSC[tsc].orEmpty()
      if (yAbs.isEmpty()) return@forEach
      val possible =
          possiblePerTSC.getOrPut(tsc) { tsc.getAllPossibleNWayPredicateCombinations(n) }.size
      val seen = observedPerTSC[tsc]?.size ?: 0
      val legend = "${tsc.identifier} ($seen/$possible)"
      val yPct = yAbs.map { if (possible == 0) 0f else (it.toFloat() / possible) * 100f }
      combinedAbsolute[legend] = List(yAbs.size) { it } to yAbs
      combinedPercentage[legend] = List(yPct.size) { it } to yPct
    }

    if (combinedAbsolute.isEmpty()) return
    val xName = "seen instances"
    val yName = "unique n-combinations"
    val legendHeader = "TSC"
    val base = "nWayLeafCombinationsProgressCombined_n$n"

    val plotAbs =
        getPlot(
            nameToValuesMap = combinedAbsolute,
            xAxisName = xName,
            yAxisName = yName,
            legendHeader = legendHeader,
        )
    val plotPct =
        getPlot(
            nameToValuesMap = combinedPercentage,
            xAxisName = xName,
            yAxisName = "$yName (in %)",
            legendHeader = legendHeader,
        )

    plotDataAsLineChart(plot = plotAbs, folder = loggerIdentifier, fileName = base)
    plotDataAsLineChart(plot = plotPct, folder = loggerIdentifier, fileName = "${base}_percentage")
    plotDataAsLineChart(
        plot = plotPct,
        folder = loggerIdentifier,
        yAxisScaleMaxValue = 100,
        fileName = "${base}_percentage_scaled",
    )
  }

  private fun saveProgressionPerTSC() {
    observedPerTSC.forEach { (tsc, seenSet) ->
      val yAbs = timedCountsPerTSC[tsc].orEmpty()
      if (yAbs.isEmpty()) return@forEach

      val possible =
          possiblePerTSC.getOrPut(tsc) { tsc.getAllPossibleNWayPredicateCombinations(n) }.size
      val legend = "${tsc.identifier} (${seenSet.size}/$possible)"
      val baseName = "nWayLeafCombinationsProgressPerTSC_${tsc.identifier}_n$n"

      saveAsCSVFile(
          csvString = getCSVString(legend, yAbs),
          folder = loggerIdentifier,
          fileName = baseName,
          subFolder = tsc.identifier,
      )
      saveAsCSVFile(
          csvString = getCSVString(legend, yAbs, 100),
          folder = loggerIdentifier,
          fileName = "${baseName}_slice100",
          subFolder = tsc.identifier,
      )

      val yPct = yAbs.map { if (possible == 0) 0f else (it.toFloat() / possible) * 100f }
      saveAsCSVFile(
          csvString = getCSVString(legend, yPct),
          folder = loggerIdentifier,
          fileName = "${baseName}_percentage",
          subFolder = tsc.identifier,
      )
      saveAsCSVFile(
          csvString = getCSVString(legend, yPct, 100),
          folder = loggerIdentifier,
          fileName = "${baseName}_percentage_slice100",
          subFolder = tsc.identifier,
      )
    }
  }

  private fun saveCombinedProgression() {
    // Build combined maps on demand (same as in plotCombinedProgression)
    combinedAbsolute.clear()
    combinedPercentage.clear()

    observedPerTSC.forEach { (tsc, _) ->
      val yAbs = timedCountsPerTSC[tsc].orEmpty()
      if (yAbs.isEmpty()) return@forEach
      val possible =
          possiblePerTSC.getOrPut(tsc) { tsc.getAllPossibleNWayPredicateCombinations(n) }.size
      val seen = observedPerTSC[tsc]?.size ?: 0
      val legend = "${tsc.identifier} ($seen/$possible)"
      val yPct = yAbs.map { if (possible == 0) 0f else (it.toFloat() / possible) * 100f }
      combinedAbsolute[legend] = List(yAbs.size) { it } to yAbs
      combinedPercentage[legend] = List(yPct.size) { it } to yPct
    }

    val base = "nWayLeafCombinationsProgressCombined_n$n"
    if (combinedAbsolute.isNotEmpty()) {
      saveAsCSVFile(
          csvString = getCSVString(combinedAbsolute),
          folder = loggerIdentifier,
          fileName = base,
      )
      saveAsCSVFile(
          csvString = getCSVString(combinedAbsolute, sliceValue = 100),
          folder = loggerIdentifier,
          fileName = "${base}_slice100",
      )
    }
    if (combinedPercentage.isNotEmpty()) {
      saveAsCSVFile(
          csvString = getCSVString(combinedPercentage),
          folder = loggerIdentifier,
          fileName = "${base}_percentage",
      )
      saveAsCSVFile(
          csvString = getCSVString(combinedPercentage, sliceValue = 100),
          folder = loggerIdentifier,
          fileName = "${base}_percentage_slice100",
      )
    }
  }

  // endregion
}
