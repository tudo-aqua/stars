@file:Suppress("UNCHECKED_CAST", "unused")

package tools.aqua.stars.logic.kcmftbl

import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType
import kotlin.reflect.cast

// region CMFTBL Previous

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>previous(
    tickData: T,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    eval: (T) -> Boolean
): Boolean {
    val segment = tickData.segment
    val nowIndex = segment.tickData.indexOf(tickData)
    if (nowIndex - 1 < 0)
        return false
    val previousTick = segment.tickData[nowIndex - 1]

    return if (previousTick.currentTick in (tickData.currentTick - interval.second)..(tickData.currentTick - interval.first))
        eval(previousTick)
    else
        false
}

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>> previous(
    entity: E1,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    eval: (entity: E1) -> Boolean
): Boolean =
    previous(entity.tickData, interval,
        eval = { td ->
            val previousEntity = td.entity(entity.id)
            if (entity::class.isInstance(previousEntity))
                eval(entity::class.cast(previousEntity))
            else
                false
        })

// endregion

// region CMFTBL Next

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>next(
    tickData: T,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    eval: (T) -> Boolean
): Boolean {
    val segment = tickData.segment
    val nowIndex = segment.tickData.indexOf(tickData)
    if (segment.tickData.lastIndex < nowIndex + 1)
        return false
    val nextTick = segment.tickData[nowIndex + 1]

    return if (nextTick.currentTick in (tickData.currentTick + interval.first)..(tickData.currentTick + interval.second))
        eval(nextTick)
    else
        false
}

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>next(
    entity: E1,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    eval: (E1) -> Boolean
): Boolean =
    next(entity.tickData, interval,
        eval = { td ->
            val nextEntity = td.entity(entity.id)
            if (entity::class.isInstance(nextEntity))
                eval(entity::class.cast(nextEntity))
            else
                false
        })

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>next(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    eval: (E1, E2) -> Boolean
): Boolean {
    require(entity1.tickData == entity2.tickData) { "the two entities provided as argument are not from same tick" }
    return next(entity1.tickData, interval,
        eval = { td ->
            val futureEntity1 = td.entity(entity1.id)
            val futureEntity2 = td.entity(entity2.id)
            if (entity1::class.isInstance(futureEntity1) && entity2::class.isInstance(futureEntity2))
                eval(entity1::class.cast(futureEntity1), entity2::class.cast(futureEntity2))
            else
                false
        })
}

// endregion

//region CMFTBL Since

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>since(
    tickData: T,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (T) -> Boolean,
    phi2: (T) -> Boolean
): Boolean {

    val segment = tickData.segment
    val now = tickData.currentTick
    val nowIndex = segment.tickData.indexOf(tickData)

    for (searchIndex in nowIndex downTo 0) {
        val searchTickData = segment.tickData[searchIndex]

        if (phi2(searchTickData) && searchTickData.currentTick in now - interval.second..now - interval.first)
            return true
        else if (!phi1(searchTickData))
            return false
    }
    return false
}

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>since(
    entity: E1,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (E1) -> Boolean,
    phi2: (E1) -> Boolean
): Boolean =
    since(entity.tickData, interval,
        phi1 = { td ->
            val pastEntity = td.entity(entity.id)
            if (entity::class.isInstance(pastEntity))
                phi1(entity::class.cast(pastEntity))
            else
                false
        },
        phi2 = { td ->
            val pastEntity = td.entity(entity.id)
            if (entity::class.isInstance(pastEntity))
                phi2(entity::class.cast(pastEntity))
            else
                false
        }
    )

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>since(
    entity1: E1, entity2: E2,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (E1, E2) -> Boolean,
    phi2: (E1, E2) -> Boolean
): Boolean {

    require(entity1.tickData == entity2.tickData) { "the two entities provided as argument are not from same tick" }
    return since(entity1.tickData, interval,
        phi1 = { td ->
            val pastEntity1 = td.entity(entity1.id)
            val pastEntity2 = td.entity(entity2.id)
            if (entity1::class.isInstance(pastEntity1) && entity2::class.isInstance(pastEntity2))
                phi1(entity1::class.cast(pastEntity1), entity2::class.cast(pastEntity2))
            else
                false
        },
        phi2 = { td ->
            val pastEntity1 = td.entity(entity1.id)
            val pastEntity2 = td.entity(entity2.id)
            if (entity1::class.isInstance(pastEntity1) && entity2::class.isInstance(pastEntity2))
                phi2(entity1::class.cast(pastEntity1), entity2::class.cast(pastEntity2))
            else
                false
        }
    )
}

