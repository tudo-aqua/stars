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

package tools.aqua.stars.importer.carla.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Json object for the world.
 *
 * @property straights All straight roads in the map that are not pat of a junction.
 * @property junctions All junctions in the map, which contain multiple roads.
 * @property crosswalks All crosswalks in the map.
 */
@Serializable
data class JsonWorld(
    @SerialName("straights") val straights: List<JsonRoad>,
    @SerialName("junctions") val junctions: List<JsonJunction>,
    @SerialName("crosswalks") val crosswalks: List<JsonCrosswalk>,
) {
  /**
   * Returns a list of all lanes in the map.
   *
   * @return [List] of all lanes in the map.
   */
  fun getAllLanes(): List<JsonLane> = getAllRoads().flatMap { it.lanes }

  /**
   * Returns a list of all roads in the map.
   *
   * @return [List] of all roads in the map.
   */
  fun getAllRoads(): List<JsonRoad> = straights + junctions.flatMap { it.roads }
}
