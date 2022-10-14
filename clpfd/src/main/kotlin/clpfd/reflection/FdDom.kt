package clpfd.reflection

import clpCore.chocoModel
import clpCore.flip
import clpCore.getOuterVariable
import clpCore.variablesMap
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object FdDom : BinaryRelation.NonBacktrackable<ExecutionContext>("fd_dom") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        val domFunctor = ".."
        ensuringArgumentIsVariable(0)
        val variable = first.castToVar()
        require(second.let { it is Var || (it is Struct && it.functor == domFunctor && it.arity == 2 ) }){
            "$second is neither a variable nor a domain interval"
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
            when(second){
                is Var -> replyWith(Substitution.of(second.getOuterVariable(subContext) to domainStruct))
                is Struct -> {
                    val struct = second.castToStruct()
                    val queryDom = Struct.of(domFunctor, struct[0].castToInteger(), struct[1].castToInteger())
                    if( queryDom == domainStruct)
                        replySuccess()
                    else
                        replyFail()
                }
                else -> throw IllegalStateException()
            }
        }
    }
}