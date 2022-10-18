package clpfd.global

import clpCore.*
import clpfd.getAsIntVar
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.core.List as LogicList
import org.chocosolver.solver.variables.IntVar

object LexChain : UnaryPredicate.NonBacktrackable<ExecutionContext>("lex") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val outerList = first.castToList().toList()
        for (elem in outerList) {
            if(elem !is LogicList)
                throw TypeError.forArgument(context, signature, TypeError.Expected.LIST, elem)
        }
        val firstTermList = outerList[0].castToList().toList()
        val firstSize = firstTermList.size
        val secondTermList = outerList[1].castToList().toList()
        val secondSize = secondTermList.size
        val firstListVars = firstTermList.filterIsInstance<Var>().distinct().toSet()
        val secondListVars = secondTermList.filterIsInstance<Var>().distinct().toSet()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(firstListVars.union(secondListVars), context.substitution).flip()
        val firstList = mutableListOf<IntVar>()
        val secondList = mutableListOf<IntVar>()
        for(i in 0 until firstSize){
            if(!(firstTermList[i].let { it is Var || it is Integer }))
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, outerList[0].castToList(), i)
            val listElem = getAsIntVar(firstTermList[i], varsMap, context.substitution)
            firstList.add(listElem)
        }
        for(i in 0 until secondSize){
            if(!(secondTermList[i].let { it is Var || it is Integer }))
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, outerList[1].castToList(), i)

            val listElem = getAsIntVar(secondTermList[i], varsMap, context.substitution)
            secondList.add(listElem)
        }
        val firstLexList = firstList.toTypedArray()
        val secondLexList = secondList.toTypedArray()
        chocoModel.lexLessEq(firstLexList, secondLexList).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}