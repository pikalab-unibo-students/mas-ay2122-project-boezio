package clpfd.relational

import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression

object Equals : BinaryClpOperator("#=") {
    override val operation: (ArExpression, ArExpression) -> ReExpression = ArExpression::eq
}
