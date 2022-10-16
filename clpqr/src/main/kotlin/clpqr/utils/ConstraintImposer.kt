package clpqr.utils

import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import org.chocosolver.solver.Model
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression
import org.chocosolver.solver.expression.continuous.relational.CReExpression

internal class ConstraintImposer(
    private val chocoModel: Model,
    private val context: ExecutionContext
) : DefaultTermVisitor<Unit>() {
    override fun defaultValue(term: Term) = throw IllegalStateException("Cannot handle $term as constraint")

    override fun visitTuple(term: Tuple) {
        term.unfoldedSequence.forEach { it.accept(this) }
    }

    override fun visitStruct(term: Struct) {
        require(term.arity == 2){
            "Invalid constraint structure"
        }
        val struct = term.castToStruct()
        val firstTerm = struct.args[0]
        val secondTerm = struct.args[1]
        when(struct.functor){
            "<" -> applyRelOperator(firstTerm, secondTerm, CArExpression::lt, chocoModel)
            ">" -> applyRelOperator(firstTerm, secondTerm, CArExpression::gt, chocoModel)
            "=<", "<=" -> applyRelOperator(firstTerm, secondTerm, CArExpression::le, chocoModel)
            ">=" -> applyRelOperator(firstTerm, secondTerm, CArExpression::ge, chocoModel)
            "=:=", "="-> applyRelOperator(firstTerm, secondTerm, CArExpression::eq, chocoModel)
            else -> throw IllegalStateException("Cannot handle constraint ${struct.functor}")
        }
    }

    private fun applyRelOperator(
        firstTerm: Term,
        secondTerm: Term,
        operation: (CArExpression, CArExpression) -> CReExpression,
        model: Model
    ){
        val logicalVars = (firstTerm.variables + secondTerm.variables).toSet()
        val varMap = model.variablesMap(logicalVars, context.substitution).flip()
        val parser = ExpressionParser(model, varMap, context.substitution)
        val firstExpression = firstTerm.accept(parser)
        val secondExpression = secondTerm.accept(parser)
        operation(firstExpression, secondExpression).equation().post()
    }

}