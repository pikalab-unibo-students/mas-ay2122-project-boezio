package clpqr.utils

import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.exception.Warning
import org.chocosolver.solver.Model
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression
import org.chocosolver.solver.expression.continuous.relational.CReExpression
import org.chocosolver.util.ESat

class ConstraintChecker(
    private val chocoModel: Model,
    private val substitution: Substitution.Unifier) : DefaultTermVisitor<Boolean>() {
    override fun defaultValue(term: Term) = throw IllegalStateException("Cannot handle $term as constraint")

    override fun visitTuple(term: Tuple): Boolean {
        return term.unfoldedSequence.all { it.accept(this) }
    }

    override fun visitStruct(term: Struct): Boolean {
        require(term.arity == 2){
            "Invalid constraint structure"
        }
        val struct = term.castToStruct()
        val firstTerm = struct.args[0]
        val secondTerm = struct.args[1]
        return when(struct.functor){
            "<" -> checkConstraint(firstTerm, secondTerm, CArExpression::lt, chocoModel)
            ">" -> checkConstraint(firstTerm, secondTerm, CArExpression::gt, chocoModel)
            "=<" -> checkConstraint(firstTerm, secondTerm, CArExpression::le, chocoModel)
            "<=" -> checkConstraint(firstTerm, secondTerm, CArExpression::le, chocoModel)
            ">=" -> checkConstraint(firstTerm, secondTerm, CArExpression::ge, chocoModel)
            "=:=" -> checkConstraint(firstTerm, secondTerm, CArExpression::eq, chocoModel)
            "=" -> checkConstraint(firstTerm, secondTerm, CArExpression::eq, chocoModel)
            else -> throw IllegalStateException("Cannot handle constraint ${struct.functor}")
        }
    }

    private class UndefinedWarning: Warning(
        message = "constraints check is undefined",
        context = ClassicExecutionContext()
    ){
        override fun pushContext(newContext: ExecutionContext): Warning {
            return this
        }

        override fun updateContext(newContext: ExecutionContext, index: Int): Warning {
            return this
        }

        override fun updateLastContext(newContext: ExecutionContext): Warning {
            return this
        }

    }

    private fun checkConstraint(
        firstTerm: Term,
        secondTerm: Term,
        operation: (CArExpression, CArExpression) -> CReExpression,
        model: Model
    ):Boolean{
        val logicalVars = (firstTerm.variables + secondTerm.variables).toList()
        val varMap = model.variablesMap(logicalVars, substitution).flip()
        val parser = ExpressionParser(model, varMap, substitution)
        val firstExpression = firstTerm.accept(parser)
        val secondExpression = secondTerm.accept(parser)
        return when(operation(firstExpression, secondExpression).equation().isSatisfied){
            ESat.TRUE -> true
            ESat.FALSE -> false
            else -> throw UndefinedWarning()
        }
    }
}