//endregion

// region CMFTBL Once

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>once(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1, E2) -> Boolean
): Boolean =
    since(entity1, entity2, interval, phi1 = { _, _ -> true }, phi2 = { a1, a2 -> eval(a1, a2) })

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>once(
    entity: E1,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1) -> Boolean
): Boolean =
    since(entity, interval, phi1 = { _ -> true }, phi2 = { a -> eval(a) })

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>once(
    tickData: T,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (T) -> Boolean
): Boolean =
    since(tickData, interval, phi1 = { _ -> true }, phi2 = { td -> eval(td) })

// endregion

// region CMFTBL Historically

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>historically(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1, E2) -> Boolean
): Boolean =
    !once(entity1, entity2, interval, eval = { a1, a2 -> !eval(a1, a2) })

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>historically(
    entity: E1,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1) -> Boolean
): Boolean =
    !once(entity, interval, eval = { a -> !eval(a) })

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>historically(
    tickData: T,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (T) -> Boolean
): Boolean =
    !once(tickData, interval, eval = { td -> !eval(td) })

// endregion

//region CMFTBL Until

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>until(
    tickData: T,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (T) -> Boolean,
    phi2: (T) -> Boolean
): Boolean {

    val segment = tickData.segment
    val now = tickData.currentTick
    val nowIndex = segment.tickData.indexOf(tickData)

    for (searchIndex in nowIndex..segment.tickData.lastIndex) {
        val searchTickData = segment.tickData[searchIndex]

        if (phi2(searchTickData) && searchTickData.currentTick in now + interval.first..now + interval.second)
            return true
        else if (!phi1(searchTickData))
            return false
    }
    return false
}

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>until(
    entity: E1,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (E1) -> Boolean,
    phi2: (E1) -> Boolean
): Boolean =
    until(entity.tickData, interval,
        phi1 = { td ->
            val futureEntity = td.entity(entity.id)
            if (entity::class.isInstance(futureEntity))
                phi1(entity::class.cast(futureEntity))
            else
                false
        },
        phi2 = { td ->
            val futureEntity = td.entity(entity.id)
            if (entity::class.isInstance(futureEntity))
                phi2(entity::class.cast(futureEntity))
            else
                false
        }
    )

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>until(
    entity1: E1, entity2: E2,
    interval: Pair<Double, Double> = 0.0 to Double.MAX_VALUE,
    phi1: (E1, E2) -> Boolean,
    phi2: (E1, E2) -> Boolean
): Boolean {

    require(entity1.tickData == entity2.tickData) { "the two entities provided as argument are not from same tick" }
    return until(entity1.tickData, interval,
        phi1 = { td ->
            val futureEntity1 = td.entity(entity1.id)
            val futureEntity2 = td.entity(entity2.id)
            if (entity1::class.isInstance(futureEntity1) && entity2::class.isInstance(futureEntity2))
                phi1(entity1::class.cast(futureEntity1), entity2::class.cast(futureEntity2))
            else
                false
        },
        phi2 = { td ->
            val futureEntity1 = td.entity(entity1.id)
            val futureEntity2 = td.entity(entity2.id)
            if (entity1::class.isInstance(futureEntity1) && entity2::class.isInstance(futureEntity2))
                phi2(entity1::class.cast(futureEntity1), entity2::class.cast(futureEntity2))
            else
                false
        }
    )
}

// endregion

// region CMFTBL Eventually

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>eventually(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1, E2) -> Boolean
): Boolean =
    until(entity1, entity2, interval, phi1 = { _, _ -> true }, phi2 = { a1, a2 -> eval(a1, a2) })

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>eventually(
    entity: E1,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1) -> Boolean
): Boolean =
    until(entity, interval, phi1 = { _ -> true }, phi2 = { a -> eval(a) })

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>eventually(
    tickData: T,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (T) -> Boolean
): Boolean =
    until(tickData, interval, phi1 = { _ -> true }, phi2 = { td -> eval(td) })

