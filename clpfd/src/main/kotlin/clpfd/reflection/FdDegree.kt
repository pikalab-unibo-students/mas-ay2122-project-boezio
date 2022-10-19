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

object FdDegree: BinaryRelation.NonBacktrackable<ExecutionContext>("fd_degree") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(0)
        val variable = first.castToVar()
        if(!second.let { it is Var || it is Integer })
            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, second)
        val chocoModel = chocoModel
        val subContext = context.substitution
        val varsMap = chocoModel.variablesMap(listOf(variable), subContext).flip()
        val firstOriginal = first.castToVar().getOuterVariable(subContext)
        // the variable has not been previously defined, or it is not an integer variable
        if(varsMap.let { it.isEmpty() || it[firstOriginal] !is IntVar }){
            return if(second is Var)
                replyWith(Substitution.of(second.getOuterVariable(subContext) to Integer.of(0)))
            else if(second.castToInteger().value.toInt() == 0)
                replySuccess()
            else
                replyFail()
        }
        else{
            chocoModel.solver.propagate()
            val numConstraints = (varsMap[firstOriginal] as IntVar).nbProps
            return if(second is Var)
                replyWith(Substitution.of(second.getOuterVariable(subContext) to Integer.of(numConstraints)))
            else {
                if(second.castToInteger().value.toInt() == numConstraints)
                    replySuccess()
                else
                    replyFail()
            }
        }
    }
}