package clpfd.reification

import clpCore.chocoModel
import clpfd.getReifiedTerms
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp

abstract class UnaryReificationOperator(operator: String): UnaryPredicate.NonBacktrackable<ExecutionContext>(operator) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {

        val terms = getReifiedTerms(LogicList.of(first))
        val firstReif = terms[0]
        chocoModel.addClauses(operation(arrayOf(firstReif)))
        return replySuccess()
    }

    protected abstract val operation: (Array<ILogical>) -> LogOp

}