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
 * Interface for segment types.
 *
 * @param E Entity type.
 * @param T Tick data type.
 * @param S Segment type.
 * @param U Tick type.
 */
interface SegmentType<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> {

  /** List of Tick data. */
  val tickData: List<T>

  /** Ticks in this [SegmentType]. */
  val ticks: Map<U, T>

  /** Segment source String. */
  val segmentSource: String

  /** Identifier of primary entity. */
  val primaryEntityId: Int

  /** Returns an Identifier for this segment. */
  fun getSegmentIdentifier(): String =
      "Segment[(${tickData.first().currentTick}..${tickData.last().currentTick})] from $segmentSource " +
          "with primary entity ${this.primaryEntityId}"
}
