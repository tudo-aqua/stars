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

package tools.aqua.stars.core.tsc.edge

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.builder.CONST_TRUE
import tools.aqua.stars.core.tsc.node.TSCBoundedNode
import tools.aqua.stars.core.types.*

/**
 * Baseclass for TSC bounded edges.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param condition Condition of the edge.
 * @param destination Destination [TSCBoundedNode].
 */
open class TSCBoundedEdge<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    condition: (PredicateContext<E, T, S, U, D>) -> Boolean = CONST_TRUE,
    destination: TSCBoundedNode<E, T, S, U, D>
) : TSCEdge<E, T, S, U, D>(condition = condition, destination = destination)
