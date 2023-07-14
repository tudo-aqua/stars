@file:Suppress("unused")

package tools.aqua.stars.import.carla

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt
import tools.aqua.stars.data.av.*

@Serializable
data class JsonTickData(
    val current_tick: Double,
    val actor_positions: List<JsonActorPosition>,
    val weather_parameters: JSonDataWeatherParameters
)

@Serializable
data class JSonDataWeatherParameters(
    val type: JsonDataWeatherParametersType,
    val cloudiness: Double,
    val precipitation: Double,
    val precipitation_deposits: Double,
    val wind_intensity: Double,
    val sun_azimuth_angle: Double,
    val sun_altitude_angle: Double,
    val fog_density: Double,
    val fog_distance: Double,
    val wetness: Double,
    val fog_falloff: Double,
    val scattering_intensity: Double,
    val mie_scattering_scale: Double,
    val rayleigh_scattering_scale: Double,
) {
    fun toWeatherParameters(): WeatherParameters {
        val weatherType = this.type.toWeatherType()
        return WeatherParameters(
            type = weatherType,
            cloudiness = this.cloudiness,
            precipitation = this.precipitation,
            precipitationDeposits = this.precipitation_deposits,
            windIntensity = this.wind_intensity,
            sunAzimuthAngle = this.sun_azimuth_angle,
            sunAltitudeAngle = this.sun_altitude_angle,
            fogDensity = this.fog_density,
            fogDistance = this.fog_distance,
            wetness = this.wetness,
            fogFalloff = this.fog_falloff,
            scatteringIntensity = this.scattering_intensity,
            mieScatteringScale = this.mie_scattering_scale,
            rayleighScatteringScale = this.rayleigh_scattering_scale
        )
    }
}


@Serializable
data class JsonBlock(
    val id: String,
    val roads: List<JsonRoad>,
)

@Serializable
data class JsonRoad(
    @SerialName("road_id") val roadId: Int,
    @SerialName("is_junction") val isJunction: Boolean,
    val lanes: List<JsonLane>,
)

@Serializable
data class JsonLane(
    val road_id: Int,
    val lane_id: Int,
    val lane_type: JsonLaneType,
    val lane_width: Double,
    var lane_length: Double,
    val s: Double,
    val predecessor_lanes: List<JsonContactLaneInfo>,
    val successor_lanes: List<JsonContactLaneInfo>,
    val intersecting_lanes: List<JsonContactLaneInfo>,
    val lane_midpoints: List<JsonLaneMidpoint>,
    val speed_limits: List<JsonSpeedLimit>,
    var landmarks: List<JsonLandmark>,
    val contact_areas: List<JsonContactArea>,
    var traffic_lights: List<JsonStaticTrafficLight>,
)

@Serializable
data class JsonLaneMidpoint(
    val lane_id: Int,
    val road_id: Int,
    val distance_to_start: Double,
    val location: JsonLocation,
    val rotation: JsonRotation,
) {
    fun toLaneMidpoint(): LaneMidpoint {
        return LaneMidpoint(
            distanceToStart = this.distance_to_start,
            location = this.location.toLocation(),
            rotation = this.rotation.toRotation()
        )
    }
}

@Serializable
data class JsonActorPosition(
    @SerialName("position_on_lane") val positionOnLane: Double,
    @SerialName("lane_id") var laneId: Int,
    @SerialName("road_id") var roadId: Int,
    var actor: JsonActor
)

@Serializable
data class JsonSpeedLimit(
    @SerialName("speed_limit") val speedLimit: Double,
    @SerialName("from_distance") val fromDistanceFromStart: Double,
    @SerialName("to_distance") val toDistanceFromStart: Double,
)

@Serializable
data class JsonLocation(
    val x: Double,
    val y: Double,
    val z: Double
) {
    override fun toString(): String = "($x,$y,$z)"

    fun toLocation(): Location {
        return Location(this.x, this.y, this.z)
    }
}

@Serializable
data class JsonVector3D(
    val x: Double,
    val y: Double,
    val z: Double
) {
    fun toVector3D(): Vector3D {
        return Vector3D(this.x, this.y, this.z)
    }
}

