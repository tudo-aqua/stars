/*
 * Copyright 2023-2026 The STARS Project Authors
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
 * JSON enum of traffic sign types, including supplements and information signs.
 *
 * The integer identifiers mirror the CARLA Python API.
 *
 * @property value Numeric code used by CARLA; also provided via [SerialName] to ensure stable I/O.
 */
@Suppress("unused")
@Serializable
enum class JsonTrafficSignType(val value: Int) {
  /** Invalid/placeholder entry. */
  @SerialName("0") INVALID(0),

  /** Supplement: restriction applies to the left. */
  @SerialName("1") SUPPLEMENT_ARROW_APPLIES_LEFT(1),
  /** Supplement: restriction applies to the right. */
  @SerialName("2") SUPPLEMENT_ARROW_APPLIES_RIGHT(2),
  /** Supplement: applies left or right. */
  @SerialName("3") SUPPLEMENT_ARROW_APPLIES_LEFT_RIGHT(3),
  /** Supplement: applies up or down. */
  @SerialName("4") SUPPLEMENT_ARROW_APPLIES_UP_DOWN(4),
  /** Supplement: applies left/right for bicycles. */
  @SerialName("5") SUPPLEMENT_ARROW_APPLIES_LEFT_RIGHT_BICYCLE(5),
  /** Supplement: applies up/down for bicycles. */
  @SerialName("6") SUPPLEMENT_ARROW_APPLIES_UP_DOWN_BICYCLE(6),
  /** Supplement: applies for the next N km / time. */
  @SerialName("7") SUPPLEMENT_APPLIES_NEXT_N_KM_TIME(7),
  /** Supplement: restriction ends. */
  @SerialName("8") SUPPLEMENT_ENDS(8),
  /** Supplement: residents permitted. */
  @SerialName("9") SUPPLEMENT_RESIDENTS_ALLOWED(9),
  /** Supplement: bicycles permitted. */
  @SerialName("10") SUPPLEMENT_BICYCLE_ALLOWED(10),
  /** Supplement: mopeds permitted. */
  @SerialName("11") SUPPLEMENT_MOPED_ALLOWED(11),
  /** Supplement: trams permitted. */
  @SerialName("12") SUPPLEMENT_TRAM_ALLOWED(12),
  /** Supplement: forestry vehicles permitted. */
  @SerialName("13") SUPPLEMENT_FORESTAL_ALLOWED(13),
  /** Supplement: construction vehicles permitted. */
  @SerialName("14") SUPPLEMENT_CONSTRUCTION_VEHICLE_ALLOWED(14),
  /** Supplement: environmental zone (yellow/green). */
  @SerialName("15") SUPPLEMENT_ENVIRONMENT_ZONE_YELLOW_GREEN(15),
  /** Supplement: railway only. */
  @SerialName("16") SUPPLEMENT_RAILWAY_ONLY(16),
  /** Supplement: applies for listed weight. */
  @SerialName("17") SUPPLEMENT_APPLIES_FOR_WEIGHT(17),

  /** Generic danger/warning. */
  @SerialName("18") DANGER(18),
  /** Lanes merging ahead. */
  @SerialName("19") LANES_MERGING(19),
  /** Caution: pedestrian crossing/area. */
  @SerialName("20") CAUTION_PEDESTRIAN(20),
  /** Caution: children. */
  @SerialName("21") CAUTION_CHILDREN(21),
  /** Caution: bicycles/cyclists. */
  @SerialName("22") CAUTION_BICYCLE(22),
  /** Caution: animals on road. */
  @SerialName("23") CAUTION_ANIMALS(23),
  /** Caution: rail crossing with barrier. */
  @SerialName("24") CAUTION_RAIL_CROSSING_WITH_BARRIER(24),
  /** Caution: unguarded rail crossing. */
  @SerialName("25") CAUTION_RAIL_CROSSING(25),

  /** Yield to trains. */
  @SerialName("26") YIELD_TRAIN(26),
  /** Yield / give way. */
  @SerialName("27") YIELD(27),
  /** Stop sign. */
  @SerialName("28") STOP(28),

