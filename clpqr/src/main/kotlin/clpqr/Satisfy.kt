package clpqr

import clpCore.chocoModel
import clpCore.setChocoModel
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.primitive.ZeroaryPredicate

object Satisfy: UnaryPredicate.NonBacktrackable<ExecutionContext>("satisfy") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {

        ensuringArgumentIsList(0)
        require(first.castToList().args.all { it is Var }){
            "Some elements are not variables"
        }
        val logicVars = first.variables.toList()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars)
        val config = Configuration()
        return solve(chocoModel, config, varsMap)
    }

}