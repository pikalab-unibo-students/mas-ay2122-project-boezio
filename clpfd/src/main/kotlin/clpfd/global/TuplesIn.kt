package clpfd.global

import clpCore.*
import clpfd.getAsIntVar
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.constraints.extension.Tuples
import it.unibo.tuprolog.core.List as LogicList
import org.chocosolver.solver.variables.IntVar
import it.unibo.tuprolog.core.Integer as LogicInteger

object TuplesIn : BinaryRelation.NonBacktrackable<ExecutionContext>("tuples") {
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
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        val tuple = mutableListOf<IntVar>()
        for(elem in innerList){
            require(elem.let { it is Var || it is Integer }){
                "$elem is neither a variable nor an integer"
            }
            tuple.add(getAsIntVar(elem, varsMap, context.substitution))
        }
        val relation = Tuples(true)
        val tuples = second.castToList().toList()
        val numElemList = innerList.size
        for (elem in tuples) {
            val integerElems = elem.castToList().toList().filterIsInstance<LogicInteger>()
            require(elem.let { it is LogicList && integerElems.size == numElemList }) {
                "$elem is invalid"
            }
            val elemRelation = integerElems.map { it.value.toInt() }.toIntArray()
            relation.add(elemRelation)
        }
        chocoModel.table(tuple.toTypedArray(), relation, "CT+").post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}