package tools.aqua.stars.logic.kcmftbl.dsl

data class Leq<Type>(val lhs: Term<Type>, val rhs: Term<Type>): Formula
data class Geq<Type>(val lhs: Term<Type>, val rhs: Term<Type>): Formula
data class Lt<Type>(val lhs: Term<Type>, val rhs: Term<Type>): Formula
data class Gt<Type>(val lhs: Term<Type>, val rhs: Term<Type>): Formula
data class Eq<Type>(val lhs: Term<Type>, val rhs: Term<Type>): Formula
data class Ne<Type>(val lhs: Term<Type>, val rhs: Term<Type>): Formula