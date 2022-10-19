package clpfd.reflection

import clpCore.chocoModel
import clpCore.flip
import clpCore.getOuterVariable
import clpCore.variablesMap
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object FdSize: BinaryRelation.NonBacktrackable<ExecutionContext>("fd_size") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(0)
        val variable = first.castToVar()
        if(!second.let { it is Var || it is Integer })
            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, second)
        val chocoModel = chocoModel
        val subContext = context.substitution
        val varsMap = chocoModel.variablesMap(listOf(variable), subContext).flip()
        val firstOriginal = first.castToVar().getOuterVariable(subContext)
        return if(varsMap.let { it.isEmpty() || it[firstOriginal] !is IntVar})
            replyFail()
        else{
            chocoModel.solver.propagate()
            val size = (varsMap[firstOriginal] as IntVar).domainSize
            if(second is Var)
                return replyWith(Substitution.of(second.getOuterVariable(subContext) to Integer.of(size)))
            else if(second.castToInteger().value.toInt() == size)
                    replySuccess()
                else
                    replyFail()
        }
    }
}