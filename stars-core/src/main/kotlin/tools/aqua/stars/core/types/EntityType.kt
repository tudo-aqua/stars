package tools.aqua.stars.core.types

interface EntityType<E: EntityType<E,T,S>, T: TickDataType<E,T,S>, S: SegmentType<E,T,S>> {
    val id: Int
    val tickData: T
}