package clpVarCreation

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsCompound
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsVariable
import it.unibo.tuprolog.solve.primitive.Solve

object Ins : BinaryRelation.NonBacktrackable<ExecutionContext>("ins") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val varNames = first.castToList().toList().map { it.asVar()?.completeName }
        ensuringArgumentIsCompound(1)
        val domainStruct = second.castToStruct()
        require(domainStruct.let { it.arity == 2 && it.functor == ".." && it[0] is Integer && it[1] is Integer }) {
            "Argument 2 should be a compound term of the type '..'(int, int)"
        }
        val lb = domainStruct[0].castToInteger().intValue.toInt()
        val ub = domainStruct[1].castToInteger().intValue.toInt()
        if (lb > ub) {
            return replyFail()
        }
        val chocoModel = chocoModel
        // checking whether the model has already been created
        for (name in varNames) {
            require(chocoModel.vars.none { it.name == name }) {
                "Variable $name already defined"
            }
        }

        for (name in varNames) {
            chocoModel.intVar(name, lb, ub)
        }
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}