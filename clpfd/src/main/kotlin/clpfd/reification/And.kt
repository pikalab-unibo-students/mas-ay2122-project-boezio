package clpfd.reification

import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp

object And: BinaryReificationOperator("#/\\") {
    override val operation: (Array<ILogical>) -> LogOp = LogOp::and
}