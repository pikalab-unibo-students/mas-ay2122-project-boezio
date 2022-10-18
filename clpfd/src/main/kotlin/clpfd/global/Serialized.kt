package clpfd.global

import clpCore.*
import clpfd.getAsIntVar
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object Serialized : BinaryRelation.NonBacktrackable<ExecutionContext>("serialized") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {

        ensuringArgumentIsList(0)
        ensuringArgumentIsList(1)
        val firstList = first.castToList().toList()
        val secondList = second.castToList().toList()
        if(firstList.size != secondList.size)
            throw DomainError.forArgument(context, signature, DomainError.Expected.PREDICATE_PROPERTY, first)
        val size = firstList.size
        val firstVars = firstList.filterIsInstance<Var>()
        val secondVars = secondList.filterIsInstance<Var>()
        val logicVars = firstVars.toSet().union(secondVars.toSet())
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        val chocoStarts = mutableListOf<IntVar>()
        val chocoDurations = mutableListOf<IntVar>()
        for(elem in firstList){
            if(!(elem.let { it is Var || it is Integer }))
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            chocoStarts.add(getAsIntVar(elem, varsMap, context.substitution))
        }
        for(elem in secondList){
            if(!(elem.let { it is Var || it is Integer })){
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            }
            chocoDurations.add(getAsIntVar(elem, varsMap, context.substitution))
        }
        val zeros = chocoModel.intVarArray(size, List(size){0}.toIntArray())
        val ones = chocoModel.intVarArray(size, List(size){1}.toIntArray())
        chocoModel.diffN(chocoStarts.toTypedArray(), zeros, chocoDurations.toTypedArray(), ones, false).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}