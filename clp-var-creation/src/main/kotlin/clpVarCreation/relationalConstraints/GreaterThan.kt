package clpVarCreation.relationalConstraints

import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression

object GreaterThan : BinaryClpOperator("#>") {
    override val operation: (ArExpression, ArExpression) -> ReExpression = ArExpression::gt
}