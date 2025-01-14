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

/** Returns [Lane] in list of [Block]s with given [roadId] and [laneId]. */
fun List<Block>.getLane(roadId: Int, laneId: Int): Lane? =
    this.flatMap { it.roads }
        .first { pRoad -> pRoad.id == roadId }
        .lanes
        .firstOrNull { pLane -> pLane.laneId == laneId }

/** Returns all [Lane]s in list of [Block]s. */
fun List<Block>.lanes(): List<Lane> = this.flatMap { it.roads.flatMap { road -> road.lanes } }

/** Generates UID from [Lane]. */
val Lane.uid: String
  get() = "${road.id}_${laneId}"

/** Returns [Lane] of [Actor] iff [Actor] is [Vehicle] or [Pedestrian]. */
val Actor.lane: Lane
  get() =
      when (this) {
        is Pedestrian -> lane
        is Vehicle -> lane
      }
