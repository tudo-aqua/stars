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

package tools.aqua.stars.data.av.dataclasses

/**
 * Data class for roads.
 *
 * @property id The identifier of the road.
 * @property isJunction Whether this is a junction.
 * @property lanes List of [Lane]s on this road.
 */
data class Road(val id: Int, val isJunction: Boolean, val lanes: List<Lane>) {
  /** The [Block] of the [Lane]. */
  lateinit var block: Block

  override fun toString(): String = "$id"
}
