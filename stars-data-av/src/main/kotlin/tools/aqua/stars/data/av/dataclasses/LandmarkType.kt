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
 * Data class for landmark types.
 *
 * @property value Internal json value.
 */
enum class LandmarkType(val value: Int) {
  Danger(101),
  LanesMerging(121),
  CautionPedestrian(133),
  CautionBicycle(138),
  LevelCrossing(150),
  StopSign(206),
  YieldSign(205),
  MandatoryTurnDirection(209),
  MandatoryLeftRightDirection(211),
  TwoChoiceTurnDirection(214),
  Roundabout(215),
  PassRightLeft(222),
  AccessForbidden(250),
  AccessForbiddenMotorvehicles(251),
  AccessForbiddenTrucks(253),
  AccessForbiddenBicycle(254),
  AccessForbiddenWeight(263),
  AccessForbiddenWidth(264),
  AccessForbiddenHeight(265),
  AccessForbiddenWrongDirection(267),
  ForbiddenUTurn(272),
  MaximumSpeed(274),
  ForbiddenOvertakingMotorvehicles(276),
  ForbiddenOvertakingTrucks(277),
  AbsoluteNoStop(283),
  RestrictedStop(286),
  HasWayNextIntersection(301),
  PriorityWay(306),
  PriorityWayEnd(307),
  CityBegin(310),
  CityEnd(311),
  Highway(330),
  DeadEnd(357),
  RecommendedSpeed(380),
  RecommendedSpeedEnd(381),
  LightPost(1_000_001);

  companion object {
    /** Retrieves [LandmarkType] by internal value. */
    fun getByValue(value: Int): LandmarkType = entries.first { it.value == value }
  }
}
