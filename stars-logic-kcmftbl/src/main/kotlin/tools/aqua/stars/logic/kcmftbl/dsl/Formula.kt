package tools.aqua.stars.logic.kcmftbl.dsl

import tools.aqua.stars.core.types.*

sealed interface Formula
data object TT : Formula
data object FF: Formula
data class Neg(val inner: Formula): Formula
data class And(val lhs: Formula, val rhs: Formula): Formula
data class Or(val lhs: Formula, val rhs: Formula): Formula
data class Implication(val lhs: Formula, val rhs: Formula): Formula
data class Iff(val lhs: Formula, val rhs: Formula): Formula
data class Prev(val interval: Pair<Int, Int>? = null, val inner: Formula): Formula
data class Next(val interval: Pair<Int, Int>? = null, val inner: Formula): Formula
data class Once(val interval: Pair<Int, Int>? = null, val inner: Formula): Formula
data class Historically(val interval: Pair<Int, Int>? = null, val inner: Formula): Formula
data class Eventually(val interval: Pair<Int, Int>? = null, val inner: Formula): Formula
data class Globally(val interval: Pair<Int, Int>? = null, val inner: Formula): Formula
data class Since(val interval: Pair<Int, Int>? = null, val lhs: Formula, val rhs: Formula): Formula
data class Until(val interval: Pair<Int, Int>? = null, val lhs: Formula, val rhs: Formula): Formula
data class Forall(val ident: String, val inner: Formula): Formula
data class Exists(val inner: Formula): Formula
data class MinPrevalence(val fraction: Double, val inner: Formula): Formula
data class PastMinPrevalence(val fraction: Double, val inner: Formula): Formula
data class MaxPrevalence(val fraction: Double, val inner: Formula): Formula
data class PastMaxPrevalence(val fraction: Double, val inner: Formula): Formula
data class Binding<Type>(val bindTerm: Term<Type>, val inner: Formula): Formula

sealed interface Term<Type>
data class Constant<Type>(val value: Type) : Term<Type>
data class Variable<Type>(val phi: () -> Type) : Term<Type>