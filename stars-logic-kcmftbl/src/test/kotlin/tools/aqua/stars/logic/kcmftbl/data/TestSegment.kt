/*
 * Copyright 2024-2025 The STARS Project Authors
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

import tools.aqua.stars.core.types.SegmentType

/**
 * This class is used for tests and implements the [SegmentType] interface.
 *
 * @property tickData The [List] of [BooleanTick]s belonging to this [TestSegment].
 * @property ticks A [Map] that maps the actual tick value to its [BooleanTick] object.
 * @property segmentSource The identifier from which source this [TestSegment] was created.
 * @property primaryEntityId The ID of the primary entity in this [TestSegment].
 */
class TestSegment(
    override val tickData: List<BooleanTick>,
    override val ticks: Map<TestUnit, BooleanTick>,
    override val segmentSource: String,
    override val primaryEntityId: Int
) : SegmentType<TestEntity, BooleanTick, TestSegment, TestUnit, TestDifference>
