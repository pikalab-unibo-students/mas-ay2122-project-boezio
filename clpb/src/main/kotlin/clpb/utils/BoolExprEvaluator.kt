package clpb.utils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor

class BoolExprEvaluator : DefaultTermVisitor<Boolean>() {
    override fun defaultValue(term: Term): Boolean =
        error("Unsupported sub-expression: $term")

    override fun visitInteger(term: Integer): Boolean {
        return when(term.value.toInt()){
            1 -> true
            0 -> false
            else -> throw IllegalStateException()
        }
    }

    override fun visitStruct(term: Struct): Boolean {
        when (term.arity) {
            2 -> when (term.functor) {
                "+" -> return binaryExpression(term, Boolean::or)
                "*" -> return binaryExpression(term, Boolean::and)
                "#" -> return binaryExpression(term, Boolean::xor)
                "=:=" -> return binaryExpression(term, Boolean::equals)
                "=\\=" -> return term[0].accept(this) != term[1].accept(this)
                "=<" -> return !term[0].accept(this) or term[1].accept(this)
                ">=" -> return binaryExpression(term, Boolean::xor)
                "<" -> return !term[0].accept(this) and term[1].accept(this)
                ">" -> return term[0].accept(this) and !term[1].accept(this)
            }
            1 -> when (term.functor) {
                "+" -> return unaryExpression(term, UnaryOp.OR)
                "*" -> return unaryExpression(term, UnaryOp.AND)
                "~" -> return !term[0].accept(this)
            }
        }
        return super.visitStruct(term) // indirectly calls defaultValue(term)
    }

    private fun unaryExpression(struct: Struct, op: UnaryOp): Boolean {
        require(struct[0] is List){
            "${struct[0]} is not a logic list"
        }
        val expressions = struct[0].castToList().toList()
        val first = expressions.map { it.accept(this) }.toTypedArray()
        return when(op){
            UnaryOp.AND -> first.all { it }
            UnaryOp.OR -> first.any { it }
            else -> throw IllegalStateException()
        }
    }

    private fun binaryExpression(struct: Struct, operator: (Boolean, Boolean) -> Boolean): Boolean {
        val first = struct[0].accept(this)
        val second = struct[1].accept(this)
        return operator(first, second)
    }
}