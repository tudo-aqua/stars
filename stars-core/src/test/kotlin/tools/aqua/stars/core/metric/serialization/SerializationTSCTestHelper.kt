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

import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.SimpleTickDataDifference
import tools.aqua.stars.core.SimpleTickDataUnit
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.tsc
import tools.aqua.stars.core.tsc.edge.TSCEdge
import tools.aqua.stars.core.tsc.instance.TSCInstance
import tools.aqua.stars.core.tsc.instance.TSCInstanceEdge
import tools.aqua.stars.core.tsc.instance.TSCInstanceNode
import tools.aqua.stars.core.tsc.node.TSCNode

// region TSC 1 definition
/** Holds a simple [TSC] with two mandatory leaf nodes. */
val simpleTSC =
    tsc<SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> {
      all("root") {
        leaf("leaf1")
        leaf("leaf2")
      }
    }

/** Holds the root node of the [simpleTSC]. */
val simpleTSCRootNode:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC.rootNode

/** Holds the edge to the first leaf node of the [simpleTSC]. */
val simpleTSCLeafEdge:
    TSCEdge<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSCRootNode.edges[0]

/** Holds the node of the first leaf node of the [simpleTSC]. */
val simpleTSCLeafNode:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSCLeafEdge.destination

/** Holds the edge to the second leaf node of the [simpleTSC]. */
val simpleTSCLeafEdge2:
    TSCEdge<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSCRootNode.edges[1]

/** Holds the node of the second leaf node of the [simpleTSC]. */
val simpleTSCLeafNode2:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSCLeafEdge2.destination

/** Holds a valid [TSCInstance] for the [simpleTSC]. */
val simpleTSCValidInstance =
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
val simpleTSCValidInstance2 =
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
val simpleTSCValidInstance3 =
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
val simpleTSCInvalidInstance =
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
val simpleTSC2 =
    tsc<SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> {
      any("root") { leaf("leaf1") { monitors { monitor("leaf1monitor") { true } } } }
    }

/** Holds the root node of the [simpleTSC2]. */
val simpleTSC2RootNode:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC2.rootNode

/** Holds the edge to the first leaf node of the [simpleTSC2]. */
val simpleTSC2LeafEdge = simpleTSC2RootNode.edges[0]

/** Holds the node of the first leaf node of the [simpleTSC2]. */
val simpleTSC2LeafNode:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC2LeafEdge.destination

/** Holds a valid [TSCInstance] for the [simpleTSC2]. */
val simpleTSC2ValidInstance =
    TSCInstance(
        rootNode =
            TSCInstanceNode(simpleTSC2.rootNode).apply {
              edges +=
                  listOf(
                      TSCInstanceEdge(
                          TSCInstanceNode(
                              simpleTSC2LeafNode, monitorResults = mapOf("leaf1monitor" to true)),
                          simpleTSC2LeafEdge))
            },
        sourceSegmentIdentifier = "")

// endregion

// region TSC 3 definition
/** Holds a simple [TSC] with one mandatory leaf nodes. */
val simpleTSC3 =
    tsc<SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> {
      optional("root") {
        leaf("leaf1") {
          condition { true } // Condition necessary, as automatic 'always true'-edges are filtered
        }
        leaf("leaf2") {
          condition { true } // Condition necessary, as automatic 'always true'-edges are filtered
        }
      }
    }

/** Holds the root node of the [simpleTSC3]. */
val simpleTSC3RootNode:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC3.rootNode

/** Holds the edge to the first leaf node of the [simpleTSC3]. */
val simpleTSC3LeafEdge:
    TSCEdge<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC3RootNode.edges[0]

/** Holds the node of the first leaf node of the [simpleTSC3]. */
val simpleTSC3LeafNode:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC3LeafEdge.destination

/** Holds the edge to the second leaf node of the [simpleTSC3]. */
val simpleTSC3LeafEdge2:
    TSCEdge<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC3RootNode.edges[1]

/** Holds the node of the second leaf node of the [simpleTSC3]. */
val simpleTSC3LeafNode2:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC3LeafEdge2.destination

/** Holds a valid [TSCInstance] for the [simpleTSC3] with both leaf nodes present. */
val simpleTSC3ValidInstance =
    TSCInstance(
        rootNode =
            TSCInstanceNode(simpleTSC3.rootNode).apply {
              edges +=
                  listOf(
                      TSCInstanceEdge(TSCInstanceNode(simpleTSC3LeafNode), simpleTSC3LeafEdge),
                      TSCInstanceEdge(TSCInstanceNode(simpleTSC3LeafNode2), simpleTSC3LeafEdge2))
            },
        sourceSegmentIdentifier = "file 1")

