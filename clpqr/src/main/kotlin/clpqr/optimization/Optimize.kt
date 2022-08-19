package clpqr.optimization

import clpCore.chocoModel
import clpqr.createChocoSolver
import clpqr.getVectorValue
import clpqr.search.Configuration
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

abstract class Optimize(operator: String): UnaryPredicate.NonBacktrackable<ExecutionContext>(operator) {

    protected abstract val problemType: ProblemType

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val config = Configuration(problemType = problemType, objective = first)
        val varsMap = chocoModel.vars.associateWith { Var.of(it.name) }
        val solver = createChocoSolver(chocoModel, config, varsMap)
        val logicVars = varsMap.values.toList()
        val solutions = solver.getVectorValue(varsMap, logicVars).last()
        val mapSolutions = mutableMapOf<Var,Term>()
        for((i,solution) in solutions.withIndex()){
            mapSolutions[logicVars[i]] = solution
        }
        return replyWith(Substitution.of(mapSolutions))
    }
}