package tools.aqua.stars.import.carla

import tools.aqua.stars.data.av.*

fun emptyJsonVehicle(): JsonVehicle {
    return JsonVehicle(
        id = 0,
        rotation = emptyJsonRotation(),
        velocity = emptyJsonVector3D(),
        location = emptyJsonLocation(),
        acceleration = emptyJsonVector3D(),
        egoVehicle = false,
        angularVelocity = emptyJsonVector3D(),
        forwardVector = emptyJsonVector3D(),
        typeId = ""
    )
}

fun emptyJsonActorPosition(): JsonActorPosition {
    return JsonActorPosition(
        actor = emptyJsonVehicle(),
        positionOnLane = 0.0,
        laneId = 0,
        roadId = 0
    )
}

fun emptyBlock(id: String = ""): Block {
    return Block(
        id = id,
        roads = listOf(),
        fileName = ""
    )
}

fun emptyRoad(id: Int = 0, isJunction: Boolean = false, block: Block = emptyBlock()): Road {
    return Road(
        id = id,
        block = block,
        isJunction = isJunction,
        lanes = listOf(),
    )
}

fun emptyLane(
    laneId: Int = 1, road: Road = emptyRoad(), laneLength: Double = 0.0, speedLimits: List<SpeedLimit> =
        listOf(), staticTrafficLights: List<StaticTrafficLight> = listOf(), successorLanes: List<ContactLaneInfo> =
        listOf(), landmarks: List<Landmark> = emptyList(), laneDirection: LaneDirection = LaneDirection.UNKNOWN
): Lane {
    return Lane(
        laneId = laneId,
        road = road,
        intersectingLanes = listOf(),
        laneLength = laneLength,
        laneMidpoints = listOf(),
        laneType = LaneType.Driving,
        laneWidth = 0.0,
        predecessorLanes = listOf(),
        contactAreas = listOf(),
        successorLanes = successorLanes,
        speedLimits = speedLimits,
        trafficLights = staticTrafficLights,
        landmarks = landmarks,
        laneDirection = laneDirection,
        yieldLanes = listOf()
    )
}

fun emptyJsonLane(): JsonLane {
    return JsonLane(
        laneId = 0,
        roadId = 0,
        s = 0.0,
        intersectingLanes = listOf(),
        laneLength = 0.0,
        laneMidpoints = listOf(),
        laneType = JsonLaneType.Driving,
        laneWidth = 0.0,
        predecessorLanes = listOf(),
        contactAreas = listOf(),
        successorLanes = listOf(),
        speedLimits = listOf(),
        trafficLights = listOf(),
        landmarks = listOf()
    )
}

fun emptyRotation(): Rotation {
    return Rotation(0.0, 0.0, 0.0)
}

fun emptyJsonRotation(): JsonRotation {
    return JsonRotation(0.0, 0.0, 0.0)
}

fun emptyLocation(): Location {
    return Location(0.0, 0.0, 0.0)
}

fun emptyJsonLocation(): JsonLocation {
    return JsonLocation(0.0, 0.0, 0.0)
}

fun emptyVector3D(): Vector3D {
    return Vector3D(0.0, 0.0, 0.0)
}

fun emptyJsonVector3D(): JsonVector3D {
    return JsonVector3D(0.0, 0.0, 0.0)
}

fun emptyWeatherParameters(weatherType: WeatherType = WeatherType.Clear): WeatherParameters {
    return WeatherParameters(
        type = weatherType,
        cloudiness = 0.0,
        rayleighScatteringScale = 0.0,
        mieScatteringScale = 0.0,
        scatteringIntensity = 0.0,
        fogFalloff = 0.0,
        wetness = 0.0,
        fogDistance = 0.0,
        fogDensity = 0.0,
        sunAltitudeAngle = 0.0,
        sunAzimuthAngle = 0.0,
        windIntensity = 0.0,
        precipitationDeposits = 0.0,
        precipitation = 0.0
    )
}

fun emptyTickData(
    currentTick: Double = 0.0,
    blocks: List<Block> = listOf(),
    trafficLights: List<TrafficLight> = listOf(),
    weatherParameters: WeatherParameters = emptyWeatherParameters(),
    actors: List<Actor> = listOf(),
    daytime: Daytime = Daytime.Sunset
): TickData {
    return TickData(
        currentTick = currentTick,
        entities = actors,
        blocks = blocks,
        trafficLights = trafficLights,
        weather = weatherParameters,
        daytime = daytime
    )
}

fun emptyPedestrian(
    id: Int = 1,
    lane: Lane = emptyLane(),
    positionOnLane: Double = 0.0,
    tickData: TickData = emptyTickData()
): Pedestrian {
    return Pedestrian(
        id = id,
        positionOnLane = positionOnLane,
        tickData = tickData,
        lane = lane
    )
}

fun emptyVehicle(
    egoVehicle: Boolean = false,
    id: Int = 0,
    lane: Lane = emptyLane(),
    positionOnLane: Double = 0.0,
    tickData: TickData = emptyTickData(),
    effVelocityMPH: Double = 0.0
): Vehicle {
    return Vehicle(
        id = id,
        rotation = emptyRotation(),
        location = emptyLocation(),
        egoVehicle = egoVehicle,
        acceleration = emptyVector3D(),
        angularVelocity = emptyVector3D(),
        effVelocityInMPerS = effVelocityMPH / 2.237,
        forwardVector = emptyVector3D(),
        lane = lane,
        positionOnLane = positionOnLane,
        tickData = tickData,
        typeId = "",
        velocity = emptyVector3D()
    )
}

fun emptyJsonStaticTrafficLight(): JsonStaticTrafficLight {
    return JsonStaticTrafficLight(
        id = 0,
        rotation = emptyJsonRotation(),
        location = emptyJsonLocation(),
        stopLocations = listOf(),
        positionDistance = 0.0f
    )
}

fun emptyJsonTrafficLight(): JsonTrafficLight {
    return JsonTrafficLight(
        id = 0,
        state = 0,
        typeId = "",
        relatedOpenDriveId = 0,
        rotation = emptyJsonRotation(),
        location = emptyJsonLocation()
    )
}

fun emptyStaticTrafficLight(): StaticTrafficLight {
    return StaticTrafficLight(
        id = 0,
        rotation = emptyRotation(),
        location = emptyLocation(),
        stopLocations = listOf()
    )
}

fun emptyTrafficLight(id: Int = 0, relatedOpenDriveId: Int = 0, state: TrafficLightState = TrafficLightState.Unknown):
        TrafficLight {
    return TrafficLight(
        id = id,
        state = state,
        relatedOpenDriveId = relatedOpenDriveId
    )
}

fun emptyJsonLandmark(): JsonLandmark {
    return JsonLandmark(
        id = 0,
        roadId = 0,
        location = JsonLocation(0.0, 0.0, 0.0),
        text = "",
        unit = "",
        value = 0.0,
        type = JsonLandmarkType.LightPost,
        country = "",
        distance = 0.0,
        rotation = JsonRotation(0.0, 0.0, 0.0),
        hOffset = 0.0,
        height = 0.0,
        isDynamic = false,
        name = "",
        orientation = JsonLandmarkOrientation.Both,
        pitch = 0.0,
        roll = 0.0,
        s = 0.0,
        subType = "",
        width = 0.0,
        zOffset = 0.0
    )
}