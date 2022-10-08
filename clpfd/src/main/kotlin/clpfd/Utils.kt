package clpfd

import clpCore.chocoModel
import clpCore.getOuterVariable
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
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

fun Solve.Request<ExecutionContext>.getAsIntVar(term: Term, map: Map<Var, Variable>, substitution: Substitution.Unifier): IntVar {
    return if(term is Var) {
        val originalVar = term.castToVar().getOuterVariable(substitution)
        map[originalVar] as IntVar
    }else
        chocoModel.intVar(term.castToInteger().value.toInt())
}