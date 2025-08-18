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

package tools.aqua.stars.logic.kcmftbl.data

import java.util.LinkedHashSet
import tools.aqua.stars.core.types.TickDataType

/**
 * This class is used for tests and implements the [TickDataType] interface. It holds two [Boolean]
 * values representing the evaluations of 'phi1' 'phi2'.
 *
 * @param currentTickUnit The current tick.
 * @param entities The [List] of [TestEntity]s.
 * @property phi1 Represents the evaluation results of the formula 'phi1'.
 * @property phi2 Represents the evaluation results of the formula 'phi2'.
 */
@Suppress("BooleanPropertyNaming")
class BooleanTickData(
    currentTickUnit: TestUnit,
    entities: LinkedHashSet<TestEntity>,
    val phi1: Boolean,
    val phi2: Boolean,
) : TickDataType<TestEntity, BooleanTickData, TestUnit, TestDifference>(currentTickUnit, entities) {
  override val ego: TestEntity
    get() = throw UnsupportedOperationException("BooleanTickData does not have an ego entity")
}
