package clpfd.global

import clpCore.*
import clpfd.getAsIntVar
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Integer as LogicInteger
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object GlobalCardinalityTwo : BinaryRelation.NonBacktrackable<ExecutionContext>("global_cardinality") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listTerms = first.castToList().toList()
        for (elem in listTerms) {
            if(!(elem.let { it is Var || it is LogicInteger })) {
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            }
        }
        val firstVars = listTerms.filterIsInstance<Var>().toSet()
        ensuringArgumentIsList(1)
        val pairs = second.castToList().toList()
        val chocoModel = chocoModel
        val occVars = second.variables.toSet()
        val logicVars = firstVars.union(occVars)
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        val values = mutableListOf<Int>()
        val occurrences = mutableListOf<IntVar>()
        for (pair in pairs) {
            if(!(pair.let { it is Struct && it.arity == 2 && it.functor == "-" })) {
                throw DomainError.forArgument(context, signature, DomainError.Expected.PREDICATE_PROPERTY, pair)
            }
            val arguments = pair.castToStruct().args
            val value = arguments[0]

            if(value is Var) {
                throw ExistenceError.of(context,ExistenceError.ObjectType.RESOURCE,value,"Value as variable has not been supported yet")
            } else if(value !is LogicInteger)
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, value)

            values.add(value.castToInteger().value.toInt())
            val occ = arguments[1]
            if(!(occ.let { it is Var || it is LogicInteger })) {
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, occ)
            }
            occurrences.add(getAsIntVar(occ,varsMap, context.substitution))
        }
        val chocoVars = listTerms.map { getAsIntVar(it, varsMap, context.substitution) }.toTypedArray()
        val chocoValues = values.toIntArray()
        val chocoOccurrences = occurrences.toTypedArray()
        chocoModel.globalCardinality(chocoVars, chocoValues, chocoOccurrences, true).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}