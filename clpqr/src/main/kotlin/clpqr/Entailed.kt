package clpqr

import clpCore.chocoModel
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Entailed: UnaryPredicate.NonBacktrackable<ExecutionContext>("entailed") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        if (first !is Tuple) {
            throw TypeError.forArgument(context, signature, TypeError.Expected.PAIR, first, 0)
        }
        val chocoModel = chocoModel
        // Checking constraints
        val entailed = first.accept(ConstraintChecker(chocoModel))
        return if(entailed){
            replySuccess()
        }else{
            replyFail()
        }
    }
}