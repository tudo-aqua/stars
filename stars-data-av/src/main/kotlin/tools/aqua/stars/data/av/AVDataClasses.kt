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

package tools.aqua.stars.data.av

import kotlin.math.pow
import kotlin.math.sqrt
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

data class Segment(
    val mainInitList: List<TickData>,
    val simulationRunId: String = "",
    override val mapName: String,
) : SegmentType<Actor, TickData, Segment> {
  override val tickData: List<TickData> =
      mainInitList.map {
        it.segment = this
        it
      }
  override val ticks: Map<Double, TickData> = tickData.associateBy { it.currentTick }
  override val tickIDs: List<Double> = tickData.map { it.currentTick }

  override val firstTickId: Double = this.tickIDs.first()

  override val egoVehicleId: Int
    get() {
      val firstEgo = tickData.first().egoVehicle
      if (tickData.any { it.egoVehicle.id != firstEgo.id })
          error("inconsistent ego ids in segment $this")
      return firstEgo.id
    }

  /** cache for all vehicle IDs */
  private val vehicleIdsCache = mutableListOf<Int>()

  /** all vehicle IDs of the segment */
  val vehicleIds: List<Int>
    get() {
      if (vehicleIdsCache.isEmpty()) {
        vehicleIdsCache.addAll(
            tickData
                .flatMap { tickData -> tickData.entities.filterIsInstance<Vehicle>().map { it.id } }
                .distinct())
      }
      return vehicleIdsCache
    }

  /** cache for all vehicle IDs */
  private val pedestrianIdsCache = mutableListOf<Int>()

  /** all vehicle IDs of the segment */
  val pedestrianIds: List<Int>
    get() {
      if (pedestrianIdsCache.isEmpty()) {
        pedestrianIdsCache.addAll(
            tickData
                .flatMap { tickData ->
                  tickData.entities.filterIsInstance<Pedestrian>().map { it.id }
                }
                .distinct())
      }
      return pedestrianIdsCache
    }

  override fun toString(): String {
    return "Segment[(${tickData.first().currentTick}..${tickData.last().currentTick})] from $simulationRunId " +
        "with ego ${this.egoVehicleId}"
  }

  override fun equals(other: Any?): Boolean {
    if (other is Segment) {
      return other.toString() == this.toString()
    }
    return super.equals(other)
  }

  override fun hashCode(): Int {
    return this.toString().hashCode()
  }
}

data class TickData(
    override val currentTick: Double,
    override var entities: List<Actor>,
    val trafficLights: List<TrafficLight>,
    val blocks: List<Block>,
    val weather: WeatherParameters,
    val daytime: Daytime
) : TickDataType<Actor, TickData, Segment> {
  override lateinit var segment: Segment
  val actors: List<Actor>
    get() = entities

  fun actor(actorID: Int): Actor? = actors.firstOrNull { it.id == actorID }
  val egoVehicle: Vehicle
    get() = actors.firstOrNull { it is Vehicle && it.egoVehicle } as Vehicle
  val vehicles: List<Vehicle>
    get() = actors.filterIsInstance<Vehicle>()
  fun vehiclesInBlock(block: Block): List<Vehicle> = vehicles.filter { it.lane.road.block == block }
  val pedestrians: List<Pedestrian>
    get() = actors.filterIsInstance<Pedestrian>()

  override fun toString() = "$currentTick"
  fun clone(): TickData {
    val newTickData = TickData(currentTick, emptyList(), trafficLights, blocks, weather, daytime)
    newTickData.entities = actors.map { it.clone(newTickData) }
    return newTickData
  }
}

data class WeatherParameters(
    val type: WeatherType,
    val cloudiness: Double,
    val precipitation: Double,
    val precipitationDeposits: Double,
    val windIntensity: Double,
    val sunAzimuthAngle: Double,
    val sunAltitudeAngle: Double,
    val fogDensity: Double,
    val fogDistance: Double,
    val wetness: Double,
    val fogFalloff: Double,
    val scatteringIntensity: Double,
    val mieScatteringScale: Double,
    val rayleighScatteringScale: Double,
)

data class Block(
    val fileName: String,
    val id: String,
    var roads: List<Road>,
) {
  override fun toString() = id

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (other is Block) {
      return other.id == this.id
    }
    return false
  }
}

fun List<Block>.getLane(roadId: Int, laneId: Int): Lane? {
  val road = this.flatMap { it.roads }.firstOrNull { pRoad -> pRoad.id == roadId }
  checkNotNull(road)
  return road.lanes.firstOrNull { pLane -> pLane.laneId == laneId }
}

fun List<Block>.lanes(): List<Lane> = this.flatMap { it.roads.flatMap { road -> road.lanes } }

