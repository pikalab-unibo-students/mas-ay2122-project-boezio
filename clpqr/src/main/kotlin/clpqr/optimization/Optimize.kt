package clpqr.optimization

import clpCore.chocoModel
import clpCore.getOuterVariable
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.search.ProblemType
import clpqr.utils.convertExpression
import clpqr.utils.createChocoSolver
import clpqr.utils.equations
import clpqr.utils.filterNotConstantVar
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

abstract class Optimize(operator: String): UnaryPredicate.NonBacktrackable<ExecutionContext>(operator) {

    protected abstract val problemType: ProblemType

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val expressionVars = first.variables.distinct().toList()
        // map of variables in the expression
        val varsMap = chocoModel.variablesMap(expressionVars, context.substitution).toMutableMap()
        // if first is not a variable, a new variable is created which denotes the expression
        val expression = convertExpression(first, varsMap, context.substitution)
        val expressionVar = expression.castToVar()
        // if the expression is not a variable, the new variable has to be added to the varsMap
        if(!varsMap.values.contains(expressionVar)) {
            val newVarsMap = chocoModel.variablesMap(listOf(expressionVar), context.substitution)
            varsMap.putAll(newVarsMap)
        // if the expression is a variable, related variables in an equality constraint has to be retrieved
        }else{
            val otherVars = equations[first.castToVar().getOuterVariable(context.substitution)]!!.toList()
            val newVarsMap = chocoModel.variablesMap(otherVars, context.substitution)
            varsMap.putAll(newVarsMap)
        }
        val config = Configuration(problemType = problemType, objective = first)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        return replyWith(solver.solutions(varsMap, context.substitution).last())
    }
}