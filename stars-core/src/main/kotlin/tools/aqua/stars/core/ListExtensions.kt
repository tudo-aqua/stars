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

@file:Suppress("unused")

package tools.aqua.stars.core

/**
 * Creates TxT cross product.
 *
 * @return The TxT cross product of the current [List].
 */
fun <T> List<T>.x2(): List<Pair<T, T>> = this.flatMap { a -> this.map { b -> a to b } }

/**
 * Creates TxTxT cross product.
 *
 * @return The TxTxT cross product of the current [List].
 */
fun <T> List<T>.x3(): List<Triple<T, T, T>> =
    this.flatMap { a -> this.flatMap { b -> this.map { c -> Triple(a, b, c) } } }

/**
 * Adaption of com.marcinmoskala.math.powerset for lists while preserving the order of the original
 * list, going from small to big subsets see
 * https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit/blob/master/src/main/java/com/marcinmoskala/math/PowersetExt.kt
 *
 * @return The power list of the current [List].
 */
fun <T> List<T>.powerlist(): List<List<T>> =
    when {
      isEmpty() -> listOf(emptyList())
      else -> dropLast(1).powerlist().let { it + it.map { t -> t + last() } }.sortedBy { it.size }
    }

/**
 * Build all possible combinations of the lists in the input list. Example instances kept until
 * better documentation will be written.
 *
 * ```
 *    val input = listOf(
 *        listOf(listOf("a"), listOf("b"), listOf("c")),
 *        listOf(listOf("x"), listOf("y")),
 *        listOf(listOf("1"), listOf("2"), listOf("3"), listOf("4"))
 *    )
 *
 *    val afterFirstStep = listOf(
 *        listOf(listOf("a", "x"), listOf("a", "y"), /*...*/ listOf("c", "y")),
 *        listOf(listOf("1"), listOf("2"), listOf("3"), listOf("4"))
 *    )
 *
 *     val afterSecondStep = listOf(
 *        listOf(listOf("a", "x", "1"), listOf("a", "x", "2"), /*...*/ listOf("c", "y", "4"))
 *    )
 * ```
 *
 * @return The cross product of the current [List].
 */
fun <T> List<List<List<T>>>.crossProduct(): List<List<T>> {
  require(size >= 2) { "List for cross-product building must at least contain two elements." }

  val nextLevelList = mutableListOf<List<T>>()
  this[0].forEach { it1 ->
    this[1].forEach { it2 ->
      val nextEntry = mutableListOf<T>()
      nextEntry.addAll(it1)
      nextEntry.addAll(it2)
      nextLevelList += nextEntry
    }
  }

  return if (size == 2) nextLevelList else (listOf(nextLevelList) + subList(2, size)).crossProduct()
}

/**
 * Evaluates the given [predicate] on each element of the sequence and stops the computation as soon
 * as the predicate returns false.
 */
fun <T> Sequence<T>.computeWhile(predicate: (T) -> Boolean): Unit = forEach {
  if (!predicate(it)) return
}
