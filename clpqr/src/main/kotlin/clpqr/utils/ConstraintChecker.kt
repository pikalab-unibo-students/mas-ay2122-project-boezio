package clpqr.utils

import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.chocosolver.solver.Model
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression
import org.chocosolver.solver.expression.continuous.relational.CReExpression
import org.chocosolver.util.ESat

class ConstraintChecker(
    private val chocoModel: Model,
    private val substitution: Substitution.Unifier,
    private val context: ExecutionContext,
    private val signature: Signature
) : DefaultTermVisitor<Boolean>() {
    override fun defaultValue(term: Term) =
        throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REFERENCE, term)

    override fun visitStruct(term: Struct): Boolean {
        if(term.arity != 2)
            throw DomainError.forArgument(
                context, signature, DomainError.Expected.PREDICATE_PROPERTY, term
            )
        val struct = term.castToStruct()
        val firstTerm = struct.args[0]
        val secondTerm = struct.args[1]
        return when(struct.functor){
            "<" -> checkConstraint(firstTerm, secondTerm, CArExpression::lt, chocoModel)
            ">" -> checkConstraint(firstTerm, secondTerm, CArExpression::gt, chocoModel)
            "=<" -> checkConstraint(firstTerm, secondTerm, CArExpression::le, chocoModel)
            "<=" -> checkConstraint(firstTerm, secondTerm, CArExpression::le, chocoModel)
            ">=" -> checkConstraint(firstTerm, secondTerm, CArExpression::ge, chocoModel)
            "=:=" -> checkConstraint(firstTerm, secondTerm, CArExpression::eq, chocoModel)
            "=" -> checkConstraint(firstTerm, secondTerm, CArExpression::eq, chocoModel)
            else -> throw DomainError.forArgument(
                context, signature, DomainError.Expected.PREDICATE_PROPERTY, term
            )
        }
    }

    private fun checkConstraint(
        firstTerm: Term,
        secondTerm: Term,
        operation: (CArExpression, CArExpression) -> CReExpression,
        model: Model
    ):Boolean{
        val logicalVars = (firstTerm.variables + secondTerm.variables).toList()
        val varMap = model.variablesMap(logicalVars, substitution).flip()
        val parser = ExpressionParser(model, varMap, substitution, context, signature)
        val firstExpression = firstTerm.accept(parser)
        val secondExpression = secondTerm.accept(parser)
        return when(operation(firstExpression, secondExpression).equation().isSatisfied){
            ESat.TRUE -> true
            ESat.FALSE -> false
            else -> throw DomainError.forArgument(
                context, signature, DomainError.Expected.ATOM_PROPERTY, Atom.of("ESat.UNDEFINED")
            )
        }
    }
}
