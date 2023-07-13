package tools.aqua.stars.core.types

interface SegmentType<E: EntityType, T:TickDataType<E>> {

    val tickData: List<T>
    val ticks: Map<Double, T>
    val tickIDs: List<Double>
    val mapName: String //TODO: rename to segmentIdentifier
    val firstTickId: Double
    val egoVehicleId: Int //TODO: rename to primaryEntityId

}