/*
 * Copyright 2023-2024 The STARS Project Authors
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
    override val tickData: TickData,
    val positionOnLane: Double,
    val lane: Lane,
) : Actor() {

  override fun clone(newTickData: TickData): Actor =
      Pedestrian(id, newTickData, positionOnLane, lane)

  override fun toString(): String =
      "Pedestrian(id=$id, tickData=${tickData}, positionOnLane=$positionOnLane, lane=${lane.laneId}, road=${lane.road.id})"

  override fun equals(other: Any?): Boolean = super.equals(other)

  override fun hashCode(): Int {
    var result = id
    result = 31 * result + tickData.hashCode()
    result = 31 * result + positionOnLane.hashCode()
    result = 31 * result + lane.hashCode()
    return result
  }
}
