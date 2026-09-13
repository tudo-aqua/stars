/*
 * Copyright 2026 The STARS Project Authors
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

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the CARLA data-file name helpers [getMapName], [getSeed], [hasSeed] and
 * [orderDynamicDataFilesBySeed].
 */
class FileNameHandlingTest {

  /** [getMapName] extracts the map name from a seeded dynamic data filename. */
  @Test
  fun testGetMapNameWithSeed() {
    assertEquals("Town01", getMapName("dynamic_data_Town01_seed_42.zip"))
    assertEquals("Town01", getMapName("dynamic_data_Town01_seed_0.json"))
  }

  /** [getMapName] extracts the map name from a dynamic data filename without a seed. */
  @Test
  fun testGetMapNameWithoutSeed() {
    assertEquals(
        "manual_driving_2026_08_31-22_18_29",
        getMapName("dynamic_data_manual_driving_2026_08_31-22_18_29.zip"),
    )
    assertEquals("Town10HD", getMapName("dynamic_data_Town10HD.json"))
  }

  /** [getMapName] handles static data filenames and rejects an empty or unknown filename. */
  @Test
  fun testGetMapNameOtherCases() {
    assertEquals("Town04", getMapName("static_data_Town04.zip"))
    assertFailsWith<IllegalStateException> { getMapName("") }
    assertFailsWith<IllegalStateException> { getMapName("some_other_file.txt") }
  }

  /** [hasSeed] recognizes a numeric `_seed_<n>` marker only. */
  @Test
  fun testHasSeed() {
    assertTrue(hasSeed("dynamic_data_Town01_seed_42.zip"))
    assertTrue(hasSeed("dynamic_data_Town01_seed_0.zip"))
    assertFalse(hasSeed("dynamic_data_manual_driving_2026_08_31-22_18_29.zip"))
    assertFalse(hasSeed("dynamic_data_Town01.json"))
  }

  /** [getSeed] parses the seed and falls back to `0` when no marker is present. */
  @Test
  fun testGetSeed() {
    assertEquals(42, getSeed("dynamic_data_Town01_seed_42.zip"))
    assertEquals(0, getSeed("dynamic_data_Town01_seed_0.zip"))
    assertEquals(0, getSeed("dynamic_data_manual_driving_2026_08_31-22_18_29.zip"))
    assertEquals(0, getSeed(""))
  }

  /** [orderDynamicDataFilesBySeed] sorts by seed only when every file carries a seed marker. */
  @Test
  fun testOrderDynamicDataFilesBySeed() {
    val seeded =
        listOf(
            Path.of("dynamic_data_Town01_seed_10.zip"),
            Path.of("dynamic_data_Town01_seed_2.zip"),
            Path.of("dynamic_data_Town01_seed_5.zip"),
        )
    assertEquals(
        listOf("2", "5", "10"),
        orderDynamicDataFilesBySeed(seeded).map { getSeed(it.fileName.toString()).toString() },
    )

    // Mixed / seedless input is returned in the given order.
    val seedless = listOf(Path.of("dynamic_data_run_b.zip"), Path.of("dynamic_data_run_a.zip"))
    assertEquals(seedless, orderDynamicDataFilesBySeed(seedless))

    val mixed = listOf(Path.of("dynamic_data_Town01_seed_9.zip"), Path.of("dynamic_data_run_a.zip"))
    assertEquals(mixed, orderDynamicDataFilesBySeed(mixed))
  }
}