@Serializable
data class JsonRotation(
    val pitch: Double,
    val yaw: Double,
    val roll: Double
) {
    fun toRotation(): Rotation {
        return Rotation(this.pitch, this.yaw, this.roll)
    }
}

@Serializable
data class JsonContactArea(
    val id: String,
    val contact_location: JsonLocation,
    val lane_1_road_id: Int,
    val lane_1_id: Int,
    val lane_1_start_pos: Double,
    val lane_1_end_pos: Double,
    val lane_2_road_id: Int,
    val lane_2_id: Int,
    val lane_2_start_pos: Double,
    val lane_2_end_pos: Double,
)

@Serializable
data class JsonLandmark(
    var id: Int,
    val road_id: Int,
    val name: String,
    val distance: Double,
    var s: Double,
    val is_dynamic: Boolean,
    val orientation: JsonLandmarkOrientation,
    val z_offset: Double,
    val country: String,
    var type: JsonLandmarkType,
    val sub_type: String,
    var value: Double,
    var unit: String,
    val height: Double,
    val width: Double,
    val text: String,
    val h_offset: Double,
    val pitch: Double,
    val roll: Double,
    val location: JsonLocation,
    val rotation: JsonRotation
) {
    fun toLandmark(): Landmark {
        return Landmark(
            id = this.id,
            name = this.name,
            distance = this.distance,
            s = this.s,
            country = this.country,
            type = LandmarkType.getByValue(this.type.value),
            value = this.value,
            unit = this.unit,
            text = this.text,
            location = this.location.toLocation(),
            rotation = this.rotation.toRotation()
        )
    }
}

@Serializable
data class JsonStaticTrafficLight(
    @SerialName("open_drive_id") var id: Int,
    @SerialName("position_distance") val positionDistance: Float,
    var location: JsonLocation,
    var rotation: JsonRotation,
    var stop_locations: List<JsonLocation>,
) {
    fun toStaticTrafficLight(): StaticTrafficLight {
        return StaticTrafficLight(
            id = this.id,
            location = this.location.toLocation(),
            rotation = this.rotation.toRotation(),
            stopLocations = this.stop_locations.map { it.toLocation() }
        )
    }
}

@Serializable
@SerialName("Actor")
sealed class JsonActor {
    abstract val id: Int
    abstract var location: JsonLocation
    abstract var rotation: JsonRotation
}

@Serializable
@SerialName("TrafficLight")
data class JsonTrafficLight(
    @SerialName("id") override var id: Int,
    @SerialName("type_id") val typeId: String,
    @SerialName("state") var state: Int,
    override var location: JsonLocation,
    override var rotation: JsonRotation,
    @SerialName("related_open_drive_id") val relatedOpenDriveId: Int,
) : JsonActor() {
    fun toTrafficLight(): TrafficLight {
        return TrafficLight(
            id = this.id,
            state = TrafficLightState.getByValue(this.state),
            relatedOpenDriveId = this.relatedOpenDriveId
        )
    }
}

@Serializable
@SerialName("Pedestrian")
data class JsonPedestrian(
    override val id: Int,
    @SerialName("type_id") val typeId: String,
    override var location: JsonLocation,
    override var rotation: JsonRotation,
) : JsonActor()

@Serializable
@SerialName("TrafficSign")
data class JsonTrafficSign(
    override val id: Int,
    @SerialName("traffic_sign_type") val trafficSignType: JsonTrafficSignType,
    @SerialName("speed_limit") val speedLimit: Double?,
    @SerialName("type_id") val typeId: String,
    override var location: JsonLocation,
    override var rotation: JsonRotation,
) : JsonActor()

@Serializable
@SerialName("Vehicle")
data class JsonVehicle(
    override val id: Int,
    override var location: JsonLocation,
    override var rotation: JsonRotation,
    val type_id: String,
    var ego_vehicle: Boolean,
    val forward_vector: JsonVector3D,
    val velocity: JsonVector3D,
    val acceleration: JsonVector3D,
    val angular_velocity: JsonVector3D
) : JsonActor()

