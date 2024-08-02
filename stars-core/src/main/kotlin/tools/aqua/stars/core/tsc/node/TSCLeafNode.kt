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
import tools.aqua.stars.core.types.*

/**
 * Leaf TSC node.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label Label of the [TSCLeafNode].
 * @param monitorsMap Map of monitor labels to their predicates of the [TSCLeafNode].
 * @param projectionsMap Map of projections of the [TSCLeafNode].
 * @param valueFunction Value function predicate of the [TSCLeafNode].
 */
open class TSCLeafNode<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    label: String,
    monitorsMap: Map<String, (PredicateContext<E, T, S, U, D>) -> Boolean>?,
    projectionsMap: Map<String, Boolean>?,
    valueFunction: (PredicateContext<E, T, S, U, D>) -> Any = {},
) :
    TSCBoundedNode<E, T, S, U, D>(
        label = label,
        edges = emptyList(),
        monitorsMap = monitorsMap,
        projectionsMap = projectionsMap,
        valueFunction = valueFunction,
        bounds = 0 to 0)
