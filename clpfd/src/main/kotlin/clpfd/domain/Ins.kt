package clpfd.domain

import clpCore.chocoModel
import clpCore.setChocoModel
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.Integer as LogicInt

object Ins : BinaryRelation.NonBacktrackable<ExecutionContext>("ins") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val varNames = first.castToList().toList().mapIndexed { index, it ->
            (it as? Var)?.completeName ?:
                throw TypeError.forArgument(context, signature, TypeError.Expected.VARIABLE, first, index)
        }
        val chocoModel = chocoModel
        when(second) {
            is Struct -> {
                val domainStruct = second.castToStruct()
                require(domainStruct.let { it.arity == 2 && it.functor == ".." && it[0] is Integer && it[1] is Integer }) {
                    "Argument 2 should be a compound term of the type '..'(int, int)"
                }
                val lb = (domainStruct[0] as Integer).intValue.toInt()
                val ub = (domainStruct[1] as Integer).intValue.toInt()
                if (lb > ub) {
                    return replyFail()
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
            else -> throw IllegalStateException()
        }

        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}