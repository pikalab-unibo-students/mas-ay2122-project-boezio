package clpfd.reification

import clpCore.chocoModel
import clpfd.getReifiedTerms
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp

object InverseImplies: BinaryReificationOperator(" #<==") {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {

        val terms = getReifiedTerms(LogicList.of(first, second))
        val firstReif = terms[0]
        val secondReif = terms[1]
        chocoModel.addClauses(operation(secondReif, firstReif))
        return replySuccess()
    }

    override val operation: (ILogical, ILogical) -> LogOp = LogOp::implies
}