package clpfd.reflection

import clpCore.chocoModel
import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object FdSize: BinaryRelation.NonBacktrackable<ExecutionContext>("fd_size") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(0)
        val variable = first.castToVar()
        require(second.let { it is Var || it is Integer }){
            "$second is neither a variable nor an integer value"
        }
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(listOf(variable)).flip()
        return if(varsMap.let { it.isEmpty() || it[first] !is IntVar})
            replyFail()
        else{
            chocoModel.solver.propagate()
            val size = (varsMap[first] as IntVar).domainSize
            when(second){
                is Var -> replyWith(Substitution.of(second to Integer.of(size)))
                is Integer -> if(second.value.toInt() == size)
                    replySuccess()
                else
                    replyFail()
                else -> throw IllegalStateException()
            }
        }
    }
}