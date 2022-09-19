package clpqr.optimization

import clpCore.chocoModel
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.search.ProblemType
import clpqr.utils.createChocoSolver
import clpqr.utils.filterNotConstantVar
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

abstract class Optimize(operator: String): UnaryPredicate.NonBacktrackable<ExecutionContext>(operator) {

    protected abstract val problemType: ProblemType

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {

        var varsMap = chocoModel.vars.associateWith { Var.of(it.name) }
        // there are some variables which are constants
        varsMap = varsMap.filterNotConstantVar()
        val expressionVars = first.variables.toList()
        val expressionMap = chocoModel.variablesMap(expressionVars)
        val config = Configuration(problemType = problemType, objective = first)
        val solver = createChocoSolver(chocoModel, config, expressionMap)
        return replyWith(solver.solutions(varsMap).last())
    }
}