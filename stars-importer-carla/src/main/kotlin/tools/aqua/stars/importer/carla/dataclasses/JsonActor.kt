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

/** Abstract JSON actor. */
@Serializable
@SerialName("Actor")
sealed class JsonActor {
  /** Identifier of the actor. */
  @SerialName("id") abstract val id: Int

  /** Current [JsonLocation]. */
  @SerialName("location") abstract val location: JsonLocation

  /** Current [JsonRotation]. */
  @SerialName("rotation") abstract val rotation: JsonRotation

  /** All additional attributes from CARLA. */
  @SerialName("attributes") abstract val attributes: Map<String, String>

  /** Whether the [JsonActor] is alive in the simulation. */
  @SerialName("is_alive") abstract val isAlive: Boolean

  /** Whether the [JsonActor] is active in the simulation. */
  @SerialName("is_active") abstract val isActive: Boolean

  /** Whether the [JsonActor] is dormant in the simulation. */
  @SerialName("is_dormant") abstract val isDormant: Boolean

  /** All semantic tags from CARLA. */
  @SerialName("semantic_tags") abstract val semanticTags: List<Int>

  /** The [JsonBoundingBox] of the [JsonActor]. */
  @SerialName("bounding_box") abstract val boundingBox: JsonBoundingBox?

  /** The [List] of all collision events ([List] of ActorIDs). */
  @SerialName("collisions") abstract val collisions: List<Int>
}