data class Road(var id: Int, val isJunction: Boolean, val block: Block, var lanes: List<Lane>) {
  override fun toString() = "$id"
}

data class Lane(
    val laneId: Int,
    var road: Road,
    val laneType: LaneType,
    val laneWidth: Double,
    val laneLength: Double,
    var predecessorLanes: List<ContactLaneInfo>,
    var successorLanes: List<ContactLaneInfo>,
    var intersectingLanes: List<ContactLaneInfo>,
    /**
     * lanes that this lane must yield to according to the "right before left" rule, i.e., traffic
     * signs or lights are not taken into account.
     *
     * it is always a subset of [intersectingLanes]
     */
    var yieldLanes: List<ContactLaneInfo>,
    val laneMidpoints: List<LaneMidpoint>,
    var speedLimits: List<SpeedLimit>,
    val landmarks: List<Landmark>,
    var contactAreas: List<ContactArea>,
    var trafficLights: List<StaticTrafficLight>,
    var laneDirection: LaneDirection,
) {
  override fun hashCode(): Int {
    return this.toString().hashCode()
  }

  override fun toString() = "Lane(road=${road.id}, id=$laneId)"

  /**
   * Returns the speed limit for the current position in mph or 30mph if no speed sign is available
   */
  fun speedAt(vPos: Double): Double =
      speedLimits
          .firstOrNull { it.fromDistanceFromStart <= vPos && vPos < it.toDistanceFromStart }
          ?.speedLimit
          ?: 30.0

  val turnsLeft
    get() = laneDirection == LaneDirection.LEFT_TURN
  val turnsRight
    get() = laneDirection == LaneDirection.RIGHT_TURN
  val isStraight
    get() = laneDirection == LaneDirection.STRAIGHT
  val hasStopSign
    get() =
        landmarks.any { it.type == LandmarkType.StopSign && it.s > laneLength - 10.0 } ||
            successorLanes.any {
              it.lane.landmarks.any { it.type == LandmarkType.StopSign && it.s < 10.0 }
            }
  val hasYieldSign
    get() =
        landmarks.any { it.type == LandmarkType.YieldSign && it.s > laneLength - 10.0 } ||
            successorLanes.any {
              it.lane.landmarks.any { it.type == LandmarkType.YieldSign && it.s < 10.0 }
            }
  val hasStopOrYieldSign
    get() = hasStopSign || hasYieldSign
  val hasTrafficLight
    get() = trafficLights.isNotEmpty()

  /**
   * retrieve the position (relative to this lane's start) where [otherLane] is crossed. if the
   * lanes do not cross, return -1.0
   */
  fun contactPointPos(otherLane: Lane): Double? {
    // collect all positions where this lane crosses other lane; should yield 0 or 1 results
    val positions =
        contactAreas.mapNotNull {
          // I am lane 1, check if (and where) lane 2 contacts
          if (this == it.lane1 && otherLane == it.lane2) {
            it.lane1StartPos
          }
          // I am lane 2, check if (and where) lane 1 contacts
          else if (this == it.lane2 && otherLane == it.lane1) {
            it.lane2StartPos
          } else {
            // the two lanes have no contact; do not include in mapping
            null
          }
        }
    return if (positions.isNotEmpty()) positions[0] else null
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Lane

    if (laneId != other.laneId) return false
    if (road != other.road) return false

    return true
  }
}

data class SpeedLimit(
    val speedLimit: Double,
    val fromDistanceFromStart: Double,
    val toDistanceFromStart: Double
)

data class ContactArea(
    val id: String,
    val contactLocation: Location,
    val lane1: Lane,
    val lane1StartPos: Double,
    val lane1EndPos: Double,
    val lane2: Lane,
    val lane2StartPos: Double,
    val lane2EndPos: Double
)

data class Landmark(
    val id: Int,
    val name: String,
    val distance: Double,
    val s: Double,
    val country: String,
    val type: LandmarkType,
    val value: Double,
    val unit: String,
    val text: String,
    val location: Location,
    val rotation: Rotation
)

data class LaneMidpoint(
    val distanceToStart: Double,
    val location: Location,
    val rotation: Rotation,
)

data class StaticTrafficLight(
    var id: Int,
    val location: Location,
    val rotation: Rotation,
    val stopLocations: List<Location>,
) {
  override fun toString(): String {
    return "StaticTrafficLight($id)"
  }

  fun getStateInTick(tickData: TickData): TrafficLightState {
    val trafficLight = tickData.trafficLights.firstOrNull { it.relatedOpenDriveId == this.id }
    return trafficLight?.state ?: TrafficLightState.Unknown
  }
}

data class TrafficLight(var id: Int, var state: TrafficLightState, val relatedOpenDriveId: Int) {
  override fun toString(): String {
    return "TrafficLight($id, $state)"
  }
}

