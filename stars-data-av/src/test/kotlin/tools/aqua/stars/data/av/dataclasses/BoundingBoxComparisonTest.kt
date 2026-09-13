/*
 * Copyright 2023-2026 The STARS Project Authors
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [BoundingBox2D.isBehindOf], [BoundingBox2D.isInFrontOf], and
 * [BoundingBox2D.isParallelTo].
 */
class BoundingBoxComparisonTest {

  /** Test AABoundingBox2D one in front of the other. */
  @Test
  fun `Test AABoundingBox2D in front of other`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(3.0, 3.0),
            rightFront = Location2D(3.0, 2.0),
            rightBack = Location2D(2.0, 2.0),
            leftBack = Location2D(2.0, 3.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(1.0, 1.0),
            rightFront = Location2D(1.0, 0.0),
            rightBack = Location2D(0.0, 0.0),
            leftBack = Location2D(0.0, 1.0),
        )

    assertFalse(bb1.isBehindOf(bb2))
    assertTrue(bb2.isBehindOf(bb1))

    assertFalse(bb1.isParallelTo(bb2))
    assertFalse(bb2.isParallelTo(bb1))

    assertTrue(bb1.isInFrontOf(bb2))
    assertFalse(bb2.isInFrontOf(bb1))
  }

  /** Test AABoundingBox2D one in front of the other but touching at a point. */
  @Test
  fun `Test AABoundingBox2D in front of other but touching at a point`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(2.0, 3.0),
            rightFront = Location2D(2.0, 2.0),
            rightBack = Location2D(1.0, 2.0),
            leftBack = Location2D(1.0, 3.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(1.0, 1.0),
            rightFront = Location2D(1.0, 0.0),
            rightBack = Location2D(0.0, 0.0),
            leftBack = Location2D(0.0, 1.0),
        )

    assertFalse(bb1.isBehindOf(bb2))
    assertFalse(bb2.isBehindOf(bb1))

    assertTrue(bb1.isParallelTo(bb2))
    assertTrue(bb2.isParallelTo(bb1))

    assertFalse(bb1.isInFrontOf(bb2))
    assertFalse(bb2.isInFrontOf(bb1))
  }

  /** Test AABoundingBox2D parallel. */
  @Test
  fun `Test AABoundingBox2D parallel`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(1.0, 3.0),
            rightFront = Location2D(1.0, 2.0),
            rightBack = Location2D(0.0, 2.0),
            leftBack = Location2D(0.0, 3.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(1.0, 1.0),
            rightFront = Location2D(1.0, 0.0),
            rightBack = Location2D(0.0, 0.0),
            leftBack = Location2D(0.0, 1.0),
        )

    assertFalse(bb1.isBehindOf(bb2))
    assertFalse(bb2.isBehindOf(bb1))

    assertTrue(bb1.isParallelTo(bb2))
    assertTrue(bb2.isParallelTo(bb1))

    assertFalse(bb1.isInFrontOf(bb2))
    assertFalse(bb2.isInFrontOf(bb1))
  }

  /** Test AABoundingBox2D parallel with offset. */
  @Test
  fun `Test AABoundingBox2D parallel with offset`() {
    val bb1 =
        BoundingBox2D(
            leftFront = Location2D(1.5, 3.0),
            rightFront = Location2D(1.5, 2.0),
            rightBack = Location2D(0.5, 2.0),
            leftBack = Location2D(0.5, 3.0),
        )

    val bb2 =
        BoundingBox2D(
            leftFront = Location2D(1.0, 1.0),
            rightFront = Location2D(1.0, 0.0),
            rightBack = Location2D(0.0, 0.0),
            leftBack = Location2D(0.0, 1.0),
        )

    assertFalse(bb1.isBehindOf(bb2))
    assertFalse(bb2.isBehindOf(bb1))

    assertTrue(bb1.isParallelTo(bb2))
    assertTrue(bb2.isParallelTo(bb1))

    assertFalse(bb1.isInFrontOf(bb2))
    assertFalse(bb2.isInFrontOf(bb1))
  }
}
