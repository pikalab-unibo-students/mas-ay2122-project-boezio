package clpfd.reification

import clpCore.chocoModel
import clpCore.flip
import clpCore.variablesMap
import clpfd.applyRelConstraint
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.Variable

abstract class BinaryReificationOperator(operator: String): BinaryRelation.NonBacktrackable<ExecutionContext>(operator) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        applyReificationConstraint(first, second)
        return replySuccess()
    }

    protected abstract val operation: (Array<ILogical>) -> LogOp

    // apply a binary reification constraint
    private fun Solve.Request<ExecutionContext>.applyReificationConstraint(first: Term, second: Term) {
        val chocoModel = chocoModel
        require(first is Var || first is Struct){
            "$first in neither a variable nor a struct"
        }
        require(second is Var || second is Struct) {
            "$second in neither a variable nor a struct"
        }
        val logicalVars = (first.variables + second.variables).toSet()
        val varMap = chocoModel.variablesMap(logicalVars).flip()

        val firstReif = generateReificationTerm(first, varMap)
        val secondReif = generateReificationTerm(second, varMap)

        chocoModel.addClauses(operation(arrayOf(firstReif, secondReif)))
    }

    private fun Solve.Request<ExecutionContext>.generateReificationTerm(term: Term, varsMap: Map<Var, Variable>): BoolVar{
        if(term is Var){
            return (varsMap[term] ?: chocoModel.boolVar(term.completeName)) as BoolVar
        }else if(term.let { it is Struct && it.arity == 2 }){
            val struct = term.castToStruct()
            val op: (ArExpression, ArExpression) -> ReExpression = when(struct.functor){
                "#=" -> ArExpression::eq
                "#\\=" -> ArExpression::ne
                "#>" -> ArExpression::gt
                "#<" -> ArExpression::lt
                "#>=" -> ArExpression::ge
                "#=<" -> ArExpression::le
                else -> throw IllegalStateException("operator ${struct.functor} is not valid")
            }
            return applyRelConstraint(struct[0], struct[1], op).boolVar()
        }else{
            throw IllegalStateException("$term is not a valid term")
        }
    }
}