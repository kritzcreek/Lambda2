package lambda

import lambda.syntax.Name

inline class TyVar(val name: Name) {
    override fun toString(): String = name.toString()
}

sealed class Type {
    val span: Span
        get() = when (this) {
            ErrorSentinel -> Span.DUMMY
            is Constructor -> sp
            is Var -> v.name.span
            is Fun -> sp
            is Unknown -> Span.DUMMY
        }

    object ErrorSentinel : Type()
    data class Constructor(val name: Name, val tyArgs: List<Type> = emptyList(), val sp: Span = Span.DUMMY) : Type()
    data class Var(val v: TyVar) : Type()
    data class Unknown(val u: Int) : Type() {
        override fun toString(): String = "u$u"
    }

    data class Fun(val arg: Type, val result: Type, val sp: Span = Span.DUMMY) : Type()

    fun isError() = this is ErrorSentinel

    fun subst(tyVar: TyVar, type: Type): Type =
        over {
            when {
                it is Var && it.v == tyVar -> type
                else -> it
            }
        }

    fun unknowns(): HashSet<Int> {
        val res = HashSet<Int>()
        over {
            if (it is Unknown) res.add(it.u)
            it
        }
        return res
    }

    fun freeVars(): HashSet<TyVar> {
        val res = HashSet<TyVar>()
        over {
            if (it is Var) res.add(it.v)
            it
        }
        return res
    }

    fun substMany(subst: List<Pair<TyVar, Type>>): Type =
        subst.fold(this) { acc, (v, t) -> acc.subst(v, t) }

    fun over(f: (Type) -> Type): Type =
        when (this) {
            is ErrorSentinel, is Var, is Unknown -> f(this)
            is Constructor -> f(Constructor(name, tyArgs.map { it.over(f) }, sp))
            is Fun -> f(Fun(arg.over(f), result.over(f), sp))
        }

    companion object {
        fun v(name: String) = Var(TyVar(Name(name)))
        val Int = Constructor(Name("Int"))
        val Bool = Constructor(Name("Bool"))
        val String = Constructor(Name("String"))
        val Unit = Constructor(Name("Unit"))
    }
}

data class Scheme(val vars: List<TyVar>, val ty: Type) {
    val span: Span get() = Span(vars.firstOrNull()?.name?.span?.start ?: ty.span.start, ty.span.end)
    fun freeVars(): HashSet<TyVar> = ty.freeVars().apply { removeAll(vars) }
    fun unknowns(): HashSet<Int> = ty.unknowns()

    companion object {
        fun fromType(type: Type): Scheme = Scheme(emptyList(), type)
    }
}