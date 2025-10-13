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

package tools.aqua.stars.importer.carla.dataclasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Json object for landmark types.
 *
 * @property value Json value.
 */
@Serializable
enum class JsonLandmarkType(val value: Int) {
  @SerialName("101") Danger(101),
  @SerialName("121") LanesMerging(121),
  @SerialName("0") CautionPedestrian(133),
  @SerialName("138") CautionBicycle(138),
  @SerialName("150") LevelCrossing(150),
  @SerialName("206") StopSign(206),
  @SerialName("205") YieldSign(205),
  @SerialName("209") MandatoryTurnDirection(209),
  @SerialName("211") MandatoryLeftRightDirection(211),
  @SerialName("214") TwoChoiceTurnDirection(214),
  @SerialName("215") Roundabout(215),
  @SerialName("222") PassRightLeft(222),
  @SerialName("250") AccessForbidden(250),
  @SerialName("251") AccessForbiddenMotorvehicles(251),
  @SerialName("253") AccessForbiddenTrucks(253),
  @SerialName("254") AccessForbiddenBicycle(254),
  @SerialName("263") AccessForbiddenWeight(263),
  @SerialName("264") AccessForbiddenWidth(264),
  @SerialName("265") AccessForbiddenHeight(265),
  @SerialName("267") AccessForbiddenWrongDirection(267),
  @SerialName("272") ForbiddenUTurn(272),
  @SerialName("274") MaximumSpeed(274),
  @SerialName("276") ForbiddenOvertakingMotorvehicles(276),
  @SerialName("277") ForbiddenOvertakingTrucks(277),
  @SerialName("283") AbsoluteNoStop(283),
  @SerialName("286") RestrictedStop(286),
  @SerialName("301") HasWayNextIntersection(301),
  @SerialName("306") PriorityWay(306),
  @SerialName("307") PriorityWayEnd(307),
  @SerialName("310") CityBegin(310),
  @SerialName("311") CityEnd(311),
  @SerialName("330") Highway(330),
  @SerialName("357") DeadEnd(357),
  @SerialName("380") RecommendedSpeed(380),
  @SerialName("381") RecommendedSpeedEnd(381),
  @SerialName("1000001") LightPost(1_000_001),
}
