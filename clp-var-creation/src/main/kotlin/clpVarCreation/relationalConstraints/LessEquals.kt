package clpVarCreation.relationalConstraints

import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression

object LessEquals : BinaryClpOperator("#=<") {
    override val operation: (ArExpression, ArExpression) -> ReExpression = ArExpression::le
}