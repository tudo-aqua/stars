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

package tools.aqua.stars.core.tsc.node

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.TSCIterator
import tools.aqua.stars.core.tsc.builder.CONST_TRUE
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.edge.TSCMonitorsEdge
import tools.aqua.stars.core.tsc.edge.TSCProjectionsEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.*

/**
 * Abstract baseclass for TSC nodes.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property edges [TSCEdge]s of the TSC.
 * @param tscProjectionsEdge [TSCProjectionsEdge] of the TSC.
 * @param tscMonitorsEdge [TSCMonitorsEdge] of the TSC.
 * @property valueFunction Value function predicate of the node.
 */
sealed class TSCNode<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    open val edges: List<TSCEdge<E, T, S, U, D>>,
    private val tscProjectionsEdge: TSCProjectionsEdge<E, T, S, U, D>?,
    private val tscMonitorsEdge: TSCMonitorsEdge<E, T, S, U, D>?,
    val valueFunction: (PredicateContext<E, T, S, U, D>) -> Any
) : Iterable<TSCEdge<E, T, S, U, D>> {

  private val projections: Map<Any, Boolean> =
      (tscProjectionsEdge?.destination as? TSCProjectionsNode)?.projectionMap ?: emptyMap()

  val monitors: Map<String, (PredicateContext<E, T, S, U, D>) -> Boolean> =
      (tscMonitorsEdge?.destination as? TSCMonitorsNode)?.monitorMap ?: emptyMap()

  /** Generates all TSC instances. */
  abstract fun generateAllInstances(): List<TSCInstanceNode<E, T, S, U, D>>

  /** Evaluates this TSC in the given context. */
  fun evaluate(
      ctx: PredicateContext<E, T, S, U, D>,
      depth: Int = 0
  ): TSCInstanceNode<E, T, S, U, D> =
      TSCInstanceNode(this.valueFunction(ctx), true, this).also {
        it.edges +=
            this.edges
                .filter { t -> t.condition(ctx) }
                .map { tscEdge ->
                  TSCInstanceEdge(
                      tscEdge.label, tscEdge.destination.evaluate(ctx, depth + 1), tscEdge)
                }
      }

  /**
   * Builds the TSCs for each projection defined in this [TSCNode] and returns a [TSCProjection] for
   * each projection id. All projection ids in [projectionIgnoreList] are ignored and will not be in
   * the resulting projection list.
   *
   * @param projectionIgnoreList Projections to ignore.
   */
  fun buildProjections(
      projectionIgnoreList: List<Any> = listOf()
  ): List<TSCProjection<E, T, S, U, D>> =
      projections
          .filter { wrapper -> !projectionIgnoreList.any { wrapper.key == it } }
          .mapNotNull {
            buildProjection(it.key)?.let { tsc -> TSCProjection(it.key, TSC(rootNode = tsc)) }
          }

  /**
   * Builds the TSC (rooted in the returned [TSCNode]) based on the given [projectionId]. Returns
   * 'null' if the given [projectionId] is not found in [projections] of the current [TSCNode].
   *
   * @param projectionId The projection identifier as in [projections].
   */
  private fun buildProjection(projectionId: Any): TSCNode<E, T, S, U, D>? {
    val mappedId = projections[projectionId]

    // this projection id is not found, don't project, return null
    if (mappedId == null) return null

    // projection id is there and everything below should be included -> just deep clone
    if (mappedId) return deepClone()

    // the normal case: projection id is there, but recursive is off
    return when (this) {
      is TSCMonitorsNode -> TSCMonitorsNode(this.valueFunction, this.monitors)
      is TSCLeafNode ->
          TSCLeafNode(this.valueFunction, this.tscProjectionsEdge, this.tscMonitorsEdge)
      is TSCBoundedNode -> {
        val outgoingEdges =
            edges
                .map { edge -> edge to edge.destination.buildProjection(projectionId) }
                .map { TSCEdge(it.first.label, it.first.condition, it.second!!) }
                .toList()
        val alwaysEdgesBefore = edges.count { it.condition == CONST_TRUE }
        val alwaysEdgesAfter = outgoingEdges.count { it.condition == CONST_TRUE }
        val alwaysEdgesDiff = alwaysEdgesBefore - alwaysEdgesAfter
        TSCBoundedNode(
            this.valueFunction,
            this.tscProjectionsEdge,
            this.bounds.first - alwaysEdgesDiff to this.bounds.second - alwaysEdgesDiff,
            outgoingEdges,
            this.tscMonitorsEdge)
      }
    }
  }

  /** Deeply clones [TSCNode]. */
  private fun deepClone(): TSCNode<E, T, S, U, D> {
    val outgoingEdges =
        edges
            .map { it to it.destination.deepClone() }
            .map { TSCEdge(it.first.label, it.first.condition, it.second) }
            .toList()

    return when (this) {
      is TSCMonitorsNode -> TSCMonitorsNode(this.valueFunction, this.monitors)
      is TSCLeafNode ->
          TSCLeafNode(this.valueFunction, this.tscProjectionsEdge, this.tscMonitorsEdge)
      is TSCBoundedNode ->
          TSCBoundedNode(
              this.valueFunction,
              this.tscProjectionsEdge,
              this.bounds,
              outgoingEdges,
              this.tscMonitorsEdge)
    }
  }

  /**
   * Prints [TSCNode] up to given [depth].
   *
   * @param depth Depth to print up to.
   */
  fun toString(depth: Int): String =
      StringBuilder()
          .apply {
            when (this@TSCNode) {
              is TSCBoundedNode -> append("(${bounds.first}..${bounds.second})\n")
            }

            edges.forEach { instanceEdge ->
              append("  ".repeat(depth))
              append(if (instanceEdge.condition == CONST_TRUE) "-T-> " else "---> ")
              append(instanceEdge.label)
              append(instanceEdge.destination.toString(depth + 1))
            }
          }
          .toString()

  /**
   * Outputs the same output as [toString], but with labels on the edges provided by [labels].
   *
   * @return The [String] representation of the [TSCNode] with labels on the edges.
   */
  @Suppress("unused")
  fun toStringWithEdgeLabels(labels: Map<TSCEdge<E, T, S, U, D>, Int>): String =
      toStringWithEdgeLabels(0, labels)

  private fun toStringWithEdgeLabels(depth: Int, labels: Map<TSCEdge<E, T, S, U, D>, Any>): String {
    val builder = StringBuilder()
    when (this) {
      is TSCBoundedNode -> builder.append("(${this.bounds.first}..${this.bounds.second})\n")
    }
    this.edges.forEach { instanceEdge ->
      builder.append("  ".repeat(depth))
      builder.append("-[${labels[instanceEdge] ?: 0}]-> ${instanceEdge.label} ")
      builder.append(instanceEdge.destination.toStringWithEdgeLabels(depth + 1, labels))
    }
    return builder.toString()
  }

  override fun toString(): String = toString(0)

  override fun iterator(): Iterator<TSCEdge<E, T, S, U, D>> = TSCIterator(this)
}
