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

package tools.aqua.stars.core.metric.utils

import java.io.File
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.pos.positionDodge
import org.jetbrains.letsPlot.sampling.samplingNone
import org.jetbrains.letsPlot.scale.scaleXContinuous
import org.jetbrains.letsPlot.scale.scaleYContinuous

/** Sets the offset to distinguish lines with equal trajectory. */
private const val POSITION_DODGE = 0.3

/**
 * Saves a PNG file with a line chart based on the given values.
 *
 * @param plot Contains all necessary data points for the bar chart.
 * @param fileName The name of the resulting PNG file.
 * @param folder The name of the metric from which the plot is created. The resulting PNG file is
 * located under the metrics names folder.
 * @param subFolder (Default: "") This optional folder will be added after the [folder] to the
 * resulting path.
 * @param yAxisScaleMaxValue (Default: null) Sets the max y-value for the chart. The data is scaled
 * accordingly.
 * @param xAxisScaleMaxValue (Default: null) Sets the max x-value for the chart. The data is scaled
 * accordingly.
 */
fun plotDataAsLineChart(
    plot: Plot?,
    fileName: String,
    folder: String,
    subFolder: String = "",
    yAxisScaleMaxValue: Number? = null,
    xAxisScaleMaxValue: Number? = null
) {
  if (plot == null) {
    println("Skip plotting, as there was no data provided.")
    return
  }
  var innerPlot = plot
  val plotFolder = getAndCreatePlotFolder(folder, subFolder)

  innerPlot += geomLine(stat = Stat.identity, position = positionDodge(POSITION_DODGE))

  if (yAxisScaleMaxValue != null)
      innerPlot += scaleYContinuous(limits = -0.001 to yAxisScaleMaxValue, expand = listOf(0, 0))

  if (xAxisScaleMaxValue != null)
      innerPlot += scaleXContinuous(limits = -0.001 to xAxisScaleMaxValue, expand = listOf(0, 0))

  ggsave(innerPlot, "$fileName.png", path = plotFolder)
}

/**
 * Saves a PNG file with a bar chart based on the given values.
 *
 * @param plot Contains all necessary data points for the bar chart.
 * @param fileName The name of the resulting PNG file.
 * @param folder The name of the top-level folder for this file.
 * @param subFolder (Default: "") This optional folder will be added after the [folder] to the
 * resulting path.
 * @param yAxisScaleMaxValue (Default: null) Sets the max y-value for the chart. The data is scaled
 * accordingly.
 * @param xAxisScaleMaxValue (Default: null) Sets the max x-value for the chart. The data is scaled
 * accordingly.
 */
fun plotDataAsBarChart(
    plot: Plot?,
    fileName: String,
    folder: String,
    subFolder: String = "",
    yAxisScaleMaxValue: Number? = null,
    xAxisScaleMaxValue: Number? = null
) {
  if (plot == null) {
    println("Skip plotting, as there was no data provided.")
    return
  }

  var innerPlot = plot
  val plotFolder = getAndCreatePlotFolder(folder, subFolder)

  if (yAxisScaleMaxValue != null)
      innerPlot += scaleYContinuous(limits = -0.001 to yAxisScaleMaxValue, expand = listOf(0, 0))

  if (xAxisScaleMaxValue != null)
      innerPlot += scaleXContinuous(limits = -0.001 to xAxisScaleMaxValue, expand = listOf(0, 0))

  innerPlot += geomBar(stat = Stat.identity, position = positionDodge(), sampling = samplingNone)
  ggsave(innerPlot, "$fileName.png", path = plotFolder)
}

/**
 * Creates a [Plot] object with the given values, so that it can be given to the lets-plot library
 * for plotting.
 *
 * Creates a new [Map] with [legendEntry] and [yValues] and calls [getPlot] with it.
 *
 * @param T1 [Number].
 * @param T2 [Number].
 * @param legendEntry The name that is displayed in the legend for this data set.
 * @param xValues The x-values that should be used for the y-values.
 * @param yValues The y-values that should be displayed.
 * @param xAxisName (Default: 'x') The name that is displayed below the x-axis of the plot.
 * @param yAxisName (Default 'y') The name that is displayed besides the y-axis of the plot.
 * @param legendHeader (Default 'Legend') The name that is displayed above the legend list.
 * @return The initialized [Plot] object.
 */
fun <T1 : Number, T2 : Number> getPlot(
    legendEntry: String,
    xValues: List<T1>,
    yValues: List<T2>,
    xAxisName: String = "x",
    yAxisName: String = "y",
    legendHeader: String = "Legend"
): Plot? = getPlot(mapOf(legendEntry to (xValues to yValues)), xAxisName, yAxisName, legendHeader)

