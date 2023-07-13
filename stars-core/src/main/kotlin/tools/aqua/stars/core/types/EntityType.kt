package tools.aqua.stars.core.types

interface EntityType {
    val id: Int
    val tickData: TickDataType<*>
}