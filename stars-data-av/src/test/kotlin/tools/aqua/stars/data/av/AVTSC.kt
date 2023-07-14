package tools.aqua.stars.data.av

import org.junit.jupiter.api.Test
import tools.aqua.stars.core.evaluation.*
import tools.aqua.stars.core.tsc.*
import tools.aqua.stars.logic.kcmftbl.*

class AVTSC {

    @Test
    fun testTSCconstruction() {

        val soBetween = predicate(Vehicle::class to Vehicle::class) { _, v0, v1 ->
            v1.tickData.vehicles.filter { it.id != v0.id && it.id != v1.id }.any { vx ->
                (v0.lane.uid == vx.lane.uid || v1.lane.uid == vx.lane.uid)
                        && (!(v0.lane.uid == vx.lane.uid) || (v0.positionOnLane < vx.positionOnLane))
                        && (!(v1.lane.uid == vx.lane.uid) || (v1.positionOnLane > vx.positionOnLane))
            }
        }

        val obeyedSpeedLimit = predicate(Vehicle::class) { _, v ->
            globally(v) { v ->
                (v.effVelocityInMPH) <= v.lane.speedAt(v.positionOnLane)
            }
        }

        root<Actor, TickData, Segment> {
            all("TSC Root") {
                leaf("someone between") {
                    condition = { ctx ->
                        ctx.segment.vehicleIds.any { v1 ->
                            soBetween.holds(ctx, ctx.segment.firstTickId, ctx.egoID, v1)
                        }
                    }
                }
                leaf("obeyed speed limit") {
                    condition = { ctx ->
                        obeyedSpeedLimit.holds(ctx)
                    }
                }
            }



        }



    }
}

