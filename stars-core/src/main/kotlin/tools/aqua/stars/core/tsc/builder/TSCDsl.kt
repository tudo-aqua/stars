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

@file:Suppress("unused")

package tools.aqua.stars.core.tsc.builder

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/** Constant predicate for always true edges. */
val CONST_TRUE: ((PredicateContext<*, *, *, *, *>) -> Boolean) = { true }

/** Label of the [TSCNode] built by the [tsc] function. */
const val ROOT_NODE_LABEL = "root"

/**
 * Builds root node. Applies [init] function to [TSCNode].
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @param init The init function. Must add exactly one edge.
 * @return The [TSCNode] at the root level of the TSC.
 */
fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> tsc(
    init: TSCBoundedBuilder<E, T, S, U, D>.() -> Unit = {}
): TSC<E, T, S, U, D> {
  val rootEdge =
      TSCBoundedBuilder<E, T, S, U, D>(ROOT_NODE_LABEL)
          .apply { init() }
          .apply { this.bounds = edgesCount() to edgesCount() }
          .build()

  check(rootEdge.destination.edges.size < 2) {
    "Too many elements to add - root can only host one."
  }

  check(rootEdge.destination.edges.isNotEmpty()) { "Init must add exactly one element to root." }

  check(rootEdge.destination.edges[0].condition == CONST_TRUE) {
    "Root node must not have a condition. Consider adding a fitting bounded parent node."
  }

  return TSC(rootEdge.destination.edges[0].destination)
}
