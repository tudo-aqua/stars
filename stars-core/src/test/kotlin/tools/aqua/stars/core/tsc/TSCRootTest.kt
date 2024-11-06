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

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import tools.aqua.stars.core.*
import tools.aqua.stars.core.tsc.builder.tsc

/** Tests for root node DSL function. */
class TSCRootTest {

  // region Test adding of root nodes
  /** Test adding one node as root. */
  @Test
  fun `Test adding one node as root`() {
    val label = "root_node_label"
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          all(label)
        }

    assertEquals(label, tsc.rootNode.label)
  }

  /** Test adding a condition to root node. */
  @Test
  fun `Test adding a condition to root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("rooted") { condition { true } }
      }
    }
  }

  /** Test adding two nodes as root. */
  @Test
  fun `Test adding two nodes as root`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("all_1")
        all("all_2")
      }
    }
  }

  /** Test adding two nodes as root. */
  @Test
  fun `Test adding no node as root`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {}
    }
  }

  // endregion

  // region Test adding simple true conditions to root note
  /** Test adding a simple true condition to the 'all' root node. */
  @Test
  fun `Test throwing of exception with added true condition at all root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") { condition { true } }
      }
    }
  }

  /** Test adding a simple true condition to the 'any' root node. */
  @Test
  fun `Test throwing of exception with added true condition at any root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        any("root") { condition { true } }
      }
    }
  }

  /** Test adding a simple true condition to the 'optional' root node. */
  @Test
  fun `Test throwing of exception with added true condition at optional root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        optional("root") { condition { true } }
      }
    }
  }

  /** Test adding a simple true condition to the 'exclusive' root node. */
  @Test
  fun `Test throwing of exception with added true condition at exclusive root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        exclusive("root") { condition { true } }
      }
    }
  }

  /** Test adding a simple true condition to the 'leaf' root node. */
  @Test
  fun `Test throwing of exception with added true condition at leaf root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        leaf("root") { condition { true } }
      }
    }
  }

  /** Test adding a simple true condition to the 'bounded' root node. */
  @Test
  fun `Test throwing of exception with added true condition at bounded root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        bounded("root", 2 to 3) { condition { true } }
      }
    }
  }

  // endregion

  // region Test adding simple false conditions to root note
  /** Test adding a simple false condition to the 'all' root node. */
  @Test
  fun `Test throwing of exception with added false condition at all root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") { condition { false } }
      }
    }
  }

  /** Test adding a simple false condition to the 'any' root node. */
  @Test
  fun `Test throwing of exception with added false condition at any root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        any("root") { condition { false } }
      }
    }
  }

  /** Test adding a simple false condition to the 'optional' root node. */
  @Test
  fun `Test throwing of exception with added false condition at optional root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        optional("root") { condition { false } }
      }
    }
  }

  /** Test adding a simple false condition to the 'exclusive' root node. */
  @Test
  fun `Test throwing of exception with added false condition at exclusive root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        exclusive("root") { condition { false } }
      }
    }
  }

  /** Test adding a simple false condition to the 'leaf' root node. */
  @Test
  fun `Test throwing of exception with added false condition at leaf root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        leaf("root") { condition { false } }
      }
    }
  }

  /** Test adding a simple false condition to the 'bounded' root node. */
  @Test
  fun `Test throwing of exception with added false condition at bounded root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        bounded("root", 2 to 3) { condition { false } }
      }
    }
  }

  // endregion

  // region Test adding complex true conditions to root note
  /** Test adding a complex true condition to the 'all' root node. */
  @Test
  fun `Test throwing of exception with added complex true condition at all root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") { condition { LocalDate.now().year > 0 } }
      }
    }
  }

  /** Test adding a complex true condition to the 'any' root node. */
  @Test
  fun `Test throwing of exception with added complex true condition at any root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        any("root") { condition { LocalDate.now().year > 0 } }
      }
    }
  }

  /** Test adding a complex true condition to the 'optional' root node. */
  @Test
  fun `Test throwing of exception with added complex true condition at optional root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        optional("root") { condition { LocalDate.now().year > 0 } }
      }
    }
  }

  /** Test adding a complex true condition to the 'exclusive' root node. */
  @Test
  fun `Test throwing of exception with added complex true condition at exclusive root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        exclusive("root") { condition { LocalDate.now().year > 0 } }
      }
    }
  }

  /** Test adding a complex true condition to the 'leaf' root node. */
  @Test
  fun `Test throwing of exception with added complex true condition at leaf root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        leaf("root") { condition { LocalDate.now().year > 0 } }
      }
    }
  }

  /** Test adding a complex true condition to the 'bounded' root node. */
  @Test
  fun `Test throwing of exception with added complex true condition at bounded root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        bounded("root", 2 to 3) { condition { LocalDate.now().year > 0 } }
      }
    }
  }

  // endregion

  // region Test adding complex false conditions to root note
  /** Test adding a complex false condition to the 'all' root node. */
  @Test
  fun `Test throwing of exception with added complex complex false condition at all root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        all("root") { condition { LocalDate.now().year < 0 } }
      }
    }
  }

  /** Test adding a complex false condition to the 'any' root node. */
  @Test
  fun `Test throwing of exception with added complex false condition at any root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        any("root") { condition { LocalDate.now().year < 0 } }
      }
    }
  }

  /** Test adding a complex false condition to the 'optional' root node. */
  @Test
  fun `Test throwing of exception with added complex false condition at optional root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        optional("root") { condition { LocalDate.now().year < 0 } }
      }
    }
  }

  /** Test adding a complex false condition to the 'exclusive' root node. */
  @Test
  fun `Test throwing of exception with added complex false condition at exclusive root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        exclusive("root") { condition { LocalDate.now().year < 0 } }
      }
    }
  }

  /** Test adding a complex false condition to the 'leaf' root node. */
  @Test
  fun `Test throwing of exception with added complex false condition at leaf root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        leaf("root") { condition { LocalDate.now().year < 0 } }
      }
    }
  }

  /** Test adding a complex false condition to the 'bounded' root node. */
  @Test
  fun `Test throwing of exception with added complex false condition at bounded root node`() {
    assertFailsWith<IllegalStateException> {
      tsc<
          SimpleEntity,
          SimpleTickData,
          SimpleSegment,
          SimpleTickDataUnit,
          SimpleTickDataDifference> {
        bounded("root", 2 to 3) { condition { LocalDate.now().year < 0 } }
      }
    }
  }
  // endregion
}
