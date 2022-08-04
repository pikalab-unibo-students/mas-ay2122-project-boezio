package clpVarCreation.globalConstraints


import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation


object GlobalCardinalityThree: TernaryRelation.NonBacktrackable<ExecutionContext>("global_cardinality") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        throw IllegalStateException()
    }
}