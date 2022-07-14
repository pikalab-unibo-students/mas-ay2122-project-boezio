package clpVarCreation

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression

object GreaterThan : BinaryClpOperator("#>") {
    override val operation: (ArExpression, ArExpression) -> ReExpression = ArExpression::gt
}