package clpfd.reflection

import clpCore.chocoModel
import clpCore.flip
import clpCore.getOuterVariable
import clpCore.variablesMap
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object FdDom : BinaryRelation.NonBacktrackable<ExecutionContext>("fd_dom") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        val domFunctor = ".."
        ensuringArgumentIsVariable(0)
        val variable = first.castToVar()
        if(!second.let { (it is Struct && it.functor == ".." && it.arity == 2) || it is Var })
            throw TypeError.forArgument(context, signature, TypeError.Expected.COMPOUND, second)
        if(second is Struct){
            val args = second.castToStruct().args
            for(arg in args){
                if(arg !is Integer)
                    throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, arg)
            }
        }
        val chocoModel = chocoModel
        val subContext = context.substitution
        val varsMap = chocoModel.variablesMap(listOf(variable), subContext).flip()
        val firstOriginal = first.castToVar().getOuterVariable(subContext)
        return if(varsMap.let { it.isEmpty() || it[firstOriginal] !is IntVar })
            replyFail()
        else{
            chocoModel.solver.propagate()
            val chocoVar = (varsMap[firstOriginal] as IntVar)
            val lb = chocoVar.lb
            val ub = chocoVar.ub
            val domainStruct = Struct.of(domFunctor, Integer.of(lb), Integer.of(ub))
            if(second is Var)
                return replyWith(Substitution.of(second.getOuterVariable(subContext) to domainStruct))
            else {
                val struct = second.castToStruct()
                val queryDom = Struct.of(domFunctor, struct[0].castToInteger(), struct[1].castToInteger())
                if( queryDom == domainStruct)
                        replySuccess()
                else
                    replyFail()
                }
        }
    }
}