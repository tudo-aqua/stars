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
 * Landmark types recognized by the simulator.
 *
 * The integer IDs mirror the values used by the **CARLA Python API**.
 *
 * @property value CARLA's integer identifier for this landmark type (as serialized in JSON).
 */
enum class LandmarkType(val value: Int) {
  /** Generic danger warning. */
  Danger(101),
  /** Lanes merging ahead. */
  LanesMerging(121),
  /** Caution: pedestrian crossing or pedestrian area. */
  CautionPedestrian(133),
  /** Caution: bicycles/cyclists. */
  CautionBicycle(138),
  /** Railroad level crossing. */
  LevelCrossing(150),
  /** Stop sign. */
  StopSign(206),
  /** Yield / give way. */
  YieldSign(205),
  /** Mandatory turn in a specified direction. */
  MandatoryTurnDirection(209),
  /** Mandatory left or right. */
  MandatoryLeftRightDirection(211),
  /** Choose one of two turn directions. */
  TwoChoiceTurnDirection(214),
  /** Roundabout ahead. */
  Roundabout(215),
  /** Passing on right or left permitted as indicated. */
  PassRightLeft(222),
  /** Access forbidden (no entry) for all vehicles. */
  AccessForbidden(250),
  /** Access forbidden for motor vehicles. */
  AccessForbiddenMotorvehicles(251),
  /** Access forbidden for trucks. */
  AccessForbiddenTrucks(253),
  /** Access forbidden for bicycles. */
  AccessForbiddenBicycle(254),
  /** Weight limit. */
  AccessForbiddenWeight(263),
  /** Width limit. */
  AccessForbiddenWidth(264),
  /** Height limit. */
  AccessForbiddenHeight(265),
  /** Wrong-way / entry prohibited in this direction. */
  AccessForbiddenWrongDirection(267),
  /** U-turn prohibited. */
  ForbiddenUTurn(272),
  /** Maximum speed. */
  MaximumSpeed(274),
  /** Overtaking prohibited for motor vehicles. */
  ForbiddenOvertakingMotorvehicles(276),
  /** Overtaking prohibited for trucks. */
  ForbiddenOvertakingTrucks(277),
  /** No stopping (absolute). */
  AbsoluteNoStop(283),
  /** Restricted stopping/standing. */
  RestrictedStop(286),
  /** Priority over oncoming traffic at the next intersection. */
  HasWayNextIntersection(301),
  /** Start of priority road. */
  PriorityWay(306),
  /** End of priority road. */
  PriorityWayEnd(307),
  /** City limits begin. */
  CityBegin(310),
  /** City limits end. */
  CityEnd(311),
  /** Highway / highway begins. */
  Highway(330),
  /** Dead end / no through road. */
  DeadEnd(357),
  /** Recommended speed begins. */
  RecommendedSpeed(380),
  /** End of recommended speed. */
  RecommendedSpeedEnd(381),
  /** Utility: generic light post (non-traffic sign landmark). */
  LightPost(1_000_001);

  /** Companion object for the [LandmarkType] class. */
  companion object {
    /**
     * Returns the [LandmarkType] that matches the given CARLA integer ID.
     *
     * @param value CARLA's integer identifier (as found in JSON).
     * @return The matching [LandmarkType].
     * @throws NoSuchElementException if no matching value exists.
     */
    fun getByValue(value: Int): LandmarkType = entries.first { it.value == value }
  }
}
