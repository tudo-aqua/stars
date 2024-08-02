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

package tools.aqua.stars.importer.carla

import kotlin.math.abs
import tools.aqua.stars.data.av.dataclasses.*
import tools.aqua.stars.importer.carla.dataclasses.*

// region converter
/**
 * Converts [JsonTickData] to [TickData].
 *
 * @param jsonTickData The [JsonTickData].
 * @param blocks List ob [Block]s.
 */
fun convertJsonTickDataToTickData(jsonTickData: JsonTickData, blocks: List<Block>): TickData {
  // Create new empty TickData
  val tickData =
      TickData(
          currentTick = TickDataUnitSeconds(jsonTickData.currentTick),
          trafficLights =
              jsonTickData.actorPositions.mapNotNull { convertJsonActorPositionToTrafficLight(it) },
          blocks = blocks,
          weather = jsonTickData.weatherParameters.toWeatherParameters(),
          daytime = jsonTickData.weatherParameters.type.toDaytime(),
          entities = emptyList())

  tickData.entities =
      jsonTickData.actorPositions.mapNotNull {
        convertJsonActorPositionToEntity(it, tickData, blocks)
      }

  return tickData
}

/**
 * Converts [JsonActorPosition] to [Actor].
 *
 * @param position The [JsonActorPosition].
 * @param tickData The [TickData].
 * @param blocks List ob [Block]s.
 */
fun convertJsonActorPositionToEntity(
    position: JsonActorPosition,
    tickData: TickData,
    blocks: List<Block>
): Actor? {
  val lane = checkNotNull(blocks.getLane(position.roadId, position.laneId))

  return when (position.actor) {
    is JsonPedestrian ->
        convertJsonPedestrianToPedestrian(
            position.actor as JsonPedestrian, tickData, position.positionOnLane, lane)
    is JsonTrafficLight -> null
    is JsonTrafficSign -> null
    is JsonVehicle ->
        convertJsonVehicleToVehicle(
            position.actor as JsonVehicle, tickData, position.positionOnLane, lane)
  }
}

/**
 * Converts [JsonActorPosition] to [TrafficLight].
 *
 * @param position The [JsonActorPosition].
 */
fun convertJsonActorPositionToTrafficLight(position: JsonActorPosition): TrafficLight? =
    (position.actor as? JsonTrafficLight)?.toTrafficLight()

/**
 * Converts [JsonVehicle] to [Vehicle].
 *
 * @param vehicle The [JsonVehicle].
 * @param tickData The [TickData].
 * @param positionOnLane The position on the [Lane].
 * @param lane The [Lane].
 */
fun convertJsonVehicleToVehicle(
    vehicle: JsonVehicle,
    tickData: TickData,
    positionOnLane: Double,
    lane: Lane
): Vehicle =
    Vehicle(
        typeId = vehicle.typeId,
        acceleration = vehicle.acceleration.toVector3D(),
        angularVelocity = vehicle.angularVelocity.toVector3D(),
        isEgo = vehicle.egoVehicle,
        forwardVector = vehicle.forwardVector.toVector3D(),
        id = vehicle.id,
        lane = lane,
        location = vehicle.location.toLocation(),
        positionOnLane = positionOnLane,
        rotation = vehicle.rotation.toRotation(),
        tickData = tickData,
        velocity = vehicle.velocity.toVector3D())

/**
 * Converts [JsonPedestrian] to [Pedestrian].
 *
 * @param pedestrian The [JsonPedestrian].
 * @param tickData The [TickData].
 * @param positionOnLane The position on the [Lane].
 * @param lane The [Lane].
 */
fun convertJsonPedestrianToPedestrian(
    pedestrian: JsonPedestrian,
    tickData: TickData,
    positionOnLane: Double,
    lane: Lane
): Pedestrian =
    Pedestrian(
        id = pedestrian.id, tickData = tickData, positionOnLane = positionOnLane, lane = lane)

/**
 * Converts [JsonBlock] to [Block].
 *
 * @param jsonBlock The [JsonBlock].
 * @param fileName The filename.
 */
fun convertJsonBlockToBlock(jsonBlock: JsonBlock, fileName: String): Block =
    Block(id = jsonBlock.id, roads = emptyList(), fileName = fileName).apply {
      roads = jsonBlock.roads.map { convertJsonRoadToRoad(it, this) }
    }

/**
 * Converts [JsonRoad] to [Road].
 *
 * @param jsonRoad The [JsonRoad].
 * @param block The [Block].
 */
fun convertJsonRoadToRoad(jsonRoad: JsonRoad, block: Block): Road =
    Road(id = jsonRoad.roadId, block = block, lanes = emptyList(), isJunction = jsonRoad.isJunction)
        .apply { lanes = jsonRoad.lanes.map { lane -> convertJsonLaneToLane(lane, this) } }

