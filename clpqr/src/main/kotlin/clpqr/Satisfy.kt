package clpqr

import clpCore.chocoModel
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.utils.createChocoSolver
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Satisfy: UnaryPredicate<ExecutionContext>("satisfy") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        ensuringArgumentIsList(0)
        val varsList = first.castToList().toList()
        for(variable in varsList){
            if(variable !is Var)
                throw TypeError.forArgument(context, signature, TypeError.Expected.VARIABLE, variable)
        }
        val logicVars = first.variables.toList()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution)
        val config = Configuration()
        val solver = createChocoSolver(chocoModel, config, varsMap)
        return solver.solutions(varsMap).drop(1).map { replyWith(it) }
    }

}