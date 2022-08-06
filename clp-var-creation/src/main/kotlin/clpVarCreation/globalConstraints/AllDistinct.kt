package clpVarCreation.globalConstraints

import clpVarCreation.chocoModel
import clpVarCreation.setChocoModel
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar
import it.unibo.tuprolog.core.Integer as LogicInt

object AllDistinct : UnaryPredicate.NonBacktrackable<ExecutionContext>("all_distinct") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listTerm = first.castToList().toList()
        val logicVars = listTerm.filterIsInstance<Var>().distinct().toList()
        val chocoModel = chocoModel
        val integerAsVars = mutableSetOf<IntVar>()
        // Conversion of integers to int values
        for (elem in listTerm){
            if(elem is LogicInt){
                val intValue = elem.castToInteger().intValue.toInt()
                integerAsVars.add(chocoModel.intVar(intValue))
            }
        }
        val chocoVars = chocoModel.variablesMap(logicVars).keys.map { it as IntVar }.toSet()
        val allVars = chocoVars.union(integerAsVars).toList().toTypedArray()
        chocoModel.allDifferent(*allVars).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}