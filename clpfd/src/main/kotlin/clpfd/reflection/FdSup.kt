package clpfd.reflection

import clpCore.chocoModel
import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object FdSup: BinaryRelation.NonBacktrackable<ExecutionContext>("fd_inf") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(0)
        val variable = first.castToVar()
        ensuringArgumentIsInteger(1)
        val ub = second.castToInteger().value.toInt()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(listOf(variable)).flip()
        return if(varsMap.isEmpty())
            replyFail()
        else{
            val chocoVar = varsMap[variable]
            if(chocoVar.let { it is IntVar && it.ub == ub })
                replySuccess()
            else
                replyFail()
        }
    }
}