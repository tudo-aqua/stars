package tools.aqua.stars.core.types

interface SegmentType<E: EntityType<E,T,S>, T:TickDataType<E,T,S>, S: SegmentType<E,T,S>> {

    val tickData: List<T>
    val ticks: Map<Double, T>
    val tickIDs: List<Double>
    val mapName: String //TODO: rename to segmentIdentifier
    val firstTickId: Double
    val egoVehicleId: Int //TODO: rename to primaryEntityId

}