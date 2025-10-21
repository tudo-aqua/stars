/*
 * Copyright 2023-2025 The STARS Project Authors
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

/** Tests for [BoundingBox2D.containsPoint]. */
class BoundingBoxContainmentTest {

  /** Axis-aligned bounding box (AABB) for testing. */
  val axisAlignedBoundingBox: BoundingBox2D
    get() =
        BoundingBox2D(
            leftFront = Location2D(0.0, 2.0),
            rightFront = Location2D(2.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 0.0),
        )

  /** Oriented bounding box (OBB) for testing. */
  val orientedBoundingBox: BoundingBox2D
    get() =
        BoundingBox2D(
            leftFront = Location2D(2.0, 5.0),
            rightFront = Location2D(5.0, 3.0),
            rightBack = Location2D(3.0, 0.0),
            leftBack = Location2D(0.0, 2.0),
        )

  // region AABB
  /** Test AABoundingBox2D containing point. */
  @Test
  fun `Test AABoundingBox2D containing point`() {
    assertTrue(axisAlignedBoundingBox.containsPoint(Location2D(1.0, 1.0)))
  }

  /** Test AABoundingBox2D containing point on edge. */
  @Test
  fun `Test AABoundingBox2D containing point on edge`() {
    assertTrue(axisAlignedBoundingBox.containsPoint(Location2D(0.0, 1.0)))
  }

  /** Test AABoundingBox2D containing point on vertex. */
  @Test
  fun `Test AABoundingBox2D containing point on vertex`() {
    assertTrue(axisAlignedBoundingBox.containsPoint(Location2D(2.0, 2.0)))
  }

  /** Test AABoundingBox2D not containing point. */
  @Test
  fun `Test AABoundingBox2D not containing point`() {
    assertFalse(axisAlignedBoundingBox.containsPoint(Location2D(3.0, 1.0)))
  }

  // endregion

  // region OBB
  /** Test OrientedBoundingBox2D containing point. */
  @Test
  fun `Test OrientedBoundingBox2D containing point`() {
    assertTrue(orientedBoundingBox.containsPoint(Location2D(3.0, 2.0)))
  }

  /** Test OrientedBoundingBox2D containing point on edge. */
  @Test
  fun `Test OrientedBoundingBox2D containing point on vertex`() {
    assertTrue(orientedBoundingBox.containsPoint(Location2D(0.0, 2.0)))
  }

  /** Test OrientedBoundingBox2D containing point on vertex. */
  @Test
  fun `Test OrientedBoundingBox2D not containing point`() {
    assertFalse(orientedBoundingBox.containsPoint(Location2D(1.0, 1.0)))
  }
  // endregion
}
