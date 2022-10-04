package clpfd

import clpCore.chocoModel
import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.Variable

val Solve.Request<ExecutionContext>.operatorsMap
    get(): Map<Atom, String> {
        return mapOf(
            Atom.of("#=") to "=",
            Atom.of("#\\=") to "!=",
            Atom.of("#>") to ">",
            Atom.of("#<") to "<",
            Atom.of("#>=") to ">=",
            Atom.of("#=<") to "<="
        )
    }

// map of relational operators to ArExpression
val Solve.Request<ExecutionContext>.relOperatorsMap
    get(): Map<Atom, (ArExpression, ArExpression) -> ReExpression> {
        return mapOf(
            Atom.of("#=") to ArExpression::eq,
            Atom.of("#\\=") to ArExpression::ne,
            Atom.of("#>") to ArExpression::gt,
            Atom.of("#<") to ArExpression::lt,
            Atom.of("#>=") to ArExpression::ge,
            Atom.of("#=<") to ArExpression::le
        )
    }

fun Solve.Request<ExecutionContext>.getIntAsVars(termList: List<Term>): List<IntVar>{
    val integerAsVars = mutableListOf<IntVar>()
    // Conversion of integers to int values
    for (elem in termList){
        if(elem is Integer){
            val intValue = elem.castToInteger().intValue.toInt()
            integerAsVars.add(chocoModel.intVar(intValue))
        }
    }
    return integerAsVars
}

fun Solve.Request<ExecutionContext>.getAsIntVar(term: Term, map: Map<Var, Variable>): IntVar {
    if(term is Var)
        return map[term.castToVar()] as IntVar
    else
        return chocoModel.intVar(term.castToInteger().value.toInt())
}

internal fun Solve.Request<ExecutionContext>.applyRelConstraint(
    first: Term,
    second: Term,
    operation: (ArExpression, ArExpression) -> ReExpression
): ReExpression {
    val chocoModel = chocoModel
    val logicalVars = (first.variables + second.variables).toSet()
    val varMap = chocoModel.variablesMap(logicalVars).flip()
    val parser = ExpressionParser(chocoModel, varMap)
    val firstExpression = first.accept(parser)
    val secondExpression = second.accept(parser)
    return operation(firstExpression, secondExpression)
}