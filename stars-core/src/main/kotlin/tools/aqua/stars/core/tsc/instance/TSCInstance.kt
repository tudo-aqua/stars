/*
 * Copyright 2023-2025 The STARS Project Authors
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

import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.types.*

/**
 * Instance of a [TSC].
 *
 * Each instance is a subtree of the original [TSC] that was observed during evaluation.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property rootNode The root [TSCInstanceNode].
 * @property sourceIdentifier Source identifier.
 */
class TSCInstance<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(val rootNode: TSCInstanceNode<E, T, U, D>, val sourceIdentifier: String = "") :
    Iterable<TSCInstanceNode<E, T, U, D>> {

  override fun toString(): String = this.rootNode.toString()

  override fun iterator(): Iterator<TSCInstanceNode<E, T, U, D>> = TSCInstanceIterator(rootNode)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is TSCInstance<*, *, *, *>) return false
    return rootNode == other.rootNode
  }

  override fun hashCode(): Int = rootNode.hashCode()
}
