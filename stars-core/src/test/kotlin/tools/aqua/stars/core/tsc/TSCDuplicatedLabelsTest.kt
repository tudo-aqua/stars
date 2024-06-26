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

package tools.aqua.stars.core.tsc

import kotlin.test.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import tools.aqua.stars.core.*
import tools.aqua.stars.core.tsc.builder.root

class TSCDuplicatedLabelsTest {
  // region node labels
  @Test
  fun `Test duplicated node labels on same level throwing exception`() {
    assertThrows<IllegalStateException> {
      root<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") {
          projections { projection("all") }

          any("label")

          optional("label")
        }
      }
    }
  }

  @Test
  fun `Test duplicated node labels on different levels throwing no exception`() {
    assertDoesNotThrow {
      root<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") {
          projections { projection("all") }

          any("label") { optional("label") }
        }
      }
    }
  }
  // endregion
  // region projection labels
  @Test
  fun `Test duplicated projection labels on same level throwing exception`() {
    assertThrows<IllegalStateException> {
      root<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") {
          condition { _ -> true }

          projections {
            projection("P1")
            projection("P1")
          }
        }
      }
    }
  }

  @Test
  fun `Test duplicated projection labels on same level throwing exception 2`() {
    assertThrows<IllegalStateException> {
      root<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") {
          condition { _ -> true }

          projections {
            projection("P1")
            projectionRecursive("P1")
          }
        }
      }
    }
  }
  // endregion
  // region monitor labels
  @Test
  fun `Test duplicated monitor labels on same level throwing exception`() {
    assertThrows<IllegalStateException> {
      root<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") {
          condition { _ -> true }

          monitors {
            monitor("monitor1") { _ -> true }
            monitor("monitor1") { _ -> true }
          }
        }
      }
    }
  }

  @Test
  fun `Test duplicated monitor labels on different levels throwing no exception`() {
    assertDoesNotThrow {
      root<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") {
          condition { _ -> true }

          monitors { monitor("monitor1") { _ -> true } }

          any("label") { monitors { monitor("monitor1") { _ -> true } } }
        }
      }
    }
  }
}
