package tools.aqua.stars.logic.kcmftbl.dslFormulas

import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula
import tools.aqua.stars.logic.kcmftbl.dsl.Ref

class exampleDSL {
    fun monitors() {
        val hasMidTrafficDensity = formula {
            exists { x: Ref<Vehicle> ->
                eventually {
                    (const(6) leq term {
                        x.now().let { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size }
                    }) and (term {
                        x.now().let { v -> v.tickData.vehiclesInBlock(v.lane.road.block).size }
                    } leq const(15))
                }
            }
        }
        formula { v: Ref<Vehicle> ->
            minPrevalence(0.6) {
                neg(hasMidTrafficDensity) or (term {
                    v.now().effVelocityInKmPH
                } leq term {
                    v.now().lane.speedLimits[v.now().positionOnLane.toInt()].speedLimit
                })
            }
        }

        val changedLane = formula { v: Ref<Vehicle> ->
            binding(term { v.now().lane }) { l ->
                eventually {
                    l ne term { v.now().lane }
                }
            }
        }
    }
}