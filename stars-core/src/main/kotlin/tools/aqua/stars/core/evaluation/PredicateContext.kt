package tools.aqua.stars.core.evaluation

import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType
import kotlin.reflect.cast

class PredicateContext<E: EntityType<E,T,S>, T:TickDataType<E,T,S>,S:SegmentType<E,T,S>>(val segment: S) {

    var egoID = segment.egoVehicleId // TODO: rename to primaryEntityId

    /** cache for all entity IDs */
    private val entityIdsCache = mutableListOf<Int>()

    /**
     * all entity IDs of the current context state
     */
    val entityIds: List<Int> get() {
        if (entityIdsCache.isEmpty()) {
            entityIdsCache.addAll(
                segment.tickData.flatMap { tickData ->
                    tickData.entities.map { it.id }
                }.distinct()
            )
        }
        return entityIdsCache
    }

    /**
     * all tick IDs of the current context state
     */
    val tids get() = segment.tickIDs

    // region evaluation for TickPredicateZero

    private val nullaryPredicateCache: MutableMap<NullaryPredicate<E, T, S>, List<Double>> = mutableMapOf()

    // TODO: NullaryPredicate should have `holds` method like the others; with timestamp?

    fun evaluate(p: NullaryPredicate<E, T, S>): List<Double> {
        var evaluation = nullaryPredicateCache[p]
        return if (evaluation == null) {
            evaluation = segment.tickData.filter {
                p.eval(this, it)
            }.map { it.currentTick }
            nullaryPredicateCache += p to evaluation
            evaluation
        } else {
            evaluation
        }
    }

    // endregion

    // region evaluation for TickPredicateOne

    private val tp1holdsCache: MutableMap<Pair<UnaryPredicate<*, E, T, S>, Pair<Double, Int>>, Boolean> = mutableMapOf()

    fun <E1:E>holds(p: UnaryPredicate<E1,E,T,S>, tid: Double, vid1: Int): Boolean =
        tp1holdsCache.getOrPut(p to (tid to vid1)) {
            val tick = segment.ticks[tid]
            val actor = tick?.entities?.firstOrNull { it.id == vid1 }
            if (tick != null && p.klass.isInstance(actor)) {
                p.eval(this, p.klass.cast(actor))
            }
            else {
                false
            }
        }

    // endregion

    // region evaluation for region TickPredicateTwo


    private val tp2holdsCache: MutableMap<Pair<BinaryPredicate<*,*,E,T,S>, Triple<Double, Int, Int>>, Boolean> = mutableMapOf()

    fun <E1:E, E2:E> holds(p: BinaryPredicate<E1,E2,E,T,S>, tid: Double, vid1: Int, vid2: Int): Boolean =
        tp2holdsCache.getOrPut(p to (Triple(tid, vid1, vid2))) {
            val tick = segment.ticks[tid]
            val actor1 = tick?.entities?.firstOrNull { it.id == vid1 }
            val actor2 = tick?.entities?.firstOrNull { it.id == vid2 }
            if (vid1 == vid2) {
                false
            }
            else if (tick != null && p.klass.first.isInstance(actor1) && p.klass.second.isInstance(actor2)) {
                p.eval(this, p.klass.first.cast(actor1), p.klass.second.cast(actor2))
            }
            else {
                false
            }
        }

    // endregion

}