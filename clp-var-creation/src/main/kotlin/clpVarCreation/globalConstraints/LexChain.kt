package clpVarCreation.globalConstraints

import clpVarCreation.*
import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.setChocoModel
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.core.List as LogicList
import org.chocosolver.solver.variables.IntVar

object LexChain : UnaryPredicate.NonBacktrackable<ExecutionContext>("lex") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val outerList = first.castToList().toList()
        require(outerList.size == 2) {
            "List has an invalid length"
        }
        for (elem in outerList) {
            require(elem is LogicList) {
                "$elem is not a list"
            }
        }
        val firstTermList = outerList[0].castToList().toList()
        val firstSize = firstTermList.size
        val secondTermList = outerList[1].castToList().toList()
        val secondSize = secondTermList.size
        val firstListVars = firstTermList.filterIsInstance<Var>().distinct().toSet()
        val secondListVars = secondTermList.filterIsInstance<Var>().distinct().toSet()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(firstListVars.union(secondListVars)).flip()
        val firstList = mutableListOf<IntVar>()
        val secondList = mutableListOf<IntVar>()
        for(i in 0 until firstSize){
            val listElem = getAsIntVar(firstTermList[i], varsMap)
            firstList.add(listElem)
        }
        for(i in 0 until secondSize){
            val listElem = getAsIntVar(secondTermList[i], varsMap)
            secondList.add(listElem)
        }
        val first = firstList.toTypedArray()
        val second = secondList.toTypedArray()
        chocoModel.lexLessEq(first, second).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}