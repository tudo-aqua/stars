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

import tools.aqua.stars.core.types.EntityType

/**
 * This class is used for tests and implements the [EntityType] interface.
 *
 * @property id The ID of this entity.
 */
class TestEntity(val id: Int) : EntityType<TestEntity, BooleanTickData, TestUnit, TestDifference>() {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is TestEntity) return false
    return id == other.id
  }

  override fun hashCode(): Int = id
}
