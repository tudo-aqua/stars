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

package tools.aqua.stars.core.metric.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import tools.aqua.stars.core.*
import tools.aqua.stars.core.metric.metrics.evaluation.InvalidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.metrics.evaluation.MissedTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.metric.serialization.extensions.compareTo
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.tsc
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.node.TSCNode

/**
 * Tests the [SerializableResult] sealed class implementation for the [SerializableTSCResultTest].
 */
class SerializableTSCResultTest {

  // region TSC 1 definition
  /** Holds a simple [TSC] with two mandatory leaf nodes. */
  private val simpleTSC =
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") {
          leaf("leaf1")
          leaf("leaf2")
        }
      }

  /** Holds the root node of the [simpleTSC]. */
  private val simpleTSCRootNode:
      TSCNode<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> =
      simpleTSC.rootNode

  /** Holds the edge to the first leaf node of the [simpleTSC]. */
  private val simpleTSCLeafEdge:
      TSCEdge<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> =
      simpleTSCRootNode.edges[0]

  /** Holds the node of the first leaf node of the [simpleTSC]. */
  private val simpleTSCLeafNode:
      TSCNode<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> =
      simpleTSCLeafEdge.destination

  /** Holds the edge to the second leaf node of the [simpleTSC]. */
  private val simpleTSCLeafEdge2:
      TSCEdge<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> =
      simpleTSCRootNode.edges[1]

  /** Holds the node of the second leaf node of the [simpleTSC]. */
  private val simpleTSCLeafNode2:
      TSCNode<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> =
      simpleTSCLeafEdge2.destination

  /** Holds a valid [TSCInstance] for the [simpleTSC]. */
  private val simpleTSCValidInstance =
      TSCInstance(
          rootNode =
              TSCInstanceNode(simpleTSC.rootNode).apply {
                edges +=
                    listOf(
                        TSCInstanceEdge(TSCInstanceNode(simpleTSCLeafNode), simpleTSCLeafEdge),
                        TSCInstanceEdge(TSCInstanceNode(simpleTSCLeafNode2), simpleTSCLeafEdge2))
              },
          sourceSegmentIdentifier = "file 1")

  /** Holds a valid [TSCInstance] for the [simpleTSC]. */
  private val simpleTSCValidInstance2 =
      TSCInstance(
          rootNode =
              TSCInstanceNode(simpleTSC.rootNode).apply {
                edges +=
                    listOf(
                        TSCInstanceEdge(TSCInstanceNode(simpleTSCLeafNode), simpleTSCLeafEdge),
                        TSCInstanceEdge(TSCInstanceNode(simpleTSCLeafNode2), simpleTSCLeafEdge2))
              },
          sourceSegmentIdentifier = "file 1")

  /** Holds a valid [TSCInstance] for the [simpleTSC]. */
  private val simpleTSCValidInstance3 =
      TSCInstance(
          rootNode =
              TSCInstanceNode(simpleTSC.rootNode).apply {
                edges +=
                    listOf(
                        TSCInstanceEdge(TSCInstanceNode(simpleTSCLeafNode), simpleTSCLeafEdge),
                        TSCInstanceEdge(TSCInstanceNode(simpleTSCLeafNode2), simpleTSCLeafEdge2))
              },
          sourceSegmentIdentifier = "file 2")

  /** Holds an invalid [TSCInstance] for the [simpleTSC]. */
  private val simpleTSCInvalidInstance =
      TSCInstance(
          rootNode =
              TSCInstanceNode(simpleTSC.rootNode).apply {
                edges +=
                    listOf(TSCInstanceEdge(TSCInstanceNode(simpleTSCLeafNode), simpleTSCLeafEdge))
              },
          sourceSegmentIdentifier = "file 1")

  // endregion

  // region TSC 2 definition
  /** Holds a simple [TSC] with one mandatory leaf nodes. */
  private val simpleTSC2 =
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        any("root") { leaf("leaf1") }
      }

  /** Holds the root node of the [simpleTSC2]. */
  private val simpleTSC2RootNode:
      TSCNode<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> =
      simpleTSC2.rootNode

  /** Holds the edge to the first leaf node of the [simpleTSC2]. */
  private val simpleTSC2LeafEdge = simpleTSC2RootNode.edges[0]

  /** Holds the node of the first leaf node of the [simpleTSC2]. */
  private val simpleTSC2LeafNode:
      TSCNode<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> =
      simpleTSC2LeafEdge.destination

  /** Holds a valid [TSCInstance] for the [simpleTSC2]. */
  private val simpleTSC2ValidInstance =
      TSCInstance(
          rootNode =
              TSCInstanceNode(simpleTSC2.rootNode).apply {
                edges +=
                    listOf(TSCInstanceEdge(TSCInstanceNode(simpleTSC2LeafNode), simpleTSC2LeafEdge))
              },
          sourceSegmentIdentifier = "")

  // endregion

  // region TSC 3 definition
  /** Holds a simple [TSC] with one mandatory leaf nodes. */
  private val simpleTSC3 =
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        optional("root") {
          leaf("leaf1")
          leaf("leaf2")
        }
      }

  // endregion

  /**
   * Tests the correct calculation and return of a [SerializableTSCResult] for a valid TSC instance.
   */
  @Test
  fun `Test return of valid TSC Result`() {
    val validTSCInstancesPerTSCMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Test serialization and calculation of one valid TSC instance
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCValidInstance)
    var serializedResult = validTSCInstancesPerTSCMetric.getSerializableResults()
    assertEquals(1, serializedResult.size)
    assertEquals("root", serializedResult[0].value[0].tscInstance.label)
    assertEquals(
        "leaf1", serializedResult[0].value[0].tscInstance.outgoingEdges[0].destination.label)
    assertEquals(
        "leaf2", serializedResult[0].value[0].tscInstance.outgoingEdges[1].destination.label)

    // Test serialization and calculation of one additional invalid TSC instance
    validTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)
    serializedResult = validTSCInstancesPerTSCMetric.getSerializableResults()
    assertEquals(1, serializedResult.size)
  }

  /**
   * Tests the correct calculation and return of a [SerializableTSCResult] for an invalid TSC
   * instance.
   */
  @Test
  fun `Test return of invalid TSC Result`() {
    val invalidTSCInstancesPerTSCMetric =
        InvalidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    // Test serialization and calculation of one invalid TSC instance
    invalidTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)
    var serializedResult = invalidTSCInstancesPerTSCMetric.getSerializableResults()
    assertEquals(1, serializedResult.size)
    assertEquals("root", serializedResult[0].value[0].tscInstance.label)
    assertEquals(
        "leaf1", serializedResult[0].value[0].tscInstance.outgoingEdges[0].destination.label)

    // Test serialization and calculation of one additional valid TSC instance
    invalidTSCInstancesPerTSCMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)
    serializedResult = invalidTSCInstancesPerTSCMetric.getSerializableResults()
    assertEquals(1, serializedResult.size)
  }

  /** Tests the correct comparison of two valid [SerializableTSCResult] with different results. */
  @Test
  fun `Test correct comparison of two different valid TSC results`() {
    val currentMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    val groundTruthMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    currentMetric.evaluate(simpleTSC, simpleTSCValidInstance)
    groundTruthMetric.evaluate(simpleTSC2, simpleTSC2ValidInstance)

    val currentResult = currentMetric.getSerializableResults()
    val groundTruthResult = groundTruthMetric.getSerializableResults()

    val comparison = currentResult.compareTo(groundTruthResult)

    assertEquals(1, comparison.size)
    assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, comparison[0].verdict)
  }

  /** Tests the correct comparison of two invalid [SerializableTSCResult] with different results. */
  @Test
  fun `Test correct comparison of two different invalid TSC results`() {
    val currentMetric =
        InvalidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    val groundTruthMetric =
        InvalidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    currentMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)
    groundTruthMetric.evaluate(simpleTSC2, simpleTSCInvalidInstance)

    val currentResult = currentMetric.getSerializableResults()
    assertEquals(1, currentResult.size)

    val groundTruthResult = groundTruthMetric.getSerializableResults()
    assertEquals(1, groundTruthResult.size)

    val comparison = currentResult.compareTo(groundTruthResult)

    assertEquals(1, comparison.size)
    assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, comparison[0].verdict)
  }

  /**
   * Tests the correct comparison of two valid [SerializableTSCResult] with the same content from
   * the same segment source.
   */
  @Test
  fun `Test correct comparison of two same valid TSC results from equal segment sources`() {
    val currentMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    val groundTruthMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    currentMetric.evaluate(simpleTSC, simpleTSCValidInstance)
    groundTruthMetric.evaluate(simpleTSC, simpleTSCValidInstance2)

    val currentResult = currentMetric.getSerializableResults()
    assertEquals(1, currentResult.size)

    val groundTruthResult = groundTruthMetric.getSerializableResults()
    assertEquals(1, groundTruthResult.size)

    val comparison = currentResult.compareTo(groundTruthResult)

    assertEquals(1, comparison.size)
    assertEquals(SerializableResultComparisonVerdict.EQUAL_RESULTS, comparison[0].verdict)
  }

  /**
   * Tests the correct comparison of two valid [SerializableTSCResult] with the same content from
   * different segment sources.
   */
  @Test
  fun `Test correct comparison of two same valid TSC results from different segment sources`() {
    val currentMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    val groundTruthMetric =
        ValidTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    currentMetric.evaluate(simpleTSC, simpleTSCValidInstance)
    groundTruthMetric.evaluate(simpleTSC, simpleTSCValidInstance3)

    val currentResult = currentMetric.getSerializableResults()
    assertEquals(1, currentResult.size)

    val groundTruthResult = groundTruthMetric.getSerializableResults()
    assertEquals(1, groundTruthResult.size)

    val comparison = currentResult.compareTo(groundTruthResult)

    assertEquals(1, comparison.size)
    assertEquals(SerializableResultComparisonVerdict.NOT_EQUAL_RESULTS, comparison[0].verdict)
  }

  /**
   * Test the correct calculation and return of [SerializableTSCResult] for one missed TSC instance.
   */
  @Test
  fun `Test return of one missed TSC instance`() {
    val missedInstancesMetric =
        MissedTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    missedInstancesMetric.evaluate(simpleTSC, simpleTSCInvalidInstance)

    val missedInstancesResult = missedInstancesMetric.getSerializableResults()
    assertEquals(1, missedInstancesResult.size)

    assertEquals(1, missedInstancesResult[0].value.size)
  }

  /**
   * Test the correct calculation and return of [SerializableTSCResult] for all missed TSC
   * instances.
   */
  @Test
  fun `Test return of all missed TSC instanced`() {
    val missedInstancesMetric =
        MissedTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    missedInstancesMetric.evaluate(simpleTSC3, simpleTSCInvalidInstance)

    val missedInstancesResult = missedInstancesMetric.getSerializableResults()
    assertEquals(1, missedInstancesResult.size)

    assertEquals(3, missedInstancesResult[0].value.size)
  }

  /**
   * Test the correct calculation and return of [SerializableTSCResult] for no missed TSC instance.
   */
  @Test
  fun `Test return of no missed TSC instances`() {
    val missedInstancesMetric =
        MissedTSCInstancesPerTSCMetric<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>()

    missedInstancesMetric.evaluate(simpleTSC, simpleTSCValidInstance)

    val missedInstancesResult = missedInstancesMetric.getSerializableResults()
    assertEquals(1, missedInstancesResult.size)

    assertEquals(0, missedInstancesResult[0].value.size)
  }
}
