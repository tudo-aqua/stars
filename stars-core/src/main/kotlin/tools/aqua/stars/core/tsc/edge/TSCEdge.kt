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

package tools.aqua.stars.core.tsc.edge

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * Baseclass for TSC edges.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @property label Edge label.
 * @property condition Predicate for the edge condition.
 * @property destination Destination [TSCNode].
 */
open class TSCEdge<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val label: String,
    val condition: (PredicateContext<E, T, S>) -> Boolean = { true },
    val destination: TSCNode<E, T, S>,
) {

  override fun equals(other: Any?): Boolean =
      other is TSCEdge<*, *, *> &&
          label == other.label &&
          condition == other.condition &&
          destination == other.destination

  override fun hashCode(): Int = label.hashCode() + condition.hashCode() + destination.hashCode()
}
