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

import tools.aqua.stars.core.types.SegmentType

/**
 * Evaluation segment.
 *
 * @property mainInitList [TickData] of the [Segment].
 * @property simulationRunId Identifier of the simulation run.
 * @property segmentSource Source identifier.
 */
data class Segment(
    val mainInitList: List<TickData>,
    val simulationRunId: String = "",
    override val segmentSource: String,
) : SegmentType<Actor, TickData, Segment> {

  override val tickData: List<TickData> = mainInitList.onEach { it.segment = this }

  override val ticks: Map<Double, TickData> = tickData.associateBy { it.currentTick }

  override val tickIDs: List<Double> = tickData.map { it.currentTick }

  override val firstTickId: Double = this.tickIDs.first()

  override val primaryEntityId: Int
    get() {
      val firstEgo = tickData.first().egoVehicle
      if (tickData.any { it.egoVehicle.id != firstEgo.id })
          error("inconsistent ego ids in segment $this")
      return firstEgo.id
    }

  /** Cache for all vehicle IDs. */
  private val vehicleIdsCache = mutableListOf<Int>()

  /** All vehicle IDs of the segment. */
  val vehicleIds: List<Int>
    get() {
      if (vehicleIdsCache.isEmpty()) {
        vehicleIdsCache.addAll(
            tickData
                .flatMap { tickData -> tickData.entities.filterIsInstance<Vehicle>().map { it.id } }
                .distinct())
      }
      return vehicleIdsCache
    }

  /** Cache for all pedestrian IDs. */
  private val pedestrianIdsCache = mutableListOf<Int>()

  /** All pedestrian IDs of the segment. */
  @Suppress("unused")
  val pedestrianIds: List<Int>
    get() {
      if (pedestrianIdsCache.isEmpty()) {
        pedestrianIdsCache.addAll(
            tickData
                .flatMap { tickData ->
                  tickData.entities.filterIsInstance<Pedestrian>().map { it.id }
                }
                .distinct())
      }
      return pedestrianIdsCache
    }

  override fun toString(): String =
      "Segment[(${tickData.first().currentTick}..${tickData.last().currentTick})] from $simulationRunId " +
          "with ego ${this.primaryEntityId}"

  override fun equals(other: Any?): Boolean {
    if (other is Segment) {
      return other.toString() == this.toString()
    }
    return super.equals(other)
  }

  override fun hashCode(): Int = this.toString().hashCode()
}
