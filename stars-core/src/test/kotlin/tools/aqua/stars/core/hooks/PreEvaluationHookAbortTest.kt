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

package tools.aqua.stars.core.hooks

import kotlin.test.*
import tools.aqua.stars.core.*
import tools.aqua.stars.core.hooks.defaulthooks.MinNodesInTSCHook
import tools.aqua.stars.core.tsc.builder.tsc

/** PreEvaluationHookAbortTest. */
class PreEvaluationHookAbortTest {
  /** Test PreEvaluationHookAbort with one failing hook. */
  @Test
  fun `Test PreEvaluationHookAbort with one failing hook`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("") {}
        }

    val hook =
        MinNodesInTSCHook<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            minNodes = 1)

    try {
      throw PreEvaluationHookAbort(tsc = tsc, hooks = listOf(hook))
    } catch (e: PreEvaluationHookAbort) {
      val msg = checkNotNull(e.message)

      assertTrue { msg.contains("PreEvaluationHook ") }
      assertFalse { msg.contains("PreEvaluationHooks") }
    }
  }

  /** Test PreEvaluationHookAbort with two failing hooks. */
  @Test
  fun `Test PreEvaluationHookAbort with two failing hooks`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("") {}
        }

    val hook =
        MinNodesInTSCHook<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference>(
            minNodes = 1)

    try {
      throw PreEvaluationHookAbort(tsc = tsc, hooks = listOf(hook, hook))
    } catch (e: PreEvaluationHookAbort) {
      val msg = checkNotNull(e.message)

      assertFalse { msg.contains("PreEvaluationHook ") }
      assertTrue { msg.contains("PreEvaluationHooks") }
    }
  }

  /** Test PreEvaluationHookAbort with zero failing hooks. */
  @Test
  fun `Test PreEvaluationHookAbort with zero failing hooks`() {
    val tsc =
        tsc<
            SimpleEntity,
            SimpleTickData,
            SimpleSegment,
            SimpleTickDataUnit,
            SimpleTickDataDifference> {
          any("") {}
        }

    assertFailsWith<IllegalArgumentException> {
      throw PreEvaluationHookAbort(tsc = tsc, hooks = emptyList())
    }
  }
}
