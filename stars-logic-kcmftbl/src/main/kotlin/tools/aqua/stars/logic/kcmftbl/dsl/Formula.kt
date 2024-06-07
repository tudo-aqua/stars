/*
 * Copyright 2024 The STARS Project Authors
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

package tools.aqua.stars.logic.kcmftbl.dsl

sealed interface Formula

data object TT : Formula

data object FF : Formula

data class Neg(val inner: Formula) : Formula

data class And(val lhs: Formula, val rhs: Formula) : Formula

data class Or(val lhs: Formula, val rhs: Formula) : Formula

data class Implication(val lhs: Formula, val rhs: Formula) : Formula

data class Iff(val lhs: Formula, val rhs: Formula) : Formula

data class Prev(val interval: Pair<Int, Int>? = null, val inner: Formula) : Formula

data class Next(val interval: Pair<Int, Int>? = null, val inner: Formula) : Formula

data class Once(val interval: Pair<Int, Int>? = null, val inner: Formula) : Formula

data class Historically(val interval: Pair<Int, Int>? = null, val inner: Formula) : Formula

data class Eventually(val interval: Pair<Int, Int>? = null, val inner: Formula) : Formula

data class Globally(val interval: Pair<Int, Int>? = null, val inner: Formula) : Formula

data class Since(val interval: Pair<Int, Int>? = null, val lhs: Formula, val rhs: Formula) :
    Formula

data class Until(val interval: Pair<Int, Int>? = null, val lhs: Formula, val rhs: Formula) :
    Formula

data class Forall(val inner: Formula) : Formula

data class Exists(val inner: Formula) : Formula

data class MinPrevalence(val fraction: Double, val inner: Formula) : Formula

data class PastMinPrevalence(val fraction: Double, val inner: Formula) : Formula

data class MaxPrevalence(val fraction: Double, val inner: Formula) : Formula

data class PastMaxPrevalence(val fraction: Double, val inner: Formula) : Formula

data class Binding<Type>(val bindTerm: Term<Type>, val inner: Formula) : Formula

sealed interface Term<Type>

data class Constant<Type>(val value: Type) : Term<Type>

data class Variable<Type>(val phi: () -> Type) : Term<Type>
