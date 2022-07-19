package clpVarCreation.globalConstraints

import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.core.Integer as LogicInteger
import org.chocosolver.solver.variables.IntVar

object Element : TernaryRelation.NonBacktrackable<ExecutionContext>("element") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsVariable(0)
        val value = first.castToVar()
        ensuringArgumentIsList(1)
        val table = second.castToList().toList()
        for (elem in table) {
            require(elem is LogicInteger) {
                "$elem is not an integer"
            }
        }
        val integerList = table.filterIsInstance<LogicInteger>().map { it.value.toInt() }.toIntArray()
        ensuringArgumentIsVariable(2)
        val index = third.castToVar()
        val logicVars = setOf(value, index)
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        val chocoValue = varsMap[value] as IntVar
        val chocoIndex = varsMap[index] as IntVar
        return replySuccess {
            chocoModel.element(chocoValue, integerList, chocoIndex).post()
        }
    }
}