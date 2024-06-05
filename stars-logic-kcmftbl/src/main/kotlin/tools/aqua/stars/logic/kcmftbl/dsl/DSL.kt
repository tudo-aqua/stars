package tools.aqua.stars.logic.kcmftbl.dsl

import tools.aqua.stars.core.types.*
import tools.aqua.stars.data.av.dataclasses.Vehicle
import tools.aqua.stars.logic.kcmftbl.dsl.FormulaBuilder.Companion.formula

class FormulaBuilder(
    val phi: MutableList<Formula> = mutableListOf()
) {
    companion object {
        fun formula(init: FormulaBuilder.() -> Unit): Formula {
            val builder = FormulaBuilder()
            init.invoke(builder)
            return builder.phi[0]
        }

        fun <Type> formula(init: FormulaBuilder.(Ref<Type>) -> Unit): Formula {
            val builder = FormulaBuilder()
            init.invoke(builder, Ref())
            return builder.phi[0]
        }
    }

    private fun buildNeg(): Neg = assert(phi.size == 1).let { Neg(phi.first()) }
    private fun buildAnd(): And = assert(phi.size == 2).let { And(phi[0], phi[1]) }
    private fun buildOr(): Or = assert(phi.size == 2).let { Or(phi[0], phi[1]) }
    private fun buildImpl(): Implication = assert(phi.size == 2).let { Implication(phi[0], phi[1]) }
    private fun buildIff(): Iff = assert(phi.size == 2).let { Iff(phi[0], phi[1]) }
    private fun buildPrev(interval: Pair<Int, Int>?): Prev {
        assert(phi.size == 1)
        return Prev(interval, phi.first())
    }

    private fun buildNext(interval: Pair<Int, Int>?): Next {
        assert(phi.size == 1)
        return Next(interval, phi.first())
    }

    private fun buildOnce(interval: Pair<Int, Int>?): Once {
        assert(phi.size == 1)
        return Once(interval, phi.first())
    }

    private fun buildHistorically(interval: Pair<Int, Int>?): Historically {
        assert(phi.size == 1)
        return Historically(interval, phi.first())
    }

    private fun buildEventually(interval: Pair<Int, Int>?): Eventually {
        assert(phi.size == 1)
        return Eventually(interval, phi.first())
    }

    private fun buildGlobally(interval: Pair<Int, Int>? = null): Globally {
        assert(phi.size == 1)
        return Globally(interval, inner = phi[0])
    }

    private fun buildSince(interval: Pair<Int, Int>? = null): Since {
        assert(phi.size == 2)
        return Since(interval, lhs = phi[0], rhs = phi[1])
    }

    private fun buildUntil(interval: Pair<Int, Int>? = null): Until {
        assert(phi.size == 2)
        return Until(interval, lhs = phi[0], rhs = phi[1])
    }

    private fun buildForall(ident: String): Forall {
        assert(phi.size == 1)
        return Forall(ident, phi[0])
    }

    private fun buildExists(): Exists {
        assert(phi.size == 1)
        return Exists(phi[0])
    }

    private fun buildMinPrevalence(fraction: Double): MinPrevalence {
        assert(phi.size == 1)
        return MinPrevalence(fraction, phi[0])
    }

    private fun buildMaxPrevalence(fraction: Double): MaxPrevalence {
        assert(phi.size == 1)
        return MaxPrevalence(fraction, phi[0])
    }

    private fun buildPastMinPrevalence(fraction: Double): PastMinPrevalence {
        assert(phi.size == 1)
        return PastMinPrevalence(fraction, phi[0])
    }

    private fun buildPastMaxPrevalence(fraction: Double): PastMaxPrevalence {
        assert(phi.size == 1)
        return PastMaxPrevalence(fraction, phi[0])
    }

    private fun <Type> buildBinding(term: Term<Type>): Binding<Type> {
        assert(phi.size == 1)
        return Binding(term, phi[0])
    }

    fun FormulaBuilder.tt(): TT = TT.also { phi.add(it) }
    fun FormulaBuilder.ff(): FF = FF.also { phi.add(it) }
    fun FormulaBuilder.neg(input: Formula): Neg {
        return Neg(input).also { phi.add(it) }
    }

    fun FormulaBuilder.neg(init: FormulaBuilder.() -> Unit = {}): Neg {
        return FormulaBuilder().apply(init).buildNeg().also { phi.add(it) }
    }

    infix fun Formula.and(other: Formula): And = And(this, other).also { phi.add(it) }

    infix fun Formula.or(other: Formula): Or = Or(this, other).also { phi.add(it) }

    infix fun Formula.impl(other: Formula): Implication = Implication(this, other).also { phi.add(it) }

    infix fun Formula.iff(other: Formula): Iff = Iff(this, other).also { phi.add(it) }

    fun FormulaBuilder.prev(interval: Pair<Int, Int>? = null, init: FormulaBuilder.() -> Unit = {}): Prev {
        return FormulaBuilder().apply(init).buildPrev(interval).also { phi.add(it) }
    }

    fun FormulaBuilder.next(interval: Pair<Int, Int>? = null, init: FormulaBuilder.() -> Unit = {}): Next {
        return FormulaBuilder().apply(init).buildNext(interval).also { phi.add(it) }
    }

    fun FormulaBuilder.once(interval: Pair<Int, Int>? = null, init: FormulaBuilder.() -> Unit = {}): Once {
        return FormulaBuilder().apply(init).buildOnce(interval).also { phi.add(it) }
    }

    fun FormulaBuilder.historically(
        interval: Pair<Int, Int>? = null,
        init: FormulaBuilder.() -> Unit = {}
    ): Historically {
        return FormulaBuilder().apply(init).buildHistorically(interval).also { phi.add(it) }
    }

    fun eventually(interval: Pair<Int, Int>? = null, init: FormulaBuilder.() -> Unit = {}): Eventually {
        return FormulaBuilder().apply(init).buildEventually(interval).also { phi.add(it) }
    }

    fun FormulaBuilder.globally(interval: Pair<Int, Int>? = null, init: FormulaBuilder.() -> Unit = {}): Globally {
        return FormulaBuilder().apply(init).buildGlobally(interval).also { phi.add(it) }
    }

    fun FormulaBuilder.since(interval: Pair<Int, Int>? = null, init: FormulaBuilder.() -> Unit = {}): Since {
        return FormulaBuilder().apply(init).buildSince(interval).also { phi.add(it) }
    }

    fun FormulaBuilder.until(interval: Pair<Int, Int>? = null, init: FormulaBuilder.() -> Unit = {}): Until {
        return FormulaBuilder().apply(init).buildUntil(interval).also { phi.add(it) }
    }

    fun FormulaBuilder.forall(ident: String, init: FormulaBuilder.() -> Unit = {}): Forall {
        return FormulaBuilder().apply(init).buildForall(ident).also { phi.add(it) }
    }

    fun <Type> FormulaBuilder.exists(init: FormulaBuilder.(Ref<Type>) -> Unit = {}): Exists {
        return FormulaBuilder().apply { init(Ref()) }.buildExists().also { phi.add(it) }
    }

    fun FormulaBuilder.minPrevalence(fraction: Double, init: FormulaBuilder.() -> Unit = {}): MinPrevalence {
        return FormulaBuilder().apply(init).buildMinPrevalence(fraction).also { phi.add(it) }
    }

    fun FormulaBuilder.maxPrevalence(fraction: Double, init: FormulaBuilder.() -> Unit = {}): MaxPrevalence {
        return FormulaBuilder().apply(init).buildMaxPrevalence(fraction).also { phi.add(it) }
    }

    fun FormulaBuilder.pastMinPrevalence(fraction: Double, init: FormulaBuilder.() -> Unit = {}): PastMinPrevalence {
        return FormulaBuilder().apply(init).buildPastMinPrevalence(fraction).also { phi.add(it) }
    }

    fun FormulaBuilder.pastMaxPrevalence(fraction: Double, init: FormulaBuilder.() -> Unit = {}): PastMaxPrevalence {
        return FormulaBuilder().apply(init).buildPastMaxPrevalence(fraction).also { phi.add(it) }
    }

    fun <Type> FormulaBuilder.binding(term: Term<Type>, init: FormulaBuilder.(Term<Type>) -> Unit = {}): Binding<Type> {
        return FormulaBuilder().apply { init(term) }.buildBinding(term).also { phi.add(it) }
    }

    infix fun <Type> Term<Type>.leq(other: Term<Type>): Leq<Type> = Leq(this, other).also { phi.add(it) }

    infix fun <Type> Term<Type>.lt(other: Term<Type>): Lt<Type> = Lt(this, other).also { phi.add(it) }

    infix fun <Type> Term<Type>.geq(other: Term<Type>): Geq<Type> = Geq(this, other).also { phi.add(it) }

    infix fun <Type> Term<Type>.gt(other: Term<Type>): Gt<Type> = Gt(this, other).also { phi.add(it) }

    infix fun <Type> Term<Type>.eq(other: Term<Type>): Eq<Type> = Eq(this, other).also { phi.add(it) }

    infix fun <Type> Term<Type>.ne(other: Term<Type>): Ne<Type> = Ne(this, other).also { phi.add(it) }
    fun <Type> term(init: () -> Type): Variable<Type> = Variable(init)
    fun <Type> const(value: Type): Constant<Type> = Constant(value)
}

class Ref<T> {
    var tick: Int = 0
    fun now(): T = TODO()
}