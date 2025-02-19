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
 * @property id The identifier of the pedestrian.
 * @property tickData [TickData].
 * @property positionOnLane The [Pedestrian]'s position on the [Lane].
 * @property lane The [Pedestrian]'s [Lane].
 */
data class Pedestrian(
    override val id: Int,
    val positionOnLane: Double,
    val lane: Lane,
) : Actor() {

  override lateinit var tickData: TickData

  override fun clone(newTickData: TickData): Actor =
      Pedestrian(id, positionOnLane, lane).apply { tickData = newTickData }

  override fun toString(): String =
      "Pedestrian(id=$id, tickData=${tickData}, positionOnLane=$positionOnLane, lane=${lane.laneId}, road=${lane.road.id})"

  override fun equals(other: Any?): Boolean =
      other is Pedestrian && id == other.id && tickData.currentTick == other.tickData.currentTick

  override fun hashCode(): Int = 31 * id + tickData.currentTick.hashCode()
}
