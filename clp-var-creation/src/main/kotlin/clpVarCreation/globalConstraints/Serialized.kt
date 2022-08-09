package clpVarCreation.globalConstraints

import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.setChocoModel
import clpVarCreation.variablesMap
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
        val size = firstList.size
        val firstVars = firstList.filterIsInstance<Var>()
        val secondVars = secondList.filterIsInstance<Var>()
        require(firstVars.distinct().size + secondVars.size == firstList.size * 2) {
            "There are some elements of the lists which are not variables"
        }
        val logicVars = firstVars.toSet().union(secondVars.toSet())
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        val chocoStarts = firstVars.map { varsMap[it] as IntVar }.toTypedArray()
        val chocoDurations = secondVars.map { varsMap[it] as IntVar }.toTypedArray()
        val zeros = chocoModel.intVarArray(size, List(size){0}.toIntArray())
        val ones = chocoModel.intVarArray(size, List(size){1}.toIntArray())
        chocoModel.diffN(chocoStarts, zeros, chocoDurations, ones, false).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}