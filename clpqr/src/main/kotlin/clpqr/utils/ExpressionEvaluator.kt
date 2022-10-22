package clpqr.utils

import clpCore.getOuterVariable
import clpCore.valueAsTerm
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.chocosolver.solver.variables.Variable
import kotlin.math.*

class ExpressionEvaluator<T : Variable>(
    private val variables: Map<Var, T>,
    private val substitution: Substitution.Unifier,
    private val context: ExecutionContext,
    private val signature: Signature
) : DefaultTermVisitor<Double>() {
    override fun defaultValue(term: Term): Double =
        throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REFERENCE, term)

    override fun visitVar(term: Var): Double =
        when(val valueAsTerm = variables[term.getOuterVariable(substitution)]?.valueAsTerm){
            is Integer -> valueAsTerm.value.toDouble()
            is Real -> valueAsTerm.value.toDouble()
            else -> throw DomainError.forArgument(
                context, signature, DomainError.Expected.ATOM_PROPERTY, term
            )
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

    private fun binaryExpression(struct: Struct, operator: (Double, Double) -> Double): Double {
        val first = struct[0].accept(this)
        val second = struct[1].accept(this)
        return operator(first, second)
    }
}