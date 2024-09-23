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

import tools.aqua.stars.data.av.dataclasses.*
import tools.aqua.stars.importer.carla.dataclasses.JsonTickData
import tools.aqua.stars.importer.carla.dataclasses.JsonVehicle

/**
 * Returns the name of the map.
 *
 * @param fileName The filename.
 * @throws IllegalStateException When the [fileName] is not empty and does not include "static_data"
 *   or "dynamic_data".
 */
@Suppress("unused")
fun getMapName(fileName: String): String =
    when {
      fileName.isEmpty() -> "test_case"
      fileName.contains("static_data") -> fileName.split("static_data_")[1].split(".zip")[0]
      fileName.contains("dynamic_data") -> fileName.split("dynamic_data_")[1].split("_seed")[0]
      else -> error("Unknown filename format")
    }

/**
 * Returns the seed value for the given [fileName].
 *
 * @param fileName The filename from which the seed value should be calculated from.
 * @throws IllegalStateException When the [fileName] does not include "dynamic_data".
 */
fun getSeed(fileName: String): Int =
    when {
      fileName.isEmpty() -> 0
      fileName.contains("dynamic_data") ->
          fileName.split("dynamic_data_")[1].split("_seed")[1].split(".")[0].toInt()
      fileName.contains("static_data") ->
          error("Cannot get seed name for map data! Analyzed file: $fileName")
      else -> error("Unknown filename format")
    }

/**
 * Returns the lane progress of a vehicle.
 *
 * @param blocks The list of [Block]s.
 * @param jsonSimulationRun The list of [JsonTickData] in current observation.
 * @param vehicle The [JsonVehicle].
 */
fun getLaneProgressionForVehicle(
    blocks: List<Block>,
    jsonSimulationRun: List<JsonTickData>,
    vehicle: JsonVehicle
): MutableList<Pair<Lane?, Boolean>> {
  val roads = blocks.flatMap { it.roads }
  val lanes = roads.flatMap { it.lanes }
  val laneProgression: MutableList<Pair<Lane?, Boolean>> = mutableListOf()

  jsonSimulationRun.forEach { jsonTickData ->
    val vehiclePosition = jsonTickData.actorPositions.firstOrNull { it.actor.id == vehicle.id }

    if (vehiclePosition == null) {
      laneProgression.add(null to false)
      return@forEach
    }

    val vehicleLane =
        lanes.first { it.laneId == vehiclePosition.laneId && it.road.id == vehiclePosition.roadId }
    val vehicleRoad = roads.first { it.id == vehiclePosition.roadId }
    laneProgression.add(vehicleLane to vehicleRoad.isJunction)
  }

  return laneProgression
}

/**
 * Convert Json data.
 *
 * @param blocks The list of [Block]s.
 * @param jsonSimulationRun The list of [JsonTickData] in current observation.
 * @param useEveryVehicleAsEgo Whether to treat every vehicle as own.
 * @param simulationRunId Identifier of the simulation run.
 */
// @Suppress("LABEL_NAME_CLASH")
@Suppress("unused")
fun convertJsonData(
    blocks: List<Block>,
    jsonSimulationRun: List<JsonTickData>,
    useEveryVehicleAsEgo: Boolean,
    simulationRunId: String
): MutableList<Pair<String, List<TickData>>> {
  var egoVehicles: List<JsonVehicle> =
      jsonSimulationRun.first().actorPositions.map { it.actor }.filterIsInstance<JsonVehicle>()

  if (!useEveryVehicleAsEgo && egoVehicles.any { e -> e.egoVehicle }) {
    egoVehicles = egoVehicles.filter { e -> e.egoVehicle }
  }

  // Stores all simulation runs (List<TickData>) for each ego vehicle
  val simulationRuns = mutableListOf<Pair<String, List<TickData>>>()

  // Stores a complete TickData list which will be cloned for each ego vehicle
  var referenceTickData: List<TickData>? = null

  egoVehicles.forEach { egoVehicle ->
    // If UseEveryVehicleAsEgo is false and there was already on simulationRun recorded: skip next
    // vehicles
    if (simulationRuns.isNotEmpty() && !useEveryVehicleAsEgo) {
      return@forEach
    }

    // Either load data from json or clone existing data
    if (referenceTickData == null) {
      referenceTickData =
          jsonSimulationRun.map { jsonTickData ->
            convertJsonTickDataToTickData(jsonTickData, blocks)
          }
    }
    val egoTickData = checkNotNull(referenceTickData).map { it.clone() }

    // Remove all existing ego flags when useEveryVehicleAsEgo is set
    if (useEveryVehicleAsEgo) {
      egoTickData.forEach { tickData -> tickData.vehicles.forEach { it.isEgo = false } }
    }

    // Set egoVehicle flag for each TickData
    var isTickWithoutEgo = false
    egoTickData.forEach { tickData ->
      if (!isTickWithoutEgo) {
        val egoInTickData = tickData.vehicles.firstOrNull { it.id == egoVehicle.id }
        if (egoInTickData != null) {
          egoInTickData.isEgo = true
        } else {
          isTickWithoutEgo = true
        }
      }
    }

    // There were some simulation runs where some vehicles are not always there.
    // Therefore, check if the egoVehicle was found in each tick
    if (!isTickWithoutEgo) {
      simulationRuns.add(simulationRunId to egoTickData)
    }
  }
  // Update actor velocity as it is not in the JSON data
  simulationRuns.forEach { updateActorVelocityForSimulationRun(it.second) }
  return simulationRuns
}

