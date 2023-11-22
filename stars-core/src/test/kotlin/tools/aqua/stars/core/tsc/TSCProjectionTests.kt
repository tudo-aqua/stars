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

import kotlin.test.Test
import tools.aqua.stars.core.SimpleEntity
import tools.aqua.stars.core.SimpleSegment
import tools.aqua.stars.core.SimpleTickData
import tools.aqua.stars.core.tsc.builder.all
import tools.aqua.stars.core.tsc.builder.root
import tools.aqua.stars.core.tsc.projection.proj

class TSCProjectionTests {

  /**
   * This test check that with two existing projections in the TSC, exactly two projections are
   * returned by 'TSCNode.buildProjections'.
   */
  @Test
  fun testSimpleProjectionBuilding() {
    val projection1 = "projection1"
    val projection2 = "projection2"
    val tsc =
        root<SimpleEntity, SimpleTickData, SimpleSegment> {
          all("root") { projectionIDs = mapOf(proj(projection1), proj(projection2)) }
        }

    val projections = tsc.buildProjections()
    // Check that exactly two projections are produced
    assert(projections.size == 2)
    // Check that the projections are correctly represented in their respective TSCProjection class
    assert(projections.any { it.id == projection1 })
    assert(projections.any { it.id == projection2 })
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
        root<SimpleEntity, SimpleTickData, SimpleSegment> {
          all("root") { projectionIDs = mapOf(proj(projection1), proj(projection2)) }
        }

    var projections = tsc.buildProjections()
    // Check that exactly two projections are produced
    assert(projections.size == 2)
    // Check that the projections are correctly represented in their respective TSCProjection class
    assert(projections.any { it.id == projection1 })
    assert(projections.any { it.id == projection2 })

    // Check empty ignore list. The projections should not be filtered
    projections = tsc.buildProjections(listOf())
    // Check that exactly two projections are produced
    assert(projections.size == 2)
    // Check that the projections are correctly represented in their respective TSCProjection class
    assert(projections.any { it.id == projection1 })
    assert(projections.any { it.id == projection2 })

    // Check ignore list with projection 1 to be ignored
    projections = tsc.buildProjections(listOf(projection1))
    // Check that exactly one projection is produced
    assert(projections.size == 1)
    // Check that projection 1 is not included, while projection 2 is still in the result
    assert(!projections.any { it.id == projection1 })
    assert(projections.any { it.id == projection2 })

    // Check ignore list with projection 2 to be ignored
    projections = tsc.buildProjections(listOf(projection2))
    // Check that exactly one projection is produced
    assert(projections.size == 1)
    // Check that projection 2 is not included, while projection 1 is still in the result
    assert(projections.any { it.id == projection1 })
    assert(!projections.any { it.id == projection2 })

    // Check ignore list with both projections to be ignored
    projections = tsc.buildProjections(listOf(projection1, projection2))
    // Check that exactly zero projections are produced
    assert(projections.isEmpty())
    // Check that projection 2 is not included, while projection 1 is still in the result
    assert(!projections.any { it.id == projection1 })
    assert(!projections.any { it.id == projection2 })
  }

  /**
   * This test checks that no projections are returned by 'TSCNode.buildProjections' when no
   * projections are defined in the TSC.
   */
  @Test
  fun testNoExistingProjections() {
    val tsc = root<SimpleEntity, SimpleTickData, SimpleSegment> { all("root") }

    val projections = tsc.buildProjections()
    // Check that exactly two projections are produced
    assert(projections.isEmpty())
  }
}
