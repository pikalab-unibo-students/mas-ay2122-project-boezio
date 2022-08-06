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
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.core.Integer as LogicInteger
import org.chocosolver.solver.variables.IntVar

object Element : TernaryRelation.NonBacktrackable<ExecutionContext>("element") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        val logicVars = first.variables.toSet().union(second.variables.toSet()).union(third.variables.toSet())
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        require(first.let { it is Var || it is LogicInteger }){
            "$first is neither a variable nor an integer"
        }
        val index = getAsIntVar(first, varsMap)
        ensuringArgumentIsList(1)
        val intVarTable = mutableListOf<IntVar>()
        val table = second.castToList().toList()
        for (elem in table) {
            require(elem is LogicInteger) {
                "$elem is not an integer"
            }
        }
        val integerList = table.filterIsInstance<LogicInteger>().map { it.value.toInt() }.toIntArray()

        require(third.let { it is Var || it is LogicInteger }){
            "$first is neither a variable nor an integer"
        }
        val value = getAsIntVar(third, varsMap)
        chocoModel.element(value, integerList, index).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}