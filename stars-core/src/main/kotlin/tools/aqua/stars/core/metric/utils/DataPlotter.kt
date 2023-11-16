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
import org.jetbrains.letsPlot.scale.scaleYContinuous

/** Sets the offset to distinguish lines with equal trajectory */
private const val POSITION_DODGE = 0.3

/**
 * Holds the current time in yyyy-MM-dd-HH-mm format taken from the [ApplicationStartTimeHolder]
 * singleton.
 */
val currentTimeAndDate: String = ApplicationStartTimeHolder.applicationStartTimeString

/**
 * Saves a PNG file with a line chart based on the given values.
 *
 * @param plot Contains all necessary data points for the bar chart
 * @param fileName The name of the resulting PNG file
 * @param folder The name of the metric from which the plot is created. The resulting PNG file is
 * located under the metrics names folder
 * @param subFolder (Default: "") This optional folder will be added after the [folder] to the
 * resulting path
 * @param yAxisScaleMaxValue (Default: null) Sets the max y-value for the chart. The data is scaled
 * accordingly.
 */
fun plotDataAsLineChart(
    plot: Plot?,
    fileName: String,
    folder: String,
    subFolder: String = "",
    yAxisScaleMaxValue: Number? = null,
) {
  if (plot == null) {
    println("Skip plotting, as there was no data provided.")
    return
  }
  var innerPlot = plot
  val plotFolder = getAndCreatePlotFolder(folder, subFolder)

  innerPlot += geomLine(stat = Stat.identity, position = positionDodge(POSITION_DODGE))
  if (yAxisScaleMaxValue != null) {
    innerPlot += scaleYContinuous(limits = -0.001 to yAxisScaleMaxValue, expand = listOf(0, 0))
  }
  ggsave(innerPlot, "$fileName.png", path = plotFolder)
}

/**
 * Saves a PNG file with a bar chart based on the given values.
 *
 * @param plot Contains all necessary data points for the bar chart
 * @param fileName The name of the resulting PNG file
 * @param folder The name of the top-level folder for this file
 * @param subFolder (Default: "") This optional folder will be added after the [folder] to the
 * resulting path
 */
fun plotDataAsBarChart(
    plot: Plot?,
    fileName: String,
    folder: String,
    subFolder: String = "",
) {
  if (plot == null) {
    println("Skip plotting, as there was no data provided.")
    return
  }
  var innerPlot = plot
  val plotFolder = getAndCreatePlotFolder(folder, subFolder)

  innerPlot += geomBar(stat = Stat.identity, position = positionDodge(), sampling = samplingNone)
  ggsave(innerPlot, "$fileName.png", path = plotFolder)
}

/**
 * Creates a [Plot] object with the given values, so that it can be given to the lets-plot library
 * for plotting.
 *
 * Creates a new [Map] with [legendEntry] and [yValues] and calls [getPlot] with it.
 *
 * @param legendEntry The name that is displayed in the legend for this data set
 * @param yValues The y-values that should be displayed
 * @param xAxisName The name that is displayed below the x-axis of the plot
 * @param yAxisName The name that is displayed besides the y-axis of the plot
 * @param legendHeader The name that is displayed above the legend list
 * @return The initialized [Plot] object
 */
fun <T : Number> getPlot(
    legendEntry: String,
    yValues: List<T>,
    xAxisName: String,
    yAxisName: String,
    legendHeader: String
): Plot? {
  return getPlot(mapOf(legendEntry to yValues), xAxisName, yAxisName, legendHeader)
}

/**
 * Creates a [Plot] object with the given values, so that it can be given to the lets-plot library
 * for plotting.
 *
 * @param nameToValuesMap The [Map] that contains the column values and its related header entry
 * @param xAxisName The name that is displayed below the x-axis of the plot
 * @param yAxisName The name that is displayed besides the y-axis of the plot
 * @param legendHeader The name that is displayed above the legend list
 * @return The initialized [Plot] object
 */
