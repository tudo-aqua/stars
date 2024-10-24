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
import tools.aqua.stars.core.metric.metrics.evaluation.ValidTSCInstancesPerTSCMetric
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.tsc
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode

class SerializableTSCResultTest {

  // region TSC definition
  /** Defines a simple [TSC]. */
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
  private val simpleTSCRootNode = simpleTSC.rootNode
  private val simpleTSCLeafEdge = simpleTSCRootNode.edges[0]
  private val simpleTSCLeafNode = simpleTSCLeafEdge.destination
  private val simpleTSCLeafEdge2 = simpleTSCRootNode.edges[1]
  private val simpleTSCLeafNode2 = simpleTSCLeafEdge2.destination

  /** Holds a valid [TSCInstance] for the [simpleTSC]. */
  private val simpleTSCValidInstance =
      TSCInstance<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference>(
          rootNode =
              TSCInstanceNode<
                  SimpleEntity,
                  SimpleTickData,
                  SimpleSegment,
                  SimpleTickDataUnit,
                  SimpleTickDataDifference>(simpleTSC.rootNode).apply {
                edges +=
                    listOf(
                        TSCInstanceEdge<
                            SimpleEntity,
                            SimpleTickData,
                            SimpleSegment,
                            SimpleTickDataUnit,
                            SimpleTickDataDifference>(TSCInstanceNode<
                            SimpleEntity,
                            SimpleTickData,
                            SimpleSegment,
                            SimpleTickDataUnit,
                            SimpleTickDataDifference>(simpleTSCLeafNode), simpleTSCLeafEdge),
                        TSCInstanceEdge<
                            SimpleEntity,
                            SimpleTickData,
                            SimpleSegment,
                            SimpleTickDataUnit,
                            SimpleTickDataDifference>(TSCInstanceNode<
                            SimpleEntity,
                            SimpleTickData,
                            SimpleSegment,
                            SimpleTickDataUnit,
                            SimpleTickDataDifference>(simpleTSCLeafNode2), simpleTSCLeafEdge2))
              },
          sourceSegmentIdentifier = "")

  /** Holds an invalid [TSCInstance] for the [simpleTSC]. */
  private val simpleTSCInvalidInstance =
      TSCInstance<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference>(
          rootNode =
              TSCInstanceNode<
                  SimpleEntity,
                  SimpleTickData,
                  SimpleSegment,
                  SimpleTickDataUnit,
                  SimpleTickDataDifference>(simpleTSC.rootNode).apply {
                edges +=
                    listOf(TSCInstanceEdge<
                        SimpleEntity,
                        SimpleTickData,
                        SimpleSegment,
                        SimpleTickDataUnit,
                        SimpleTickDataDifference>(TSCInstanceNode<
                        SimpleEntity,
                        SimpleTickData,
                        SimpleSegment,
                        SimpleTickDataUnit,
                        SimpleTickDataDifference>(simpleTSCLeafNode), simpleTSCLeafEdge))
              },
          sourceSegmentIdentifier = "")

  /** Defines a simple [TSC]. */
  private val simpleTSC2 =
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        any("root") { leaf("leaf1") }
      }
  private val simpleTSC2RootNode = simpleTSC2.rootNode
  private val simpleTSC2LeafEdge = simpleTSC2RootNode.edges[0]
  private val simpleTSC2LeafNode = simpleTSC2LeafEdge.destination

  /** Holds a valid [TSCInstance] for the [simpleTSC2]. */
  private val simpleTSC2ValidInstance =
      TSCInstance(
          rootNode =
              TSCInstanceNode<
                  SimpleEntity,
                  SimpleTickData,
                  SimpleSegment,
                  SimpleTickDataUnit,
                  SimpleTickDataDifference>(simpleTSC2.rootNode).apply {
                edges +=
                    listOf(TSCInstanceEdge<
                        SimpleEntity,
                        SimpleTickData,
                        SimpleSegment,
                        SimpleTickDataUnit,
                        SimpleTickDataDifference>(TSCInstanceNode<
                        SimpleEntity,
                        SimpleTickData,
                        SimpleSegment,
                        SimpleTickDataUnit,
                        SimpleTickDataDifference>(simpleTSC2LeafNode), simpleTSC2LeafEdge))
              },
          sourceSegmentIdentifier = "")

  /** Holds an invalid [TSCInstance] for the [simpleTSC2]. */
  private val simpleTSC2InvalidInstance =
      TSCInstance(rootNode = TSCInstanceNode(simpleTSC.rootNode), sourceSegmentIdentifier = "")

  // endregion

  /**
   * Tests the correct calculation and setting of a [SerializableTSCResult] for a valid TSC
   * instance.
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
   * Tests the correct calculation and setting of a [SerializableTSCResult] for an invalid TSC
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

  @Test
  fun `Test correct comparison of two different TSC results`() {
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
  }
}
