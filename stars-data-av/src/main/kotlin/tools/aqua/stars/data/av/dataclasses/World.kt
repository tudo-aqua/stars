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
 * Data class for the world (i.e., the map).
 *
 * @property straights All straight roads in the [World] that are not pat of a junction.
 * @property junctions All junctions in the [World], which contain multiple roads.
 * @property crosswalks All crosswalks in the [World].
 */
data class World(
    val straights: List<Road> = emptyList(),
    val junctions: List<Junction> = emptyList(),
    val crosswalks: List<Crosswalk> = emptyList(),
) {

  init {
    check(getAllRoads().isNotEmpty()) { "World must have at least one road" }
  }

  /**
   * Returns a list of all lanes in the [World].
   *
   * @return [List] of all lanes in the [World].
   */
  fun getAllLanes(): List<Lane> = getAllRoads().flatMap { it.lanes }

  /**
   * Returns a list of all roads in the [World].
   *
   * @return [List] of all roads in the [World].
   */
  fun getAllRoads(): List<Road> = straights + junctions.flatMap { it.roads }
}
