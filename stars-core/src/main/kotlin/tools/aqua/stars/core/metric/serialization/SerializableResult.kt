/*
 * Copyright 2024 The STARS Project Authors
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

package tools.aqua.stars.core.metric.serialization

import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.readText
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tools.aqua.stars.core.metric.utils.ApplicationConstantsHolder.jsonConfiguration

@Serializable
sealed class SerializableResult {
  abstract val identifier: String?
  abstract val source: String
  abstract val value: Any

  abstract override fun equals(other: Any?): Boolean

  abstract override fun hashCode(): Int

  override fun toString(): String = getJsonString()

  fun getJsonString(): String = jsonConfiguration.encodeToString(this)

  companion object {
    fun getJsonContentOfPath(file: Path): SerializableResult {

      // Check if inputFilePath exists
      check(file.exists()) { "The given file path does not exist: ${file.toUri()}" }

      // Check whether the given inputFilePath is a directory
      check(!file.isDirectory()) { "Cannot get InputStream for directory. Path: $file" }

      // If ".json"-file: Just return InputStream of file
      if (file.extension == "json") {
        return getJsonContentFromString(file.readText())
      }

      // If none of the supported file extensions is present, throw an Exception
      error("Unexpected file extension: ${file.extension}. Supported extensions: '.json'")
    }

    fun getJsonContentFromString(content: String): SerializableResult =
        Json.decodeFromString<SerializableResult>(content)
  }
}
