package clpVarCreation.domain

import clpVarCreation.chocoModel
import clpVarCreation.setChocoModel
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object Ins : BinaryRelation.NonBacktrackable<ExecutionContext>("ins") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val varNames = first.castToList().toList().mapIndexed { index, it ->
            (it as? Var)?.completeName ?:
                throw TypeError.forArgument(context, signature, TypeError.Expected.VARIABLE, first, index)
        }
        ensuringArgumentIsCompound(1)
        val domainStruct = second.castToStruct()
        require(domainStruct.let { it.arity == 2 && it.functor == ".." && it[0] is Integer && it[1] is Integer }) {
            "Argument 2 should be a compound term of the type '..'(int, int)"
        }
        val lb = (domainStruct[0] as Integer).intValue.toInt()
        val ub = (domainStruct[1] as Integer).intValue.toInt()
        if (lb > ub) {
            return replyFail()
        }
        val chocoModel = chocoModel
        for (name in varNames) {
            require(chocoModel.vars.none { it.name == name }) {
                "Variable $name already defined"
            }
            chocoModel.intVar(name, lb, ub)
        }
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}