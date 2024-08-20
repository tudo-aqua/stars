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

package tools.aqua.stars.core.metric.utils

import java.io.File

/**
 * Stores the given [csvString] and saves it as a file.
 *
 * @param csvString The [String] that should be saved into the CSV file.
 * @param fileName The name of the saved file.
 * @param folder The name of the top-level folder.
 * @param subFolder (Default: "") The name of the sub-folder under [folder] in which the file will
 *   be saved.
 */
fun saveAsCSVFile(
    csvString: String,
    fileName: String,
    folder: String,
    subFolder: String = "",
) {
  val resultFolder = getAndCreateCSVFolder(folder, subFolder)
  File("$resultFolder/$fileName.csv").apply {
    createNewFile()
    writeText(csvString)
  }
}

/**
 * Create and return a CSV string based on the given [xValues] and [yValues].
 *
 * @param T1 [Number].
 * @param T2 [Number].
 * @param columnEntry The name that is used as the column header for the values.
 * @param xValues The x-values that should be used for the y-values.
 * @param yValues The y-values that should be used.
 * @param sliceValue (Default: null) If set the values will be sliced by the given [sliceValue].
 * @return The CSV [String] based on the [xValues] and [yValues].
 */
fun <T1 : Number, T2 : Number> getCSVString(
    columnEntry: String,
    xValues: List<T1>,
    yValues: List<T2>,
    sliceValue: Int? = null
): String = getCSVString(mapOf(columnEntry to (xValues to yValues)), sliceValue)

/**
 * Create and return a CSV string based on the given [yValues]. The x-values are automatically
 * increased by 1 for each y-value.
 *
 * @param T [Number].
 * @param columnEntry The name that is used as the column header for the values.
 * @param yValues The y-values that should be used.
 * @param sliceValue (Default: null) If set the values will be sliced by the given [sliceValue].
 * @return The CSV [String] based on the [yValues].
 */
fun <T : Number> getCSVString(
    columnEntry: String,
    yValues: List<T>,
    sliceValue: Int? = null
): String = getCSVString(mapOf(columnEntry to (List(yValues.size) { it } to yValues)), sliceValue)

/**
 * Create and return a CSV string based on the given [xAndYValues].
 *
 * @param T1 [Number].
 * @param T2 [Number].
 * @param columnEntries The names that are displayed in the legend for the data sets.
 * @param xAndYValues A [List] of x- and y-value [List]s. Each [List] item corresponds to one item
 *   of [columnEntries].
 * @param sliceValue (Default: null) If set the values will be sliced by the given [sliceValue].
 * @return The CSV [String] based on the [xAndYValues].
 */
fun <T1 : Number, T2 : Number> getCSVString(
    columnEntries: List<String>,
    xAndYValues: List<Pair<List<T1>, List<T2>>>,
    sliceValue: Int? = null
): String {
  require(columnEntries.size == xAndYValues.size) {
    "The amount of given column entries should equal the size of the given value pairs."
  }
  val columnEntryToValueMap = mutableMapOf<String, Pair<List<T1>, List<T2>>>()
  columnEntries.forEachIndexed { index, legendEntry ->
    columnEntryToValueMap[legendEntry] = xAndYValues[index]
  }
  return getCSVString(columnEntryToValueMap, sliceValue)
}

/**
 * Create and return a CSV string based on the given [nameToValuesMap]. Each key in the [Map] will
 * result in a new column with the value assigned to the key as its row values.
 *
 * @param T1 [Number].
 * @param T2 [Number].
 * @param nameToValuesMap The [Map] that contains the x- and y-values in relation to their legend
 *   entry.
 * @param sliceValue (Default: null) If set the values of each key in the [nameToValuesMap] will be
 *   sliced by the given [sliceValue].
 * @return The CSV [String] based on the [nameToValuesMap].
 */
fun <T1 : Number, T2 : Number> getCSVString(
    nameToValuesMap: Map<String, Pair<List<T1>, List<T2>>>,
    sliceValue: Int? = null
): String {
  val valueMap: Map<String, Pair<List<T1>, List<T2>>>
  if (sliceValue != null) {
    valueMap = mutableMapOf()

    // Only take every "sliceValue"th entry for each key in the nameToValuesMap
    nameToValuesMap.forEach { (key, values) ->
      valueMap[key] =
          values.first.slice(0..values.first.size step (sliceValue)) to
              values.second.slice(0..values.second.size step (sliceValue))
    }
  } else {
    // Take original map
    valueMap = nameToValuesMap
  }

  // Create header line
  var csvString = "index;x;${valueMap.keys.joinToString(";")}\n"

  // Iterate over all values
  for (i in 0 until valueMap.values.first().second.size) {
    csvString += "$i;"
    csvString += "${valueMap.values.first().first[i]};"
    valueMap.keys.forEach { key -> csvString += "${valueMap[key]?.run { second[i] }};" }
    csvString += "\n"
  }

  return csvString
}

/**
 * Returns the path to the folder in which the CSV files will be saved in. If the path does not
 * exist it will be created.
 *
 * @param folder The name of the top-level folder.
 * @param subFolder (Default: "") The name of the sub-folder under [folder] in which the file will
 *   be saved.
 * @return The path to the created folder.
 */
private fun getAndCreateCSVFolder(folder: String, subFolder: String): String =
    "${ApplicationConstantsHolder.logFolder}/${ApplicationConstantsHolder.applicationStartTimeString}/csv/$folder/$subFolder"
        .also { File(it).mkdirs() }
