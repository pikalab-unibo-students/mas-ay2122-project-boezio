package clpVarCreation

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar

object AllDistinct : UnaryPredicate.NonBacktrackable<ExecutionContext>("all_distinct") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val logicVars = first.castToList().toList().filterIsInstance<Var>().distinct().toList()
        val chocoModel = chocoModel
        val vars = chocoModel.variablesMap(logicVars).keys.map { it as IntVar }.toTypedArray()
        return replySuccess {
            chocoModel.allDifferent(*vars).post()
        }
    }
}