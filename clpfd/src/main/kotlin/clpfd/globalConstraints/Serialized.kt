package clpfd.globalConstraints

import clpCore.*
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object Serialized : BinaryRelation.NonBacktrackable<ExecutionContext>("serialized") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {

        ensuringArgumentIsList(0)
        ensuringArgumentIsList(1)
        val firstList = first.castToList().toList()
        val secondList = second.castToList().toList()
        require(firstList.size == secondList.size) {
            "Lists have different length"
        }
        val sizeFirst = firstList.size
        val sizeSecond = secondList.size
        val firstVars = firstList.filterIsInstance<Var>()
        val secondVars = secondList.filterIsInstance<Var>()
        require(sizeFirst == sizeSecond) {
            "Lists have different length"
        }
        val logicVars = firstVars.toSet().union(secondVars.toSet())
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        val chocoStarts = mutableListOf<IntVar>()
        val chocoDurations = mutableListOf<IntVar>()
        for(elem in firstList){
            require(elem.let { it is Var || it is Integer }){
                "$elem is neither a variable nor an integer"
            }
            chocoStarts.add(getAsIntVar(elem, varsMap))
        }
        for(elem in secondList){
            require(elem.let { it is Var || it is Integer }){
                "$elem is neither a variable nor an integer"
            }
            chocoDurations.add(getAsIntVar(elem, varsMap))
        }
        val zeros = chocoModel.intVarArray(sizeFirst, List(sizeFirst){0}.toIntArray())
        val ones = chocoModel.intVarArray(sizeFirst, List(sizeFirst){1}.toIntArray())
        chocoModel.diffN(chocoStarts.toTypedArray(), zeros, chocoDurations.toTypedArray(), ones, false).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}