package clpfd.reflection

import clpCore.chocoModel
import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.variables.IntVar

object FdVar: UnaryPredicate.NonBacktrackable<ExecutionContext>("fd_var") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVariable(0)
        val variable = first.castToVar()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(listOf(variable), context.substitution).flip()
        return if(varsMap.isEmpty())
            replyFail()
        else{
            val chocoVar = varsMap[variable]
            if(chocoVar is IntVar)
                replySuccess()
            else
                replyFail()
        }
    }
}