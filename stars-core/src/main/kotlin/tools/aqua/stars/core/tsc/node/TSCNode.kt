/*
 * Copyright 2023 The STARS Project Authors
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
import tools.aqua.stars.core.tsc.edge.TSCAlwaysEdge
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.projection.TSCProjection
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/**
 * Abstract baseclass for TSC nodes.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @property valueFunction Value function predicate of the node.
 * @property monitorFunction Monitor function predicate of the node.
 * @property projectionIDMapper Mapper for projection identifiers.
 * @property edges [TSCEdge]s of the TSC.
 */
sealed class TSCNode<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val valueFunction: (PredicateContext<E, T, S>) -> Any,
    val monitorFunction: (PredicateContext<E, T, S>) -> Boolean,
    val projectionIDMapper: Map<Any, Boolean>,
    vararg val edges: TSCEdge<E, T, S>,
) {

  /** Generates all TSC instances. */
  abstract fun generateAllInstances(): List<TSCInstanceNode<E, T, S>>

  /** Evaluates this TSC in the given context. */
  fun evaluate(ctx: PredicateContext<E, T, S>, depth: Int = 0): TSCInstanceNode<E, T, S> =
      TSCInstanceNode(this.valueFunction(ctx), this.monitorFunction(ctx), this).also {
        this.edges.forEach { tscEdge ->
          if (tscEdge.condition(ctx))
              it.edges +=
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
  fun buildProjections(projectionIgnoreList: List<Any> = listOf()): List<TSCProjection<E, T, S>> =
      projectionIDMapper
          .filter { wrapper -> !projectionIgnoreList.any { wrapper.key == it } }
          .mapNotNull {
            buildProjection(it.key)?.let { tsc -> TSCProjection(it.key, TSC(rootNode = tsc)) }
          }

  /**
   * Builds the TSC (rooted in the returned [TSCNode]) based on the given [projectionId]. Returns
   * 'null' if the given [projectionId] is not found in [projectionIDMapper] of the current
   * [TSCNode].
   *
   * @param projectionId The projection identifier as in [projectionIDMapper].
   */
  fun buildProjection(projectionId: Any): TSCNode<E, T, S>? =
      when (projectionIDMapper[projectionId]) {
        // this projection id is not found, don't project, return null
        null -> null

        // projection id is there and everything below should be included -> just deep clone
        true -> deepClone()

        // the normal case: projection id is there, but recursive is off
        false -> {
          val outgoingEdges =
              edges
                  .map { edge -> edge to edge.destination.buildProjection(projectionId) }
                  .mapNotNull {
                    when (it.first) {
                      is TSCAlwaysEdge ->
                          it.second?.let { _ -> TSCAlwaysEdge(it.first.label, it.second!!) }
                      else ->
                          it.second?.let { _ ->
                            TSCEdge(it.first.label, it.first.condition, it.second!!)
                          }
                    }
                  }
                  .toTypedArray()

          val alwaysEdgesBefore = edges.filterIsInstance<TSCAlwaysEdge<E, T, S>>().size
          val alwaysEdgesAfter = outgoingEdges.filterIsInstance<TSCAlwaysEdge<E, T, S>>().size
          val alwaysEdgesDiff = alwaysEdgesBefore - alwaysEdgesAfter

          when (this) {
            is TSCBoundedNode ->
                TSCBoundedNode(
                    this.valueFunction,
                    this.monitorFunction,
                    this.projectionIDMapper,
                    this.bounds.first - alwaysEdgesDiff to this.bounds.second - alwaysEdgesDiff,
                    *outgoingEdges)
          }
        }
      }

  /** Deeply clones [TSCNode]. */
  fun deepClone(): TSCNode<E, T, S> {
    val outgoingEdges =
        edges
            .map { it to it.destination.deepClone() }
            .map {
              when (it.first) {
                is TSCAlwaysEdge<E, T, S> -> TSCAlwaysEdge(it.first.label, it.second)
                else -> TSCEdge(it.first.label, it.first.condition, it.second)
              }
            }
            .toTypedArray()

    return when (this) {
      is TSCBoundedNode ->
          TSCBoundedNode(
              this.valueFunction,
              this.monitorFunction,
              this.projectionIDMapper,
              this.bounds,
              *outgoingEdges)
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
              append(if (instanceEdge is TSCAlwaysEdge) "-T-> " else "---> ")
              append(instanceEdge.label)
              append(instanceEdge.destination.toString(depth + 1))
            }
          }
          .toString()

  /** same output as toString, but with labels on the edges provided by [labels]. */
  fun toStringWithEdgeLabels(labels: Map<TSCEdge<E, T, S>, Int>): String =
      toStringWithEdgeLabels(0, labels)

  private fun toStringWithEdgeLabels(depth: Int, labels: Map<TSCEdge<E, T, S>, Any>): String {
    val builder = StringBuilder()
    when (this) {
      is TSCBoundedNode -> builder.append("(${this.bounds.first}..${this.bounds.second})\n")
    }
    this.edges.forEach { instanceEdge ->
      builder.append("  ".repeat(depth))
      when (instanceEdge) {
        is TSCAlwaysEdge ->
            builder.append("-[${labels[instanceEdge] ?: 0}]-> ${instanceEdge.label} ")
        else -> builder.append("-[${labels[instanceEdge] ?: 0}]-> ${instanceEdge.label} ")
      }
      builder.append(instanceEdge.destination.toStringWithEdgeLabels(depth + 1, labels))
    }
    return builder.toString()
  }

  override fun toString() = toString(0)

  override fun equals(other: Any?): Boolean =
      other is TSCNode<*, *, *> &&
          javaClass == other.javaClass &&
          valueFunction == other.valueFunction &&
          monitorFunction == other.monitorFunction &&
          projectionIDMapper == other.projectionIDMapper &&
          edges.contentEquals(other.edges)

  override fun hashCode(): Int =
      valueFunction.hashCode() +
          monitorFunction.hashCode() +
          projectionIDMapper.hashCode() +
          edges.sumOf { it.hashCode() }
}
