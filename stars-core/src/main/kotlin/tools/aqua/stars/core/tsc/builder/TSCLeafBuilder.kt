/*
 * Copyright 2023-2026 The STARS Project Authors
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

package tools.aqua.stars.core.tsc.builder

import tools.aqua.stars.core.evaluation.Predicate
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.node.TSCLeafNode
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating [TSCLeafNode]s in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property label Label of the [TSCLeafNode].
 */
open class TSCLeafBuilder<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(val label: String) : TSCBuilder<E, T, U, D>() {

  /**
   * Creates a [TSCEdge] with a [TSCLeafNode].
   *
   * @return The created [TSCEdge].
   */
  fun build(): TSCEdge<E, T, U, D> =
      TSCEdge(
          condition = condition,
          inverseCondition = inverseCondition,
          destination =
              TSCLeafNode(
                  label = label,
                  monitorsMap = monitors,
                  valueFunction = valueFunction,
              ),
      )

  /**
   * DSL function for [TSCEdge] conditions.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param condition The [TSCEdge] condition defined by a [Predicate].
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCLeafBuilder<E, T, U, D>.condition(condition: Predicate<T>) {
    this.condition = condition
  }

  /**
   * DSL function for inline definition of [TSCEdge] conditions. Creates a [Predicate] internally.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param name The name of the [TSCEdge] condition.
   * @param condition The [TSCEdge] condition.
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCLeafBuilder<E, T, U, D>.condition(name: String = "", condition: (T) -> Boolean) =
      condition(Predicate(name, condition))

  /**
   * DSL function for inverse edge conditions.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param inverseCondition The inverse [TSCEdge] condition.
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCLeafBuilder<E, T, U, D>.inverseCondition(inverseCondition: Predicate<T>) {
    this.inverseCondition = inverseCondition
  }

  /**
   * DSL function for inline definition of [TSCEdge] inverse conditions. Creates a [Predicate]
   * internally.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param name The name of the inverse [TSCEdge] condition.
   * @param condition The inverse [TSCEdge] condition.
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCLeafBuilder<E, T, U, D>.inverseCondition(name: String = "", condition: (T) -> Boolean) =
      inverseCondition(Predicate(name, condition))

  /**
   * DSL function for a value function.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param valueFunction The value function.
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCLeafBuilder<E, T, U, D>.valueFunction(valueFunction: (T) -> Any) {
    this.valueFunction = valueFunction
  }

  /**
   * DSL function for a [TSCEdge] with MonitorsEdge in the [TSCLeafNode] scope.
   *
   * @param E [EntityType].
   * @param T [TickDataType].
   * @param U [TickUnit].
   * @param D [TickDifference].
   * @param init The init function.
   * @return The [TSCEdge] that is connected to a 'monitors' [TSCNode].
   */
  fun <
      E : EntityType<E, T, U, D>,
      T : TickDataType<E, T, U, D>,
      U : TickUnit<U, D>,
      D : TickDifference<D>,
  > TSCLeafBuilder<E, T, U, D>.monitors(init: TSCMonitorsBuilder<E, T, U, D>.() -> Unit = {}) =
      TSCMonitorsBuilder<E, T, U, D>().apply { init() }.also { this.monitors = it.build() }
}
