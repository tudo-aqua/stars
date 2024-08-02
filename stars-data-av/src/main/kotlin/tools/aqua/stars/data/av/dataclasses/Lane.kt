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
 * Data class for lanes.
 *
 * @property laneId The identifier of the [Lane].
 * @property road The identifier of the [Road].
 * @property laneType The [LaneType] of the [Lane].
 * @property laneWidth The width of the [Lane].
 * @property laneLength The length of the [Lane].
 * @property predecessorLanes List of [ContactLaneInfo]s for preceding [Lane]s.
 * @property successorLanes List of [ContactLaneInfo]s for successive [Lane]s.
 * @property intersectingLanes List of [ContactLaneInfo]s for intersecting [Lane]s.
 * @property yieldLanes List of [Lane]s that this [Lane] yields to. Lanes that this lane must yield
 *   to according to the "right before left" rule, i.e., traffic signs or lights are not taken into
 *   account. it is always a subset of [intersectingLanes]
 * @property laneMidpoints List of [LaneMidpoint]s for [Lane] midpoints.
 * @property speedLimits List of [SpeedLimit]s for the speed limits.
 * @property landmarks List of [Landmark]s on list [Lane].
 * @property contactAreas List of [ContactArea]s on this [Lane].
 * @property trafficLights List of [StaticTrafficLight]s on this [Lane].
 * @property laneDirection The [LaneDirection] of this [Lane].
 */
@Suppress("MemberVisibilityCanBePrivate")
data class Lane(
    val laneId: Int,
    var road: Road,
    val laneType: LaneType,
    val laneWidth: Double,
    val laneLength: Double,
    var predecessorLanes: List<ContactLaneInfo>,
    var successorLanes: List<ContactLaneInfo>,
    var intersectingLanes: List<ContactLaneInfo>,
    var yieldLanes: List<ContactLaneInfo>,
    val laneMidpoints: List<LaneMidpoint>,
    var speedLimits: List<SpeedLimit>,
    val landmarks: List<Landmark>,
    var contactAreas: List<ContactArea>,
    var trafficLights: List<StaticTrafficLight>,
    var laneDirection: LaneDirection,
) {

  /** Whether this [Lane] turns left. */
  val isTurningLeft: Boolean
    get() = laneDirection == LaneDirection.LEFT_TURN

  /** Whether this [Lane] turns right. */
  val isTurningRight: Boolean
    get() = laneDirection == LaneDirection.RIGHT_TURN

  /** Whether this [Lane] is straight. */
  val isStraight: Boolean
    get() = laneDirection == LaneDirection.STRAIGHT

  /** Whether this [Lane] has stop signs. */
  val hasStopSign: Boolean
    get() =
        landmarks.any { it.type == LandmarkType.StopSign && it.s > laneLength - 10.0 } ||
            successorLanes.any {
              it.lane.landmarks.any { l -> l.type == LandmarkType.StopSign && l.s < 10.0 }
            }

  /** Whether this [Lane] has yield signs. */
  val hasYieldSign: Boolean
    get() =
        landmarks.any { it.type == LandmarkType.YieldSign && it.s > laneLength - 10.0 } ||
            successorLanes.any {
              it.lane.landmarks.any { l -> l.type == LandmarkType.YieldSign && l.s < 10.0 }
            }

  /** Whether this [Lane] has stop or yield signs. */
  val hasStopOrYieldSign: Boolean
    get() = hasStopSign || hasYieldSign

  /** Whether this [Lane] has traffic lights. */
  val hasTrafficLight: Boolean
    get() = trafficLights.isNotEmpty()

  /**
   * Returns the speed limit for the current position in mph or 30mph if no speed sign is available.
   */
  fun speedAt(vPos: Double): Double =
      speedLimits
          .firstOrNull { it.fromDistanceFromStart <= vPos && vPos < it.toDistanceFromStart }
          ?.speedLimit ?: 30.0

  /**
   * Retrieve the position (relative to this lane's start) where [otherLane] is crossed. if the
   * lanes do not cross, return -1.0
   */
  fun contactPointPos(otherLane: Lane): Double? {
    // collect all positions where this lane crosses other lane; should yield 0 or 1 results
    val positions =
        contactAreas.mapNotNull {
          // I am lane 1, check if (and where) lane 2 contacts
          if (this == it.lane1 && otherLane == it.lane2) {
            it.lane1StartPos
          }
          // I am lane 2, check if (and where) lane 1 contacts
          else if (this == it.lane2 && otherLane == it.lane1) {
            it.lane2StartPos
          } else {
            // the two lanes have no contact; do not include in mapping
            null
          }
        }
    return if (positions.isNotEmpty()) positions[0] else null
  }

  override fun toString(): String = "Lane(road=${road.id}, id=$laneId)"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Lane

    if (laneId != other.laneId) return false
    if (road != other.road) return false

    return true
  }

  override fun hashCode(): Int = this.toString().hashCode()
}
