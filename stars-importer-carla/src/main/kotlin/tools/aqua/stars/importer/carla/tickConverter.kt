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

package tools.aqua.stars.importer.carla

import java.nio.file.Path
import kotlin.io.path.name
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
          fileName.split("dynamic_data_")[1].split("_seed_")[1].split(".")[0].toInt()
      fileName.contains("static_data") ->
          error("Cannot get seed name for map data! Analyzed file: $fileName")
      else -> error("Unknown filename format")
    }

/**
 * Convert Json data.
 *
 * @param world The [World].
 * @param jsonSimulationRun The list of [JsonTickData] in current observation.
 * @param tickDataSourcePath The [Path] from which the [JsonTickData] was loaded.
 * @param egoIds The list of ego ids to use. If empty, the ego flag in the Json data is used.
 * @param useEveryVehicleAsEgo If true, every vehicle is used as ego vehicle
 * @param useFirstVehicleAsEgo If true, the first vehicle found in the first tick that is present in
 *   every tick is used as ego vehicle
 * @return A list of list of [TickData], one for each ego vehicle.
 */
fun convertTickData(
    world: World,
    jsonSimulationRun: List<JsonTickData>,
    tickDataSourcePath: Path,
    egoIds: List<Int> = emptyList(),
    useEveryVehicleAsEgo: Boolean = false,
    useFirstVehicleAsEgo: Boolean = false,
): List<List<TickData>> {

  cleanJsonData(world, jsonSimulationRun)
  // Vehicles per tick
  val vehiclesPerTick: List<List<JsonVehicle>> =
      jsonSimulationRun.map { tick ->
        tick.actorPositions.map { it.actor }.filterIsInstance<JsonVehicle>()
      }

  // Union of all vehicle IDs across the whole run
  val allVehicleIds: Set<Int> = vehiclesPerTick.flatten().map { it.id }.toSet()
  check(allVehicleIds.isNotEmpty()) { "No vehicles found in Json data: $tickDataSourcePath" }

  // Ego-flagged IDs seen anywhere in the run
  val egoFlaggedIds: Set<Int> =
      vehiclesPerTick.flatten().filter { it.egoVehicle }.map { it.id }.toSet()

  val enabledModes =
      listOf(
              useFirstVehicleAsEgo,
              useEveryVehicleAsEgo,
              egoIds.isNotEmpty(),
              egoFlaggedIds.isNotEmpty(),
          )
          .count { it }

  require(enabledModes == 1) {
    "Exactly one of useEveryVehicleAsEgo, useFirstVehicleAsEgo, egoIds, or ego-flagged IDs must be used " +
        "to select ego vehicles. Found $enabledModes. Source: $tickDataSourcePath"
  }

  vehiclesPerTick.forEachIndexed { idx, vehicles ->
    check(vehicles.count { it.egoVehicle } <= 1) {
      "More than one ego vehicle flagged in tick index=$idx. Source: $tickDataSourcePath"
    }
  }

  val egoIdsDistinct = egoIds.distinct()
  check(egoIdsDistinct.size == egoIds.size) { "egoIds contains duplicates: $egoIds" }

  val egosToUse: List<Int> =
      when {
        useEveryVehicleAsEgo -> allVehicleIds.toList()

        egoIdsDistinct.isNotEmpty() -> {
          val missing = egoIdsDistinct.filterNot { it in allVehicleIds }
          check(missing.isEmpty()) {
            "Some egoIds were not found anywhere in the Json data: missing=$missing. Source: $tickDataSourcePath"
          }
          egoIdsDistinct
        }

        egoFlaggedIds.isNotEmpty() -> {
          check(egoFlaggedIds.size == 1) {
            "Expected exactly one ego vehicle id across the run, found ${egoFlaggedIds.size}: $egoFlaggedIds. Source: $tickDataSourcePath"
          }
          listOf(egoFlaggedIds.single())
        }

        useFirstVehicleAsEgo -> {
          val firstVehicleId = vehiclesPerTick.firstOrNull { it.isNotEmpty() }?.first()?.id
          check(firstVehicleId != null) {
            "useFirstVehicleAsEgo=true but no vehicle was found in any tick. Source: $tickDataSourcePath"
          }
          listOf(firstVehicleId)
        }

        else -> emptyList()
      }

  check(egosToUse.isNotEmpty()) {
    "No ego vehicle selected (egosToUse is empty). Check egoIds / flags / useFirstVehicleAsEgo. Source: $tickDataSourcePath"
  }

  return egosToUse.map { egoId ->
    val tickDataForEgo =
        jsonSimulationRun
            .mapNotNull { tick ->
              val vehiclesInRun =
                  tick.actorPositions.map { it.actor }.filterIsInstance<JsonVehicle>()

              // Snapshot original egoVehicle flags to avoid persistent mutation across ego iterations.
              val originalEgoFlags = vehiclesInRun.map { it to it.egoVehicle }

              vehiclesInRun.forEach { v -> v.egoVehicle = (v.id == egoId) }

              val result =
                  if (vehiclesInRun.none { it.egoVehicle }) {
                    null
                  } else {
                    tick.toTickData(world, tickDataSourcePath.fileName.name)
                  }

              // Restore original egoVehicle flags so JsonVehicle instances are left unchanged.
              originalEgoFlags.forEach { (vehicle, originalFlag) ->
                vehicle.egoVehicle = originalFlag
              }

              result
            }
            .also { updateActorVelocityForSimulationRun(it) }

    check(tickDataForEgo.isNotEmpty()) {
      "Ego vehicle id=$egoId never appeared in any tick (or produced no TickData). Source: $tickDataSourcePath"
    }

    tickDataForEgo
  }
}

