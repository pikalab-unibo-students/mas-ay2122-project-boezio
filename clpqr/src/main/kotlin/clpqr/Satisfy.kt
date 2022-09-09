package clpqr

import clpCore.chocoModel
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.utils.createChocoSolver
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Satisfy: UnaryPredicate<ExecutionContext>("satisfy") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        ensuringArgumentIsList(0)
        require(first.castToList().toList().all { it is Var }){
            "First argument does not contain only variables"
        }
        val logicVars = first.variables.toList()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars)
        val config = Configuration()
        val solver = createChocoSolver(chocoModel, config, varsMap)
        return solver.solutions(varsMap).map { replyWith(it) }
    }

}