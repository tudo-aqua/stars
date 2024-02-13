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
 * Converts the given [nameToValuesMap] to a CSV [String] and saves this as a file.
 *
 * @param T [Number].
 * @param nameToValuesMap The [Map] that contains the column values and its related header entry.
 * @param fileName The name of the saved file.
 * @param folder The name of the top-level folder.
 * @param sliceValue (Default: null) If set the values of each key in the [nameToValuesMap] will be
 * sliced by the given [sliceValue].
 * @param subFolder (Default: "") The name of the subfolder under [folder] in which the file will be
 * saved.
 */
fun <T : Number> saveAsCSVFile(
    nameToValuesMap: Map<String, List<T>>,
    fileName: String,
    folder: String,
    sliceValue: Int? = null,
    subFolder: String = "",
) {
  val resultFolder = getAndCreateCSVFolder(folder, subFolder)
  val sliceString = if (sliceValue != null) "_slice_$sliceValue" else ""
  File("$resultFolder/$fileName$sliceString.csv").apply {
    createNewFile()
    writeText(getCSVString(nameToValuesMap = nameToValuesMap, sliceValue = sliceValue))
  }
}

/**
 * Create and return a CSV string based on the given [nameToValuesMap]. Each key in the [Map] will
 * result in a new column with the value assigned to the key as its row values.
 *
 * @param T [Number].
 * @param nameToValuesMap The [Map] that contains the column values and its related header entry.
 * @param sliceValue (Default: null) If set the values of each key in the [nameToValuesMap] will be
 * sliced by the given [sliceValue].
 * @return The CSV [String] based on the [nameToValuesMap].
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
 * Returns the path to the folder in which the CSV files will be saved in. If the path does not
 * exist it will be created.
 *
 * @param folder The name of the top-level folder.
 * @param subFolder (Default: "") The name of the subfolder under [folder] in which the file will be
 * saved.
 * @return The path to the created folder.
 */
private fun getAndCreateCSVFolder(folder: String, subFolder: String): String =
    "analysis-result-logs/${ApplicationConstantsHolder.applicationStartTimeString}/csv/$folder/$subFolder".also {
      File(it).mkdirs()
    }