/**
 * Creates a [Plot] object with the given values, so that it can be given to the lets-plot library
 * for plotting.
 *
 * Creates a new [Map] with [legendEntry] and [yValues] and calls [getPlot] with it.
 *
 * @param T [Number].
 * @param legendEntry The name that is displayed in the legend for this data set.
 * @param yValues The y-values that should be displayed.
 * @param xAxisName (Default: 'x') The name that is displayed below the x-axis of the plot.
 * @param yAxisName (Default 'y') The name that is displayed besides the y-axis of the plot.
 * @param legendHeader (Default 'Legend') The name that is displayed above the legend list.
 * @return The initialized [Plot] object.
 */
fun <T : Number> getPlot(
    legendEntry: String,
    yValues: List<T>,
    xAxisName: String = "x",
    yAxisName: String = "y",
    legendHeader: String = "Legend"
): Plot? =
    getPlot(
        mapOf(legendEntry to (List(yValues.size) { it } to yValues)),
        xAxisName,
        yAxisName,
        legendHeader)

/**
 * Creates a [Plot] object with the given values, so that it can be given to the lets-plot library
 * for plotting.
 *
 * @param T1 [Number].
 * @param T2 [Number].
 * @param legendEntries The names that are displayed in the legend for the data sets.
 * @param xAndYValues A [List] of x- and y-value [List]s. Each [List] item corresponds to one item
 * of [legendEntries].
 * @param xAxisName (Default: 'x') The name that is displayed below the x-axis of the plot.
 * @param yAxisName (Default 'y') The name that is displayed besides the y-axis of the plot.
 * @param legendHeader (Default 'Legend') The name that is displayed above the legend list.
 * @return The initialized [Plot] object.
 */
fun <T1 : Number, T2 : Number> getPlot(
    legendEntries: List<String>,
    xAndYValues: List<Pair<List<T1>, List<T2>>>,
    xAxisName: String = "x",
    yAxisName: String = "y",
    legendHeader: String = "Legend"
): Plot? {
  require(legendEntries.size == xAndYValues.size) {
    "The amount of given legend entries should equal the size of the given value pairs."
  }
  val legendEntryToValueMap = mutableMapOf<String, Pair<List<T1>, List<T2>>>()
  legendEntries.forEachIndexed { index, legendEntry ->
    legendEntryToValueMap[legendEntry] = xAndYValues[index]
  }
  return getPlot(legendEntryToValueMap, xAxisName, yAxisName, legendHeader)
}

/**
 * Creates a [Plot] object with the given values, so that it can be given to the lets-plot library
 * for plotting.
 *
 * @param T1 [Number].
 * @param T2 [Number].
 * @param nameToValuesMap The [Map] that contains the x- and y-values in relation to their legend
 * entry.
 * @param xAxisName The name that is displayed below the x-axis of the plot.
 * @param yAxisName The name that is displayed besides the y-axis of the plot.
 * @param legendHeader The name that is displayed above the legend list.
 * @return The initialized [Plot] object.
 */
fun <T1 : Number, T2 : Number> getPlot(
    nameToValuesMap: Map<String, Pair<List<T1>, List<T2>>>,
    xAxisName: String,
    yAxisName: String,
    legendHeader: String
): Plot? {
  if (nameToValuesMap.isEmpty()) {
    return null
  }
  if (nameToValuesMap.values.any { it.second.isEmpty() }) {
    return null
  }
  // Check that every value list has the exact same amount of elements
  check(nameToValuesMap.values.map { it.first.size + it.second.size }.distinct().count() == 1)

  val xValues = mutableListOf<T1>()
  val yValues = mutableListOf<T2>()
  val legendEntries = mutableListOf<String>()

  // Bring data into correct form for lets-plot
  nameToValuesMap.forEach { (name, values) ->
    xValues += values.first
    yValues += values.second
    legendEntries += List(values.first.size) { name }
  }

  // Create Map with all necessary data points
  val plotData =
      mutableMapOf(xAxisName to xValues, yAxisName to yValues, legendHeader to legendEntries)

  return letsPlot(plotData) {
    x = xAxisName
    y = yAxisName
    color = legendHeader
    fill = legendHeader
  }
}

/**
 * Returns the path to the folder in which the plots will be saved in. If the path does not exist it
 * will be created.
 *
 * @param folder The name of the top-level folder.
 * @param subFolder (Default: "") The name of the sub-folder under [folder] in which the file will
 * be saved.
 * @return The path to the created folder.
 */
private fun getAndCreatePlotFolder(folder: String, subFolder: String): String =
    "analysis-result-logs/${ApplicationStartTimeHolder.applicationStartTimeString}/plots/$folder/$subFolder".also {
      File(it).mkdirs()
    }
