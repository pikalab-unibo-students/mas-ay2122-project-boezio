package clpqr.mip

import clpCore.chocoModel
import clpCore.flip
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.search.ProblemType
import clpqr.utils.*
import clpqr.utils.calculateExpression
import clpqr.utils.createChocoSolver
import clpqr.utils.getVectorValue
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsGround
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
        ensuringArgumentIsVariable(2)
        val inf = third.castToVar()
        ensuringArgumentIsVariable(3)
        val vertex = fourth.castToVar()
        ensuringArgumentIsGround(4)
        // range of eps described in SWI Prolog
        require(fifth.let { it is Real && it.value.toDouble() >= 0 && it.value.toDouble() <= 0.5 }){
            "$fifth is not a valid eps value"
        }
        val eps = fifth.castToReal().value.toDouble()
        // eps is not supported by Choco solver
        if(eps != 0.0){
            throw IllegalStateException()
        }
        val vars = vector.toMutableList()
        vars.addAll(second.variables)
        val varsMap = chocoModel.variablesMap(vars)
        // impose an integer constraint for variables contained in the first argument
        for(variable in vector){
            val intVar = chocoModel.intVar(-bound, bound)
            chocoModel.eq(varsMap.flip()[variable] as RealVar, intVar).post()
        }
        val config = Configuration(problemType = ProblemType.MINIMIZE, objective = second)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        // Substitution for Vertex
        val vertexValue = solver.getVectorValue(varsMap, vector).last()
        val vertexList = List.of(vertexValue)
        // Substitution for optimum
        solver.hardReset()
        val infValue = Real.of(solver.calculateExpression(varsMap, second).last())
        // Substitution for all variables
        // val allVarsSubstitution = solver.solutions(varsMap).last()
        // Overall substitution
        val substitution = mapOf(inf to infValue, vertex to vertexList)
        return replyWith(Substitution.of(substitution))
    }
}