/*
 * Copyright 2023-2026 The STARS Project Authors
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
 * @param id The identifier of the [Pedestrian].
 * @param typeId The type identifier of the [Pedestrian].
 * @param attributes The attributes of the [Pedestrian].
 * @param isAlive Whether the [Pedestrian] is alive.
 * @param isActive Whether the [Pedestrian] is active.
 * @param isDormant Whether the [Pedestrian] is dormant.
 * @param semanticTags The semantic tags of the [Pedestrian].
 * @param boundingBox The [BoundingBox] of the [Pedestrian].
 * @param location The [Location] of the [Pedestrian].
 * @param rotation The [Rotation] of the [Pedestrian].
 * @param collisions The [List] of all colliding [Actor] IDs. Default: empty [List].
 */
class Pedestrian(
    id: Int = 0,
    typeId: String = "",
    attributes: Map<String, String> = emptyMap(),
    isAlive: Boolean = true,
    isActive: Boolean = true,
    isDormant: Boolean = false,
    semanticTags: List<Int> = emptyList(),
    boundingBox: BoundingBox = BoundingBox(),
    location: Location = Location(),
    rotation: Rotation = Rotation(),
    collisions: List<Int> = emptyList(),
    lane: Lane = Lane(),
    positionOnLane: Double = 0.0,
) :
    Actor(
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
        lane = lane,
        positionOnLane = positionOnLane,
    ) {

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
          lane = lane,
          positionOnLane = positionOnLane,
      )
}