// endregion

// region CMFTBL Globally

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>globally(
    entity1: E1,
    entity2: E2,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1, E2) -> Boolean
): Boolean =
    !eventually(entity1, entity2, interval, eval = { a1, a2 -> !eval(a1, a2) })

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>globally(
    entity: E1,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1) -> Boolean
): Boolean =
    !eventually(entity, interval, eval = { a -> !eval(a) })

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>globally(
    tickData: T,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (T) -> Boolean
): Boolean =
    !eventually(tickData, interval, eval = { td -> !eval(td) })

// endregion

// region CMFTBL Min Prevalence

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>minPrevalence(
    tickData: T,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (T) -> Boolean
): Boolean {
    val segment = tickData.segment
    val now = tickData.currentTick
    val nowIndex = segment.tickData.indexOf(tickData)

    val tickDataLength = segment.tickData.takeLast(segment.tickData.size - nowIndex).size

    val trueCount = (nowIndex..segment.tickData.lastIndex).map { currentIndex ->
        val currentTickData = segment.tickData[currentIndex]
        if (currentTickData.currentTick < (now + interval.first) || currentTickData.currentTick > (now + interval.second))
            true
        else
            eval(currentTickData)
    }.count { it }
    return trueCount >= tickDataLength * percentage
}

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>> minPrevalence(
    entity: E1,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1) -> Boolean
): Boolean {
    val segment = entity.tickData.segment
    val now = entity.tickData.currentTick
    val nowIndex = segment.tickData.indexOf(entity.tickData)

    val tickDataLength = segment.tickData.takeLast(segment.tickData.size - nowIndex).size

    val trueCount = (nowIndex..segment.tickData.lastIndex).map { currentIndex ->
        val currentTickData = segment.tickData[currentIndex]
        if (currentTickData.currentTick < (now + interval.first) || currentTickData.currentTick > (now + interval.second))
            true
        else
            currentTickData.entities.firstOrNull { it.id == entity.id }?.let { ac ->
                eval(ac as E1)
            } ?: false
    }.count { it }
    return trueCount >= tickDataLength * percentage
}

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>minPrevalence(
    entity1: E1,
    entity2: E2,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1, E2) -> Boolean
): Boolean {
    require(entity1.tickData == entity2.tickData) { "the two actors provided as argument are not from same tick" }

    val segment = entity1.tickData.segment
    val now = entity1.tickData.currentTick
    val nowIndex = segment.tickData.indexOf(entity1.tickData)

    val tickDataLength = segment.tickData.takeLast(segment.tickData.size - nowIndex).size

    val trueCount = (nowIndex..segment.tickData.lastIndex).map { currentIndex ->
        val currentTickData = segment.tickData[currentIndex]
        if (currentTickData.currentTick < (now + interval.first) || currentTickData.currentTick > (now + interval.second))
            true
        else
            currentTickData.entities.firstOrNull { it.id == entity1.id }?.let { nextEntity1 ->
                currentTickData.entities.firstOrNull { it.id == entity2.id }?.let { nextEntity2 ->
                    eval(nextEntity1 as E1, nextEntity2 as E2)
                }
            } ?: false
    }.count { it }
    return trueCount >= tickDataLength * percentage
}

// endregion

// region CMFTBL Max Prevalence

fun <E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>maxPrevalence(
    tickData: T,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (T) -> Boolean
): Boolean = minPrevalence(tickData, 1 - percentage, interval, eval = { td -> !eval(td) })

fun <E1:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>maxPrevalence(
    entity: E1,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1) -> Boolean
): Boolean = minPrevalence(entity, 1 - percentage, interval, eval = { a -> !eval(a) })

fun <E1:E, E2:E, E:EntityType<E,T,S>, T:TickDataType<E,T,S>, S:SegmentType<E,T,S>>maxPrevalence(
    entity1: E1,
    entity2: E2,
    percentage: Double,
    interval: Pair<Double, Double> = Pair(0.0, Double.MAX_VALUE),
    eval: (E1,E2) -> Boolean
): Boolean = minPrevalence(entity1, entity2, 1 - percentage, interval, eval = { a1, a2 -> !eval(a1, a2) })

// endregion