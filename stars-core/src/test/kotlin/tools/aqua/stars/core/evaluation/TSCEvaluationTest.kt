/*
 * Copyright 2023 The STARS Project Authors
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

package tools.aqua.stars.core.evaluation

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import tools.aqua.stars.core.metric.metrics.evaluation.SegmentCountMetric
import tools.aqua.stars.core.tsc.TSC
import tools.aqua.stars.core.tsc.builder.*
import tools.aqua.stars.core.tsc.projection.proj
import tools.aqua.stars.core.tsc.projection.projRec
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType

/** Tests for list extension functions. */
class TSCEvaluationTest {

  /** Placeholder [EntityType] type. */
  class EType(override val id: Int, override val tickData: TType) : EntityType<EType, TType, SType>

  /** Placeholder [TickDataType] type. */
  class TType(
      override val currentTick: Double,
      override var entities: List<EType>,
      override var segment: SType
  ) : TickDataType<EType, TType, SType>

  /** Placeholder [SegmentType] type. */
  class SType(
      override val tickData: List<TType>,
      override val ticks: Map<Double, TType>,
      override val tickIDs: List<Double>,
      override val segmentSource: String,
      override val firstTickId: Double,
      override val primaryEntityId: Int
  ) : SegmentType<EType, TType, SType>

  private lateinit var tscEvaluation: TSCEvaluation<EType, TType, SType>

  /** Sets up a [TSC] and [TSCEvaluation]. */
  @BeforeTest
  fun prepare() {
    val tsc =
        TSC(
            root<EType, TType, SType> {
              all("TSCRoot") {
                valueFunction = { "TSCRoot" }
                projectionIDs = mapOf(projRec("all"), proj("projection"))
                exclusive("Weather") {
                  projectionIDs = mapOf(projRec("projection"))
                  leaf("Truth") { condition = { _ -> true } }
                  leaf("Falseness") { condition = { _ -> false } }
                }
              }
            })
    tscEvaluation = TSCEvaluation(tsc = tsc, numThreads = 1)
  }

  /** Tests calling prepare() without registered metric provider. */
  @Test
  fun testPrepareWithoutMetricProvider() {
    assertFailsWith<IllegalStateException> { tscEvaluation.prepare() }
  }

  /** Tests calling prepare() multiple times. */
  @Test
  fun testMultiplePrepare() {
    tscEvaluation.registerMetricProviders(SegmentCountMetric())
    tscEvaluation.prepare()
    assertFailsWith<IllegalStateException> { tscEvaluation.prepare() }
  }

  /** Tests registerMetricProvider() after calling prepare(). */
  @Test
  fun testRegisterMetricProvidersAfterPrepare() {
    tscEvaluation.registerMetricProviders(SegmentCountMetric())
    tscEvaluation.prepare()
    assertFailsWith<IllegalStateException> {
      tscEvaluation.registerMetricProviders(SegmentCountMetric())
    }
  }
}
