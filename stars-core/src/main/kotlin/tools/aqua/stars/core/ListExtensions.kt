/*
 * Copyright 2023 The STARS Project Authors
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

package tools.aqua.stars.core

/** creates TxT cross product */
fun <T> List<T>.x2() = this.flatMap { a -> this.map { b -> a to b } }

/** creates TxTxT cross product */
fun <T> List<T>.x3() = this.flatMap { a -> this.flatMap { b -> this.map { c -> Triple(a, b, c) } } }

/**
 * Adaption of com.marcinmoskala.math.powerset for lists while preserving the order of the original
 * list, going from small to big subsets see
 * https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit/blob/master/src/main/java/com/marcinmoskala/math/PowersetExt.kt
 */
fun <T> List<T>.powerlist(): List<List<T>> =
    when {
      isEmpty() -> listOf(listOf())
      else -> dropLast(1).powerlist().let { it + it.map { it + last() } }.sortedBy { it.size }
    }
