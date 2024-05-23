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

package tools.aqua.stars.core.tsc

import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * Iterator for the TSC. Iterates over all edges of the TSC.
 *
 * The following TSC will be traversed in the order ["exclusive", "leaf_exclusive_1",
 * "leaf_exclusive_2", "any", "leaf_any_1", "leaf_any_2"]. The root node gets ignored.
 *
 * ```
 * all("root") {
 *   exclusive("exclusive") {
 *     leaf("leaf_exclusive_1")
 *     leaf("leaf_exclusive_2")
 *   }
 *   any("any") {
 *     leaf("leaf_any_1")
 *     leaf("leaf_any_2")
 *   }
 * }
 * ```
 */
class TSCIterator<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(startNode: TSCNode<E, T, S, U, D>) : Iterator<TSCEdge<E, T, S, U, D>> {

  private val items: MutableList<TSCEdge<E, T, S, U, D>> = mutableListOf()

  init {
    startNode.edges.forEach { addEdgesRecursively(it) }
  }

  private fun addEdgesRecursively(edge: TSCEdge<E, T, S, U, D>) {
    items.add(edge)
    edge.destination.edges.forEach { addEdgesRecursively(it) }
  }

  override fun hasNext(): Boolean = items.isNotEmpty()

  override fun next(): TSCEdge<E, T, S, U, D> = items.removeFirst()
}
