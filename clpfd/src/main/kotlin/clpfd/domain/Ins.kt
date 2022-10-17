package clpfd.domain

import clpCore.chocoModel
import clpCore.getOuterVariables
import clpCore.setChocoModel
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.Integer as LogicInt

object Ins : BinaryRelation.NonBacktrackable<ExecutionContext>("ins") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val varNames = first.castToList().toSequence().mapIndexed { index, it ->
            it as? Var ?: throw TypeError.forArgument(context, signature, TypeError.Expected.VARIABLE, first, index)
        }.getOuterVariables(context.substitution).map { it.completeName }
        val chocoModel = chocoModel
        when(second) {
            is Struct -> {
                val domainStruct = second.castToStruct()
                if(!(domainStruct.let { it.arity == 2 && it.functor == ".." && it[0] is Integer && it[1] is Integer })) {
                    throw DomainError.forArgument(context,signature,DomainError.Expected.PREDICATE_PROPERTY,domainStruct)
                }
                val lb = (domainStruct[0] as Integer).intValue.toInt()
                val ub = (domainStruct[1] as Integer).intValue.toInt()
                if (lb > ub) {
                    throw DomainError.forArgument(context,signature,DomainError.Expected.ATOM_PROPERTY, Atom.of(lb.toString()))
                }
                for (name in varNames) {
                    chocoModel.intVar(name, lb, ub)
                }
            }
            is LogicInt -> {
                val domainInt = second.castToInteger().intValue.toInt()
                for (name in varNames) {
                    chocoModel.intVar(name, domainInt)
                }
            }
            else -> throw TypeError.forArgument(context, signature, TypeError.Expected.COMPOUND, second)
        }

        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}