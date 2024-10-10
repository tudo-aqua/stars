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

package tools.aqua.stars.core.tsc.instance

import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.types.*

/**
 * Evaluated TSC edge.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property destination Destination [TSCInstanceNode].
 * @property tscEdge Associated [TSCEdge].
 */
data class TSCInstanceEdge<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val destination: TSCInstanceNode<E, T, S, U, D>,
    val tscEdge: TSCEdge<E, T, S, U, D>,
) {

  override fun equals(other: Any?): Boolean =
      other is TSCInstanceEdge<*, *, *, *, *> && destination == other.destination

  override fun hashCode(): Int = destination.hashCode()
}
