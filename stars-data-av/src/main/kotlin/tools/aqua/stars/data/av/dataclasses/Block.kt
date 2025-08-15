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

package tools.aqua.stars.data.av.dataclasses

/**
 * Data class for [Road] blocks.
 *
 * @property id Identifier of the road block.
 * @property roads Roads incorporated in this block.
 */
data class Block(
    val id: String = "",
    var roads: List<Road>,
) {

  init {
    check(roads.isNotEmpty()) { "Block must have at least one road" }
    roads.forEach { it.block = this }
  }

  override fun toString(): String = id

  override fun hashCode(): Int = id.hashCode()

  override fun equals(other: Any?): Boolean = if (other is Block) other.id == this.id else false
}
