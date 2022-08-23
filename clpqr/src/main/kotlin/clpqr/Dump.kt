package clpqr

import clpCore.chocoModel
import clpCore.solutions
import clpCore.variablesMap
import clpqr.search.Configuration
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation


object Dump: TernaryRelation.NonBacktrackable<ExecutionContext>("dump") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        require(first.castToList().toList().all { it is Var }){
            "First argument does not contain only variables"
        }
        val target = first.variables.distinct().toList()
        ensuringArgumentIsList(1)
        val newVars = second.castToList().toList()
        ensuringArgumentIsVariable(2)
        val codedAnswers = third.castToVar()

        return replySuccess {  }

    }

}