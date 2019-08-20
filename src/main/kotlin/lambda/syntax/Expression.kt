package lambda.syntax

import lambda.Span
import lambda.Spanned
import lambda.Type

sealed class Lit
data class IntLit(val int: Int) : Lit()
data class BoolLit(val bool: Boolean) : Lit()

sealed class Expression {
    data class Literal(val lit: Lit) : Expression()
    data class Var(val name: Name) : Expression()
    data class Lambda(val binder: Spanned<Name>, val body: Spanned<Expression>) : Expression()
    data class App(val func: Spanned<Expression>, val arg: Spanned<Expression>) : Expression()
    data class Typed(val expr: Spanned<Expression>, val type: Spanned<Type>) : Expression() {
        override fun withSpan(span: Span) = Spanned(span, this)
    }

    data class Let(val binder: Spanned<Name>, val expr: Spanned<Expression>, val body: Spanned<Expression>) :
        Expression()

    data class If(
        val condition: Spanned<Expression>,
        val thenBranch: Spanned<Expression>,
        val elseBranch: Spanned<Expression>
    ) : Expression()

    open fun withSpan(span: Span) = Spanned(span, this)

}