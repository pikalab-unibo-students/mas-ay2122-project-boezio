package clpfd.global

import clpCore.chocoModel
import clpCore.setChocoModel
import clpCore.variablesMap
import clpfd.getIntAsVars
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar

object AllDistinctExcept0: UnaryPredicate.NonBacktrackable<ExecutionContext>("all_distinct_except_0") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listTerm = first.castToList().toList()
        for(elem in listTerm){
            if(!(elem.let { it is Var || it is Integer }))
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
        }
        val logicVars = listTerm.filterIsInstance<Var>().distinct().toList()
        val chocoModel = chocoModel
        val integerAsVars = getIntAsVars(listTerm)
        val chocoVars = chocoModel.variablesMap(logicVars, context.substitution).keys.map { it as IntVar }.toMutableList()
        chocoVars.addAll(integerAsVars)
        val allVars = chocoVars.toList().toTypedArray()
        chocoModel.allDifferentExcept0(allVars).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}