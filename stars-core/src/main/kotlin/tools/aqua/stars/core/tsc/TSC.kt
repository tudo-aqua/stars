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

package tools.aqua.stars.core.tsc

import java.math.BigInteger
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.types.*

/**
 * TSC tree structure.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property rootNode The root node of the [TSC].
 * @property identifier The identifier of the [TSC].
 */
class TSC<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(val rootNode: TSCNode<E, T, U, D>, val identifier: String = "TSC") :
    Iterable<TSCNode<E, T, U, D>> {

  init {
    charArrayOf('"', '*', '<', '>', '?', '|', '\u0000').forEach {
      check(!identifier.contains(it)) { "Identifier must not contain illegal character $it." }
    }
  }

  /** Buffer for the number of possible [TSCInstance]s. */
  private var instanceCountBuffer: BigInteger? = null

  /**
   * Returns the number of possible [TSCInstance]s. The calculation is performed on-demand upon
   * first access and is the buffered.
   */
  val instanceCount: BigInteger
    get() = instanceCountBuffer ?: rootNode.countAllInstances().also { instanceCountBuffer = it }

  /** Buffer for the possible [TSCInstance]s. */
  private var possibleTSCInstancesBuffer: List<TSCInstance<E, T, U, D>>? = null

  /**
   * Returns the [List] of all possible [TSCInstance]s. The generation is performed on-demand upon
   * first access and is then buffered.
   */
  val possibleTSCInstances: List<TSCInstance<E, T, U, D>>
    get() =
        possibleTSCInstancesBuffer
            ?: rootNode.generateAllInstances().also { possibleTSCInstancesBuffer = it }

  /**
   * Evaluates [List] of [TickDataType]s on [TSC].
   *
   * @param tick The current [TickDataType].
   * @return The calculated [TSCInstance] based on the evaluation.
   */
  fun evaluate(tick: T): TSCInstance<E, T, U, D> =
      TSCInstance(rootNode.evaluate(tick), tick.toString())

  override fun toString(): String = this.rootNode.toString()

  override fun iterator(): Iterator<TSCNode<E, T, U, D>> = TSCIterator(rootNode)
}
