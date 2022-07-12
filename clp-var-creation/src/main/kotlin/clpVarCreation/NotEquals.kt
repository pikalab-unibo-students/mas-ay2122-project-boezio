package clpVarCreation

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression

object NotEquals: BinaryRelation.NonBacktrackable<ExecutionContext>("#\\=") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        applyRelConstraint(first, second, ArExpression::ne)
        return replySuccess()
    }
}