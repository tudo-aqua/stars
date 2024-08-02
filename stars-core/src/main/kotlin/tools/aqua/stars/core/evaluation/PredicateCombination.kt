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

package tools.aqua.stars.core.evaluation

/**
 * This is a simple class to store two "predicates" given by their [String] representation.
 *
 * @property predicate1 The name of the first predicate.
 * @property predicate2 The name of the second predicate.
 */
data class PredicateCombination(val predicate1: String, val predicate2: String) {
  /**
   * Checks if this is equals to another object.
   *
   * If [other] is also a [PredicateCombination] then the two are equals when the two predicates are
   * equal. The order is irrelevant. ["pre1", "pre2"] == ["pre2", "pre1"] is true.
   *
   * @param other The other object to which "this" should be checked for equality.
   * @return Whether the two object are equal.
   */
  override fun equals(other: Any?): Boolean =
      other is PredicateCombination &&
          ((predicate1 == other.predicate1 && predicate2 == other.predicate2) ||
              (predicate1 == other.predicate2 && predicate2 == other.predicate1))

  /**
   * Returns the hashCode as [Int] based on the two predicates.
   *
   * @return the hashCode for this object.
   */
  override fun hashCode(): Int = (predicate1 + predicate2).toSortedSet().hashCode()

  /**
   * Formats the [PredicateCombination] nicely.
   *
   * @return The [PredicateCombination] as a nicely formatted [String].
   */
  override fun toString(): String = "[$predicate1, $predicate2]"
}
