package clpVarCreation

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object In : BinaryRelation.NonBacktrackable<ExecutionContext>("in") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(0)
        val varName = first.castToVar().completeName
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
        require(chocoModel.vars.none { it.name == varName }) {
            "Variable $varName already defined"
        }
        chocoModel.intVar(varName, lb, ub)
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}