package clpb

import clpCore.chocoModel
import clpCore.solutions
import clpCore.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object RandomLabeling: BinaryRelation.NonBacktrackable<ExecutionContext>("random_labeling") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsInteger(0)
        val seed = first.castToInteger().value.toInt()
        ensuringArgumentIsList(1)
        require(second.castToList().toList().all { it is Var }){
            "First argument does not contain only variables"
        }
        val chocoModel = chocoModel
        val vars = second.variables.distinct().toList()
        val varsMap = chocoModel.variablesMap(vars, context.substitution).toMutableMap()
        // if the labeling is used only with taut, it must fail
        if (varsMap.isEmpty())
            return replyFail()
        // set of variables not contained in the model
        val newVars = (vars.toSet() subtract varsMap.values.toSet()).toList()
        for(variable in newVars){
            chocoModel.boolVar(variable.completeName)
        }
        val newVarsMap = chocoModel.variablesMap(newVars, context.substitution)
        varsMap.putAll(newVarsMap)
        val solver = chocoModel.solver
        val solutions = solver.solutions(varsMap, context.substitution).toList()
        val numSolutions = solutions.size
        return replyWith(solutions[seed.mod(numSolutions)])
    }
}