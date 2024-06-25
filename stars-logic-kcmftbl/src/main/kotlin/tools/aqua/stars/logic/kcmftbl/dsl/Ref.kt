/*
 * Copyright 2024 The STARS Project Authors
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package tools.aqua.stars.logic.kcmftbl.dsl

import kotlin.reflect.KClass
import tools.aqua.stars.core.types.*

/**
 * Ref is used in the DSL to symbolize entities of a specific type. Function now() is used inside
 * predicates and terms of the DSL. nextTick() and allAtTick() are helper functions for the model
 * checker.
 *
 * @see makeRef
 */
class Ref<E1 : EntityType<*, *, *, *, *>>(
    private val kClass: KClass<E1>,
    id: Int? = null,
    val fixed: Boolean = false
) {
  var id: Int? = id
    set(value) {
      if (!fixed) field = value else throw Exception("The Id of a fixed Ref can not be changed.")
    }
  var tickIdx: Int = -1
    set(value) {
      field = value
      entities = tickDataType[value].entities.filterIsInstance(kClass.java)
    }
  private var entities: List<E1>? = listOf()

  /** sets tick to the next tick, updates entities accordingly */
  fun nextTick() {
    tickIdx++
    entities = tickDataType[tickIdx].entities.filterIsInstance(kClass.java)
  }

  /**
   * returns all entities at the given tick: correct tickdatatype must be specified for Ref before
   * calling
   */
  fun allAtTick(): List<E1> {
    if (tickIdx < 0) nextTick()
    return entities!! // TODO NullPointerException
  }

  /**
   * returns the entity with the given id at the given tickData. before now() is called, the id must
   * be set and the correct tickdatatype must be specified for Ref
   */
  fun now(): E1 {
    assert(id != null)
    if (tickIdx < 0) nextTick()
    return entities?.first { it.id == id }!! // TODO NullPointerException
  }

  companion object {
    lateinit var tickDataType: List<TickDataType<*, *, *, *, *>>
    fun <
        E : EntityType<E, T, S, U, D>,
        T : TickDataType<E, T, S, U, D>,
        S : SegmentType<E, T, S, U, D>,
        U : TickUnit<U, D>,
        D : TickDifference<D>> setSegment(segment: SegmentType<E, T, S, U, D>) {
      this.tickDataType = segment.tickData
    }
  }
}

/** helper function to create instances of Ref without needing an explicit class parameter */
inline fun <
    reified E1 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> makeRef(): Ref<E1> = Ref(E1::class)

inline fun <
    reified E1 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> makeFixedRef(id: Int): Ref<E1> = Ref(E1::class, id, true)

inline fun <
    reified E1 : E,
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> makeFixedRef(entity: E1): Ref<E1> = Ref(E1::class, entity.id, true)