@Serializable
data class JsonContactLaneInfo(
    val road_id: Int,
    val lane_id: Int,
)

val JsonVehicle.effVelocity get() = sqrt(this.velocity.x.pow(2) + this.velocity.y.pow(2) + this.velocity.z.pow(2))

@Serializable
data class JsonCoordinates(
    val x: Double,
    val y: Double,
    val z: Double,
)

@Serializable
enum class JsonLandmarkOrientation(val value: Int) {
    @SerialName("0")
    Positive(0),

    @SerialName("1")
    Negative(1),

    @SerialName("2")
    Both(2),
}

@Serializable
enum class JsonDataWeatherParametersType(val value: Int) {
    @SerialName("0")
    Default(0),

    @SerialName("1")
    ClearNoon(1),

    @SerialName("2")
    CloudyNoon(2),

    @SerialName("3")
    WetNoon(3),

    @SerialName("4")
    WetCloudyNoon(4),

    @SerialName("5")
    SoftRainNoon(5),

    @SerialName("6")
    MidRainNoon(6),

    @SerialName("7")
    HardRainNoon(7),

    @SerialName("8")
    ClearSunset(8),

    @SerialName("9")
    CloudySunset(9),

    @SerialName("10")
    WetSunset(10),

    @SerialName("11")
    WetCloudySunset(11),

    @SerialName("12")
    SoftRainSunset(12),

    @SerialName("13")
    MidRainSunset(13),

    @SerialName("14")
    HardRainSunset(14);


    fun toWeatherType(): WeatherType {
        return when (this) {
            ClearNoon, ClearSunset -> WeatherType.Clear
            CloudyNoon, CloudySunset -> WeatherType.Cloudy
            WetNoon, WetSunset -> WeatherType.Wet
            WetCloudyNoon, WetCloudySunset -> WeatherType.WetCloudy
            SoftRainNoon, SoftRainSunset -> WeatherType.SoftRainy
            MidRainNoon, MidRainSunset -> WeatherType.MidRainy
            HardRainNoon, HardRainSunset -> WeatherType.HardRainy
            else -> {
                WeatherType.Clear
            }
        }
    }

    fun toDaytime(): Daytime {
        return when (this) {
            HardRainNoon, WetNoon,
            MidRainNoon, SoftRainNoon,
            CloudyNoon, WetCloudyNoon,
            ClearNoon -> Daytime.Noon

            HardRainSunset, SoftRainSunset,
            MidRainSunset, WetSunset,
            WetCloudySunset, CloudySunset,
            ClearSunset -> Daytime.Sunset

            else -> Daytime.Sunset
        }
    }
}

@Serializable
enum class JsonLaneType(val value: Int) {
    @SerialName("-2")
    Any(-2),

    @SerialName("512")
    Bidirectional(512),

    @SerialName("16")
    Biking(16),

    @SerialName("64")
    Border(64),

    @SerialName("2")
    Driving(2),

    @SerialName("131072")
    Entry(131072),

    @SerialName("262144")
    Exit(262144),

    @SerialName("1024")
    Median(1024),

    @SerialName("1")
    NONE(1),

    @SerialName("524288")
    OffRamp(524288),

    @SerialName("1048576")
    OnRamp(1048576),

    @SerialName("256")
    Parking(256),

    @SerialName("65536")
    Rail(65536),

    @SerialName("128")
    Restricted(128),

    @SerialName("16384")
    RoadWorks(16384),

    @SerialName("8")
    Shoulder(8),

    @SerialName("32")
    Sidewalk(32),

    @SerialName("2048")
    Special1(2048),

    @SerialName("4096")
    Special2(4096),

    @SerialName("8192")
    Special3(8192),

    @SerialName("4")
    Stop(4),

    @SerialName("32768")
    Tram(32768),
}

@Serializable
enum class JsonLandmarkType(val value: Int) {
    @SerialName("101")
    Danger(101),

    @SerialName("121")
    LanesMerging(121),

