/*
 * Copyright 2023-2024 The STARS Project Authors
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

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.edge.TSCMonitorsEdge
import tools.aqua.stars.core.tsc.node.TSCMonitorsNode
import tools.aqua.stars.core.types.*

/**
 * Class to assist in creating monitors nodes in the DSL.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param valueFunction (Default: empty) Value function predicate of the node.
 * @param projectionIDs (Default: empty map) Projection identifier of the node.
 * @param condition (Default: null) Condition predicate of the edge.
 */
open class TSCMonitorsBuilder<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    valueFunction: (PredicateContext<E, T, S, U, D>) -> Any = {},
    projectionIDs: Map<Any, Boolean> = mapOf(),
    condition: ((PredicateContext<E, T, S, U, D>) -> Boolean)? = null
) :
    TSCBuilder<E, T, S, U, D, TSCMonitorsEdge<E, T, S, U, D>>(
        "monitors", valueFunction, projectionIDs, 0 to 0, condition) {

  override fun build(): TSCMonitorsEdge<E, T, S, U, D> =
      TSCMonitorsEdge(TSCMonitorsNode(valueFunction, emptyList())) //FIXME: emptyList() should be monitors
}
