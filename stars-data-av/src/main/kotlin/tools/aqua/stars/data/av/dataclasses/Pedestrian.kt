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

/**
 * Data class for pedestrians.
 *
 * @param id The identifier of the pedestrian.
 */
class Pedestrian(
    id: Int = 0,
    override val typeId: String = "",
    override val attributes: Map<String, String> = emptyMap(),
    override val isAlive: Boolean = true,
    override val isActive: Boolean = true,
    override val isDormant: Boolean = false,
    override val semanticTags: List<Int> = emptyList(),
    override val boundingBox: BoundingBox = BoundingBox(),
    override val location: Location = Location(),
    override val rotation: Rotation = Rotation(),
    override val collisions: List<Int> = emptyList(),
) : Actor(id) {

  override fun clone(newTickData: TickData): Actor =
      Pedestrian(
          id = id,
          typeId = typeId,
          attributes = attributes,
          isAlive = isAlive,
          isActive = isActive,
          isDormant = isDormant,
          semanticTags = semanticTags,
          boundingBox = boundingBox,
          location = location,
          rotation = rotation,
          collisions = collisions,
      )
}
