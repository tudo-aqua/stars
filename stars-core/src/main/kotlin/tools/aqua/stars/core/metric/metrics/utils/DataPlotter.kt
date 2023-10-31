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

package tools.aqua.stars.core.metric.metrics.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import jetbrains.letsPlot.Stat
import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomLine
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.positionDodge
import jetbrains.letsPlot.scale.scaleXContinuous
import jetbrains.letsPlot.scale.scaleYContinuous

/** Sets the offset to distinguish lines with equal trajectory */
private const val POSITION_DODGE = 0.3

/** Holds the current time in yyyy-MM-dd-HH-mm format */
val currentTimeAndDate: String =
    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"))

/**
 * Saves a PNG file with a line chart of the given values.
 *
 * It is possible to plot multiple sources into on chart. It is important that each source has the
 * exact same amount of elements. The Lets-Plot library requires the data to be given in the
 * following way: Take x elements from source "a". The first x elements of [xValues] and [yValues]
 * have to be the values of "a". To create a legend entry, the [legendEntries] have to be filled. In
 * this [List] x elements with the value "a" have to be created. You can use [getNTimes] for this
 * purpose. The next x entries then have to be from another source "b", and so on.
 *
 * For n sources each with x entries, the [xValues] and [yValues] have to have the size "x * n".
 *
 * @param yValues The [List] of all y-values
 * @param xValues The [List] of all x-values
 * @param xAxisName The name of the x-axis
 * @param yAxisName The name of the y-axis
 * @param legendEntries The [List] of the entries for the legend (see description)
 * @param legendHeader The headline which is displayed above the legend entries
 * @param plotFileName The name of the resulting PNG file
 * @param metricName The name of the metric from which the plot is created. The resulting PNG file
 * is located under the metrics names folder
 * @param plotFileSubFolder (Default: "") This optional folder will be added after the [metricName]
 * to the resulting path
 */
fun <T : Number> plotDataAsLineChart(
    yValues: List<T>,
    xValues: List<T>,
    xAxisName: String,
    yAxisName: String,
    legendEntries: List<String>,
    legendHeader: String,
    plotFileName: String,
    metricName: String,
    plotFileSubFolder: String = ""
) {
    // Check that both xValues and yValues are not empty
    if (xValues.isEmpty() || yValues.isEmpty()) {
        return
    }

    // Check that both xValues and yValues have the same amount of elements
    if (xValues.size != yValues.size) {
        return
    }

    // Convert values to float to calculate max plot size later on
    val xFloatValues = xValues.map { it.toFloat() }
    val yFloatValues = yValues.map { it.toFloat() }

    val plotData =
        mutableMapOf(
            xAxisName to xFloatValues, yAxisName to yFloatValues, legendHeader to legendEntries)

    var plot =
        letsPlot(plotData) {
            x = xAxisName
            y = yAxisName
            color = legendHeader
            fill = legendHeader
        }

    val plotFolder = "analysis-result-logs/$currentTimeAndDate/plots/$metricName/$plotFileSubFolder"
    File(plotFolder).mkdirs()

    plot +=
        geomLine(stat = Stat.identity, position = positionDodge(POSITION_DODGE)) +
                scaleXContinuous(limits = -0.001 to xFloatValues.max(), expand = listOf(0, 0)) +
                scaleYContinuous(limits = -0.001 to yFloatValues.max(), expand = listOf(0, 0))
    ggsave(plot, "$plotFileName.png", path = plotFolder)
}

/**
 * Returns the given [element] for [amount]-times in a new [List]
 *
 * @param element: The element that should be duplicated in the [List]
 * @param amount: The amount of how many times the [element] should be included in the returned
 * [List]
 * @return A [List] filled with [amount]-times the given [element]
 */
fun <T> getNTimes(element: T, amount: Int): List<T> {
    return List(amount) { element }
}