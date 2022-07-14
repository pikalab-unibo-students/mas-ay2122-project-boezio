package clpVarCreation

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.Variable
import it.unibo.tuprolog.core.List as LogicList

object Labeling : BinaryRelation.NonBacktrackable<ExecutionContext>("labeling") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val keys: Set<Struct> = (first as LogicList).toList().filterIsInstance<Struct>().toSet()
        ensuringArgumentIsList(1)
        val logicVariables: List<Var> = (second as LogicList).toList().filterIsInstance<Var>().distinct().toList()
        val chocoModel = chocoModel
        val configuration = LabelingConfiguration.fromTerms(keys)
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