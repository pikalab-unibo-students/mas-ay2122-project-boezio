package clpqr

import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import org.chocosolver.solver.Model
import org.chocosolver.solver.expression.continuous.arithmetic.CArExpression
import org.chocosolver.solver.expression.continuous.relational.CReExpression
import org.chocosolver.util.ESat

class ConstraintChecker(private val chocoModel: Model) : DefaultTermVisitor<Boolean>() {
    override fun defaultValue(term: Term) = throw IllegalStateException("Cannot handle $term as constraint")

    override fun visitTuple(term: Tuple): Boolean {
        return term.unfoldedSequence.all { it.accept(this) }
    }

    override fun visitStruct(term: Struct): Boolean {
        require(term.arity == 2){
            "Invalid constraint structure"
        }
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
            else -> throw IllegalStateException("Cannot handle constraint ${struct.functor}")
        }
    }
    private fun checkConstraint(
        firstTerm: Term,
        secondTerm: Term,
        operation: (CArExpression, CArExpression) -> CReExpression,
        model: Model
    ):Boolean{
        val logicalVars = (firstTerm.variables + secondTerm.variables).toSet()
        val varMap = model.variablesMap(logicalVars).flip()
        val parser = ExpressionParser(model, varMap)
        val firstExpression = firstTerm.accept(parser)
        val secondExpression = secondTerm.accept(parser)
        return when(operation(firstExpression, secondExpression).equation().isSatisfied){
            ESat.TRUE -> true
            ESat.FALSE -> false
            else -> false
        }
    }
}

