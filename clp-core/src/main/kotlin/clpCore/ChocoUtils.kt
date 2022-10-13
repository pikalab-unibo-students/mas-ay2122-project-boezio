package clpCore

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
import org.chocosolver.solver.Solver
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

fun SideEffectsBuilder.setChocoModel(chocoModel: ChocoModel) {
    setDurableData(CHOCO_MODEL, chocoModel)
}

fun ChocoModel.variablesMap(logicVariables: Iterable<Var>, substitution: Substitution.Unifier): Map<Variable, Var> {
    val actualVariables = logicVariables.getOuterVariables(substitution)
    val logicVariablesByName = actualVariables.groupBy { it.completeName }.mapValues { it.value.single() }
    return vars.filter { it.name in logicVariablesByName }.associateWith { logicVariablesByName[it.name]!! }
}

fun Var.getOuterVariable(substitution: Substitution.Unifier): Var =
    substitution.getOriginal(this) ?: this

fun Sequence<Var>.getOuterVariables(substitution: Substitution.Unifier): Sequence<Var> =
    map { it.getOuterVariable(substitution) }

fun Iterable<Var>.getOuterVariables(substitution: Substitution.Unifier): List<Var> =
    map { it.getOuterVariable(substitution) }

val Variable.valueAsTerm: Term
    get() = when (this) {
        is IntVar -> Integer.of(value)
        // RealVar.toString returns <name_var>=[lb..ub] which is a range of the first found solution
        is RealVar -> Real.of(this.ub)
        is BoolVar -> when (booleanValue) {
            ESat.TRUE -> Integer.of(1)
            ESat.FALSE -> Integer.of(0)
            else -> throw IllegalStateException("Invalid value for ${BoolVar::class.java.simpleName}: $booleanValue")
        }
        else -> throw IllegalStateException("Not supported type of Choco variable: ${this::class.java.name}")
    }

fun <K, V> Map<K, V>.flip(): Map<V, K> = map { (k, v) -> v to k }.toMap()


// why? see https://choco-solver.org/docs/solving/solving/#mono-objective-optimization
// how? see https://kotlinlang.org/docs/sequences.html#from-chunks
fun Solver.solutions(chocoToLogic: Map<Variable, Var>): Sequence<Substitution> = sequence {
    var atLeastOne = false
    while (solve()) {
        atLeastOne = true
        yield(Substitution.of(chocoToLogic.map { (k, v) -> v to k.valueAsTerm }))
    }
    if (!atLeastOne) {
        yield(Substitution.failed())
    }
}
