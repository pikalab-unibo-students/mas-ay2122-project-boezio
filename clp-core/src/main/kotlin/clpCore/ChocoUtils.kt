package clpCore

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.RealVar
import org.chocosolver.solver.variables.Variable
import org.chocosolver.util.ESat
import kotlin.collections.component1
import kotlin.collections.component2
import org.chocosolver.solver.Model as ChocoModel


private const val CHOCO_MODEL = "chocoModel"

val Solve.Request<ExecutionContext>.chocoModel
    get() = if (CHOCO_MODEL !in context.customData.durable) {
        ChocoModel()
    } else {
        context.customData.durable[CHOCO_MODEL] as ChocoModel
    }

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

fun Solve.Request<ExecutionContext>.getAsIntVar(term: Term, map: Map<Var, Variable>): IntVar{
    if(term is Var)
        return map[term.castToVar()] as IntVar
    else
        return chocoModel.intVar(term.castToInteger().value.toInt())
}

fun SideEffectsBuilder.setChocoModel(chocoModel: ChocoModel) {
    setDurableData(CHOCO_MODEL, chocoModel)
}

fun ChocoModel.variablesMap(logicVariables: Iterable<Var>): Map<Variable, Var> {
    val logicVariablesByName = logicVariables.groupBy { it.completeName }.mapValues { it.value.single() }
    return vars.filter { it.name in logicVariablesByName }.associateWith { logicVariablesByName[it.name]!! }
}

val Variable.valueAsTerm: Term
    get() = when (this) {
        is IntVar -> Integer.of(value)
        // RealVar.toString returns <name_var>=<value>
        is RealVar -> Real.of((this.toString().split("=")[1]).toDouble())
        is BoolVar -> when (booleanValue) {
            ESat.TRUE -> Truth.of(true)
            ESat.FALSE -> Truth.of(false)
            else -> throw IllegalStateException("Invalid value for ${BoolVar::class.java.simpleName}: $booleanValue")
        }
        else -> throw IllegalStateException("Not supported type of Choco variable: ${this::class.java.name}")
    }

fun <K, V> Map<K, V>.flip(): Map<V, K> = map { (k, v) -> v to k }.toMap()