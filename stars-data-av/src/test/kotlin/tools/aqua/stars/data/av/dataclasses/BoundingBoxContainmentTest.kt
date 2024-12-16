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

package tools.aqua.stars.data.av.dataclasses

import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse

class BoundingBoxContainmentTest {

  val axisAlignedBoundingBox: BoundingBox2D
    get() =
        BoundingBox2D(
            leftFront = Location2D(0.0, 2.0),
            rightFront = Location2D(2.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 0.0))

  val orientedBoundingBox: BoundingBox2D
    get() =
        BoundingBox2D(
            leftFront = Location2D(2.0, 5.0),
            rightFront = Location2D(5.0, 3.0),
            rightBack = Location2D(3.0, 0.0),
            leftBack = Location2D(0.0, 2.0))

  // region AABB
  @Test
  fun `Test AABoundingBox2D containing point`() {
    assertTrue(axisAlignedBoundingBox.containsPoint(Location2D(1.0, 1.0)))
  }

  @Test
  fun `Test AABoundingBox2D containing point on edge`() {
    assertTrue(axisAlignedBoundingBox.containsPoint(Location2D(0.0, 1.0)))
  }

  @Test
  fun `Test AABoundingBox2D containing point on vertex`() {
    assertTrue(axisAlignedBoundingBox.containsPoint(Location2D(2.0, 2.0)))
  }

  @Test
  fun `Test AABoundingBox2D not containing point`() {
    assertFalse(axisAlignedBoundingBox.containsPoint(Location2D(3.0, 1.0)))
  }

  // endregion

  // region OBB
  @Test
  fun `Test OrientedBoundingBox2D containing point`() {
    assertTrue(orientedBoundingBox.containsPoint(Location2D(3.0, 2.0)))
  }

  @Test
  fun `Test OrientedBoundingBox2D containing point on vertex`() {
    assertTrue(orientedBoundingBox.containsPoint(Location2D(0.0, 2.0)))
  }

  @Test
  fun `Test OrientedBoundingBox2D not containing point`() {
    assertFalse(orientedBoundingBox.containsPoint(Location2D(1.0, 1.0)))
  }
  // endregion
}
