package clpfd.global

import clpCore.*
import clpfd.getAsIntVar
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.core.Integer as LogicInteger

object Element : TernaryRelation.NonBacktrackable<ExecutionContext>("element") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        val logicVars = first.variables.toSet().union(second.variables.toSet()).union(third.variables.toSet())
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        if(!(first.let { it is Var || it is LogicInteger })){
            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, first)
        }
        val index = getAsIntVar(first, varsMap, context.substitution)
        ensuringArgumentIsList(1)
        val table = second.castToList().toList()
        for (elem in table) {
            if(elem is Var)
                throw ExistenceError.of(context, ExistenceError.ObjectType.RESOURCE, elem, "Vs accepts only integer values")
            else if(elem !is LogicInteger) {
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            }
        }
        val integerList = table.filterIsInstance<LogicInteger>().map { it.value.toInt() }.toIntArray()

        if(!(third.let { it is Var || it is LogicInteger })){
            throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, third)
        }
        val value = getAsIntVar(third, varsMap, context.substitution)
        chocoModel.element(value, integerList, index).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}