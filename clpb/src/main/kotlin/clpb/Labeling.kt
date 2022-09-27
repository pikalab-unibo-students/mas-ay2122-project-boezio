package clpb

import clpCore.*
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Labeling: UnaryPredicate<ExecutionContext>("labeling") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        ensuringArgumentIsList(0)
        require(first.castToList().toList().all { it is Var }){
            "First argument does not contain only variables"
        }
        val chocoModel = chocoModel
        val vars = first.variables.distinct().toList()
        val varsMap = chocoModel.variablesMap(vars)
        val solver = chocoModel.solver
        return solver.solutions(varsMap).map { replyWith(it) }
    }
}