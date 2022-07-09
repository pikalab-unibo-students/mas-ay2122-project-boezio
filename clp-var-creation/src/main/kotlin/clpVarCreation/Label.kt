package clpVarCreation

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.Variable

object Label: UnaryPredicate.NonBacktrackable<ExecutionContext>("label") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val logicVariables: List<Var> = (first as it.unibo.tuprolog.core.List).toList().filterIsInstance<Var>().distinct().toList()
        val chocoModel = chocoModel
        val configuration = LabelingConfiguration()
        val chocoToLogic: Map<Variable, Var> = chocoModel.variablesMap(logicVariables)
        val solver = createChocoSolver(chocoModel, configuration, chocoToLogic)
        return if (solver.solve()) {
            replyWith(
                Substitution.of(chocoToLogic.map { (k, v) -> v to k.valueAsTerm })
            )
        } else {
            replyFail()
        }
    }
}