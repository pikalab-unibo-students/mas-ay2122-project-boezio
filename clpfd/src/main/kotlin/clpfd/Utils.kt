package clpfd

import clpCore.chocoModel
import clpCore.flip
import clpCore.variablesMap
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression
import org.chocosolver.solver.variables.BoolVar
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

// utils for reification

internal fun Solve.Request<ExecutionContext>.getReifiedTerms(terms: LogicList): List<BoolVar> {
    val chocoModel = chocoModel

    val logicalVars = terms.variables.distinct().toList()
    val varMap = chocoModel.variablesMap(logicalVars).flip()

    val termList = terms.toList()
    val reificationList = mutableListOf<BoolVar>()

    for(element in termList){
        require(element is Var || element is Struct){
            "$element in neither a variable nor a struct"
        }
        reificationList.add(generateReificationTerm(element, varMap))
    }

    return reificationList.toList()
}

internal fun Solve.Request<ExecutionContext>.generateReificationTerm(term: Term, varsMap: Map<Var, Variable>): BoolVar {
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