fun <T : Number> getPlot(
    nameToValuesMap: Map<String, List<T>>,
    xAxisName: String,
    yAxisName: String,
    legendHeader: String
): Plot? {
  if (nameToValuesMap.values.any { it.isEmpty() }) {
    return null
  }
  // Check that every value list has the exact same amount of elements
  check(nameToValuesMap.values.map { it.size }.distinct().count() == 1)

  val xValues = mutableListOf<Int>()
  val yValues = mutableListOf<T>()
  val legendEntries = mutableListOf<String>()

  // Bring data into correct form for lets-plot
  nameToValuesMap.forEach { (name, values) ->
    xValues += List(values.size) { it }
    yValues += values
    legendEntries += repeatNTimes(name, values.size)
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
 * Create and return a CSV string based on the given [nameToValuesMap]. Each key in the [Map] will
 * result in a new column with the value assigned to the key as its row values.
 *
 * @param nameToValuesMap The [Map] that contains the column values and its related header entry
 * @param sliceValue (Default: null) If set the values of each key in the [nameToValuesMap] will be
 * sliced by the given [sliceValue]
 * @return The CSV [String] based on the [nameToValuesMap]
 */
private fun <T : Number> getCSVString(
    nameToValuesMap: Map<String, List<T>>,
    sliceValue: Int? = null
): String {
  val valueMap: Map<String, List<T>>
  if (sliceValue != null) {
    valueMap = mutableMapOf()
    // Only take every "sliceValue"th entry for each key in the nameToValuesMap
    nameToValuesMap.forEach { (key, values) ->
      valueMap[key] = values.slice(0..values.size step (sliceValue))
    }
  } else {
    // Take original map
    valueMap = nameToValuesMap
  }

  // Create header line
  var csvString = "index;${valueMap.keys.joinToString(";")}\n"

  // Iterate over all values
  for (i in 0 until valueMap.values.first().size) {
    csvString += "$i;"
    valueMap.keys.forEach { key -> csvString += "${valueMap[key]?.get(i)};" }
    csvString += "\n"
  }
  return csvString
}

/**
 * Converts the given [nameToValuesMap] to a CSV [String] and saves this as a file.
 *
 * @param nameToValuesMap The [Map] that contains the column values and its related header entry
 * @param nameToValuesMap The [Map] that contains the column values and its related header entry
 * @param fileName The name of the saved file
 * @param folder The name of the top-level folder
 * @param subFolder (Default: "") The name of the subfolder under [folder] in which the file will be
 * saved
 * @param sliceValue (Default: null) If set the values of each key in the [nameToValuesMap] will be
 * sliced by the given [sliceValue]
 */
fun <T : Number> saveAsCSVFile(
    nameToValuesMap: Map<String, List<T>>,
    fileName: String,
    folder: String,
    sliceValue: Int? = null,
    subFolder: String = "",
) {
  val resultFolder = getAndCreateCSVFolder(folder, subFolder)
  val csvString = getCSVString(nameToValuesMap = nameToValuesMap, sliceValue = sliceValue)
  val csvFile =
      if (sliceValue == null) {
        File("$resultFolder/$fileName.csv")
      } else {
        File("$resultFolder/${fileName}_slice_$sliceValue.csv")
      }
  csvFile.createNewFile()
  csvFile.writeText(csvString)
}

/**
 * Returns the path to the folder in which the plots will be saved in. If the path does not exist it
 * will be created
 *
 * @param folder The name of the top-level folder
 * @param subFolder (Default: "") The name of the subfolder under [folder] in which the file will be
 * saved
 * @return The path to the created folder
 */
private fun getAndCreatePlotFolder(folder: String, subFolder: String): String {
  val resultFolder = "analysis-result-logs/$currentTimeAndDate/plots/$folder/$subFolder"
  File(resultFolder).mkdirs()
  return resultFolder
}

/**
 * Returns the path to the folder in which the CSV files will be saved in. If the path does not
 * exist it will be created
 *
 * @param folder The name of the top-level folder
 * @param subFolder (Default: "") The name of the subfolder under [folder] in which the file will be
 * saved
 * @return The path to the created folder
 */
private fun getAndCreateCSVFolder(folder: String, subFolder: String): String {
  val resultFolder = "analysis-result-logs/$currentTimeAndDate/csv/$folder/$subFolder"
  File(resultFolder).mkdirs()
  return resultFolder
}

/**
 * Returns the given [element] for [amount]-times in a new [List]
 *
 * @param element: The element that should be duplicated in the [List]
 * @param amount: The amount of how many times the [element] should be included in the returned
 * [List]
 * @return A [List] filled with [amount]-times the given [element]
 */
fun <T> repeatNTimes(element: T, amount: Int): List<T> {
  return List(amount) { element }
}
