package clpVarCreation.globalConstraints

import clpVarCreation.*
import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.setChocoModel
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.constraints.extension.Tuples
import it.unibo.tuprolog.core.List as LogicList
import org.chocosolver.solver.variables.IntVar
import it.unibo.tuprolog.core.Integer as LogicInteger

object TuplesIn : BinaryRelation.NonBacktrackable<ExecutionContext>("tuples_in") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        ensuringArgumentIsList(1)
        val listVar = first.castToList().toList()
        require(listVar.let { it.size == 1 && it[0] is LogicList }) {
            "first argument is invalid"
        }
        val innerList = listVar[0].castToList().toList()
        val logicVars = innerList.filterIsInstance<Var>().distinct()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        val tuple = mutableListOf<IntVar>()
        for(elem in innerList){
            tuple.add(getAsIntVar(elem, varsMap))
        }
        val relation = Tuples(true)
        val tuples = second.castToList().toList()
        val numElemList = innerList.size
        for (elem in tuples) {
            require(elem.let { it is LogicList && it.castToList().toList().size == numElemList }) {
                "$elem is invalid"
            }
            val elemRelation = elem.castToList().toList().filterIsInstance<LogicInteger>().map { it.value.toInt() }.toIntArray()
            relation.add(elemRelation)
        }
        chocoModel.table(tuple.toTypedArray(), relation, "CT+").post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}