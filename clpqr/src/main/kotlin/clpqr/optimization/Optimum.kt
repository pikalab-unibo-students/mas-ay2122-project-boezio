package clpqr.optimization

import clpCore.chocoModel
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.search.ProblemType
import clpqr.utils.calculateExpression
import clpqr.utils.createChocoSolver
import clpqr.utils.filterNotConstantVar
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
        val varsMap = chocoModel.variablesMap(expressionVars)
        val config = Configuration(problemType = problemType, objective = first)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        val optimumValue = Real.of(solver.calculateExpression(varsMap, first).last())
        return replyWith(Substitution.of(optimum to optimumValue))
    }
}