/**
 * Updates velocity of actors.
 *
 * @param simulationRun List of [TickData].
 */
fun updateActorVelocityForSimulationRun(simulationRun: List<TickData>) {
  for (i in 1 until simulationRun.size) {
    val currentTick = simulationRun[i]
    val previousTick = simulationRun[i - 1]
    currentTick.vehicles.forEach { currentActor ->
      updateActorVelocityAndAcceleration(
          currentActor, previousTick.vehicles.firstOrNull { it.id == currentActor.id })
    }
  }
}

/**
 * Updates velocity and acceleration of [vehicle].
 *
 * @param vehicle The [Vehicle] to update.
 * @param previousActor The previous [Actor].
 * @throws IllegalStateException iff [previousActor] is not [Vehicle].
 */
fun updateActorVelocityAndAcceleration(vehicle: Vehicle, previousActor: Actor?) {

  // When there is no previous actor position, set velocity and acceleration to 0.0
  if (previousActor == null) {
    vehicle.velocity = Vector3D(0.0, 0.0, 0.0)
    vehicle.acceleration = Vector3D(0.0, 0.0, 0.0)
    return
  }

  check(previousActor is Vehicle) {
    "The Actor with id '${previousActor.id}' from the previous tick is of type '${previousActor::class}' " +
        "but '${Vehicle::class}' was expected."
  }

  // Calculate the time difference
  val timeDelta: Double =
      (vehicle.tickData.currentTick - previousActor.tickData.currentTick).differenceSeconds

  check(timeDelta >= 0) {
    "The time delta between the vehicles is less than 0. Maybe you have switched the vehicles? " +
        "Tick of current vehicle: ${vehicle.tickData.currentTick} vs. previous vehicle: ${previousActor.tickData.currentTick}"
  }

  if (timeDelta == 0.0) {
    // If the time difference is exactly 0.0 set default values, as division by 0.0 is not allowed
    vehicle.velocity = Vector3D(0.0, 0.0, 0.0)
    vehicle.acceleration = Vector3D(0.0, 0.0, 0.0)
  } else {
    // Set velocity and acceleration vector based on velocity values for each direction
    vehicle.velocity = (Vector3D(vehicle.location) - Vector3D(previousActor.location)) / timeDelta
    vehicle.acceleration =
        (Vector3D(vehicle.velocity) - Vector3D(previousActor.velocity) / timeDelta)
  }
}

/**
 * Slices run into segments.
 *
 * @param blocks The list of [Block]s.
 * @param jsonSimulationRun The list of [JsonTickData] in current observation.
 * @param useEveryVehicleAsEgo Whether to treat every vehicle as own.
 * @param simulationRunId Identifier of the simulation run.
 * @param minSegmentTickCount Minimal count of ticks per segment.
 */
fun sliceRunIntoSegments(
    blocks: List<Block>,
    jsonSimulationRun: List<JsonTickData>,
    useEveryVehicleAsEgo: Boolean,
    simulationRunId: String,
    minSegmentTickCount: Int
): List<Segment> {
  cleanJsonData(blocks, jsonSimulationRun)
  val simulationRuns =
      convertJsonData(blocks, jsonSimulationRun, useEveryVehicleAsEgo, simulationRunId)

  val segments = mutableListOf<Segment>()

  simulationRuns.forEach { (simulationRunId, simulationRun) ->
    val blockRanges = mutableListOf<Pair<TickDataUnitSeconds, TickDataUnitSeconds>>()
    var prevBlockID = checkNotNull(simulationRun.first().egoVehicle).lane.road.block.id
    var firstTickInBlock = simulationRun.first().currentTick

    simulationRun.forEachIndexed { index, tick ->
      val currentBlockID = checkNotNull(tick.egoVehicle).lane.road.block.id
      if (currentBlockID != prevBlockID) {
        blockRanges += (firstTickInBlock to simulationRun[index - 1].currentTick)
        prevBlockID = currentBlockID
        firstTickInBlock = tick.currentTick
      }
    }
    blockRanges += (firstTickInBlock to simulationRun.last().currentTick)

    blockRanges.forEachIndexed { _, blockRange ->
      val mainSegment =
          simulationRun
              .filter { it.currentTick in blockRange.first..blockRange.second }
              .map { it.clone() }
      if (mainSegment.size >= minSegmentTickCount) {
        segments +=
            Segment(mainSegment, simulationRunId = simulationRunId, segmentSource = simulationRunId)
      }
    }
  }

  return segments
}

