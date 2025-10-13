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
 * Json object for traffic sign types.
 *
 * @property value Json value.
 */
@Suppress("unused")
@Serializable
enum class JsonTrafficSignType(val value: Int) {
  @SerialName("0") INVALID(0),
  @SerialName("1") SUPPLEMENT_ARROW_APPLIES_LEFT(1),
  @SerialName("2") SUPPLEMENT_ARROW_APPLIES_RIGHT(2),
  @SerialName("3") SUPPLEMENT_ARROW_APPLIES_LEFT_RIGHT(3),
  @SerialName("4") SUPPLEMENT_ARROW_APPLIES_UP_DOWN(4),
  @SerialName("5") SUPPLEMENT_ARROW_APPLIES_LEFT_RIGHT_BICYCLE(5),
  @SerialName("6") SUPPLEMENT_ARROW_APPLIES_UP_DOWN_BICYCLE(6),
  @SerialName("7") SUPPLEMENT_APPLIES_NEXT_N_KM_TIME(7),
  @SerialName("8") SUPPLEMENT_ENDS(8),
  @SerialName("9") SUPPLEMENT_RESIDENTS_ALLOWED(9),
  @SerialName("10") SUPPLEMENT_BICYCLE_ALLOWED(10),
  @SerialName("11") SUPPLEMENT_MOPED_ALLOWED(11),
  @SerialName("12") SUPPLEMENT_TRAM_ALLOWED(12),
  @SerialName("13") SUPPLEMENT_FORESTAL_ALLOWED(13),
  @SerialName("14") SUPPLEMENT_CONSTRUCTION_VEHICLE_ALLOWED(14),
  @SerialName("15") SUPPLEMENT_ENVIRONMENT_ZONE_YELLOW_GREEN(15),
  @SerialName("16") SUPPLEMENT_RAILWAY_ONLY(16),
  @SerialName("17") SUPPLEMENT_APPLIES_FOR_WEIGHT(17),
  @SerialName("18") DANGER(18),
  @SerialName("19") LANES_MERGING(19),
  @SerialName("20") CAUTION_PEDESTRIAN(20),
  @SerialName("21") CAUTION_CHILDREN(21),
  @SerialName("22") CAUTION_BICYCLE(22),
  @SerialName("23") CAUTION_ANIMALS(23),
  @SerialName("24") CAUTION_RAIL_CROSSING_WITH_BARRIER(24),
  @SerialName("25") CAUTION_RAIL_CROSSING(25),
  @SerialName("26") YIELD_TRAIN(26),
  @SerialName("27") YIELD(27),
  @SerialName("28") STOP(28),
  @SerialName("29") REQUIRED_RIGHT_TURN(29),
  @SerialName("30") REQUIRED_LEFT_TURN(30),
  @SerialName("31") REQUIRED_STRAIGHT(31),
  @SerialName("32") REQUIRED_STRAIGHT_OR_RIGHT_TURN(32),
  @SerialName("33") REQUIRED_STRAIGHT_OR_LEFT_TURN(33),
  @SerialName("34") ROUNDABOUT(34),
  @SerialName("35") PASS_RIGHT(35),
  @SerialName("36") PASS_LEFT(36),
  @SerialName("37") BICYCLE_PATH(37),
  @SerialName("38") FOOTWALK(38),
  @SerialName("39") FOOTWALK_BICYCLE_SHARED(39),
  @SerialName("40") FOOTWALK_BICYCLE_SEP_RIGHT(40),
  @SerialName("41") FOOTWALK_BICYCLE_SEP_LEFT(41),
  @SerialName("42") PEDESTRIAN_AREA_BEGIN(42),
  @SerialName("43") ACCESS_FORBIDDEN(43),
  @SerialName("44") ACCESS_FORBIDDEN_TRUCKS(44),
  @SerialName("45") ACCESS_FORBIDDEN_BICYCLE(45),
  @SerialName("46") ACCESS_FORBIDDEN_MOTORVEHICLES(46),
  @SerialName("47") ACCESS_FORBIDDEN_WEIGHT(47),
  @SerialName("48") ACCESS_FORBIDDEN_WIDTH(48),
  @SerialName("49") ACCESS_FORBIDDEN_HEIGHT(49),
  @SerialName("50") ACCESS_FORBIDDEN_WRONG_DIR(50),
  @SerialName("51") ENVIRONMENT_ZONE_BEGIN(51),
  @SerialName("52") ENVIRONMENT_ZONE_END(52),
  @SerialName("53") MAX_SPEED(53),
  @SerialName("54") SPEED_ZONE_30_BEGIN(54),
  @SerialName("55") SPEED_ZONE_30_END(55),
  @SerialName("56") HAS_WAY_NEXT_INTERSECTION(56),
  @SerialName("57") PRIORITY_WAY(57),
  @SerialName("58") CITY_BEGIN(58),
  @SerialName("59") CITY_END(59),
  @SerialName("60") MOTORWAY_BEGIN(60),
  @SerialName("61") MOTORWAY_END(61),
  @SerialName("62") MOTORVEHICLE_BEGIN(62),
  @SerialName("63") MOTORVEHICLE_END(63),
  @SerialName("64") INFO_MOTORWAY_INFO(64),
  @SerialName("65") CUL_DE_SAC(65),
  @SerialName("66") CUL_DE_SAC_EXCEPT_PED_BICYCLE(66),
  @SerialName("67") INFO_NUMBER_OF_AUTOBAHN(67),
  @SerialName("68") DIRECTION_TURN_TO_AUTOBAHN(68),
  @SerialName("69") DIRECTION_TURN_TO_LOCAL(69),
  @SerialName("70") DESTINATION_BOARD(70),
  @SerialName("71") FREE_TEXT(71),
  @SerialName("72") UNKNOWN(72),
}
