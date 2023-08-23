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

package tools.aqua.stars.core.tsc

import tools.aqua.stars.core.evaluation.PredicateContext
import tools.aqua.stars.core.powerlist
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

fun proj(id: Any) = Pair(id, false)

fun projRec(id: Any) = Pair(id, true)

abstract class TSCNode<
    E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val valueFunction: (PredicateContext<E, T, S>) -> Any,
    val monitorFunction: (PredicateContext<E, T, S>) -> Boolean,
    val projectionIDWrappers: Map<Any, Boolean>,
    vararg val edges: TSCEdge<E, T, S>,
) {
  abstract fun generateAllInstances(): List<TSCInstanceNode<E, T, S>>

  override fun toString() = toString(0)

  override fun equals(other: Any?): Boolean {
    return if (other is TSCNode<*, *, *>) {
      javaClass == other.javaClass &&
          valueFunction == other.valueFunction &&
          monitorFunction == other.monitorFunction &&
          projectionIDWrappers == other.projectionIDWrappers &&
          edges.contentEquals(other.edges)
    } else {
      false
    }
  }

  override fun hashCode(): Int =
      valueFunction.hashCode() +
          monitorFunction.hashCode() +
          projectionIDWrappers.hashCode() +
          edges.sumOf { it.hashCode() }

  /** evaluates this TSC in the given context */
  fun evaluate(ctx: PredicateContext<E, T, S>, depth: Int = 0): TSCInstanceNode<E, T, S> {
    val instanceNode = TSCInstanceNode(this.valueFunction(ctx), this.monitorFunction(ctx), this)
    this.edges.forEach { tscEdge ->
      if (tscEdge.condition(ctx))
          instanceNode.edges +=
              TSCInstanceEdge(tscEdge.label, tscEdge.destination.evaluate(ctx, depth + 1), tscEdge)
    }
    return instanceNode
  }

  /**
   * Builds the TSCs for each projection defined in this [TSCNode] and returns a [TSCProjection] for
   * each projection id. All projection ids in [projectionIgnoreList] are ignored and will not be in
   * the resulting projection list.
   */
  fun buildProjections(projectionIgnoreList: List<Any> = listOf()): List<TSCProjection<E, T, S>> {
    /** Holds the filtered projection id list based on [projectionIgnoreList] */
    val filteredProjectionIds =
        projectionIDWrappers.filter { wrapper -> !projectionIgnoreList.any { wrapper.key == it } }
    // Build the TSC for each remaining projection
    return filteredProjectionIds.mapNotNull {
      val projectionId = it.key
      buildProjection(projectionId)?.let { tsc -> TSCProjection(projectionId, tsc) }
    }
  }

  /**
   * Builds the TSC (rooted in the returned [TSCNode]) based on the given [projectionId]. Returns
   * null if the given [projectionId] is not found in [projectionIDWrappers] of the current
   * [TSCNode].
   */
  fun buildProjection(projectionId: Any): TSCNode<E, T, S>? {
    when (projectionIDWrappers[projectionId]) {
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

        return when (this) {
          is TSCBoundedNode ->
              TSCBoundedNode(
                  this.valueFunction,
                  this.monitorFunction,
                  this.projectionIDWrappers,
                  this.bounds.first - alwaysEdgesDiff to this.bounds.second - alwaysEdgesDiff,
                  *outgoingEdges)
          else -> error("unknown tsc node type $this")
        }
      }
      // projection id is there and everything below should be included -> just deep clone
      true -> {
        return deepClone()
      }
      // this projection id is not found, don't project, return null
      null -> return null
    }
  }

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
              this.projectionIDWrappers,
              this.bounds,
              *outgoingEdges)
      else -> error("unknown tsc node type $this")
    }
  }

  fun toString(depth: Int): String {
    val builder = StringBuilder()
    when (this) {
      is TSCBoundedNode -> builder.append("(${this.bounds.first}..${this.bounds.second})\n")
    }
    edges.forEach { instanceEdge ->
      builder.append("  ".repeat(depth))
      when (instanceEdge) {
        is TSCAlwaysEdge -> builder.append("-T-> ${instanceEdge.label} ")
        else -> builder.append("---> ${instanceEdge.label} ")
      }
      builder.append(instanceEdge.destination.toString(depth + 1))
    }
    return builder.toString()
  }

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
}

