/*
 * Copyright 2023 The STARS Project Authors
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

package tools.aqua.stars.import.carla

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import tools.aqua.stars.data.av.*

fun calculateStaticBlocks(staticJsonBlocks: List<JsonBlock>, fileName: String): List<Block> {
  val staticBlocks = staticJsonBlocks.map { block -> convertJsonBlockToBlock(block, fileName) }
  val jsonLanes = staticJsonBlocks.flatMap { it.roads }.flatMap { it.lanes }
  val lanes = staticBlocks.flatMap { it.roads }.flatMap { it.lanes }
  updateLaneContactLaneInfos(jsonLanes, lanes)
  return staticBlocks
}

fun convertJsonTickDataToTickData(jsonTickData: JsonTickData, blocks: List<Block>): TickData {
  // Create new empty TickData
  val tickData =
      TickData(
          currentTick = jsonTickData.currentTick,
          trafficLights =
              jsonTickData.actorPositions.mapNotNull { convertJsonActorPositionToTrafficLight(it) },
          blocks = blocks,
          weather = jsonTickData.weatherParameters.toWeatherParameters(),
          daytime = jsonTickData.weatherParameters.type.toDaytime(),
          entities = listOf())

  tickData.entities =
      jsonTickData.actorPositions.mapNotNull {
        convertJsonActorPositionToEntity(it, tickData, blocks)
      }

  return tickData
}

fun convertJsonActorPositionToEntity(
    position: JsonActorPosition,
    tickData: TickData,
    blocks: List<Block>
): Actor? {
  val lane = blocks.getLane(position.roadId, position.laneId)
  checkNotNull(lane)
  when (position.actor) {
    is JsonPedestrian ->
        return convertJsonPedestrianToPedestrian(
            position.actor as JsonPedestrian, tickData, position.positionOnLane, lane)
    is JsonTrafficLight -> return null
    is JsonTrafficSign -> return null
    is JsonVehicle ->
        return convertJsonVehicleToVehicle(
            position.actor as JsonVehicle, tickData, position.positionOnLane, lane)
  }
}

fun convertJsonActorPositionToTrafficLight(position: JsonActorPosition): TrafficLight? {
  if (position.actor is JsonTrafficLight) {
    return (position.actor as JsonTrafficLight).toTrafficLight()
  }
  return null
}

fun updateActorVelocityForSimulationRun(simulationRun: List<TickData>) {
  for (i in 1 until simulationRun.size) {
    val currentTick = simulationRun[i]
    val previousTick = simulationRun[i - 1]
    currentTick.actors.forEach {
      if (it is Vehicle) {
        updateActorVelocity(it, currentTick, previousTick)
      }
    }
  }
}

fun updateActorVelocity(actor: Vehicle, currentTick: TickData, previousTick: TickData) {
  val previousPActor = previousTick.actors.firstOrNull { it.id == actor.id }

  if (previousPActor == null) {
    actor.effVelocityInMPerS = 0.0
    return
  }

  previousPActor as Vehicle
  val xDelta = previousPActor.location.x - actor.location.x
  val yDelta = previousPActor.location.y - actor.location.y
  val zDelta = previousPActor.location.z - actor.location.z

  actor.effVelocityInMPerS =
      sqrt(xDelta.pow(2) + yDelta.pow(2) + zDelta.pow(2)) /
          (currentTick.currentTick - previousTick.currentTick)
}

fun convertJsonVehicleToVehicle(
    actor: JsonVehicle,
    tickData: TickData,
    positionOnLane: Double,
    lane: Lane
): Vehicle {
  return Vehicle(
      typeId = actor.typeId,
      acceleration = actor.acceleration.toVector3D(),
      angularVelocity = actor.angularVelocity.toVector3D(),
      egoVehicle = false,
      forwardVector = actor.forwardVector.toVector3D(),
      id = actor.id,
      lane = lane,
      location = actor.location.toLocation(),
      positionOnLane = positionOnLane,
      rotation = actor.rotation.toRotation(),
      tickData = tickData,
      velocity = actor.velocity.toVector3D(),
      effVelocityInMPerS = 0.0)
}

fun convertJsonPedestrianToPedestrian(
    pedestrian: JsonPedestrian,
    tickData: TickData,
    positionOnLane: Double,
    lane: Lane
): Pedestrian {
  return Pedestrian(
      id = pedestrian.id, tickData = tickData, positionOnLane = positionOnLane, lane = lane)
}

fun convertJsonBlockToBlock(jsonBlock: JsonBlock, fileName: String): Block {
  val block = Block(id = jsonBlock.id, roads = listOf(), fileName = fileName)
  val roads = jsonBlock.roads.map { convertJsonRoadToRoad(it, block) }
  block.roads = roads
  return block
}

fun convertJsonRoadToRoad(jsonRoad: JsonRoad, block: Block): Road {
  val road =
      Road(id = jsonRoad.roadId, block = block, lanes = listOf(), isJunction = jsonRoad.isJunction)
  val lanes = jsonRoad.lanes.map { lane -> convertJsonLaneToLane(lane, road) }
  road.lanes = lanes
  return road
}

fun convertJsonLaneToLane(jsonLane: JsonLane, road: Road): Lane {
  val lane =
      Lane(
          laneId = jsonLane.laneId,
          road = road,
          laneType = LaneType.getByValue(jsonLane.laneType.value),
          laneWidth = jsonLane.laneWidth,
          laneLength = jsonLane.laneLength,
          predecessorLanes = listOf(),
          successorLanes = listOf(),
          intersectingLanes = listOf(),
          yieldLanes = listOf(),
          laneMidpoints = jsonLane.laneMidpoints.map { it.toLaneMidpoint() },
          speedLimits = listOf(),
          landmarks =
              jsonLane.landmarks
                  .filter { it.type != JsonLandmarkType.LightPost }
                  .map { it.toLandmark() },
          contactAreas = listOf(),
          trafficLights = listOf(),
          laneDirection = LaneDirection.UNKNOWN,
      )
  lane.trafficLights = jsonLane.trafficLights.map { it.toStaticTrafficLight() }
  lane.speedLimits = getSpeedLimitsFromLandmarks(lane, jsonLane.landmarks)

  if (road.isJunction) {
    val firstYaw = lane.laneMidpoints.first().rotation.yaw
    val lastYaw = lane.laneMidpoints.last().rotation.yaw

    /** relative yaw change from first to last midppoint of lane. Is in range [-180..180] */
    val angleDiff =
        ((((lastYaw - firstYaw) % 360) + 540) % 360) -
            180 // Thanks to https://stackoverflow.com/a/25269402
    lane.laneDirection =
        when {
          -150.0 < angleDiff && angleDiff < -30.0 -> LaneDirection.LEFT_TURN
          30.0 < angleDiff && angleDiff < 150.0 -> LaneDirection.RIGHT_TURN
          -30.0 <= angleDiff && angleDiff <= 30.0 -> LaneDirection.STRAIGHT
          else -> LaneDirection.UNKNOWN
        }
  } else {
    // road is not junction (i.e. multilane road)
    lane.laneDirection = LaneDirection.STRAIGHT
  }

  return lane
}

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

