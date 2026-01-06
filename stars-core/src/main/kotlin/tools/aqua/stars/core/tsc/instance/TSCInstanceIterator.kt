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

package tools.aqua.stars.core.tsc.instance

import tools.aqua.stars.core.types.*

/**
 * Iterator for the [TSCInstance]. Iterates over all nodes of the [TSCInstance].
 *
 * The following [TSCInstance] will be traversed in the order
 * ["root", "exclusive", "leaf_exclusive_1", "leaf_exclusive_2", "any", "leaf_any_1", "leaf_any_2"].
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
class TSCInstanceIterator<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(startNode: TSCInstanceNode<E, T, U, D>) : Iterator<TSCInstanceNode<E, T, U, D>> {

  private val items: MutableList<TSCInstanceNode<E, T, U, D>> = mutableListOf()

  init {
    addNodesRecursively(startNode)
  }

  private fun addNodesRecursively(node: TSCInstanceNode<E, T, U, D>) {
    items.add(node)
    node.edges.forEach { addNodesRecursively(it.destination) }
  }

  override fun hasNext(): Boolean = items.isNotEmpty()

  override fun next(): TSCInstanceNode<E, T, U, D> = items.removeFirst()
}
