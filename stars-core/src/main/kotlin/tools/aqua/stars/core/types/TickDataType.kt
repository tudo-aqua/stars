package tools.aqua.stars.core.types

interface TickDataType<E: EntityType> {

    val currentTick: Double
    var entities: List<E>

}