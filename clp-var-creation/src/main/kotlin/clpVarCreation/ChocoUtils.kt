package clpVarCreation

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.RealVar
import org.chocosolver.solver.variables.Variable
import org.chocosolver.util.ESat
import kotlin.collections.component1
import kotlin.collections.component2
import org.chocosolver.solver.Model as ChocoModel


private const val CHOCO_MODEL = "chocoModel"

internal val Solve.Request<ExecutionContext>.chocoModel
    get() = if (CHOCO_MODEL !in context.customData.durable) {
        ChocoModel()
    } else {
        context.customData.durable[CHOCO_MODEL] as ChocoModel
    }

internal fun SideEffectsBuilder.setChocoModel(chocoModel: ChocoModel) {
    setDurableData(CHOCO_MODEL, chocoModel)
}

internal fun ChocoModel.variablesMap(logicVariables: Iterable<Var>): Map<Variable, Var> {
    val logicVariablesByName = logicVariables.groupBy { it.completeName }.mapValues { it.value.single() }
    return vars.filter { it.name in logicVariablesByName }.associateWith { logicVariablesByName[it.name]!! }
}

internal val Variable.valueAsTerm: Term
    get() = when (this) {
        is IntVar -> Integer.of(value)
        // toString returns <name_var>=<value>
        is RealVar -> Real.of((this.toString().split("=")[1]).toDouble())
        is BoolVar -> when(booleanValue) {
            ESat.TRUE -> Truth.of(true)
            ESat.FALSE -> Truth.of(false)
            else -> throw IllegalStateException()
        }
        else -> throw IllegalStateException()
    }

internal fun <K, V> Map<K, V>.flip(): Map<V, K> = map { (k, v) -> v to k }.toMap()