data class Rotation(val pitch: Double, val yaw: Double, val roll: Double)

data class Location(val x: Double, val y: Double, val z: Double)

val Lane.uid: String
  get() = "${road.id}_${laneId}"

data class ContactLaneInfo(
    val lane: Lane,
)

sealed class Actor : EntityType<Actor, TickData, Segment> {
  abstract fun clone(newTickData: TickData): Actor
}

val Actor.lane
  get() =
      when (this) {
        is Pedestrian -> lane
        is Vehicle -> lane
      }

data class Pedestrian(
    override val id: Int,
    override val tickData: TickData,
    val positionOnLane: Double,
    val lane: Lane,
) : Actor() {
  override fun clone(newTickData: TickData): Actor =
      Pedestrian(id, newTickData, positionOnLane, lane)

  override fun toString() =
      "Pedestrian(id=$id, tickData=${tickData.currentTick}, positionOnLane=$positionOnLane, lane=${lane.laneId}, road=${lane.road.id})"
}

data class Vehicle(
    override val id: Int,
    override val tickData: TickData,
    var positionOnLane: Double,
    var lane: Lane,
    val typeId: String,
    var egoVehicle: Boolean,
    val location: Location,
    val forwardVector: Vector3D,
    val rotation: Rotation,
    /** The velocity vector in m/s*/
    var velocity: Vector3D,
    /** The acceleration vector in m/s²*/
    var acceleration: Vector3D,
    val angularVelocity: Vector3D,
) : Actor() {
  /** Effective velocity in m/s based on the [velocity] vector */
  val effVelocityInMPerS
    get() = sqrt(velocity.x.pow(2) + velocity.y.pow(2) + velocity.z.pow(2))
  /** Effective velocity in km/h based on [effVelocityInMPerS] */
  val effVelocityInKmPH
    get() = this.effVelocityInMPerS * 3.6
  /** Effective velocity in miles/hour based on [effVelocityInMPerS] */
  val effVelocityInMPH
    get() = this.effVelocityInMPerS * 2.237
  /** Effective acceleration in m/s² based on the [acceleration] vector */
  val effAccelerationInMPerSSquared
    get() = sqrt(acceleration.x.pow(2) + acceleration.y.pow(2) + acceleration.z.pow(2))
  /** SpeedLimit of the road/lane for the current location of this vehicle */
  val applicableSpeedLimit
    get() =
        this.lane.speedLimits.firstOrNull { speedLimit ->
          this.positionOnLane in (speedLimit.fromDistanceFromStart..speedLimit.toDistanceFromStart)
        }

  override fun clone(newTickData: TickData): Actor =
      Vehicle(
          id,
          newTickData,
          positionOnLane,
          lane,
          typeId,
          egoVehicle,
          location,
          forwardVector,
          rotation,
          velocity,
          acceleration,
          angularVelocity)

  override fun toString() =
      "Vehicle(id=$id, tickData=${tickData.currentTick}, positionOnLane=$positionOnLane, lane=${lane.laneId}, road=${lane.road.id})"
}

data class Vector3D(val x: Double, val y: Double, val z: Double)

// region Enums
enum class LaneDirection {
  STRAIGHT,
  LEFT_TURN,
  RIGHT_TURN,
  UNKNOWN,
}

enum class TrafficLightState(val value: Int) {
  Red(0),
  Yellow(1),
  Green(2),
  Off(3),
  Unknown(4);

  companion object {
    private val VALUES = values()
    fun getByValue(value: Int) = VALUES.first { it.value == value }
  }
}

enum class LaneType(val value: Int) {
  Any(-2),
  Bidirectional(512),
  Biking(16),
  Border(64),
  Driving(2),
  Entry(131072),
  Exit(262144),
  Median(1024),
  NONE(1),
  OffRamp(524288),
  OnRamp(1048576),
  Parking(256),
  Rail(65536),
  Restricted(128),
  RoadWorks(16384),
  Shoulder(8),
  Sidewalk(32),
  Special1(2048),
  Special2(4096),
  Special3(8192),
  Stop(4),
  Tram(32768);

  companion object {
    private val VALUES = LaneType.values()
    fun getByValue(value: Int) = VALUES.first { it.value == value }
  }
}

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
  RecomendedSpeed(380),
  RecomendedSpeedEnd(381),
  LightPost(1000001);

  companion object {
    private val VALUES = LandmarkType.values()
    fun getByValue(value: Int) = VALUES.first { it.value == value }
  }
}

enum class WeatherType {
  Clear,
  Cloudy,
  Wet,
  WetCloudy,
  SoftRainy,
  MidRainy,
  HardRainy
}

enum class Daytime {
  Noon,
  Sunset
}
// endregion
