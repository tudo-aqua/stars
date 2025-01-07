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

import tools.aqua.stars.core.types.EntityType

/**
 * This class is used for tests and implements the [EntityType] interface.
 *
 * @property id The ID of this entity.
 * @property tickData The [BooleanTick] this [TestEntity] belongs to.
 */
class TestEntity(override val id: Int, override val tickData: BooleanTick) :
    EntityType<TestEntity, BooleanTick, TestSegment, TestUnit, TestDifference>