/**
 * Returns the lane progress of a vehicle.
 *
 * @param world The [World].
 * @param jsonSimulationRun The list of [JsonTickData] in current observation.
 * @param vehicle The [JsonVehicle].
 */
private fun getLaneProgressionForVehicle(
    world: World,
    jsonSimulationRun: List<JsonTickData>,
    vehicle: JsonVehicle,
): MutableList<Pair<Lane?, Boolean>> {
  val roads = world.getAllRoads()
  val lanes = world.getAllLanes()
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
 * Updates velocity of actors.
 *
 * @param simulationRun List of [TickData].
 */
private fun updateActorVelocityForSimulationRun(simulationRun: List<TickData>) {
  for (i in 1 until simulationRun.size) {
    val currentTick = simulationRun[i]
    val previousTick = simulationRun[i - 1]
    currentTick.vehicles.forEach { currentActor ->
      updateActorVelocityAndAcceleration(
          vehicle = currentActor,
          previousActor = previousTick.vehicles.firstOrNull { it.id == currentActor.id },
          timeDelta =
              (currentTick.currentTickUnit - previousTick.currentTickUnit).differenceSeconds,
      )
    }
  }
}

/**
 * Updates velocity and acceleration of [vehicle].
 *
 * @param vehicle The [Vehicle] to update.
 * @param previousActor The previous [Actor].
 * @param timeDelta The time difference between the current and previous tick.
 * @throws IllegalStateException iff [previousActor] is not [Vehicle].
 */
internal fun updateActorVelocityAndAcceleration(
    vehicle: Vehicle,
    previousActor: Actor?,
    timeDelta: Double,
) {
  check(timeDelta >= 0.0) {
    "The time difference between the current tick and the previous tick is negative ($timeDelta)."
  }

  // When there is no previous actor position, set velocity and acceleration to 0.0
  if (previousActor == null) {
    vehicle.velocity = Vector3D(0.0, 0.0, 0.0)
    vehicle.acceleration = Vector3D(0.0, 0.0, 0.0)
    return
  }

  check(previousActor is Vehicle) {
    "The Actor '$previousActor' from the previous tick is of type '${previousActor::class}' but '${Vehicle::class}' was expected."
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
 * Cleans Json data.
 *
 * @param world The [World].
 * @param jsonSimulationRun The list of [JsonTickData] in current observation.
 */
fun cleanJsonData(world: World, jsonSimulationRun: List<JsonTickData>) {
  val vehicles =
      jsonSimulationRun
          .flatMap { it.actorPositions }
          .map { it.actor }
          .filterIsInstance<JsonVehicle>()
          .distinctBy { it.id }
  vehicles.forEach { vehicle ->
    val laneProgression = getLaneProgressionForVehicle(world, jsonSimulationRun, vehicle)

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
              jsonSimulationRun,
              currentJunction,
              previousMultilane,
              nextMultilane,
              vehicle,
          )
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
    vehicle: JsonVehicle,
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
