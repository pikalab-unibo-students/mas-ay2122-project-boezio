package clpqr

import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.Model
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression
import org.chocosolver.solver.expression.continuous.relational.CReExpression
import java.lang.IllegalStateException


internal fun applyRelOperator(
    firstTerm: Term,
    secondTerm: Term,
    operation: (CArExpression, CArExpression) -> CReExpression,
    model: Model
){
    val logicalVars = (firstTerm.variables + secondTerm.variables).toSet()
    val varMap = model.variablesMap(logicalVars).flip()
    val parser = ExpressionParser(model, varMap)
    val firstExpression = firstTerm.accept(parser)
    val secondExpression = secondTerm.accept(parser)
    operation(firstExpression, secondExpression).equation().post()
}

internal fun Solve.Request<ExecutionContext>.imposeConstraint(term: Term, model: Model){
    require(term.let { it is Struct && it.arity == 2 }){
        "Invalid constraint structure"
    }
    val struct = term.castToStruct()
    val firstTerm = struct.args[0]
    val secondTerm = struct.args[1]
    when(struct.functor){
        "," -> {
            imposeConstraint(firstTerm, model)
            imposeConstraint(secondTerm, model)
        }
        "<" -> applyRelOperator(firstTerm, secondTerm, CArExpression::lt, model)
        ">" -> applyRelOperator(firstTerm, secondTerm, CArExpression::gt, model)
        "=<" -> applyRelOperator(firstTerm, secondTerm, CArExpression::le, model)
        "<=" -> applyRelOperator(firstTerm, secondTerm, CArExpression::le, model)
        ">=" -> applyRelOperator(firstTerm, secondTerm, CArExpression::ge, model)
        "=\\=" -> throw IllegalStateException()
        "=:=" -> applyRelOperator(firstTerm, secondTerm, CArExpression::eq, model)
        "=" -> applyRelOperator(firstTerm, secondTerm, CArExpression::eq, model)
        else -> throw IllegalStateException()
    }
}