fun convertJsonContactLaneInfoToContactLaneInfo(lane: Lane): ContactLaneInfo {
  return ContactLaneInfo(lane = lane)
}

fun convertJsonContactAreaToContactArea(
    jsonContactArea: JsonContactArea,
    lane1: Lane,
    lane2: Lane
): ContactArea {
  return ContactArea(
      id = jsonContactArea.id,
      contactLocation = jsonContactArea.contactLocation.toLocation(),
      lane1EndPos = jsonContactArea.lane1EndPos,
      lane1StartPos = jsonContactArea.lane1StartPos,
      lane2EndPos = jsonContactArea.lane2EndPos,
      lane2StartPos = jsonContactArea.lane2StartPos,
      lane1 = lane1,
      lane2 = lane2)
}

fun updateLaneContactLaneInfos(jsonLanes: List<JsonLane>, lanes: List<Lane>) {
  jsonLanes.map { jsonLane ->
    val lane = lanes.firstOrNull { it.laneId == jsonLane.laneId && it.road.id == jsonLane.roadId }
    checkNotNull(lane) {
      "No lane with the id ${jsonLane.laneId} was found while updating the ContactLaneInfos"
    }
    lane.predecessorLanes =
        jsonLane.predecessorLanes.map { contactLaneInfo ->
          val contactLane =
              lanes.first {
                it.laneId == contactLaneInfo.laneId && it.road.id == contactLaneInfo.roadId
              }
          convertJsonContactLaneInfoToContactLaneInfo(contactLane)
        }
    lane.successorLanes =
        jsonLane.successorLanes.map { contactLaneInfo ->
          val contactLane =
              lanes.first {
                it.laneId == contactLaneInfo.laneId && it.road.id == contactLaneInfo.roadId
              }
          convertJsonContactLaneInfoToContactLaneInfo(contactLane)
        }
    lane.intersectingLanes =
        jsonLane.intersectingLanes.mapNotNull { contactLaneInfo ->
          val contactLane =
              lanes.first {
                it.laneId == contactLaneInfo.laneId && it.road.id == contactLaneInfo.roadId
              }
          if (lane.laneId == contactLane.laneId && lane.road.id == contactLane.road.id) {
            check(true) { "The same lane is intersecting with itself" }
            null
          } else convertJsonContactLaneInfoToContactLaneInfo(contactLane)
        }

    lane.contactAreas =
        jsonLane.contactAreas.map { jsonContactArea ->
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

  lanes.map { lane ->

    // three variants of going through all intersecting lanes, filtering for those this lane must
    // yield
    // to and wraping them in a new [ContactLaneInfo]
    if (lane.predecessorLanes.any { it.lane.hasStopOrYieldSign }) {
      // this lane's predecessor had a stop/yield sign
      // => need to yield to all intersecting lanes without stop/yield sign *and*
      // to all with stop/yield sign that are straight/right
      lane.yieldLanes =
          lane.intersectingLanes
              .map { it.lane }
              .filter { otherLane ->
                otherLane.predecessorLanes.none { it.lane.hasStopOrYieldSign } ||
                    otherLane.isStraight ||
                    otherLane.turnsRight
              }
              .map { ContactLaneInfo(it) }
    } else if (lane.predecessorLanes.any { it.lane.hasTrafficLight }) {
      // this lane's predecessor had a traffic light
      // => need to yield to all intersecting lanes of the same ampelphase that are straight/right
      lane.yieldLanes =
          lane.intersectingLanes
              .map { it.lane }
              .filter { otherLane ->
                val otherStartYaw = otherLane.laneMidpoints.first().rotation.yaw
                val thisStartYaw = lane.laneMidpoints.first().rotation.yaw
                abs(otherStartYaw - thisStartYaw) in 175.0..185.0 &&
                    (otherLane.isStraight || otherLane.turnsRight)
              }
              .map { ContactLaneInfo(it) }
    } else {
      // this is an uncontrolled intersection => apply "right before left" rule

      lane.yieldLanes =
          lane.intersectingLanes
              .map { it.lane }
              .filter { otherLane ->
                when {
                  // the easiest case, as the one who turns left never has right of way
                  lane.turnsLeft -> true
                  // both lanes are straight -> use angle of points at contact area
                  // for "left before right" calculation
                  lane.isStraight && otherLane.isStraight -> {
                    val thisYaw =
                        lane.laneMidpoints
                            .first { lane.contactPointPos(otherLane)!! > it.distanceToStart }
                            .rotation
                            .yaw
                    val otherYaw =
                        otherLane.laneMidpoints
                            .first { otherLane.contactPointPos(lane)!! > it.distanceToStart }
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
}