    @SerialName("0")
    CautionPedestrian(133),

    @SerialName("138")
    CautionBicycle(138),

    @SerialName("150")
    LevelCrossing(150),

    @SerialName("206")
    StopSign(206),

    @SerialName("205")
    YieldSign(205),

    @SerialName("209")
    MandatoryTurnDirection(209),

    @SerialName("211")
    MandatoryLeftRightDirection(211),

    @SerialName("214")
    TwoChoiceTurnDirection(214),

    @SerialName("215")
    Roundabout(215),

    @SerialName("222")
    PassRightLeft(222),

    @SerialName("250")
    AccessForbidden(250),

    @SerialName("251")
    AccessForbiddenMotorvehicles(251),

    @SerialName("253")
    AccessForbiddenTrucks(253),

    @SerialName("254")
    AccessForbiddenBicycle(254),

    @SerialName("263")
    AccessForbiddenWeight(263),

    @SerialName("264")
    AccessForbiddenWidth(264),

    @SerialName("265")
    AccessForbiddenHeight(265),

    @SerialName("267")
    AccessForbiddenWrongDirection(267),

    @SerialName("272")
    ForbiddenUTurn(272),

    @SerialName("274")
    MaximumSpeed(274),

    @SerialName("276")
    ForbiddenOvertakingMotorvehicles(276),

    @SerialName("277")
    ForbiddenOvertakingTrucks(277),

    @SerialName("283")
    AbsoluteNoStop(283),

    @SerialName("286")
    RestrictedStop(286),

    @SerialName("301")
    HasWayNextIntersection(301),

    @SerialName("306")
    PriorityWay(306),

    @SerialName("307")
    PriorityWayEnd(307),

    @SerialName("310")
    CityBegin(310),

    @SerialName("311")
    CityEnd(311),

    @SerialName("330")
    Highway(330),

    @SerialName("357")
    DeadEnd(357),

    @SerialName("380")
    RecommendedSpeed(380),

    @SerialName("381")
    RecommendedSpeedEnd(381),

    @SerialName("1000001")
    LightPost(1000001),
}

@Serializable
enum class JsonTrafficSignType(val value: Int) {
    @SerialName("0")
    INVALID(0),

    @SerialName("1")
    SUPPLEMENT_ARROW_APPLIES_LEFT(1),

    @SerialName("2")
    SUPPLEMENT_ARROW_APPLIES_RIGHT(2),

    @SerialName("3")
    SUPPLEMENT_ARROW_APPLIES_LEFT_RIGHT(3),

    @SerialName("4")
    SUPPLEMENT_ARROW_APPLIES_UP_DOWN(4),

    @SerialName("5")
    SUPPLEMENT_ARROW_APPLIES_LEFT_RIGHT_BICYCLE(5),

    @SerialName("6")
    SUPPLEMENT_ARROW_APPLIES_UP_DOWN_BICYCLE(6),

    @SerialName("7")
    SUPPLEMENT_APPLIES_NEXT_N_KM_TIME(7),

    @SerialName("8")
    SUPPLEMENT_ENDS(8),

    @SerialName("9")
    SUPPLEMENT_RESIDENTS_ALLOWED(9),

    @SerialName("10")
    SUPPLEMENT_BICYCLE_ALLOWED(10),

    @SerialName("11")
    SUPPLEMENT_MOPED_ALLOWED(11),

    @SerialName("12")
    SUPPLEMENT_TRAM_ALLOWED(12),

    @SerialName("13")
    SUPPLEMENT_FORESTAL_ALLOWED(13),

    @SerialName("14")
    SUPPLEMENT_CONSTRUCTION_VEHICLE_ALLOWED(14),

    @SerialName("15")
    SUPPLEMENT_ENVIRONMENT_ZONE_YELLOW_GREEN(15),

    @SerialName("16")
    SUPPLEMENT_RAILWAY_ONLY(16),

    @SerialName("17")
    SUPPLEMENT_APPLIES_FOR_WEIGHT(17),

    @SerialName("18")
    DANGER(18),

    @SerialName("19")
    LANES_MERGING(19),

