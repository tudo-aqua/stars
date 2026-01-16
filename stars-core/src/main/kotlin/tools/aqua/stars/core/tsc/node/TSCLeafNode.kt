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

package tools.aqua.stars.core.tsc.node

import java.math.BigInteger
import tools.aqua.stars.core.evaluation.Predicate
import tools.aqua.stars.core.types.*

/**
 * Leaf [TSC] node.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param label Label of the [TSCLeafNode].
 * @param monitorsMap Map of monitor labels to their [Predicate]s of the [TSCLeafNode].
 * @param valueFunction Value function predicate of the [TSCLeafNode].
 */
open class TSCLeafNode<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    label: String,
    monitorsMap: Map<String, Predicate<E, T, U, D>>?,
    valueFunction: (T) -> Any = {},
) :
    TSCBoundedNode<E, T, U, D>(
        label = label,
        edges = emptyList(),
        monitorsMap = monitorsMap,
        valueFunction = valueFunction,
        bounds = 0 to 0,
    ) {
  override fun countAllInstances(): BigInteger = BigInteger.ONE
}
