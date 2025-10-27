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
}
