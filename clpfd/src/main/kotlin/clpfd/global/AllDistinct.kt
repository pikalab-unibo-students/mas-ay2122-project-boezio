package clpfd.global

import clpCore.chocoModel
import clpCore.setChocoModel
import clpCore.variablesMap
import clpfd.getIntAsVars
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar

object AllDistinct : UnaryPredicate.NonBacktrackable<ExecutionContext>("all_distinct") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listTerm = first.castToList().toList()
        val logicVars = listTerm.filterIsInstance<Var>().distinct().toList()
        val chocoModel = chocoModel
        val integerAsVars = getIntAsVars(listTerm)
        val chocoVars = chocoModel.variablesMap(logicVars, context.substitution).keys.map { it as IntVar }.toMutableList()
        chocoVars.addAll(integerAsVars)
        val allVars = chocoVars.toList().toTypedArray()
        chocoModel.allDifferent(*allVars).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}