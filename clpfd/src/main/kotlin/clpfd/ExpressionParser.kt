package clpfd

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import org.chocosolver.solver.Model
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression.IntPrimitive
import org.chocosolver.solver.variables.Variable

class ExpressionParser<T : Variable>(
    private val chocoModel: Model,
    private val variables: Map<Var, T>
) : DefaultTermVisitor<ArExpression>() {
    override fun defaultValue(term: Term): ArExpression =
        error("Unsupported sub-expression: $term")

    override fun visitVar(term: Var): ArExpression =
        asExpression(variables[term] ?: error("No such a variable: $term"))

    override fun visitInteger(term: Integer): ArExpression =
        IntPrimitive(term.value.toIntExact(), chocoModel)

    override fun visitStruct(term: Struct): ArExpression {
        when (term.arity) {
            2 -> when (term.functor) {
                "+" -> return binaryExpression(term, ArExpression::add)
                "-" -> return binaryExpression(term, ArExpression::sub)
                "*" -> return binaryExpression(term, ArExpression::mul)
                "^" -> return binaryExpression(term, ArExpression::pow)
                "div" -> return binaryExpression(term, ArExpression::div)
                "//" -> throw IllegalStateException("// is not supported")
                "mod" -> return binaryExpression(term, ArExpression::mod)
                "rem" -> throw IllegalStateException("rem is not supported")
                "min" -> return binaryExpression(term, ArExpression::min)
                "max" -> return binaryExpression(term, ArExpression::max)
            }
            1 -> when (term.functor) {
                "abs" -> return unaryExpression(term, ArExpression::abs)
                "-" -> return unaryExpression(term, ArExpression::neg)
            }
        }
        return super.visitStruct(term) // indirectly calls defaultValue(term)
    }

    private fun asExpression(variable: Variable): ArExpression {
        return variable as ArExpression // this may fail!
    }

    private fun unaryExpression(struct: Struct, operator: (ArExpression) -> ArExpression): ArExpression {
        val first = struct[0].accept(this)
        return operator(first)
    }

    private fun binaryExpression(struct: Struct, operator: (ArExpression, ArExpression) -> ArExpression): ArExpression {
        val first = struct[0].accept(this)
        val second = struct[1].accept(this)
        return operator(first, second)
    }
}

