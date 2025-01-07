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

package tools.aqua.stars.logic.kcmftbl

import tools.aqua.stars.core.types.TickDifference

/**
 * Checks the given [interval]:
 * - The first value of the interval must be non-negative.
 * - The second value of the interval must be greater than the first value.
 *
 * @param D [TickDifference].
 * @param interval The interval.
 * @throws IllegalArgumentException if the interval is invalid.
 */
fun <D : TickDifference<D>> checkInterval(interval: Pair<D, D>?) {
  if (interval != null) {
    require(interval.first <= interval.first + interval.first) {
      "The first value of the interval must be non-negative."
    }
    require(interval.first < interval.second) {
      "The second value of the interval must be greater than the first value."
    }
  }
}

/**
 * Checks the given [percentage]:
 * - The percentage must be greater than or equal to 0.
 * - The percentage must be less than or equal to 1.
 *
 * @param percentage The percentage.
 * @throws IllegalArgumentException if the interval is invalid.
 */
fun checkPercentage(percentage: Double) {
  require(percentage >= 0.0) { "The percentage must be greater than or equal to 0." }
  require(percentage <= 1.0) { "The percentage must be less than or equal to 1." }
}
