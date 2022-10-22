package clpqr.utils

import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.chocosolver.solver.Model
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression
import org.chocosolver.solver.expression.continuous.relational.CReExpression

internal class ConstraintImposer(
    private val chocoModel: Model,
    private val context: ExecutionContext,
    private val signature: Signature
) : DefaultTermVisitor<Unit>() {
    override fun defaultValue(term: Term) =
        throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REFERENCE, term)

    override fun visitTuple(term: Tuple) {
        term.unfoldedSequence.forEach { it.accept(this) }
    }

    override fun visitStruct(term: Struct) {
        if(term.arity != 2)
            throw DomainError.forArgument(
                context, signature, DomainError.Expected.PREDICATE_PROPERTY, term
            )
        val struct = term.castToStruct()
        val firstTerm = struct.args[0]
        val secondTerm = struct.args[1]
        when(struct.functor){
            "<" -> applyRelOperator(firstTerm, secondTerm, CArExpression::lt, chocoModel)
            ">" -> applyRelOperator(firstTerm, secondTerm, CArExpression::gt, chocoModel)
            "=<", "<=" -> applyRelOperator(firstTerm, secondTerm, CArExpression::le, chocoModel)
            ">=" -> applyRelOperator(firstTerm, secondTerm, CArExpression::ge, chocoModel)
            "=:=", "="-> applyRelOperator(firstTerm, secondTerm, CArExpression::eq, chocoModel)
            else -> throw DomainError.forArgument(
                context, signature, DomainError.Expected.PREDICATE_PROPERTY, term
            )
        }
    }

    private fun applyRelOperator(
        firstTerm: Term,
        secondTerm: Term,
        operation: (CArExpression, CArExpression) -> CReExpression,
        model: Model
    ){
        val logicalVars = (firstTerm.variables + secondTerm.variables).toSet()
        val varMap = model.variablesMap(logicalVars, context.substitution).flip()
        val parser = ExpressionParser(model, varMap, context.substitution, context, signature)
        val firstExpression = firstTerm.accept(parser)
        val secondExpression = secondTerm.accept(parser)
        operation(firstExpression, secondExpression).equation().post()
    }

}