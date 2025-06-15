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

import kotlin.reflect.KProperty0
import tools.aqua.stars.core.evaluation.AbstractPredicate

fun labelFile(path: String, block: FileSpec.() -> Unit): FileSpec = FileSpec(path).apply(block)

class FileSpec(val path: String) {
  internal val specs = mutableListOf<PredicateSpec<out AbstractPredicate<*, *, *, *, *>>>()

  /**
   * Accepts any concrete subclass of AbstractPredicate<…> by property‐ref. IDE will autocomplete
   * ::hasLowTrafficDensity, ::compareVehicles, etc.
   */
  fun <P : AbstractPredicate<*, *, *, *, *>> predicate(
      prop: KProperty0<P>,
      block: PredicateSpec<P>.() -> Unit
  ) {
    val spec = PredicateSpec(prop.name, prop.get())
    spec.apply(block)
    specs += spec
  }
}

class PredicateSpec<P : AbstractPredicate<*, *, *, *, *>>(
    /** the Kotlin var name, e.g. "hasLowTrafficDensity" */
    val name: String,
    /** your actual predicate instance (nullary/unary/binary) */
    val pred: P
) {
  data class Interval(val fromSec: Int, val toSec: Int)

  internal val intervals = mutableListOf<Interval>()

  /** mark [fromSec toSec] as true for this predicate */
  fun interval(fromSec: Int, toSec: Int) {
    require(fromSec >= 0 && toSec >= fromSec) { "Invalid interval [$fromSec,$toSec]" }
    intervals += Interval(fromSec, toSec)
  }
}
