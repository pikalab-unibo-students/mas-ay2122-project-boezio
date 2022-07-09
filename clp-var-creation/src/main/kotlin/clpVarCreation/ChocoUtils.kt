package clpVarCreation

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.expression.discrete.relational.ReExpression
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector
import org.chocosolver.solver.search.strategy.strategy.IntStrategy
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.RealVar
import org.chocosolver.solver.variables.Variable
import org.chocosolver.util.ESat
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

// apply a generic relational constraint
internal fun Solve.Request<ExecutionContext>.applyRelConstraint(
    first: Term, second: Term, op: (ArExpression, ArExpression) -> ReExpression
) {
    val chocoModel = chocoModel
    val logicalVars = (first.variables + second.variables).toSet()
    val varMap = chocoModel.variablesMap(logicalVars).flip()
    val parser = ExpressionParser(chocoModel, varMap)
    val firstExpression = first.accept(parser)
    val secondExpression = second.accept(parser)
    op(firstExpression, secondExpression).decompose().post()
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
        // toString returns <name_var>=<value>
        is RealVar -> Real.of((this.toString().split("=")[1]).toDouble())
        is BoolVar -> when(booleanValue) {
            ESat.TRUE -> Truth.of(true)
            ESat.FALSE -> Truth.of(false)
            else -> throw IllegalStateException()
        }
        else -> throw IllegalStateException()
    }

private fun <K, V> Map<K, V>.flip(): Map<V, K> = map { (k, v) -> v to k }.toMap()

internal fun createChocoSolver(
    model: ChocoModel,
    config: LabelingConfiguration,
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
