package clpqr.mip

import clpCore.chocoModel
import clpCore.flip
import clpCore.solutions
import clpCore.variablesMap
import clpqr.calculateExpression
import clpqr.createChocoSolver
import clpqr.getVectorValue
import clpqr.search.Configuration
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.RealVar
import java.lang.IllegalStateException

object BBInfFive: QuinaryRelation.NonBacktrackable<ExecutionContext>("bb_inf") {

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term,
        fifth: Term
    ): Solve.Response {
        ensuringArgumentIsList(0)
        require(first.castToList().toList().all { it is Var }){
            "$first is not a list of variables"
        }
        val vector = first.variables.distinct().toList()
        val expressionVars = second.variables.distinct().toList()
        ensuringArgumentIsVariable(2)
        val inf = third.castToVar()
        ensuringArgumentIsVariable(3)
        val vertex = fourth.castToVar()
        ensuringArgumentIsAtom(4)
        require(fifth.let { it is Real && it.value.toDouble() >= 0 && it.value.toDouble() <= 0.5 }){
            "$fifth is not a valid eps value"
        }
        val eps = fifth.castToReal().value.toDouble()
        // eps is not supported by Choco solver
        if(eps != 0.0){
            throw IllegalStateException()
        }
        val varsMap = chocoModel.vars.associateWith { Var.of(it.name) }.flip()
        // impose an integer constraint for variables contained in the first argument
        for(variable in vector){
            val intVar = chocoModel.intVar(Double.MIN_VALUE.toInt(), Double.MAX_VALUE.toInt())
            chocoModel.eq(varsMap[variable] as RealVar, intVar).post()
        }
        val config = Configuration(problemType = ProblemType.MINIMIZE, objective = second)
        val solver = createChocoSolver(chocoModel, config, varsMap.flip())
        // Substitution for Vertex
        val vertexValue = solver.getVectorValue(varsMap.flip(), vector).last()
        val vertexList = List.of(vertexValue)
        // Substitution for optima
        val infValue = Real.of(solver.calculateExpression(varsMap.flip(), second).last())
        // Substitution for all variables
        val allVarsSubstitution = solver.solutions(varsMap.flip()).last()
        // Overall substitution
        val substitution = allVarsSubstitution.toMap() + mapOf(inf to infValue, vertex to vertexList)
        return replyWith(Substitution.of(substitution))
    }
}