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

package tools.aqua.stars.core.tsc.node

import java.math.BigInteger
import tools.aqua.stars.core.evaluation.Predicate
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.CONST_TRUE
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.types.*

/**
 * Abstract baseclass for [TSC] nodes.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 * @property label Label of the [TSCNode].
 * @property edges Outgoing [TSCEdge]s of the [TSCNode].
 * @param monitorsMap Map of monitor labels to their [Predicate]s of the [TSCNode].
 * @property valueFunction Value function predicate of the [TSCNode].
 */
sealed class TSCNode<
    E : EntityType<E, T, U, D>,
    T : TickDataType<E, T, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>,
>(
    val label: String,
    open val edges: List<TSCEdge<E, T, U, D>>,
    private val monitorsMap: Map<String, Predicate<T>>?,
    val valueFunction: (T) -> Any,
) {

  /** Map of monitor labels to their [Predicate]s. */
  val monitors: Map<String, Predicate<T>>
    get() = monitorsMap.orEmpty()

  /** Counts all [TSC] instances. */
  abstract fun countAllInstances(): BigInteger

  /** Generates all [TSCInstance]s. */
  abstract fun generateAllInstances(): List<TSCInstance<E, T, U, D>>

  /** Evaluates this [TSC] in the given context. */
  fun evaluate(
      tick: T,
      depth: Int = 0,
  ): TSCInstanceNode<E, T, U, D> =
      TSCInstanceNode(
              tscNode = this,
              label = this.label,
              monitorResults = this.monitors.mapValues { (_, monitor) -> monitor.eval(tick) },
              value = this.valueFunction(tick),
          )
          .also {
            it.edges +=
                this.edges.mapNotNull { edge ->
                  val condition = edge.condition.eval(tick)
                  val inverseCondition = edge.inverseCondition?.eval(tick)

                  /*
                   * Decisions based on the condition and inverseCondition:
                   * [T = true, F = false, N = null]
                   *
                   * Condition InverseCondition -> Result
                   * T T -> Fail
                   * T F || T N -> OK
                   * F T || F N -> SKIP
                   * F F -> OK, but unknown
                   */

                  // Assert that not both condition and inverseCondition are true
                  check(!condition || inverseCondition != true) {
                    "Encountered TSCEdge where both condition and inverseCondition are true "
                  }

                  // Skip if condition is false and inverseCondition is not false
                  if (!condition && inverseCondition != false) null
                  else
                  // Create TSCInstanceEdge if condition is true. Flag as unknown if condition is
                  // false (inverseCondition must be false too at this point)
                  TSCInstanceEdge(
                          edge.destination.evaluate(tick, depth + 1).apply {
                            isUnknown = !condition
                          },
                          edge,
                          isUnknown = !condition,
                      )
                }
          }

  /** Deeply clones [TSCNode]. */
  private fun deepClone(): TSCNode<E, T, U, D> {
    val outgoingEdges =
        edges.map {
          TSCEdge(
              condition = it.condition,
              inverseCondition = it.inverseCondition,
              destination = it.destination.deepClone(),
          )
        }

    return when (this) {
      is TSCLeafNode ->
          TSCLeafNode(
              label = this.label,
              monitorsMap = this.monitorsMap,
              valueFunction = this.valueFunction,
          )
      is TSCBoundedNode ->
          TSCBoundedNode(
              label = this.label,
              edges = outgoingEdges,
              monitorsMap = this.monitorsMap,
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
              append(if (instanceEdge.condition.eval == CONST_TRUE) "-T-> " else "---> ")
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
  fun toStringWithEdgeLabels(labels: Map<TSCEdge<E, T, U, D>, Int>): String =
      toStringWithEdgeLabels(0, labels)

  private fun toStringWithEdgeLabels(depth: Int, labels: Map<TSCEdge<E, T, U, D>, Any>): String {
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
