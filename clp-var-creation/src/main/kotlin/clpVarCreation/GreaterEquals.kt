package clpVarCreation

import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression

object GreaterEquals : BinaryClpOperator("#>=") {
    override val operation: (ArExpression, ArExpression) -> ReExpression = ArExpression::ge
}