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

package tools.aqua.stars.core.tsc.node

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.edge.TSCMonitorsEdge
import tools.aqua.stars.core.tsc.edge.TSCProjectionsEdge
import tools.aqua.stars.core.types.*

/**
 * Leaf TSC node.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param valueFunction Value function predicate of the node.
 * @param projections [TSCProjectionsEdge] of the TSC.
 * @param monitors [TSCMonitorsEdge] of the TSC.
 */
open class TSCLeafNode<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    valueFunction: (PredicateContext<E, T, S, U, D>) -> Any = {},
    projections: TSCProjectionsEdge<E, T, S, U, D>?,
    monitors: TSCMonitorsEdge<E, T, S, U, D>?
) : TSCBoundedNode<E, T, S, U, D>(valueFunction, projections, 0 to 0, emptyList(), monitors)
