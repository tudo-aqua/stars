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

class BoundingBoxCollisionTest {

  @Test
  fun `Test AABoundingBox2D separated`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(0.0, 2.0),
            rightFront = Location2D(2.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 0.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(3.0, 2.0),
            rightFront = Location2D(5.0, 2.0),
            rightBack = Location2D(5.0, 0.0),
            leftBack = Location2D(3.0, 0.0),
        )

    assertFalse(bb1.collidesWith(bb2))
    assertFalse(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test AABoundingBox2D touching at one edge`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(0.0, 2.0),
            rightFront = Location2D(2.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 0.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 2.0),
            rightFront = Location2D(4.0, 2.0),
            rightBack = Location2D(4.0, 0.0),
            leftBack = Location2D(2.0, 0.0),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test AABoundingBox2D touching at a single point`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(0.0, 2.0),
            rightFront = Location2D(2.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 0.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 4.0),
            rightFront = Location2D(4.0, 4.0),
            rightBack = Location2D(4.0, 2.0),
            leftBack = Location2D(2.0, 2.0),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test AABoundingBox2D completely inside another AABB`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(0.0, 2.0),
            rightFront = Location2D(2.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 0.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(0.5, 1.5),
            rightFront = Location2D(1.5, 1.5),
            rightBack = Location2D(1.5, 0.5),
            leftBack = Location2D(0.5, 0.5),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test AABoundingBox2D overlapping each other where no point is inside the other`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(1.0, 4.0),
            rightFront = Location2D(3.0, 4.0),
            rightBack = Location2D(3.0, 0.0),
            leftBack = Location2D(1.0, 0.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(0.0, 3.0),
            rightFront = Location2D(4.0, 3.0),
            rightBack = Location2D(4.0, 1.0),
            leftBack = Location2D(0.0, 1.0),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test AABoundingBox2D completely inside another OBB`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 4.0),
            rightFront = Location2D(4.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 2.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(1.5, 2.5),
            rightFront = Location2D(2.5, 2.5),
            rightBack = Location2D(2.5, 1.5),
            leftBack = Location2D(1.5, 1.5),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test AABoundingBox2D inside another OBB where ABB points are on OBB edges`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 4.0),
            rightFront = Location2D(4.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 2.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(1.0, 3.0),
            rightFront = Location2D(3.0, 3.0),
            rightBack = Location2D(3.0, 1.0),
            leftBack = Location2D(1.0, 1.0),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test OOBoundingBox2D completely inside another OBB`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 4.0),
            rightFront = Location2D(4.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 2.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 3.0),
            rightFront = Location2D(3.0, 2.0),
            rightBack = Location2D(2.0, 1.0),
            leftBack = Location2D(1.0, 2.0),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test OOBoundingBox2D seperated`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 4.0),
            rightFront = Location2D(4.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 2.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(7.0, 4.0),
            rightFront = Location2D(9.0, 2.0),
            rightBack = Location2D(7.0, 0.0),
            leftBack = Location2D(5.0, 2.0),
        )

    assertFalse(bb1.collidesWith(bb2))
    assertFalse(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test OOBoundingBox2D touching at a single point`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 4.0),
            rightFront = Location2D(4.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 2.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(6.0, 4.0),
            rightFront = Location2D(8.0, 2.0),
            rightBack = Location2D(6.0, 0.0),
            leftBack = Location2D(4.0, 2.0),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }

  @Test
  fun `Test OOBoundingBox2D touching at one edge`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 4.0),
            rightFront = Location2D(4.0, 2.0),
            rightBack = Location2D(2.0, 0.0),
            leftBack = Location2D(0.0, 2.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(4.0, 6.0),
            rightFront = Location2D(6.0, 4.0),
            rightBack = Location2D(4.0, 0.0),
            leftBack = Location2D(2.0, 4.0),
        )

    assertTrue(bb1.collidesWith(bb2))
    assertTrue(bb2.collidesWith(bb1))
  }
}
