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
import tools.aqua.stars.core.tsc.builder.CONST_TRUE
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * Abstract baseclass for TSC nodes.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property label Label of the [TSCNode].
 * @property edges Outgoing [TSCEdge]s of the [TSCNode].
 * @param monitorsMap Map of monitor labels to their predicates of the [TSCNode].
 * @param projectionsMap Map of projections of the [TSCNode].
 * @property valueFunction Value function predicate of the [TSCNode].
 */
sealed class TSCNode<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(
    val label: String,
    open val edges: List<TSCEdge<E, T, S, U, D>>,
    private val monitorsMap: Map<String, (PredicateContext<E, T, S, U, D>) -> Boolean>?,
    private val projectionsMap: Map<String, Boolean>?,
    val valueFunction: (PredicateContext<E, T, S, U, D>) -> Any
) {

  /** Map of projection labels to their recursive state. */
  private val projections: Map<String, Boolean>
    get() = projectionsMap.orEmpty()

  /** Map of monitor labels to their predicates. */
  val monitors: Map<String, (PredicateContext<E, T, S, U, D>) -> Boolean>
    get() = monitorsMap.orEmpty()

  /** Generates all TSC instances. */
  abstract fun generateAllInstances(): List<TSCInstanceNode<E, T, S, U, D>>

  /** Evaluates this TSC in the given context. */
  fun evaluate(
      ctx: PredicateContext<E, T, S, U, D>,
      depth: Int = 0
  ): TSCInstanceNode<E, T, S, U, D> =
      TSCInstanceNode(
              this,
              this.label,
              this.monitors.mapValues { (_, monitor) -> monitor(ctx) },
              this.valueFunction(ctx))
          .also {
            it.edges +=
                this.edges
                    .filter { t -> t.condition(ctx) }
                    .map { tscEdge ->
                      TSCInstanceEdge(tscEdge.destination.evaluate(ctx, depth + 1), tscEdge)
                    }
          }

  /**
   * Builds the TSCs for each projection defined in this [TSCNode] and returns a [TSC] for each
   * projection id. All projection ids in [projectionIgnoreList] are ignored and will not be in the
   * resulting projection list.
   *
   * @param projectionIgnoreList Projections to ignore.
   */
  fun buildProjections(projectionIgnoreList: List<Any> = emptyList()): List<TSC<E, T, S, U, D>> =
      projections
          .filter { wrapper -> !projectionIgnoreList.any { wrapper.key == it } }
          .mapNotNull { (projectionId, _) ->
            buildProjection(projectionId = projectionId)?.let { rootNode ->
              TSC(rootNode = rootNode, identifier = projectionId)
            }
          }

  /**
   * Builds the TSC (rooted in the returned [TSCNode]) based on the given [projectionId]. Returns
   * 'null' if the given [projectionId] is not found in [projections] of the current [TSCNode].
   *
   * @param projectionId The projection identifier as in [projections].
   */
  private fun buildProjection(projectionId: Any): TSCNode<E, T, S, U, D>? {
    val isRecursive = projections[projectionId] ?: return null

    // projection id is there and everything below should be included -> just deep clone
    if (isRecursive) return deepClone()

    // the normal case: projection id is there, but recursive is off
    else
        return when (this) {
          is TSCLeafNode ->
              TSCLeafNode(
                  label = label,
                  monitorsMap = monitorsMap,
                  projectionsMap = projectionsMap,
                  valueFunction = valueFunction)
          is TSCBoundedNode -> {
            val outgoingEdges =
                edges
                    .mapNotNull { edge ->
                      edge.destination.buildProjection(projectionId = projectionId)?.let {
                          projection ->
                        TSCEdge(edge.condition, projection)
                      }
                    }
                    .toList()
            val alwaysEdgesBefore = edges.count { it.condition == CONST_TRUE }
            val alwaysEdgesAfter = outgoingEdges.count { it.condition == CONST_TRUE }
            val alwaysEdgesDiff = alwaysEdgesBefore - alwaysEdgesAfter
            TSCBoundedNode(
                label = this.label,
                edges = outgoingEdges,
                monitorsMap = this.monitorsMap,
                projectionsMap = this.projectionsMap,
                valueFunction = this.valueFunction,
                bounds =
                    this.bounds.first - alwaysEdgesDiff to this.bounds.second - alwaysEdgesDiff)
          }
        }
  }

  /** Deeply clones [TSCNode]. */
  private fun deepClone(): TSCNode<E, T, S, U, D> {
    val outgoingEdges =
        edges
            .map { it to it.destination.deepClone() }
            .map { TSCEdge(it.first.condition, it.second) }
            .toList()

    return when (this) {
      is TSCLeafNode ->
          TSCLeafNode(
              label = this.label,
              monitorsMap = this.monitorsMap,
              projectionsMap = this.projectionsMap,
              valueFunction = this.valueFunction)
      is TSCBoundedNode ->
          TSCBoundedNode(
              label = this.label,
              edges = outgoingEdges,
              monitorsMap = this.monitorsMap,
              projectionsMap = this.projectionsMap,
              valueFunction = this.valueFunction,
              bounds = this.bounds,
          )
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
              is TSCLeafNode -> append(label)
              is TSCBoundedNode -> append("${label}(${bounds.first}..${bounds.second})")
            }

            edges.forEach { instanceEdge ->
              append("\n")
              append("  ".repeat(depth))
              append(if (instanceEdge.condition == CONST_TRUE) "-T-> " else "---> ")
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
      builder.append("-[${labels[instanceEdge] ?: 0}]-> ${instanceEdge.destination.label} ")
      builder.append(instanceEdge.destination.toStringWithEdgeLabels(depth + 1, labels))
    }
    return builder.toString()
  }

  override fun toString(): String = toString(0)
}