  /** Required right turn. */
  @SerialName("29") REQUIRED_RIGHT_TURN(29),
  /** Required left turn. */
  @SerialName("30") REQUIRED_LEFT_TURN(30),
  /** Required to proceed straight. */
  @SerialName("31") REQUIRED_STRAIGHT(31),
  /** Required to go straight or turn right. */
  @SerialName("32") REQUIRED_STRAIGHT_OR_RIGHT_TURN(32),
  /** Required to go straight or turn left. */
  @SerialName("33") REQUIRED_STRAIGHT_OR_LEFT_TURN(33),
  /** Roundabout (circular intersection). */
  @SerialName("34") ROUNDABOUT(34),
  /** Pass to the right. */
  @SerialName("35") PASS_RIGHT(35),
  /** Pass to the left. */
  @SerialName("36") PASS_LEFT(36),

  /** Dedicated bicycle path. */
  @SerialName("37") BICYCLE_PATH(37),
  /** Footway/pedestrian path. */
  @SerialName("38") FOOTWALK(38),
  /** Shared footway & bicycle path. */
  @SerialName("39") FOOTWALK_BICYCLE_SHARED(39),
  /** Separated footway/bicycle: bike on right. */
  @SerialName("40") FOOTWALK_BICYCLE_SEP_RIGHT(40),
  /** Separated footway/bicycle: bike on left. */
  @SerialName("41") FOOTWALK_BICYCLE_SEP_LEFT(41),
  /** Pedestrian area begins. */
  @SerialName("42") PEDESTRIAN_AREA_BEGIN(42),

  /** No entry / access forbidden (all vehicles). */
  @SerialName("43") ACCESS_FORBIDDEN(43),
  /** Access forbidden for trucks. */
  @SerialName("44") ACCESS_FORBIDDEN_TRUCKS(44),
  /** Access forbidden for bicycles. */
  @SerialName("45") ACCESS_FORBIDDEN_BICYCLE(45),
  /** Access forbidden for motor vehicles. */
  @SerialName("46") ACCESS_FORBIDDEN_MOTORVEHICLES(46),
  /** Weight limit. */
  @SerialName("47") ACCESS_FORBIDDEN_WEIGHT(47),
  /** Width limit. */
  @SerialName("48") ACCESS_FORBIDDEN_WIDTH(48),
  /** Height limit. */
  @SerialName("49") ACCESS_FORBIDDEN_HEIGHT(49),
  /** Wrong-way / entry prohibited in this direction. */
  @SerialName("50") ACCESS_FORBIDDEN_WRONG_DIR(50),

  /** Environmental zone begins. */
  @SerialName("51") ENVIRONMENT_ZONE_BEGIN(51),
  /** Environmental zone ends. */
  @SerialName("52") ENVIRONMENT_ZONE_END(52),

  /** Maximum speed limit. */
  @SerialName("53") MAX_SPEED(53),
  /** Zone 30 begins. */
  @SerialName("54") SPEED_ZONE_30_BEGIN(54),
  /** Zone 30 ends. */
  @SerialName("55") SPEED_ZONE_30_END(55),

  /** Priority over oncoming traffic at next intersection. */
  @SerialName("56") HAS_WAY_NEXT_INTERSECTION(56),
  /** Priority road begins. */
  @SerialName("57") PRIORITY_WAY(57),

  /** City limits begin. */
  @SerialName("58") CITY_BEGIN(58),
  /** City limits end. */
  @SerialName("59") CITY_END(59),

  /** Motorway begins. */
  @SerialName("60") MOTORWAY_BEGIN(60),
  /** Motorway ends. */
  @SerialName("61") MOTORWAY_END(61),
  /** Motor vehicle road begins. */
  @SerialName("62") MOTORVEHICLE_BEGIN(62),
  /** Motor vehicle road ends. */
  @SerialName("63") MOTORVEHICLE_END(63),

  /** Motorway information sign. */
  @SerialName("64") INFO_MOTORWAY_INFO(64),
  /** Cul-de-sac / dead end. */
  @SerialName("65") CUL_DE_SAC(65),
  /** Cul-de-sac except pedestrians/bicycles. */
  @SerialName("66") CUL_DE_SAC_EXCEPT_PED_BICYCLE(66),
  /** Autobahn/route number sign. */
  @SerialName("67") INFO_NUMBER_OF_AUTOBAHN(67),
  /** Directional sign pointing to motorway. */
  @SerialName("68") DIRECTION_TURN_TO_AUTOBAHN(68),
  /** Directional sign to local destination. */
  @SerialName("69") DIRECTION_TURN_TO_LOCAL(69),
  /** Destination board. */
  @SerialName("70") DESTINATION_BOARD(70),

  /** Free text/information sign. */
  @SerialName("71") FREE_TEXT(71),
  /** Unknown/unsupported sign type. */
  @SerialName("72") UNKNOWN(72),
}
