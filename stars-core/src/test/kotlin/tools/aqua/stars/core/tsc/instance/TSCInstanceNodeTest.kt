/*
 * Copyright 2025 The STARS Project Authors
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

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.tsc.builder.tsc

typealias E = SimpleEntity

typealias T = SimpleTickData

typealias S = SimpleSegment

typealias U = SimpleTickDataUnit

typealias D = SimpleTickDataDifference

/** Test class for [TSCInstanceNode]. */
class TSCInstanceNodeTest {

  /** Tests the [TSCInstanceNode.isValid] function with a valid [TSCInstance]. */
  @Test
  fun `Test isValid() for valid TSCInstance`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("Root") {
            leaf("Leaf 1")
            leaf("Leaf 2")
          }
        }

    val testInstance =
        TSCInstance(
            sourceSegmentIdentifier = "",
            rootNode =
                TSCInstanceNode(
                        tscNode = testTSC.rootNode,
                    )
                    .apply {
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[0].destination),
                              testTSC.rootNode.edges[0],
                          )
                      )
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[1].destination),
                              testTSC.rootNode.edges[1],
                          )
                      )
                    },
        )

    assertTrue { testInstance.isValid() }
  }

  /** Tests the [TSCInstanceNode.isValid] function with an invalid [TSCInstance]. */
  @Test
  fun `Test isValid() for invalid TSCInstance`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("Root") {
            leaf("Leaf 1")
            leaf("Leaf 2")
          }
        }

    val testInstance =
        TSCInstance(
            sourceSegmentIdentifier = "",
            rootNode =
                TSCInstanceNode(
                        tscNode = testTSC.rootNode,
                    )
                    .apply {
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[0].destination),
                              testTSC.rootNode.edges[0],
                          )
                      )
                    },
        )

    assertFalse { testInstance.isValid() }
  }

  /** Test getFeatureNodeEdges() for TSCInstance with two leaf nodes. */
  @Test
  fun `Test getFeatureNodeEdges() for TSCInstance with two leaf nodes`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("Root") {
            leaf("Leaf 1") { condition { true } }
            leaf("Leaf 2") { condition { true } }
          }
        }

    val testInstance =
        TSCInstance(
            sourceSegmentIdentifier = "",
            rootNode =
                TSCInstanceNode(
                        tscNode = testTSC.rootNode,
                    )
                    .apply {
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[0].destination),
                              testTSC.rootNode.edges[0],
                          )
                      )
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[1].destination),
                              testTSC.rootNode.edges[1],
                          )
                      )
                    },
        )

    assertContentEquals(
        listOf("Leaf 1", "Leaf 2"),
        testInstance.rootNode.extractLeafLabels(),
    )
  }

  /** Test getFeatureNodeEdges() for TSCInstance with two leaf nodes and optional leaf. */
  @Test
  fun `Test getFeatureNodeEdges() for TSCInstance with two leaf nodes and optional leaf`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("Root") {
            leaf("Leaf 1") { condition { true } }
            leaf("Leaf 2") { condition { true } }
            optional("Optional 1") {
              condition { true }
              leaf("Leaf 3") { condition { false } }
            }
          }
        }

    val testInstance =
        TSCInstance(
            sourceSegmentIdentifier = "",
            rootNode =
                TSCInstanceNode(
                        tscNode = testTSC.rootNode,
                    )
                    .apply {
                      // Leaf 1
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[0].destination),
                              testTSC.rootNode.edges[0],
                          )
                      )
                      // Leaf 2
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[1].destination),
                              testTSC.rootNode.edges[1],
                          )
                      )
                      // Optional 1
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[2].destination),
                              testTSC.rootNode.edges[2],
                          )
                      )
                    },
        )

    assertEquals(
        listOf("Leaf 1", "Leaf 2", "Optional 1"),
        testInstance.rootNode.extractLeafLabels(),
    )
  }

  /**
   * Test getFeatureNodeEdges() for TSCInstance with two leaf nodes and not base-condition optional.
   */
  @Test
  fun `Test getFeatureNodeEdges() for TSCInstance with two leaf nodes and base-condition optional`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("Root") {
            leaf("Leaf 1") { condition { true } }
            leaf("Leaf 2") { condition { true } }
            optional("Optional 1") { leaf("Leaf 3") }
          }
        }

    val testInstance =
        TSCInstance(
            sourceSegmentIdentifier = "",
            rootNode =
                TSCInstanceNode(
                        tscNode = testTSC.rootNode,
                    )
                    .apply {
                      // Leaf 1
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[0].destination),
                              testTSC.rootNode.edges[0],
                          )
                      )
                      // Leaf 2
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[1].destination),
                              testTSC.rootNode.edges[1],
                          )
                      )
                    },
        )

    assertEquals(
        listOf("Leaf 1", "Leaf 2"),
        testInstance.rootNode.extractLeafLabels(),
    )
  }

  /**
   * Test getFeatureNodeEdges() for TSCInstance with two leaf nodes and base-condition optional that
   * is not in instance.
   */
  @Test
  fun `Test getFeatureNodeEdges() for TSCInstance with two leaf nodes and base-condition optional that is not in instance`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("Root") {
            leaf("Leaf 1") { condition { true } }
            leaf("Leaf 2") { condition { true } }
            optional("Optional 1") { leaf("Leaf 3") }
          }
        }

    val testInstance =
        TSCInstance(
            sourceSegmentIdentifier = "",
            rootNode =
                TSCInstanceNode(
                        tscNode = testTSC.rootNode,
                    )
                    .apply {
                      // Leaf 1
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[0].destination),
                              testTSC.rootNode.edges[0],
                          )
                      )
                      // Leaf 2
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[1].destination),
                              testTSC.rootNode.edges[1],
                          )
                      )
                      // Optional 1
                      edges.add(
                          TSCInstanceEdge(
                              TSCInstanceNode(tscNode = testTSC.rootNode.edges[2].destination),
                              testTSC.rootNode.edges[2],
                          )
                      )
                    },
        )

    assertEquals(
        listOf("Leaf 1", "Leaf 2"),
        testInstance.rootNode.extractLeafLabels(),
    )
  }

  /** Test getFeatureNodeEdges() for TSCInstance with concatenated inner feature nodes. */
  @Test
  fun `Test getFeatureNodeEdges() for TSCInstance with concatenated inner feature nodes`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("Root") {
            optional("Optional 1") {
              condition { true }
              optional("Optional 2") {
                condition { true }
                optional("Optional 3") {
                  condition { true }
                  leaf("Leaf 1") { condition { true } }
                }
              }
            }
          }
        }

    val root = testTSC.rootNode

    // References from the base TSC (structure source of truth)
    val opt1EdgeRef = root.edges[0]
    val opt1NodeRef = opt1EdgeRef.destination

    val opt2EdgeRef = opt1NodeRef.edges[0]
    val opt2NodeRef = opt2EdgeRef.destination

    val opt3EdgeRef = opt2NodeRef.edges[0]
    val opt3NodeRef = opt3EdgeRef.destination

    val leaf1EdgeRef = opt3NodeRef.edges[0]
    val leaf1NodeRef = leaf1EdgeRef.destination

    // Build a matching TSCInstance tree (destination nodes mirror the TSC nodes)
    val rootInst = TSCInstanceNode(tscNode = root)
    val opt1Inst = TSCInstanceNode(tscNode = opt1NodeRef)
    val opt2Inst = TSCInstanceNode(tscNode = opt2NodeRef)
    val opt3Inst = TSCInstanceNode(tscNode = opt3NodeRef)
    val leaf1Inst = TSCInstanceNode(tscNode = leaf1NodeRef)

    // Wire edges in the same order as the base TSC
    rootInst.edges += TSCInstanceEdge(destination = opt1Inst, tscEdge = opt1EdgeRef)
    opt1Inst.edges += TSCInstanceEdge(destination = opt2Inst, tscEdge = opt2EdgeRef)
    opt2Inst.edges += TSCInstanceEdge(destination = opt3Inst, tscEdge = opt3EdgeRef)
    opt3Inst.edges += TSCInstanceEdge(destination = leaf1Inst, tscEdge = leaf1EdgeRef)

    // Final test instance
    val testInstance =
        TSCInstance(
            sourceSegmentIdentifier = "",
            rootNode = rootInst,
        )

    assertEquals(
        listOf("Optional 1", "Optional 2", "Optional 3", "Leaf 1"),
        testInstance.rootNode.extractLeafLabels(),
    )
  }

  /**
   * Test getFeatureNodeEdges() for TSCInstance with concatenated inner feature nodes with one being
   * no feature.
   */
  @Test
  fun `Test getFeatureNodeEdges() for TSCInstance with concatenated inner feature nodes with one being no feature`() {
    val testTSC =
        tsc<E, T, S, U, D> {
          all("Root") {
            optional("Optional 1") {
              condition { true }
              optional("Optional 2") {
                optional("Optional 3") {
                  condition { true }
                  leaf("Leaf 1") { condition { true } }
                }
              }
            }
          }
        }

    val root = testTSC.rootNode

    // References from the base TSC (structure source of truth)
    val opt1EdgeRef = root.edges[0]
    val opt1NodeRef = opt1EdgeRef.destination

    val opt2EdgeRef = opt1NodeRef.edges[0]
    val opt2NodeRef = opt2EdgeRef.destination

    val opt3EdgeRef = opt2NodeRef.edges[0]
    val opt3NodeRef = opt3EdgeRef.destination

    val leaf1EdgeRef = opt3NodeRef.edges[0]
    val leaf1NodeRef = leaf1EdgeRef.destination

    // Build a matching TSCInstance tree (destination nodes mirror the TSC nodes)
    val rootInst = TSCInstanceNode(tscNode = root)
    val opt1Inst = TSCInstanceNode(tscNode = opt1NodeRef)
    val opt2Inst = TSCInstanceNode(tscNode = opt2NodeRef)
    val opt3Inst = TSCInstanceNode(tscNode = opt3NodeRef)
    val leaf1Inst = TSCInstanceNode(tscNode = leaf1NodeRef)

    // Wire edges in the same order as the base TSC
    rootInst.edges += TSCInstanceEdge(destination = opt1Inst, tscEdge = opt1EdgeRef)
    opt1Inst.edges += TSCInstanceEdge(destination = opt2Inst, tscEdge = opt2EdgeRef)
    opt2Inst.edges += TSCInstanceEdge(destination = opt3Inst, tscEdge = opt3EdgeRef)
    opt3Inst.edges += TSCInstanceEdge(destination = leaf1Inst, tscEdge = leaf1EdgeRef)

    // Final test instance
    val testInstance =
        TSCInstance(
            sourceSegmentIdentifier = "",
            rootNode = rootInst,
        )

    assertEquals(
        listOf("Optional 1", "Optional 3", "Leaf 1"),
        testInstance.rootNode.extractLeafLabels(),
    )
  }
}
