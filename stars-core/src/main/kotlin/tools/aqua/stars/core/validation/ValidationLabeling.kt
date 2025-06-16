/*
 * Copyright 2025 The STARS Project Authors
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

package tools.aqua.stars.core.validation

import tools.aqua.stars.core.evaluation.AbstractPredicate
import tools.aqua.stars.core.types.EntityType
import tools.aqua.stars.core.types.SegmentType
import tools.aqua.stars.core.types.TickDataType
import tools.aqua.stars.core.types.TickDifference
import tools.aqua.stars.core.types.TickUnit

fun <
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>> labelFile(
    path: String,
    block: FileSpec<E, T, S, U, D>.() -> Unit
): FileSpec<E, T, S, U, D> = FileSpec<E, T, S, U, D>(path).apply(block)

class FileSpec<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(val path: String) {
  internal val specs = mutableListOf<PredicateSpec<E, T, S, U, D>>()

  fun predicate(
      pred: AbstractPredicate<E, T, S, U, D>,
      block: PredicateSpec<E, T, S, U, D>.() -> Unit
  ) {
    val spec = PredicateSpec(pred)
    spec.apply(block)
    specs += spec
  }
}

class PredicateSpec<
    E : EntityType<E, T, S, U, D>,
    T : TickDataType<E, T, S, U, D>,
    S : SegmentType<E, T, S, U, D>,
    U : TickUnit<U, D>,
    D : TickDifference<D>>(val pred: AbstractPredicate<E, T, S, U, D>) {
  val name: String = pred.name

  data class Interval(val fromSec: Int, val toSec: Int)

  internal val intervals = mutableListOf<Interval>()

  fun interval(fromSec: Int, toSec: Int) {
    require(fromSec >= 0 && toSec >= fromSec) { "Invalid interval [$fromSec,$toSec]" }
    intervals += Interval(fromSec, toSec)
  }
}
