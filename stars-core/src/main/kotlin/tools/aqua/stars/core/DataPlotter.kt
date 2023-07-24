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

package tools.aqua.stars.core

import java.io.File
import jetbrains.letsPlot.Stat
import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomBar
import jetbrains.letsPlot.geom.geomLine
import jetbrains.letsPlot.ggsize
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.positionDodge
import jetbrains.letsPlot.sampling.samplingNone
import jetbrains.letsPlot.scale.scaleXContinuous
import jetbrains.letsPlot.scale.scaleYContinuous

private val POSITION_DODGE = 0.3
private val CSV_DELIMITER = ";"

fun plotUniqueTSCInstances(
    nameList: List<String>,
    dataLists: List<List<Int>>,
    plotFolder: String,
    dataFolder: String
) {
  if (dataLists.isEmpty()) {
    return
  }
  check(nameList.size == dataLists.size)
  val indices: List<Int> = List(dataLists[0].size) { it }

  val xValues = List(dataLists.size) { indices }.flatten()
  val yValues = dataLists.flatMap { it }

  nameList.forEachIndexed { index, name ->
    writeCSVFilesFromDataList(
        dataList = dataLists[index],
        plotName = "uniqueTSCInstances",
        projectionName = name,
        dataFolder = dataFolder)
  }

  writeCSVFilesFromDataLists(
      dataList = dataLists,
      nameList = nameList,
      plotName = "uniqueTSCInstances",
      dataFolder = dataFolder)

  val data =
      mapOf(
          "segment index" to xValues,
          "unique instances" to yValues,
          "projection" to
              List(dataLists.size) { index -> List(indices.size) { nameList[index] } }.flatten())

  var plot =
      letsPlot(data) {
        x = "segment index"
        y = "unique instances"
        color = "projection"
        fill = "projection"
      }
  plot += ggsize(500, 250)

  // Default histogram (stacked)
  // p1 += geomLine(stat = Stat.identity)
  plot +=
      geomLine(stat = Stat.identity, position = positionDodge(POSITION_DODGE)) +
          scaleXContinuous(limits = -0.001 to xValues.max(), expand = listOf(0, 0)) +
          scaleYContinuous(limits = -0.001 to yValues.max(), expand = listOf(0, 0))
  // p1 += geomDensity(stat = Stat.identity, alpha = .3) { fill = "label" }
  ggsave(plot, "uniqueTSCInstances.png", path = plotFolder)
}

fun plotUniqueTSCInstancePercentages(
    nameList: List<String>,
    dataLists: List<List<Int>>,
    plotFolder: String,
    dataFolder: String
) {
  if (dataLists.isEmpty()) {
    return
  }
  check(nameList.size == dataLists.size)
  val indices: List<Int> = List(dataLists[0].size) { it }
  val xValues = List(dataLists.size) { indices }.flatten()
  val yValues = dataLists.flatMap { it }

  nameList.forEachIndexed { index, name ->
    writeCSVFilesFromDataList(
        dataList = dataLists[index],
        plotName = "tscInstanceOccurrencesPercentages",
        projectionName = name,
        dataFolder = dataFolder)
  }

  writeCSVFilesFromDataLists(
      dataList = dataLists,
      nameList = nameList,
      plotName = "tscInstanceOccurrencesPercentages",
      dataFolder = dataFolder)

  val data =
      mapOf(
          "segment index" to xValues,
          "unique instances (in %)" to yValues,
          "projection" to
              List(dataLists.size) { index -> List(indices.size) { nameList[index] } }.flatten())

  var plot =
      letsPlot(data) {
        x = "segment index"
        y = "unique instances (in %)"
        color = "projection"
        fill = "projection"
      }
  plot += ggsize(500, 250)

  // Default histogram (stacked)
  // p1 += geomLine(stat = Stat.identity)
  plot +=
      geomLine(stat = Stat.identity, position = positionDodge(POSITION_DODGE)) +
          scaleXContinuous(limits = -0.001 to xValues.max(), expand = listOf(0, 0)) +
          scaleYContinuous(limits = -0.001 to yValues.max(), expand = listOf(0, 0))
  // p1 += geomDensity(stat = Stat.identity, alpha = .3) { fill = "label" }
  ggsave(plot, "uniqueTSCInstancePercentages.png", path = plotFolder)
}

fun plotTSCInstanceOccurrencesForProjection(
    projectionName: String,
    dataList: List<Int>,
    plotFolder: String,
    dataFolder: String
) {
  if (dataList.isEmpty()) {
    return
  }
  writeCSVFilesFromDataList(
      dataList = dataList,
      plotName = "tscInstanceOccurrences",
      projectionName = projectionName,
      dataFolder = dataFolder)

  val indices: List<Int> = List(dataList.size) { it }

  val data =
      mapOf(
          "instance index" to indices,
          "instance count" to dataList,
          "projection" to List(dataList.size) { projectionName })

  var plot =
      letsPlot(data) {
        x = "instance index"
        y = "instance count"
        color = "projection"
        fill = "projection"
      }
  plot += ggsize(500, 250)

  // Default histogram (stacked)
  plot += geomBar(stat = Stat.identity, position = positionDodge(), sampling = samplingNone)
  ggsave(plot, "tscInstanceOccurrences_$projectionName.png", path = plotFolder)
}

fun writeCSVFilesFromDataList(
    dataList: List<Int>,
    plotName: String,
    projectionName: String,
    dataFolder: String
) {
  val file = File("$dataFolder/${plotName}-${projectionName.replace("/", "-")}.csv")
  file.createNewFile()

  val fileOnlyEvery200th =
      File("$dataFolder/${plotName}-${projectionName.replace("/", "-")}-200th.csv")
  fileOnlyEvery200th.createNewFile()

  var fileContent = "index$CSV_DELIMITER$projectionName\n"
  var file200thContent = "index$CSV_DELIMITER$projectionName\n"
  dataList.forEachIndexed { index, value ->
    fileContent += "$index$CSV_DELIMITER$value\n"
    if (index % 200 == 0) {
      file200thContent += "$index$CSV_DELIMITER$value\n"
    }
  }

  file.writeText(fileContent)
  fileOnlyEvery200th.writeText(file200thContent)
}

fun writeCSVFilesFromDataLists(
    dataList: List<List<Int>>,
    nameList: List<String>,
    plotName: String,
    dataFolder: String
) {
  val file = File("$dataFolder/${plotName}.csv")
  file.createNewFile()

  val fileOnlyEvery200th = File("$dataFolder/${plotName}-200th.csv")
  fileOnlyEvery200th.createNewFile()

  var fileContent = ""
  var file200thContent = ""

  fileContent += "index$CSV_DELIMITER${nameList.joinToString(CSV_DELIMITER)}\n"
  file200thContent += "index$CSV_DELIMITER${nameList.joinToString(CSV_DELIMITER)}\n"
  dataList[0].forEach { index ->
    var line = "$index"
    dataList.forEach { line += "$CSV_DELIMITER${it[index]}" }
    line += "\n"
    fileContent += line
    if (index % 200 == 0) {
      file200thContent += line
    }
  }

  file.writeText(fileContent)
  fileOnlyEvery200th.writeText(file200thContent)
}
