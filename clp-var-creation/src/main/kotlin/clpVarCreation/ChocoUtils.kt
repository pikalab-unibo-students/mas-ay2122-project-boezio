package clpVarCreation

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector
import org.chocosolver.solver.search.strategy.strategy.IntStrategy
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.Variable
import kotlin.collections.component1
import kotlin.collections.component2
import org.chocosolver.solver.Model as ChocoModel
import org.chocosolver.solver.Solver as ChocoSolver


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
    return vars.associateWith { logicVariablesByName[it.name]!! }
}

internal val Variable.valueAsTerm: Term
    get() = when (this) {
        is IntVar -> Integer.of(value)
        else -> TODO("Implement value retrieval as ${Term::class} for type ${this::class}")
    }

private fun <K, V> Map<K, V>.flip(): Map<V, K> = map { (k, v) -> v to k }.toMap()

internal fun createChocoSolver(
    model: ChocoModel,
    config: LabellingConfiguration,
    variables: Map<Variable, Var>
): ChocoSolver {
    val variableStrategy: VariableSelector<IntVar> = config.variableSelection.toVariableSelector(model)
    val valueStrategy: IntValueSelector = config.valueOrder.toValueSelector()

    if (config.branchingStrategy != BranchingStrategy.STEP) {
        throw java.lang.IllegalStateException()
    }

    require((config.objective != null) xor (config.problemType == ProblemType.SATISFY))

    if (config.problemType != ProblemType.SATISFY) {
        val parser = ExpressionParser(model, variables.flip())
        val objectiveExpression = config.objective!!.accept(parser)
        model.setObjective(config.problemType.toChoco()!!, objectiveExpression.intVar())
    }

    model.solver.setSearch(
        IntStrategy(variables.keys.filterIsInstance<IntVar>().toTypedArray(), variableStrategy, valueStrategy)
    )

    return model.solver
}
