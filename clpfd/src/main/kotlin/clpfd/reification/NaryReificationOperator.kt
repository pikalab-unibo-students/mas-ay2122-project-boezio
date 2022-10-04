package clpfd.reification

import clpCore.chocoModel
import clpfd.getReifiedTerms
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp

abstract class NaryReificationOperator(operator: String): BinaryRelation.NonBacktrackable<ExecutionContext>(operator) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {

        val terms = getReifiedTerms(LogicList.of(first, second))
        val firstReif = terms[0]
        val secondReif = terms[1]
        chocoModel.addClauses(operation(arrayOf(firstReif, secondReif)))
        return replySuccess()
    }

    protected abstract val operation: (Array<ILogical>) -> LogOp

}