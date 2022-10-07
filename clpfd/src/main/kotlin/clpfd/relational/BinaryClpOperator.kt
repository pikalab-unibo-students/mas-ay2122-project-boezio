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
        var varMap = chocoModel.variablesMap(logicalVars).flip()
        val firstExpression: ArExpression
        val secondExpression: ArExpression
        // variables of Choco model are used as keys for a substitution in the current context
        if(varMap.isEmpty()){
            val inverseSubstitution = context.substitution.filter { (_,v) -> logicalVars.contains(v) }
                .entries.associate { (k,v) -> v to k }
            varMap = chocoModel.variablesMap(logicalVars.map { inverseSubstitution[it]!! }).flip()
            val parser = ExpressionParser(chocoModel, varMap)
            firstExpression = inverseSubstitution[first]!!.accept(parser)
            secondExpression = inverseSubstitution[second]!!.accept(parser)

        }else{
            val parser = ExpressionParser(chocoModel, varMap)
            firstExpression = first.accept(parser)
            secondExpression = second.accept(parser)
        }
        operation(firstExpression, secondExpression).decompose().post()
    }
}