package clpVarCreation.globalConstraints

import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.core.List as LogicList
import org.chocosolver.solver.variables.IntVar

object LexChain : UnaryPredicate.NonBacktrackable<ExecutionContext>("lex_chain") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val outerList = first.castToList().toList()
        for (elem in outerList) {
            require(elem is LogicList) {
                "$elem is not a list"
            }
        }
        require(outerList.size == 2) {
            "List has an invalid length"
        }
        val firstListVars = outerList[0].castToList().toList().filterIsInstance<Var>().distinct().toSet()
        val secondListVars = outerList[1].castToList().toList().filterIsInstance<Var>().distinct().toSet()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(firstListVars.union(secondListVars)).flip()
        val firstList = firstListVars.map{ varsMap[it] }.map{ it as IntVar }.toTypedArray()
        val secondList = secondListVars.map{ varsMap[it] }.map { it as IntVar }.toTypedArray()
        return replySuccess {
            chocoModel.lexLessEq(firstList, secondList)
        }
    }
}