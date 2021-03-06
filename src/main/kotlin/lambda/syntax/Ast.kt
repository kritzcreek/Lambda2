package lambda.syntax

import lambda.*

data class SourceFile(val declarations: List<Declaration>, val span: Span) {
    fun typeDeclarations(): List<Declaration.Type> {
        return declarations.mapNotNull { it as? Declaration.Type }

    }

    fun valueDeclarations(): List<Declaration.Value> {
        return declarations.mapNotNull { it as? Declaration.Value }
    }
}

sealed class Declaration {

    abstract val span: Span

    data class Value(
        val name: Name,
        val scheme: Scheme,
        val expr: Expression,
        override val span: Span
    ) : Declaration()

    data class Type(
        val name: Name,
        val tyArgs: List<TyVar>,
        val dataConstructors: List<DataConstructor>,
        override val span: Span
    ) : Declaration()
}

data class DataConstructor(val name: Name, val fields: List<Type>, val span: Span)
