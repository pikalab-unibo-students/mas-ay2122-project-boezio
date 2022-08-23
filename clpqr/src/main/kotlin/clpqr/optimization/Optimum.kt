package clpqr.optimization

import clpCore.chocoModel
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.search.ProblemType
import clpqr.utils.calculateExpression
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
        val varsMap = chocoModel.variablesMap(expressionVars)
        val config = Configuration(problemType = problemType, objective = first)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        // Substitution for optimum
        val optimumValue = Real.of(solver.calculateExpression(varsMap, first).last())
        // Substitution for each variable in the model
        val allVarsMap = chocoModel.vars.associateWith { Var.of(it.name) }
        val varsSubstitution = solver.solutions(allVarsMap).last()
        // overall substitution
        val finalSubstitution = varsSubstitution.toMap() + mapOf(optimum to optimumValue)
        return replyWith(Substitution.of(finalSubstitution))
    }
}