/** Holds a valid [TSCInstance] for the [simpleTSC3] with only one leaf node present. */
val simpleTSC3ValidInstance2 =
    TSCInstance(
        rootNode =
            TSCInstanceNode(simpleTSC3.rootNode).apply {
              edges +=
                  listOf(TSCInstanceEdge(TSCInstanceNode(simpleTSC3LeafNode), simpleTSC3LeafEdge))
            },
        sourceSegmentIdentifier = "file 1")

/** Holds an invalid [TSCInstance] for the [simpleTSC3]. */
val simpleTSC3InvalidInstance =
    TSCInstance(
        rootNode =
            TSCInstanceNode(simpleTSC3.rootNode).apply {
              edges +=
                  listOf(TSCInstanceEdge(TSCInstanceNode(simpleTSC3LeafNode), simpleTSC3LeafEdge))
            },
        sourceSegmentIdentifier = "file 1")

// endregion

// region TSC 4 definition
/** Holds a simple [TSC] with one mandatory leaf nodes. */
val simpleTSC4 =
    tsc<SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> {
      optional("root") {
        leaf("leaf1") {
          condition { true } // Condition necessary, as automatic 'always true'-edges are filtered
        }
        leaf("leaf2") {
          condition { true } // Condition necessary, as automatic 'always true'-edges are filtered
        }
        leaf("leaf3") {
          condition { true } // Condition necessary, as automatic 'always true'-edges are filtered
        }
      }
    }

/** Holds the root node of the [simpleTSC4]. */
val simpleTSC4RootNode:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC4.rootNode

/** Holds the edge to the first leaf node of the [simpleTSC4]. */
val simpleTSC4LeafEdge:
    TSCEdge<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC4RootNode.edges[0]

/** Holds the node of the first leaf node of the [simpleTSC4]. */
val simpleTSC4LeafNode:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC4LeafEdge.destination

/** Holds the edge to the second leaf node of the [simpleTSC4]. */
val simpleTSC4LeafEdge2:
    TSCEdge<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC4RootNode.edges[1]

/** Holds the node of the second leaf node of the [simpleTSC4]. */
val simpleTSC4LeafNode2:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC4LeafEdge2.destination

/** Holds the edge to the second leaf node of the [simpleTSC4]. */
val simpleTSC4LeafEdge3:
    TSCEdge<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC4RootNode.edges[2]

/** Holds the node of the second leaf node of the [simpleTSC4]. */
val simpleTSC4LeafNode3:
    TSCNode<
        SimpleEntity, SimpleTickData, SimpleSegment, SimpleTickDataUnit, SimpleTickDataDifference> =
    simpleTSC4LeafEdge3.destination

/** Holds a valid [TSCInstance] for the [simpleTSC4] with both leaf nodes present. */
val simpleTSC4ValidInstance =
    TSCInstance(
        rootNode =
            TSCInstanceNode(simpleTSC4.rootNode).apply {
              edges +=
                  listOf(
                      TSCInstanceEdge(TSCInstanceNode(simpleTSC4LeafNode), simpleTSC4LeafEdge),
                      TSCInstanceEdge(TSCInstanceNode(simpleTSC4LeafNode2), simpleTSC4LeafEdge2))
            },
        sourceSegmentIdentifier = "file 1")

/** Holds a valid [TSCInstance] for the [simpleTSC4] with only one leaf node present. */
val simpleTSC4ValidInstance2 =
    TSCInstance(
        rootNode =
            TSCInstanceNode(simpleTSC4.rootNode).apply {
              edges +=
                  listOf(
                      TSCInstanceEdge(
                          TSCInstanceNode(
                              simpleTSC4LeafNode, monitorResults = mapOf("leaf1monitor" to true)),
                          simpleTSC4LeafEdge),
                      TSCInstanceEdge(
                          TSCInstanceNode(
                              simpleTSC4LeafNode2, monitorResults = mapOf("leaf1monitor" to false)),
                          simpleTSC4LeafEdge2),
                      TSCInstanceEdge(
                          TSCInstanceNode(
                              simpleTSC4LeafNode3, monitorResults = mapOf("leaf1monitor" to false)),
                          simpleTSC4LeafEdge3))
            },
        sourceSegmentIdentifier = "file 1")

/** Holds a valid [TSCInstance] for the [simpleTSC4] with no leaf node present. */
val simpleTSC4ValidInstance3 =
    TSCInstance(rootNode = TSCInstanceNode(simpleTSC4.rootNode), sourceSegmentIdentifier = "file 1")

/** Holds an invalid [TSCInstance] for the [simpleTSC4]. */
val simpleTSC4InvalidInstance =
    TSCInstance(
        rootNode =
            TSCInstanceNode(simpleTSC4.rootNode).apply {
              edges +=
                  listOf(TSCInstanceEdge(TSCInstanceNode(simpleTSC4LeafNode), simpleTSC4LeafEdge))
            },
        sourceSegmentIdentifier = "file 1")

// endregion
