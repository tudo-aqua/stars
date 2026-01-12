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

import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.importer.carla.dataclasses.JsonVehicle

class EgoSelectionTest {

  /** Test convertTickData with no parameter set and multiple ego are set in same tick. */
  @Test
  fun `Test convertTickData with no parameter set and multiple ego are set in same tick`() {
    val jsonTicksWithMultipleEgoInSameTick =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            numberOfTicks = 2,
        )
    jsonTicksWithMultipleEgoInSameTick[0].actorPositions.forEach { actorPosition ->
      (actorPosition.actor as JsonVehicle).egoVehicle = true
    }
    assertFailsWith<IllegalStateException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithMultipleEgoInSameTick,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with no parameter set and multiple ego are set in different ticks. */
  @Test
  fun `Test convertTickData with no parameter set and multiple ego are set in different ticks`() {
    val jsonTicksWithMultipleEgoInDifferentTicks =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            numberOfTicks = 2,
        )
    (jsonTicksWithMultipleEgoInDifferentTicks[0].actorPositions[0].actor as JsonVehicle)
        .egoVehicle = true
    (jsonTicksWithMultipleEgoInDifferentTicks[1].actorPositions[1].actor as JsonVehicle)
        .egoVehicle = true
    assertFailsWith<IllegalStateException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithMultipleEgoInDifferentTicks,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with useEveryVehicleAsEgo when no vehicles are present. */
  @Test
  fun `Test convertTickData with useEveryVehicleAsEgo when no vehicles are present`() {
    val jsonTicksWithoutVehicles =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 0, numberOfTicks = 2)
    assertFailsWith<IllegalStateException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithoutVehicles,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with useFirstVehicleAsEgo when no vehicles are present. */
  @Test
  fun `Test convertTickData with useFirstVehicleAsEgo when no vehicles are present`() {
    val jsonTicksWithoutVehicles =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 0, numberOfTicks = 2)
    assertFailsWith<IllegalStateException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithoutVehicles,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = true,
      )
    }
  }

  /** Test convertTickData with egoId that is not present. */
  @Test
  fun `Test convertTickData with egoId that is not present`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)
    assertFailsWith<IllegalStateException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithoutEgo,
          tickDataSourcePath = Path(""),
          egoIds = listOf(999),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with one egoId. */
  @Test
  fun `Test convertTickData with five egoIds`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 10, numberOfTicks = 2)

    val result =
        convertTickData(
            simpleWorld,
            jsonSimulationRun = jsonTicksWithoutEgo,
            tickDataSourcePath = Path(""),
            egoIds = listOf(1, 2, 3, 4, 5),
            useEveryVehicleAsEgo = false,
            useFirstVehicleAsEgo = false,
        )

    assertEquals(5, result.size)
  }

  /** Test convertTickData with many vehicles and useEveryVehicleAsEgo. */
  @Test
  fun `Test convertTickData with many vehicles and useEveryVehicleAsEgo`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 100, numberOfTicks = 2)

    val result =
        convertTickData(
            simpleWorld,
            jsonSimulationRun = jsonTicksWithoutEgo,
            tickDataSourcePath = Path(""),
            egoIds = emptyList(),
            useEveryVehicleAsEgo = true,
            useFirstVehicleAsEgo = false,
        )

    assertEquals(100, result.size)
    result.forEach { assertEquals(2, it.size) }
  }

  /** Test convertTickData with no set parameters. */
  @Test
  fun `Test convertTickData with no set parameters`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithoutEgo,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with only useEveryVehicleAsEgo. */
  @Test
  fun `Test convertTickData with only useEveryVehicleAsEgo`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)

    val result =
        convertTickData(
            simpleWorld,
            jsonSimulationRun = jsonTicksWithoutEgo,
            tickDataSourcePath = Path(""),
            egoIds = emptyList(),
            useEveryVehicleAsEgo = true,
            useFirstVehicleAsEgo = false,
        )

    assertEquals(2, result.size) // 2 vehicles => 2 ego runs
  }

  /** Test convertTickData with only useFirstVehicleAsEgo. */
  @Test
  fun `Test convertTickData with only useFirstVehicleAsEgo`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)

    val result =
        convertTickData(
            simpleWorld,
            jsonSimulationRun = jsonTicksWithoutEgo,
            tickDataSourcePath = Path(""),
            egoIds = emptyList(),
            useEveryVehicleAsEgo = false,
            useFirstVehicleAsEgo = true,
        )

    assertEquals(1, result.size)
  }

  /** Test convertTickData with only egoIds. */
  @Test
  fun `Test convertTickData with only egoIds`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)

    val result =
        convertTickData(
            simpleWorld,
            jsonSimulationRun = jsonTicksWithoutEgo,
            tickDataSourcePath = Path(""),
            egoIds = listOf(1),
            useEveryVehicleAsEgo = false,
            useFirstVehicleAsEgo = false,
        )

    assertEquals(1, result.size)
  }

  /** Test convertTickData with only ego-flagged JSON. */
  @Test
  fun `Test convertTickData with only ego-flagged JSON`() {
    val jsonTicksWithEgo =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            hasEgo = true,
            numberOfTicks = 2,
        )

    val result =
        convertTickData(
            simpleWorld,
            jsonSimulationRun = jsonTicksWithEgo,
            tickDataSourcePath = Path(""),
            egoIds = emptyList(),
            useEveryVehicleAsEgo = false,
            useFirstVehicleAsEgo = false,
        )

    assertEquals(1, result.size)
  }

  /** Test convertTickData with useEveryVehicleAsEgo and useFirstVehicleAsEgo. */
  @Test
  fun `Test convertTickData with useEveryVehicleAsEgo and useFirstVehicleAsEgo`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithoutEgo,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = true,
      )
    }
  }

  /** Test convertTickData with useEveryVehicleAsEgo and egoIds. */
  @Test
  fun `Test convertTickData with useEveryVehicleAsEgo and egoIds`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithoutEgo,
          tickDataSourcePath = Path(""),
          egoIds = listOf(1, 2),
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with useFirstVehicleAsEgo and egoIds. */
  @Test
  fun `Test convertTickData with useFirstVehicleAsEgo and egoIds`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithoutEgo,
          tickDataSourcePath = Path(""),
          egoIds = listOf(1),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = true,
      )
    }
  }

  /** Test convertTickData with useFirstVehicleAsEgo and ego-flagged JSON. */
  @Test
  fun `Test convertTickData with useFirstVehicleAsEgo and ego-flagged JSON`() {
    val jsonTicksWithEgo =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            hasEgo = true,
            numberOfTicks = 2,
        )

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithEgo,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = true,
      )
    }
  }

  /** Test convertTickData with useEveryVehicleAsEgo and ego-flagged JSON. */
  @Test
  fun `Test convertTickData with useEveryVehicleAsEgo and ego-flagged JSON`() {
    val jsonTicksWithEgo =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            hasEgo = true,
            numberOfTicks = 2,
        )

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithEgo,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with egoIds and ego-flagged JSON. */
  @Test
  fun `Test convertTickData with egoIds and ego-flagged JSON`() {
    val jsonTicksWithEgo =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            hasEgo = true,
            numberOfTicks = 2,
        )

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithEgo,
          tickDataSourcePath = Path(""),
          egoIds = listOf(1),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with egoIds, useFirstVehicleAsEgo and ego-flagged JSON. */
  @Test
  fun `Test convertTickData with egoIds useFirstVehicleAsEgo and ego-flagged JSON`() {
    val jsonTicksWithEgo =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            hasEgo = true,
            numberOfTicks = 2,
        )

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithEgo,
          tickDataSourcePath = Path(""),
          egoIds = listOf(1),
          useEveryVehicleAsEgo = false,
          useFirstVehicleAsEgo = true,
      )
    }
  }

  /** Test convertTickData with egoIds, useEveryVehicleAsEgo and ego-flagged JSON. */
  @Test
  fun `Test convertTickData with egoIds useEveryVehicleAsEgo and ego-flagged JSON`() {
    val jsonTicksWithEgo =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            hasEgo = true,
            numberOfTicks = 2,
        )

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithEgo,
          tickDataSourcePath = Path(""),
          egoIds = listOf(1),
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = false,
      )
    }
  }

  /** Test convertTickData with useEveryVehicleAsEgo, useFirstVehicleAsEgo and egoIds. */
  @Test
  fun `Test convertTickData with useEveryVehicleAsEgo useFirstVehicleAsEgo and egoIds`() {
    val jsonTicksWithoutEgo =
        getTickDataWithOneLaneWithNVehicles(numberOfVehicles = 2, numberOfTicks = 2)

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithoutEgo,
          tickDataSourcePath = Path(""),
          egoIds = listOf(1, 2),
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = true,
      )
    }
  }

  /** Test convertTickData with useFirstVehicleAsEgo, useEveryVehicleAsEgo and ego-flagged JSON. */
  @Test
  fun `Test convertTickData with useFirstVehicleAsEgo, useEveryVehicleAsEgo and ego-flagged JSON`() {
    val jsonTicksWithEgo =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            hasEgo = true,
            numberOfTicks = 2,
        )

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithEgo,
          tickDataSourcePath = Path(""),
          egoIds = emptyList(),
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = true,
      )
    }
  }

  /**
   * Test convertTickData with useEveryVehicleAsEgo, useFirstVehicleAsEgo, egoIds and ego-flagged
   * JSON.
   */
  @Test
  fun `Test convertTickData with useEveryVehicleAsEgo useFirstVehicleAsEgo egoIds and ego-flagged JSON`() {
    val jsonTicksWithEgo =
        getTickDataWithOneLaneWithNVehicles(
            numberOfVehicles = 2,
            hasEgo = true,
            numberOfTicks = 2,
        )

    assertFailsWith<IllegalArgumentException> {
      convertTickData(
          simpleWorld,
          jsonSimulationRun = jsonTicksWithEgo,
          tickDataSourcePath = Path(""),
          egoIds = listOf(1, 2),
          useEveryVehicleAsEgo = true,
          useFirstVehicleAsEgo = true,
      )
    }
  }
}
