package clpqr.mip

import clpCore.chocoModel
import clpCore.flip
import clpCore.variablesMap
import clpqr.utils.calculateExpression
import clpqr.utils.createChocoSolver
import clpqr.search.Configuration
import clpqr.search.ProblemType
import clpqr.utils.bound
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.chocosolver.solver.variables.RealVar

object BBInfThree: TernaryRelation.NonBacktrackable<ExecutionContext>("bb_inf") {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val varsList = first.castToList().toList()
        for(variable in varsList){
            if(variable !is Var)
                throw TypeError.forArgument(context, signature, TypeError.Expected.VARIABLE, variable)
        }
        val vector = varsList.map { it.castToVar() }
        ensuringArgumentIsVariable(2)
        val inf = third.castToVar()
        val vars = vector.toMutableList()
        vars.addAll(second.variables.distinct())
        val varsMap = chocoModel.variablesMap(vars, context.substitution)
        // impose an integer constraint for variables contained in the first argument
        for(variable in vector){
            val intVar = chocoModel.intVar(-bound, bound)
            chocoModel.eq(varsMap.flip()[variable] as RealVar, intVar).post()
        }
        val config = Configuration(problemType = ProblemType.MINIMIZE, objective = second)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        // Substitution for optima
        val infValue = Real.of(solver.calculateExpression(
            varsMap, second, context.substitution, context, signature
        ).last())
        val substitution = mapOf(inf to infValue)
        return replyWith(Substitution.of(substitution))
    }
}