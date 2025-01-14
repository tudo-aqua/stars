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

package tools.aqua.stars.importer.carla

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import tools.aqua.stars.data.av.dataclasses.Landmark
import tools.aqua.stars.importer.carla.dataclasses.*

/** Tests converter from [JsonLandmark] to [Landmark]. */
class JSONLandmarkTest {

  /** The [JsonLandmark] instance. */
  private lateinit var completeJsonLandmark: JsonLandmark

  /** The [Landmark] instance. */
  private lateinit var completeLandmark: Landmark

  /**
   * Creates a [JsonLandmark] [completeJsonLandmark] and converts it to a [Landmark]
   * [completeLandmark].
   */
  @BeforeTest
  fun setup() {
    completeJsonLandmark =
        JsonLandmark(
            id = 100,
            roadId = 1,
            location = JsonLocation(0.0, 0.0, 0.0),
            text = "Landmark",
            unit = "mph",
            value = 30.0,
            type = JsonLandmarkType.MaximumSpeed,
            country = "Test",
            distance = 10.0,
            rotation = JsonRotation(0.0, 0.0, 0.0),
            hOffset = 0.0,
            height = 2.3,
            isDynamic = false,
            name = "SpeedLimit",
            orientation = JsonLandmarkOrientation.Both,
            pitch = 0.0,
            roll = 0.0,
            s = 10.0,
            subType = "Subtype",
            width = 1.0,
            zOffset = 0.0)
    completeLandmark = completeJsonLandmark.toLandmark()
  }

  /** Asserts that all attributes in [completeJsonLandmark] equal those in [completeLandmark]. */
  @Test
  fun checkAttributeSetter() {
    assertEquals(completeJsonLandmark.id, completeLandmark.id)
    assertEquals(completeJsonLandmark.country, completeLandmark.country)
    assertEquals(completeJsonLandmark.distance, completeLandmark.distance)
    assertEquals(completeJsonLandmark.s, completeLandmark.s)
    assertEquals(completeJsonLandmark.location.x, completeLandmark.location.x)
    assertEquals(completeJsonLandmark.location.y, completeLandmark.location.y)
    assertEquals(completeJsonLandmark.location.z, completeLandmark.location.z)
    assertEquals(completeJsonLandmark.rotation.yaw, completeLandmark.rotation.yaw)
    assertEquals(completeJsonLandmark.rotation.pitch, completeLandmark.rotation.pitch)
    assertEquals(completeJsonLandmark.rotation.roll, completeLandmark.rotation.roll)
    assertEquals(completeJsonLandmark.name, completeLandmark.name)
    assertEquals(completeJsonLandmark.text, completeLandmark.text)
    assertEquals(completeJsonLandmark.type.value, completeLandmark.type.value)
    assertEquals(completeJsonLandmark.value, completeLandmark.value)
    assertEquals(completeJsonLandmark.unit, completeLandmark.unit)
  }
}
