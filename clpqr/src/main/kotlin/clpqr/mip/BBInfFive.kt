package clpqr.mip

import clpCore.chocoModel
import clpCore.flip
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
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.QuinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.RealVar

object BBInfFive: QuinaryRelation.NonBacktrackable<ExecutionContext>("bb_inf") {

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term,
        fifth: Term
    ): Solve.Response {
        ensuringArgumentIsList(0)
        val varsList = first.castToList().toList()
        for(variable in varsList){
            if(variable !is Var)
                throw TypeError.forArgument(context, signature, TypeError.Expected.VARIABLE, variable)
        }
        val vector = varsList.map { it.castToVar() }
        ensuringArgumentIsVariable(2)
        val inf = third.castToVar()
        ensuringArgumentIsVariable(3)
        val vertex = fourth.castToVar()
        // range of eps described in SWI Prolog
        if(!fifth.let { it is Real && it.value.toDouble() >= 0 && it.value.toDouble() <= 0.5 })
            throw DomainError.forArgument(context, signature, DomainError.Expected.ATOM_PROPERTY, fifth)
        val eps = fifth.castToReal().value.toDouble()
        // eps is not supported by Choco solver
        if(eps != 0.0)
            throw ExistenceError.of(
                context,
                ExistenceError.ObjectType.RESOURCE,
                Real.of(eps),
                "Eps different from 0 are not supported"
            )
        val vars = vector.toSet()
        val allVars = vars union second.variables.distinct().toSet()
        val varsMap = chocoModel.variablesMap(allVars, context.substitution).toMutableMap()
        // impose an integer constraint for variables contained in the first argument
        for(variable in vector){
            val intVar = chocoModel.intVar(-bound, bound)
            chocoModel.eq(varsMap.flip()[variable] as RealVar, intVar).post()
        }
        val expression = convertExpression(second, varsMap, context.substitution)
        val expressionVar = expression.castToVar()
        // first term is now expressed with a new variable
        if(!varsMap.values.contains(expressionVar)) {
            val newVarsMap = chocoModel.variablesMap(listOf(expressionVar), context.substitution)
            varsMap.putAll(newVarsMap)
        }
        val config = Configuration(problemType = ProblemType.MINIMIZE, objective = expression)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        // Substitution for Vertex
        val vertexValue = solver.getVectorValue(varsMap, vector).last()
        val vertexList = List.of(vertexValue)
        // Substitution for optimum
        solver.hardReset()
        val infValue = Real.of(solver.calculateExpression(
            varsMap, second, context.substitution, context, signature
        ).last())
        // Substitution for all variables
        // val allVarsSubstitution = solver.solutions(varsMap).last()
        // Overall substitution
        val substitution = mapOf(inf to infValue, vertex to vertexList)
        return replyWith(Substitution.of(substitution))
    }
}