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

package tools.aqua.stars.core.tsc

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.node.TSCNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.*

/**
 * TSC graph.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property rootNode The root node of the [TSC].
 */
class TSC<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(val rootNode: TSCNode<E, T, S, U, D>) :
    Iterable<TSCNode<E, T, S, U, D>> {
  /**
   * Evaluates [PredicateContext] on [TSC].
   *
   * @param context The [PredicateContext].
   * @return The calculated [TSCInstance] based on the evaluation.
   */
  fun evaluate(context: PredicateContext<E, T, S, U, D>): TSCInstance<E, T, S, U, D> =
      TSCInstance(rootNode.evaluate(context), context.segment.getSegmentIdentifier())

  /**
   * Builds all possible projections ignoring those in [projectionIgnoreList].
   *
   * @param projectionIgnoreList Projections to ignore.
   * @return The [List] of all [TSCProjection]s for this [TSC].
   */
  fun buildProjections(
      projectionIgnoreList: List<Any> = emptyList()
  ): List<TSCProjection<E, T, S, U, D>> = rootNode.buildProjections(projectionIgnoreList)

  override fun toString(): String = this.rootNode.toString()

  override fun iterator(): Iterator<TSCNode<E, T, S, U, D>> = TSCIterator(rootNode)
}
