/*
 * Copyright 2023-2025 The STARS Project Authors
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

package tools.aqua.stars.importer.carla.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Abstract Json actor. */
@Serializable
@SerialName("Actor")
sealed class JsonActor {
  /** Identifier of the actor. */
  abstract val id: Int

  abstract val typeId: String // TODO: Documentation needed
  abstract val attributes: Map<String, String>
  abstract val isAlive: Boolean
  abstract val isActive: Boolean
  abstract val isDormant: Boolean
  abstract val semanticTags: List<Int>
  abstract val boundingBox: JsonBoundingBox

  /** Current [JsonLocation]. */
  abstract val location: JsonLocation

  /** Current [JsonRotation]. */
  abstract val rotation: JsonRotation
}
