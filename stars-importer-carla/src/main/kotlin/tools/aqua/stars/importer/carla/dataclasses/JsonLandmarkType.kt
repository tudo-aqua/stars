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
 * JSON enum for landmark types (traffic signs and selected roadside objects).
 *
 * Integer IDs are aligned with the CARLA Python API.
 *
 * @property value Numeric code from CARLA's JSON. Also provided via [SerialName] for stable I/O.
 */
@Serializable
enum class JsonLandmarkType(val value: Int) {
  /** Generic danger/warning. */
  @SerialName("101") Danger(101),
  /** Lanes merging ahead. */
  @SerialName("121") LanesMerging(121),
  /** Caution: pedestrian crossing/area. */
  @SerialName("0") CautionPedestrian(133),
  /** Caution: bicycles/cyclists. */
  @SerialName("138") CautionBicycle(138),
  /** Railroad level crossing. */
  @SerialName("150") LevelCrossing(150),
  /** Stop sign. */
  @SerialName("206") StopSign(206),
  /** Yield / give way. */
  @SerialName("205") YieldSign(205),
  /** Mandatory turn in a specified direction. */
  @SerialName("209") MandatoryTurnDirection(209),
  /** Mandatory left or right. */
  @SerialName("211") MandatoryLeftRightDirection(211),
  /** Choose one of two turn directions. */
  @SerialName("214") TwoChoiceTurnDirection(214),
  /** Roundabout ahead. */
  @SerialName("215") Roundabout(215),
  /** Passing on right or left permitted as indicated. */
  @SerialName("222") PassRightLeft(222),
  /** Access forbidden (no entry) for all vehicles. */
  @SerialName("250") AccessForbidden(250),
  /** Access forbidden for motor vehicles. */
  @SerialName("251") AccessForbiddenMotorvehicles(251),
  /** Access forbidden for trucks. */
  @SerialName("253") AccessForbiddenTrucks(253),
  /** Access forbidden for bicycles. */
  @SerialName("254") AccessForbiddenBicycle(254),
  /** Weight limit. */
  @SerialName("263") AccessForbiddenWeight(263),
  /** Width limit. */
  @SerialName("264") AccessForbiddenWidth(264),
  /** Height limit. */
  @SerialName("265") AccessForbiddenHeight(265),
  /** Wrong-way / entry prohibited in this direction. */
  @SerialName("267") AccessForbiddenWrongDirection(267),
  /** U-turn prohibited. */
  @SerialName("272") ForbiddenUTurn(272),
  /** Maximum speed. */
  @SerialName("274") MaximumSpeed(274),
  /** Overtaking prohibited for motor vehicles. */
  @SerialName("276") ForbiddenOvertakingMotorvehicles(276),
  /** Overtaking prohibited for trucks. */
  @SerialName("277") ForbiddenOvertakingTrucks(277),
  /** No stopping (absolute). */
  @SerialName("283") AbsoluteNoStop(283),
  /** Restricted stopping/standing. */
  @SerialName("286") RestrictedStop(286),
  /** Priority over oncoming traffic at next intersection. */
  @SerialName("301") HasWayNextIntersection(301),
  /** Start of priority road. */
  @SerialName("306") PriorityWay(306),
  /** End of priority road. */
  @SerialName("307") PriorityWayEnd(307),
  /** City limits begin. */
  @SerialName("310") CityBegin(310),
  /** City limits end. */
  @SerialName("311") CityEnd(311),
  /** Highway / highway begins. */
  @SerialName("330") Highway(330),
  /** Dead end / no through road. */
  @SerialName("357") DeadEnd(357),
  /** Recommended speed begins. */
  @SerialName("380") RecommendedSpeed(380),
  /** End of recommended speed. */
  @SerialName("381") RecommendedSpeedEnd(381),
  /** Generic light post (non-traffic sign landmark). */
  @SerialName("1000001") LightPost(1_000_001),
}
