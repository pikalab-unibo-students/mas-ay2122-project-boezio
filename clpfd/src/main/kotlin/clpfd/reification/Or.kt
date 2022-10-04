package clpfd.reification

import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp

object Or: NaryReificationOperator(" #\\/") {
    override val operation: (Array<ILogical>) -> LogOp = LogOp::or
}