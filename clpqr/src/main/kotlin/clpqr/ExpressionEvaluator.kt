package clpqr

import clpCore.valueAsTerm
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.Variable
import java.lang.IllegalStateException
import kotlin.math.*

class ExpressionEvaluator<T : Variable>(
    private val variables: Map<Var, T>
) : DefaultTermVisitor<Double>() {
    override fun defaultValue(term: Term): Double =
        error("Unsupported sub-expression: $term")

    override fun visitVar(term: Var): Double =
        when(val valueAsTerm = variables[term]?.valueAsTerm){
            is Integer -> valueAsTerm.value.toDouble()
            is Real -> valueAsTerm.value.toDouble()
            else -> throw IllegalStateException()
        }

    override fun visitInteger(term: Integer): Double =
        term.value.toDouble()

    override fun visitReal(term: Real): Double =
        term.value.toDouble()

    override fun visitStruct(term: Struct): Double {
        when (term.arity) {
            2 -> when (term.functor) {
                "+" -> return binaryExpression(term, Double::plus)
                "-" -> return binaryExpression(term, Double::minus)
                "*" -> return binaryExpression(term, Double::times)
                "^" -> return binaryExpression(term, Double::pow)
                "/" -> return binaryExpression(term, Double::div)
                "min" -> return min(term[0].accept(this), term[1].accept(this))
                "max" -> return max(term[0].accept(this), term[1].accept(this))

            }
            1 -> when (term.functor) {
                "abs" -> return abs(term[0].accept(this))
                "-" -> return - term[0].accept(this)
                "sin" -> return sin(term[0].accept(this))
                "cos" -> return cos(term[0].accept(this))
                "tan" -> return tan(term[0].accept(this))
                "exp" -> return exp(term[0].accept(this))
                "pow" -> return term[0].accept(this).pow(2.0)
            }
        }
        return super.visitStruct(term) // indirectly calls defaultValue(term)
    }

    private fun unaryExpression(struct: Struct, operator: (Double) -> Double): Double {
        val first = struct[0].accept(this)
        return operator(first)
    }

    private fun binaryExpression(struct: Struct, operator: (Double, Double) -> Double): Double {
        val first = struct[0].accept(this)
        val second = struct[1].accept(this)
        return operator(first, second)
    }
}