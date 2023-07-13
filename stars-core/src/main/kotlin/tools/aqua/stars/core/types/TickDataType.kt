package tools.aqua.stars.core.types

interface TickDataType<E: EntityType<E,T,S>, T: TickDataType<E,T,S>, S: SegmentType<E,T,S>> {

    val currentTick: Double
    var entities: List<E>
    var segment: S

    fun entity(entityID: Int): E? = entities.firstOrNull { it.id == entityID }

}