class TSCBoundedNode<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    valueFunction: (PredicateContext<E, T, S>) -> Any = {},
    monitorFunction: (PredicateContext<E, T, S>) -> Boolean = { true },
    projectionIDs: Map<Any, Boolean> = mapOf(),
    val bounds: Pair<Int, Int>,
    vararg edges: TSCEdge<E, T, S>
) : TSCNode<E, T, S>(valueFunction, monitorFunction, projectionIDs, *edges) {

  override fun equals(other: Any?): Boolean {
    return if (other is TSCBoundedNode<*, *, *>) {
      valueFunction == other.valueFunction &&
          monitorFunction == other.monitorFunction &&
          projectionIDWrappers == other.projectionIDWrappers &&
          bounds == other.bounds &&
          edges.contentEquals(other.edges)
    } else {
      false
    }
  }

  override fun hashCode(): Int =
      valueFunction.hashCode() +
          monitorFunction.hashCode() +
          projectionIDWrappers.hashCode() +
          bounds.hashCode() +
          edges.sumOf { it.hashCode() }

  override fun generateAllInstances(): List<TSCInstanceNode<E, T, S>> {

    val allSuccessorsList = mutableListOf<List<List<TSCInstanceEdge<E, T, S>>>>()
    edges.forEach { edge ->
      val successorList = mutableListOf<List<TSCInstanceEdge<E, T, S>>>()
      edge.destination.generateAllInstances().forEach { generatedChild ->
        successorList += listOf(TSCInstanceEdge(edge.label, generatedChild, edge))
      }
      allSuccessorsList += successorList
    }

    val returnList = mutableListOf<TSCInstanceNode<E, T, S>>()

    // build all subsets of allSuccessorsList and filter to subsets.size in
    // bounds.first..bounds.second
    val boundedSuccessors =
        allSuccessorsList
            .powerlist()
            .filter { subset -> subset.size in bounds.first..bounds.second }
            .toList()

    boundedSuccessors.forEach { subset ->
      when (subset.size) {
        0 -> returnList += TSCInstanceNode(Unit, true, this)
        1 ->
            subset.first().forEach { successors ->
              val generatedNode = TSCInstanceNode(Unit, true, this)
              successors.forEach { successor -> generatedNode.edges += successor }
              returnList += generatedNode
            }
        else ->
            crossProduct(subset).forEach { successors ->
              val generatedNode = TSCInstanceNode(Unit, true, this)
              successors.forEach { successor -> generatedNode.edges += successor }
              returnList += generatedNode
            }
      }
    }
    return returnList
  }
}

