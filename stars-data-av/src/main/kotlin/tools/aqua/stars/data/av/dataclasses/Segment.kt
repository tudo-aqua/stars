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

import tools.aqua.stars.core.types.SegmentType

/**
 * Data class for segments.
 *
 * @property tickData [TickData] of the [Segment].
 * @property segmentSource Source identifier.
 * @property simulationRunId Identifier of the simulation run.
 */
data class Segment(
    override val tickData: List<TickData>,
    override val segmentSource: String,
    val simulationRunId: String = "",
) : SegmentType<Actor, TickData, Segment, TickDataUnitSeconds, TickDataDifferenceSeconds> {

  init {
    tickData.onEach { it.segment = this }

    check(tickData.map { it.ego.id }.distinct().count() == 1) { "The ego changes in Segment" }
  }

  override val ticks: Map<TickDataUnitSeconds, TickData> = tickData.associateBy { it.currentTick }

  override val primaryEntityId: Int = tickData.first().ego.id

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

  /**
   * Converts segment to String representation including [tickData] range and given [egoId].
   *
   * @param egoId Identifier of the ego vehicle to be included.
   */
  fun toString(egoId: Int): String =
      "Segment[(${tickData.first().currentTick}..${tickData.last().currentTick})] from $simulationRunId " +
          "with ego $egoId"

  override fun toString(): String = toString(this.primaryEntityId)

  override fun equals(other: Any?): Boolean {
    if (other is Segment) {
      return simulationRunId == other.simulationRunId &&
          segmentSource == other.segmentSource &&
          primaryEntityId == other.primaryEntityId &&
          tickData == other.tickData
    }
    return super.equals(other)
  }

  override fun hashCode(): Int = this.toString().hashCode()
}
