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

package tools.aqua.stars.core.tsc

import kotlin.test.Test
import kotlin.test.assertNotNull
import tools.aqua.stars.core.*
import tools.aqua.stars.core.tsc.builder.tsc

/** Tests for projections. */
class TSCProjectionTest {

  /**
   * This test check that with two existing projections in the TSC, exactly two projections are
   * returned by 'TSCNode.buildProjections'.
   */
  @Test
  fun testSimpleProjectionBuilding() {
    val projection1 = "projection1"
    val projection2 = "projection2"
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all("root") {
            projections {
              projection(projection1)
              projection(projection2)
            }
          }
        }

    val projections = tsc.buildProjections()
    // Check that exactly two projections are produced
    assert(projections.size == 2)
    // Check that the projections are correctly represented in their respective TSCProjection class
    assert(projections.any { it.identifier == projection1 })
    assert(projections.any { it.identifier == projection2 })
  }

  /**
   * This test checks that the ignore list, which is given as a parameter to
   * 'TSCNode.buildProjections', is working correctly.
   */
  @Test
  fun testProjectionBuildingWithIgnoreList() {
    val projection1 = "projection1"
    val projection2 = "projection2"
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all("root") {
            projections {
              projection(projection1)
              projection(projection2)
            }
          }
        }

    var projections = tsc.buildProjections()
    // Check that exactly two projections are produced
    assert(projections.size == 2)
    // Check that the projections are correctly represented in their respective TSCProjection class
    assert(projections.any { it.identifier == projection1 })
    assert(projections.any { it.identifier == projection2 })

    // Check empty ignore list. The projections should not be filtered
    projections = tsc.buildProjections(listOf())
    // Check that exactly two projections are produced
    assert(projections.size == 2)
    // Check that the projections are correctly represented in their respective TSCProjection class
    assert(projections.any { it.identifier == projection1 })
    assert(projections.any { it.identifier == projection2 })

    // Check ignore list with projection 1 to be ignored
    projections = tsc.buildProjections(listOf(projection1))
    // Check that exactly one projection is produced
    assert(projections.size == 1)
    // Check that projection 1 is not included, while projection 2 is still in the result
    assert(!projections.any { it.identifier == projection1 })
    assert(projections.any { it.identifier == projection2 })

    // Check ignore list with projection 2 to be ignored
    projections = tsc.buildProjections(listOf(projection2))
    // Check that exactly one projection is produced
    assert(projections.size == 1)
    // Check that projection 2 is not included, while projection 1 is still in the result
    assert(projections.any { it.identifier == projection1 })
    assert(!projections.any { it.identifier == projection2 })

    // Check ignore list with both projections to be ignored
    projections = tsc.buildProjections(listOf(projection1, projection2))
    // Check that exactly zero projections are produced
    assert(projections.isEmpty())
    // Check that projection 2 is not included, while projection 1 is still in the result
    assert(!projections.any { it.identifier == projection1 })
    assert(!projections.any { it.identifier == projection2 })
  }

  /**
   * This test checks that no projections are returned by 'TSCNode.buildProjections' when no
   * projections are defined in the TSC.
   */
  @Test
  fun testNoExistingProjections() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all("root")
        }

    val projections = tsc.buildProjections()
    // Check that exactly two projections are produced
    assert(projections.isEmpty())
  }

  /** This test check that projection correctly split the TSC. */
  @Test
  fun testComplexProjectionBuilding() {
    val projectionAll = "projection_all"
    val projectionSub1 = "projection_sub1"
    val projectionSub2 = "projection_sub2"
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("root") {
            projections {
              projectionRecursive(projectionAll)
              projection(projectionSub1)
              projection(projectionSub2)
            }

            all("all") {
              projections { projectionRecursive(projectionSub1) }

              // Should be included in the projectionAll and in the projectionSub1.
              leaf("leaf_all_1")
              leaf("leaf_all_2")
            }

            exclusive("exclusive") {
              projections { projectionRecursive(projectionSub2) }

              // Should be included in the projectionAll and in the projectionSub2.
              leaf("leaf_exclusive_1")
              leaf("leaf_exclusive_2")
            }
          }
        }

    val projections = tsc.buildProjections()

    // Check that the projections are correctly represented in their respective TSCProjection class
    val projectionAllTSC = projections.find { it.identifier == projectionAll }
    val projectionSub1TSC = projections.find { it.identifier == projectionSub1 }
    val projectionSub2TSC = projections.find { it.identifier == projectionSub2 }
    assertNotNull(projectionAllTSC)
    assertNotNull(projectionSub1TSC)
    assertNotNull(projectionSub2TSC)

    // Check that no more projections are produced
    assert(projections.size == 3)

    // Assert the "all" projection contains both leafs
    val expectedLabelsAll =
        listOf(
            "root",
            "all",
            "leaf_all_1",
            "leaf_all_2",
            "exclusive",
            "leaf_exclusive_1",
            "leaf_exclusive_2")
    assert(projectionAllTSC.map { it.label } == expectedLabelsAll)

    val expectedLabelsSub1 = listOf("root", "all", "leaf_all_1", "leaf_all_2")
    assert(projectionSub1TSC.map { it.label } == expectedLabelsSub1)

    val expectedLabelsSub2 = listOf("root", "exclusive", "leaf_exclusive_1", "leaf_exclusive_2")
    assert(projectionSub2TSC.map { it.label } == expectedLabelsSub2)
  }
}