/**
 * Build all possible combinations of the lists in the input list. Example instances kept until
 * better documentation will be written.
 * ```
 *    val input = listOf(
 *        listOf(listOf("a"), listOf("b"), listOf("c")),
 *        listOf(listOf("x"), listOf("y")),
 *        listOf(listOf("1"), listOf("2"), listOf("3"), listOf("4"))
 *    )
 *
 *    val afterFirstStep = listOf(
 *        listOf(listOf("a", "x"), listOf("a", "y"), /*...*/ listOf("c", "y")),
 *        listOf(listOf("1"), listOf("2"), listOf("3"), listOf("4"))
 *    )
 *
 *     val afterSecondStep = listOf(
 *        listOf(listOf("a", "x", "1"), listOf("a", "x", "2"), /*...*/ listOf("c", "y", "4"))
 *    )
 * ```
 */
fun <T> crossProduct(input: List<List<List<T>>>): List<List<T>> {
  require(input.size >= 2) {
    "Input list for cross-product building must at least contain two elements. received $input"
  }

  val e1 = input[0]
  val e2 = input[1]
  val nextLevelList = mutableListOf<List<T>>()
  e1.forEach { it1 ->
    e2.forEach { it2 ->
      val nextEntry = mutableListOf<T>()
      nextEntry.addAll(it1)
      nextEntry.addAll(it2)
      nextLevelList += nextEntry
    }
  }

  // val monitorFunction: (PredicateContext, Segment) -> Boolean = { _, _ -> true }

  return if (input.size == 2) nextLevelList
  else crossProduct(listOf(nextLevelList) + input.subList(2, input.size))
}

open class TSCEdge<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val label: String,
    val condition: (PredicateContext<E, T, S>) -> Boolean = { true },
    val destination: TSCNode<E, T, S>,
) {
  override fun equals(other: Any?): Boolean {
    return if (other is TSCEdge<*, *, *>) {
      label == other.label && condition == other.condition && destination == other.destination
    } else {
      false
    }
  }

  override fun hashCode(): Int = label.hashCode() + condition.hashCode() + destination.hashCode()
}

class TSCAlwaysEdge<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    label: String,
    destination: TSCNode<E, T, S>
) : TSCEdge<E, T, S>(label, { true }, destination)

class TSCInstanceNode<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val value: Any,
    val monitorResult: Boolean,
    val tscNode: TSCNode<E, T, S>
) {
  var edges = mutableListOf<TSCInstanceEdge<E, T, S>>()

  fun getAllEdges(): List<TSCEdge<E, T, S>> {
    return this.edges.map { it.tscEdge } + this.edges.flatMap { it.destination.getAllEdges() }
  }

  /**
   * Validates own (and recursively all children's) successor constraints imposed by the
   * [TSCNode<E,T,S>] types the instances were built from (e.g. exactly one for [TSCXorNode] or
   * correct range for [TSCBoundedNode])
   *
   * @param label the label used to build the human-readable string; uses [TSCEdge.label] in
   * recursive call
   *
   * @return non-validating nodes; first element of pair is the node that failed to validate; second
   * element is a human-readable explanation for the failure
   */
  fun validate(label: String = "RootNode"): List<Pair<TSCInstanceNode<E, T, S>, String>> {
    val returnList = mutableListOf<Pair<TSCInstanceNode<E, T, S>, String>>()
    when (this.tscNode) {
      is TSCBoundedNode ->
          if (edges.size !in tscNode.bounds.first..tscNode.bounds.second)
              returnList +=
                  this to
                      "[$label] TSCBoundedNode successor count must be within [${tscNode.bounds.first},${tscNode.bounds.second}], but ${edges.size} successor(s) found."
    }
    returnList += edges.flatMap { it.destination.validate(it.label) }
    return returnList
  }

  /**
   * Validates own (and recursively all children's) results of the [TSCNode<E,T,S>.monitorFunction]
   * results and collects incoming edge labels for results != true.
   *
   * @param label the label added to the return list if [monitorResult] == `false`; uses
   * [TSCEdge.label] in recursive call
   *
   * @return list of edge labels leading to a node with `false` monitor result.
   */
  fun validateMonitors(label: String = "RootNode"): List<String> {
    val returnList = mutableListOf<String>()
    if (!monitorResult) returnList += label
    returnList += edges.flatMap { it.destination.validateMonitors(it.label) }
    return returnList
  }

  override fun toString() = toString(0)

  fun toString(depth: Int): String {
    val builder = StringBuilder()
    if (value is Unit) {
      builder.append("\n")
    } else {
      builder.append("($value)\n")
    }
    edges.forEach { instanceEdge ->
      builder.append("  ".repeat(depth))
      builder.append("-> ${instanceEdge.label} ")
      builder.append(instanceEdge.destination.toString(depth + 1))
    }
    return builder.toString()
  }

  override fun hashCode() = edges.sumOf { it.hashCode() }
  override fun equals(other: Any?): Boolean {
    return if (other is TSCInstanceNode<*, *, *> && edges.size == other.edges.size) {
      edges.withIndex().all { iv -> iv.value == other.edges[iv.index] }
    } else {
      false
    }
  }
}

class TSCInstanceEdge<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val label: String,
    val destination: TSCInstanceNode<E, T, S>,
    val tscEdge: TSCEdge<E, T, S>,
) {

  override fun hashCode(): Int = label.hashCode() + destination.hashCode()

  override fun equals(other: Any?): Boolean {
    return if (other is TSCInstanceEdge<*, *, *>) {
      label == other.label && destination == other.destination
    } else {
      false
    }
  }

  override fun toString() = "--${label}->"
}

/** Holds the [tsc] in form of the root [TSCNode] for a projection [id]. */
class TSCProjection<E : EntityType<E, T, S>, T : TickDataType<E, T, S>, S : SegmentType<E, T, S>>(
    val id: Any,
    val tsc: TSCNode<E, T, S>
)
