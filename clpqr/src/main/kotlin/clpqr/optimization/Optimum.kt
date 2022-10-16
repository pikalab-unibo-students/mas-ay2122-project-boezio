package clpqr.optimization

import clpCore.chocoModel
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.search.ProblemType
import clpqr.utils.calculateExpression
import clpqr.utils.convertExpression
import clpqr.utils.createChocoSolver
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

abstract class Optimum(operator: String): BinaryRelation.NonBacktrackable<ExecutionContext>(operator) {

    protected abstract val problemType: ProblemType

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        val expressionVars = first.variables.distinct().toList()
        ensuringArgumentIsVariable(1)
        val optimum = second.castToVar()
        val varsMap = chocoModel.variablesMap(expressionVars, context.substitution).toMutableMap()
        // if first is not a variable, a new variable is created which denotes the expression
        val expression = convertExpression(first, varsMap, context.substitution)
        val expressionVar = expression.castToVar()
        // first term is now expressed with a new variable
        if(!varsMap.values.contains(expressionVar)) {
            val newVarsMap = chocoModel.variablesMap(listOf(expressionVar), context.substitution)
            varsMap.putAll(newVarsMap)
        }
        val config = Configuration(problemType = problemType, objective = expression)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        val optimumValue = Real.of(solver.calculateExpression(varsMap, first, context.substitution).last())
        return replyWith(Substitution.of(optimum to optimumValue))
    }
}