/**
 * Cleans Json data.
 *
 * @param blocks The list of [Block]s.
 * @param jsonSimulationRun The list of [JsonTickData] in current observation.
 */
fun cleanJsonData(blocks: List<Block>, jsonSimulationRun: List<JsonTickData>) {
  val vehicles =
      jsonSimulationRun
          .flatMap { it.actorPositions }
          .map { it.actor }
          .filterIsInstance<JsonVehicle>()
          .distinctBy { it.id }
  vehicles.forEach { vehicle ->
    val laneProgression = getLaneProgressionForVehicle(blocks, jsonSimulationRun, vehicle)

    // Saves the lane progression of the current vehicle as a list of Triple(RoadId, LaneId,
    // IsJunction)
    var previousMultilane: Lane? = null
    var nextMultilane: Lane?
    val currentJunction: MutableList<Pair<Int, Lane>> = mutableListOf()

    laneProgression.forEachIndexed { index: Int, (lane: Lane?, isJunction: Boolean) ->
      if (lane == null) {
        return@forEach
      }
      if (!isJunction) {
        if (currentJunction.isNotEmpty()) {
          nextMultilane = lane
          cleanJunctionData(
              jsonSimulationRun, currentJunction, previousMultilane, nextMultilane, vehicle)
          currentJunction.clear()
          previousMultilane = lane
        } else {
          previousMultilane = lane
        }
      } else {
        currentJunction.add(index to lane)
      }
    }
    // The junction is the last block in the TickData.
    // Call with laneTo=null as there is no successor lane
    if (currentJunction.isNotEmpty()) {
      cleanJunctionData(jsonSimulationRun, currentJunction, previousMultilane, null, vehicle)
    }
  }
}

/**
 * Cleans junction data.
 *
 * @param jsonSimulationRun The list of [JsonTickData] in current observation.
 * @param junctionIndices Indices of the junctions.
 * @param laneFrom Incoming [Lane].
 * @param laneTo Outgoing [Lane].
 * @param vehicle The [JsonVehicle].
 */
private fun cleanJunctionData(
    jsonSimulationRun: List<JsonTickData>,
    junctionIndices: List<Pair<Int, Lane>>,
    laneFrom: Lane?,
    laneTo: Lane?,
    vehicle: JsonVehicle
) {
  // Check if the lanes are already all the same
  val junctionLaneGroups = junctionIndices.groupBy { it.second.toString() }
  if (junctionLaneGroups.size == 1) {
    return
  }
  val newLane: Lane?

  // Check which lane is mostly in the TickData
  var greatestGroup: Pair<Lane?, Int> = null to 0
  junctionLaneGroups.values.forEach {
    if (it.size > greatestGroup.second) {
      greatestGroup = it.first().second to it.size
    }
  }
  // There is at least one outlier: Clean up
  newLane =
      if (laneFrom == null || laneTo == null) {
        // The current junction is at the beginning or the end of the simulation run
        // Just take the lane which occurs more often
        greatestGroup.first
      } else if (laneFrom == laneTo) {
        // When there is a junction outlier in a multilane road just take laneFrom
        laneFrom
      } else {
        // The current junction has TickData which include MultiLane roads
        // Get connecting lane between laneFrom and laneTo
        val laneIntersect = laneFrom.successorLanes.intersect(laneTo.predecessorLanes.toSet())
        if (laneIntersect.isNotEmpty()) {
          laneIntersect.first().lane
        } else {
          // Apparently Roundabouts have connected lanes within the same road
          // To see this run Town3_Opt with seed 8 with the following code in python:
          // road_1608 = rasterizer.get_data_road(1608)
          // rasterizer.debug_road(road_1608)

          // Check for successor/predecessor connection with one step between
          val laneFromSuccessorSuccessors =
              laneFrom.successorLanes.flatMap { it.lane.successorLanes }
          val laneToPredecessors = laneTo.predecessorLanes
          val junctionIntersect = laneFromSuccessorSuccessors.intersect(laneToPredecessors.toSet())
          if (junctionIntersect.isNotEmpty()) {
            junctionIntersect.first().lane
          } else {
            // Lane change in a junction
            // See Seed34 Lane 483, which is technically a junction but only for the other side
            null
          }
        }
      }
  if (newLane != null) {
    junctionIndices.forEach { (index, _) ->
      val vehiclePositionToUpdate =
          jsonSimulationRun[index].actorPositions.firstOrNull { it.actor.id == vehicle.id }
      checkNotNull(vehiclePositionToUpdate)
      vehiclePositionToUpdate.laneId = newLane.laneId
      vehiclePositionToUpdate.roadId = newLane.road.id
    }
  }
}