/**
 * Converts [JsonLane] to [Lane].
 *
 * @param jsonLane The [JsonLane].
 * @param road The [Road].
 */
fun convertJsonLaneToLane(jsonLane: JsonLane, road: Road): Lane =
    Lane(
            laneId = jsonLane.laneId,
            road = road,
            laneType = LaneType.getByValue(jsonLane.laneType.value),
            laneWidth = jsonLane.laneWidth,
            laneLength = jsonLane.laneLength,
            predecessorLanes = emptyList(),
            successorLanes = emptyList(),
            intersectingLanes = emptyList(),
            yieldLanes = emptyList(),
            laneMidpoints = jsonLane.laneMidpoints.map { it.toLaneMidpoint() },
            speedLimits = emptyList(),
            landmarks =
                jsonLane.landmarks
                    .filter { it.type != JsonLandmarkType.LightPost }
                    .map { it.toLandmark() },
            contactAreas = emptyList(),
            trafficLights = emptyList(),
            laneDirection = LaneDirection.UNKNOWN,
        )
        .apply {
          trafficLights = jsonLane.trafficLights.map { it.toStaticTrafficLight() }
          speedLimits = getSpeedLimitsFromLandmarks(this, jsonLane.landmarks)

          if (road.isJunction) {
            val firstYaw = laneMidpoints.first().rotation.yaw
            val lastYaw = laneMidpoints.last().rotation.yaw

            /** relative yaw change from first to last midpoint of lane. Is in range [-180..180] */
            val angleDiff =
                ((((lastYaw - firstYaw) % 360) + 540) % 360) -
                    180 // Thanks to https://stackoverflow.com/a/25269402
            laneDirection =
                when (angleDiff) {
                  in (-150.0..-30.0) -> LaneDirection.LEFT_TURN
                  in -30.0..30.0 -> LaneDirection.STRAIGHT
                  in 30.0..150.0 -> LaneDirection.RIGHT_TURN
                  else -> LaneDirection.UNKNOWN
                }
          } else {
            // road is not junction (i.e. multi-lane road)
            laneDirection = LaneDirection.STRAIGHT
          }
        }

/**
 * Converts [JsonContactArea] to [ContactArea].
 *
 * @param jsonContactArea The [JsonContactArea].
 * @param lane1 [Lane] 1.
 * @param lane2 [Lane] 2.
 */
fun convertJsonContactAreaToContactArea(
    jsonContactArea: JsonContactArea,
    lane1: Lane,
    lane2: Lane
): ContactArea =
    ContactArea(
        id = jsonContactArea.id,
        contactLocation = jsonContactArea.contactLocation.toLocation(),
        lane1EndPos = jsonContactArea.lane1EndPos,
        lane1StartPos = jsonContactArea.lane1StartPos,
        lane2EndPos = jsonContactArea.lane2EndPos,
        lane2StartPos = jsonContactArea.lane2StartPos,
        lane1 = lane1,
        lane2 = lane2)
// endregion

// region helper
/**
 * Extracts the speed limit from [JsonLandmark]s.
 *
 * @param lane The [Lane].
 * @param landmarks The list of [JsonLandmark]s.
 */
fun getSpeedLimitsFromLandmarks(lane: Lane, landmarks: List<JsonLandmark>): List<SpeedLimit> {
  val speedSigns = landmarks.filter { it.type == JsonLandmarkType.MaximumSpeed }.sortedBy { it.s }
  val speedLimits = mutableListOf<SpeedLimit>()

  speedSigns.forEachIndexed { index, sign ->
    check(sign.s < lane.laneLength) { "The position of the sign is at/after the end of the road" }

    val speedLimitValue = sign.value
    var nextSignLocation = lane.laneLength
    if (index < speedSigns.size - 1) {
      val nextSpeedSign = speedSigns[index + 1]
      nextSignLocation = nextSpeedSign.s
    }
    speedLimits.add(SpeedLimit(speedLimitValue, sign.s, nextSignLocation))
  }
  return speedLimits
}

/**
 * Calculates static [JsonBlock]s to [Block]s.
 *
 * @param staticJsonBlocks List of [JsonBlock]s.
 * @param fileName File name.
 */
fun calculateStaticBlocks(staticJsonBlocks: List<JsonBlock>, fileName: String): List<Block> =
    staticJsonBlocks
        .map { block -> convertJsonBlockToBlock(block, fileName) }
        .also {
          updateLanes(
              jsonLanes = staticJsonBlocks.flatMap { b -> b.roads }.flatMap { b -> b.lanes },
              lanes = it.flatMap { b -> b.roads }.flatMap { b -> b.lanes })
        }

/** Updates [JsonLane]s and [Lane]s. */
fun updateLanes(jsonLanes: List<JsonLane>, lanes: List<Lane>) {
  jsonLanes.forEach { it.update(lanes) }
  lanes.forEach { it.update() }
}

