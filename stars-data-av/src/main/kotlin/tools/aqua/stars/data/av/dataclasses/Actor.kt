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

package tools.aqua.stars.data.av.dataclasses

import tools.aqua.stars.core.types.EntityType

/**
 * Abstract actor data class.
 *
 * @property id The ID of the [Actor].
 * @property typeId String representation of the [Actor] type. Default: empty [String].
 * @property attributes Attributes map. Default: empty [Map].
 * @property isAlive Whether the [Actor] is alive. Default: true.
 * @property isActive Whether the [Actor] is active. Default: true.
 * @property isDormant Whether the [Actor] is dormant. Default: false.
 * @property semanticTags [List] of semantic tags. Default: empty [List].
 * @property boundingBox The 3D [BoundingBox] of the [Actor]. Default: unit [BoundingBox].
 * @property location The [Location] of the [Actor]. Default: origin.
 * @property rotation The [Rotation] of the [Actor]. Default: no rotation.
 * @property collisions The [List] of all colliding [Actor] IDs. Default: empty [List].
 */
sealed class Actor(
    val id: Int,
    val typeId: String = "",
    val attributes: Map<String, String> = emptyMap(),
    val isAlive: Boolean = true,
    val isActive: Boolean = true,
    val isDormant: Boolean = false,
    val semanticTags: List<Int> = emptyList(),
    val boundingBox: BoundingBox = BoundingBox(),
    val location: Location = Location(),
    val rotation: Rotation = Rotation(),
    val collisions: List<Int> = emptyList(),
) : EntityType<Actor, TickData, TickDataUnitSeconds, TickDataDifferenceSeconds>() {
  /**
   * Clones the actor.
   *
   * @param newTickData New [TickData] to copy to new object.
   */
  abstract fun clone(newTickData: TickData): Actor

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Actor) return false
    return id == other.id
  }

  override fun hashCode(): Int = id
}
