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

package tools.aqua.stars.core.types

/**
 * Interface for tick data types.
 *
 * @param E [EntityType].
 * @param T [TickDataType].
 * @param S [SegmentType].
 * @param U [TickUnit].
 * @param D [TickDifference].
 */
interface TickDataType<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> {

  /** The current tick. */
  val currentTick: U

  /** List of [EntityType]s in tick data. */
  var entities: List<E>

  /** Current [SegmentType]. */
  var segment: S

  /**
   * Retrieves [EntityType] from [entities] by given [entityID].
   *
   * @param entityID Entity identifier.
   * @return The [EntityType] with the ID [entityID] if existing. Null otherwise.
   */
  fun getEntityById(entityID: Int): E? = entities.firstOrNull { it.id == entityID }
}