private fun JsonLane.update(lanes: List<Lane>) {
  val lane = lanes.firstOrNull { it.laneId == this.laneId && it.road.id == this.roadId }
  checkNotNull(lane) {
    "No lane with the id ${this.laneId} was found while updating the ContactLaneInfos"
  }
  lane.predecessorLanes =
      this.predecessorLanes.map { contactLaneInfo ->
        val contactLane =
            lanes.first {
              it.laneId == contactLaneInfo.laneId && it.road.id == contactLaneInfo.roadId
            }
        ContactLaneInfo(lane = contactLane)
      }
  lane.successorLanes =
      this.successorLanes.map { contactLaneInfo ->
        val contactLane =
            lanes.first {
              it.laneId == contactLaneInfo.laneId && it.road.id == contactLaneInfo.roadId
            }
        ContactLaneInfo(lane = contactLane)
      }
  lane.intersectingLanes =
      this.intersectingLanes.mapNotNull { contactLaneInfo ->
        val contactLane =
            lanes.first {
              it.laneId == contactLaneInfo.laneId && it.road.id == contactLaneInfo.roadId
            }
        if (lane.laneId == contactLane.laneId && lane.road.id == contactLane.road.id) {
          check(true) { "The same lane is intersecting with itself" }
          null
        } else ContactLaneInfo(lane = contactLane)
      }

  lane.contactAreas =
      this.contactAreas.map { jsonContactArea ->
        val contactLane1 =
            lanes.first {
              it.laneId == jsonContactArea.lane1Id && it.road.id == jsonContactArea.lane1RoadId
            }
        val contactLane2 =
            lanes.first {
              it.laneId == jsonContactArea.lane2Id && it.road.id == jsonContactArea.lane2RoadId
            }
        convertJsonContactAreaToContactArea(jsonContactArea, contactLane1, contactLane2)
      }
}

private fun Lane.update() {
  // three variants of going through all intersecting lanes, filtering for those this lane must
  // yield
  // to and wrapping them in a new [ContactLaneInfo]
  if (this.predecessorLanes.any { it.lane.hasStopOrYieldSign }) {
    // this lane's predecessor had a stop/yield sign
    // => need to yield to all intersecting lanes without stop/yield sign *and*
    // to all with stop/yield sign that are straight/right
    this.yieldLanes =
        this.intersectingLanes
            .map { it.lane }
            .filter { otherLane ->
              otherLane.predecessorLanes.none { it.lane.hasStopOrYieldSign } ||
                  otherLane.isStraight ||
                  otherLane.isTurningRight
            }
            .map { ContactLaneInfo(it) }
  } else if (this.predecessorLanes.any { it.lane.hasTrafficLight }) {
    // this lane's predecessor had a traffic light
    // => need to yield to all intersecting lanes of the same traffic light cycle that are
    // straight/right
    this.yieldLanes =
        this.intersectingLanes
            .map { it.lane }
            .filter { otherLane ->
              val otherStartYaw = otherLane.laneMidpoints.first().rotation.yaw
              val thisStartYaw = this.laneMidpoints.first().rotation.yaw
              abs(otherStartYaw - thisStartYaw) in 175.0..185.0 &&
                  (otherLane.isStraight || otherLane.isTurningRight)
            }
            .map { ContactLaneInfo(it) }
  } else {
    // this is an uncontrolled intersection => apply "right before left" rule

    this.yieldLanes =
        this.intersectingLanes
            .map { it.lane }
            .filter { otherLane ->
              when {
                // the easiest case, as the one who turns left never has right of way
                this.isTurningLeft -> true
                // both lanes are straight -> use angle of points at contact area
                // for "left before right" calculation
                this.isStraight && otherLane.isStraight -> {
                  val thisYaw =
                      this.laneMidpoints
                          .first {
                            checkNotNull(this.contactPointPos(otherLane)) > it.distanceToStart
                          }
                          .rotation
                          .yaw
                  val otherYaw =
                      otherLane.laneMidpoints
                          .first {
                            checkNotNull(otherLane.contactPointPos(this)) > it.distanceToStart
                          }
                          .rotation
                          .yaw

                  thisYaw > otherYaw
                  /*
                  calculation draft, to be discussed:

                  get the positive middle point between the two angles by
                  (thisYaw + otherYaw)/2
                  if the middle point is farther away to any of the two than 90°, the smaller angle
                  goes through the north orientation ("0")

                  extreme example: thisYaw = 45, otherYaw = 315
                  above calculation incorrectly says "I don't have to yield"

                  middle = (315+45)/2 = 180
                  |45-180| = 135
                  |315-180| = 135
                  135 > 90 -> result must be thisYaw < otherYaw

                   */

                }
                // I don't turn left and not both lanes are straight
                // in the remaining cases I either do not need to yield to the other lane
                // or the lanes to not cross
                else -> false
              }
            }
            .map { ContactLaneInfo(it) }
  }
}
// endregion