    @SerialName("20")
    CAUTION_PEDESTRIAN(20),

    @SerialName("21")
    CAUTION_CHILDREN(21),

    @SerialName("22")
    CAUTION_BICYCLE(22),

    @SerialName("23")
    CAUTION_ANIMALS(23),

    @SerialName("24")
    CAUTION_RAIL_CROSSING_WITH_BARRIER(24),

    @SerialName("25")
    CAUTION_RAIL_CROSSING(25),

    @SerialName("26")
    YIELD_TRAIN(26),

    @SerialName("27")
    YIELD(27),

    @SerialName("28")
    STOP(28),

    @SerialName("29")
    REQUIRED_RIGHT_TURN(29),

    @SerialName("30")
    REQUIRED_LEFT_TURN(30),

    @SerialName("31")
    REQUIRED_STRAIGHT(31),

    @SerialName("32")
    REQUIRED_STRAIGHT_OR_RIGHT_TURN(32),

    @SerialName("33")
    REQUIRED_STRAIGHT_OR_LEFT_TURN(33),

    @SerialName("34")
    ROUNDABOUT(34),

    @SerialName("35")
    PASS_RIGHT(35),

    @SerialName("36")
    PASS_LEFT(36),

    @SerialName("37")
    BICYCLE_PATH(37),

    @SerialName("38")
    FOOTWALK(38),

    @SerialName("39")
    FOOTWALK_BICYCLE_SHARED(39),

    @SerialName("40")
    FOOTWALK_BICYCLE_SEP_RIGHT(40),

    @SerialName("41")
    FOOTWALK_BICYCLE_SEP_LEFT(41),

    @SerialName("42")
    PEDESTRIAN_AREA_BEGIN(42),

    @SerialName("43")
    ACCESS_FORBIDDEN(43),

    @SerialName("44")
    ACCESS_FORBIDDEN_TRUCKS(44),

    @SerialName("45")
    ACCESS_FORBIDDEN_BICYCLE(45),

    @SerialName("46")
    ACCESS_FORBIDDEN_MOTORVEHICLES(46),

    @SerialName("47")
    ACCESS_FORBIDDEN_WEIGHT(47),

    @SerialName("48")
    ACCESS_FORBIDDEN_WIDTH(48),

    @SerialName("49")
    ACCESS_FORBIDDEN_HEIGHT(49),

    @SerialName("50")
    ACCESS_FORBIDDEN_WRONG_DIR(50),

    @SerialName("51")
    ENVIRONMENT_ZONE_BEGIN(51),

    @SerialName("52")
    ENVIRONMENT_ZONE_END(52),

    @SerialName("53")
    MAX_SPEED(53),

    @SerialName("54")
    SPEED_ZONE_30_BEGIN(54),

    @SerialName("55")
    SPEED_ZONE_30_END(55),

    @SerialName("56")
    HAS_WAY_NEXT_INTERSECTION(56),

    @SerialName("57")
    PRIORITY_WAY(57),

    @SerialName("58")
    CITY_BEGIN(58),

    @SerialName("59")
    CITY_END(59),

    @SerialName("60")
    MOTORWAY_BEGIN(60),

    @SerialName("61")
    MOTORWAY_END(61),

    @SerialName("62")
    MOTORVEHICLE_BEGIN(62),

    @SerialName("63")
    MOTORVEHICLE_END(63),

    @SerialName("64")
    INFO_MOTORWAY_INFO(64),

    @SerialName("65")
    CUL_DE_SAC(65),

    @SerialName("66")
    CUL_DE_SAC_EXCEPT_PED_BICYCLE(66),

    @SerialName("67")
    INFO_NUMBER_OF_AUTOBAHN(67),

    @SerialName("68")
    DIRECTION_TURN_TO_AUTOBAHN(68),

    @SerialName("69")
    DIRECTION_TURN_TO_LOCAL(69),

    @SerialName("70")
    DESTINATION_BOARD(70),

    @SerialName("71")
    FREE_TEXT(71),

    @SerialName("72")
    UNKNOWN(72),
}
