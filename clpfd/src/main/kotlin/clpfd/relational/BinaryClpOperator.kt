package clpfd.relational

import clpfd.ExpressionParser
import clpCore.chocoModel
import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression

abstract class BinaryClpOperator(operator: String) : BinaryRelation.NonBacktrackable<ExecutionContext>(operator) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        applyRelConstraint(first, second)
        return replySuccess()
    }

    protected abstract val operation: (ArExpression, ArExpression) -> ReExpression

    // apply a generic relational constraint
    private fun Solve.Request<ExecutionContext>.applyRelConstraint(first: Term, second: Term) {
        val chocoModel = chocoModel
        val logicalVars = (first.variables + second.variables).toSet()
        val varMap = chocoModel.variablesMap(logicalVars).flip()
        val parser = ExpressionParser(chocoModel, varMap)
        val firstExpression = first.accept(parser)
        val secondExpression = second.accept(parser)
        operation(firstExpression, secondExpression).decompose().post()
    }
}