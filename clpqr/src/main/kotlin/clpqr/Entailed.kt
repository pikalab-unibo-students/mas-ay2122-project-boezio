package clpqr

import clpCore.chocoModel
import clpqr.utils.ConstraintChecker
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Entailed: UnaryPredicate.NonBacktrackable<ExecutionContext>("entailed") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        if (first.let { it is Atom || it is Integer }) {
            throw TypeError.forArgument(context, signature, TypeError.Expected.COMPOUND, first, 0)
        }
        val chocoModel = chocoModel
        // solve() is called to allow the correct cal of isSatisfied
        chocoModel.solver.solve()
        // check of constraints
        val entailed = first.accept(ConstraintChecker(chocoModel, context.substitution, context, signature))
        return if(entailed){
            replySuccess()
        }else{
            replyFail()
        }
    }
}