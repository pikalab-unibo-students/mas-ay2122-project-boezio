package clpfd.reification

import clpCore.flip
import clpCore.variablesMap
import clpfd.ExpressionParser
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression

class ReificationParser(
    private val chocoModel: Model,
    private val substitution: Substitution.Unifier,
    private val context: ExecutionContext,
    private val signature: Signature
) : DefaultTermVisitor<ILogical>() {
    override fun defaultValue(term: Term): ILogical =
        throw TypeError.forArgument(context, signature, TypeError.Expected.TYPE_REFERENCE, term)

    override fun visitStruct(term: Struct): ILogical {
        when (term.arity) {
            2 -> when (term.functor) {
                // relational constraints
                "#=" -> return applyRelConstraint(term[0], term[1],ArExpression::eq).boolVar()
                "#\\=" -> return applyRelConstraint(term[0], term[1],ArExpression::ne).boolVar()
                "#>" -> return applyRelConstraint(term[0], term[1],ArExpression::gt).boolVar()
                "#<" -> return applyRelConstraint(term[0], term[1],ArExpression::lt).boolVar()
                "#>=" -> return applyRelConstraint(term[0], term[1],ArExpression::ge).boolVar()
                "#=<" -> return applyRelConstraint(term[0], term[1],ArExpression::le).boolVar()
                // reification constraints
                "#\\/" -> return LogOp.or(term[0].accept(this), term[1].accept(this))
                "#/\\" -> return LogOp.and(term[0].accept(this), term[1].accept(this))
                "#\\" -> return LogOp.xor(term[0].accept(this), term[1].accept(this))
                "#<==>" -> return LogOp.ifOnlyIf(term[0].accept(this), term[1].accept(this))
                "#==>" -> return LogOp.implies(term[0].accept(this), term[1].accept(this))
                "#<==" -> return LogOp.implies(term[1].accept(this), term[0].accept(this))
            }
            1 -> when (term.functor) {
                "#\\" -> return LogOp.nand(term[0].accept(this))
            }
        }
        return super.visitStruct(term) // indirectly calls defaultValue(term)
    }

    private fun applyRelConstraint(
        first: Term,
        second: Term,
        operation: (ArExpression, ArExpression) -> ReExpression
    ): ReExpression {
        val logicalVars = (first.variables + second.variables).toSet()
        val varMap = chocoModel.variablesMap(logicalVars, substitution).flip()
        val parser = ExpressionParser(chocoModel, varMap, substitution, context, signature)
        val firstExpression = first.accept(parser)
        val secondExpression = second.accept(parser)
        return operation(firstExpression, secondExpression)
    }
}