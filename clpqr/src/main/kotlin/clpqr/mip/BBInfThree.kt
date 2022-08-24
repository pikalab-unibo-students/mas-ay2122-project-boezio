package clpqr.mip

import clpCore.chocoModel
import clpCore.flip
import clpCore.solutions
import clpqr.utils.calculateExpression
import clpqr.utils.createChocoSolver
import clpqr.search.Configuration
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.chocosolver.solver.variables.RealVar

object BBInfThree: TernaryRelation.NonBacktrackable<ExecutionContext>("bb_inf") {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        require(first.castToList().toList().all { it is Var }){
            "$first is not a list of variables"
        }
        val vector = first.variables.distinct().toList()
        val expressionVars = second.variables.distinct().toList()
        ensuringArgumentIsVariable(2)
        val inf = third.castToVar()
        val varsMap = chocoModel.vars.associateWith { Var.of(it.name) }.flip()
        // impose an integer constraint for variables contained in the first argument
        for(variable in vector){
            val intVar = chocoModel.intVar(Double.MIN_VALUE.toInt(), Double.MAX_VALUE.toInt())
            chocoModel.eq(varsMap[variable] as RealVar, intVar).post()
        }
        val config = Configuration(problemType = ProblemType.MINIMIZE, objective = second)
        val solver = createChocoSolver(chocoModel, config, varsMap.flip())
        // Substitution for optima
        val infValue = Real.of(solver.calculateExpression(varsMap.flip(), second).last())
        // Substitution for all variables
        val allVarsSubstitution = solver.solutions(varsMap.flip()).last()
        // overall substitution
        val substitution = allVarsSubstitution.toMap() + mapOf(inf to infValue)
        return replyWith(Substitution.of(substitution))
    }
}