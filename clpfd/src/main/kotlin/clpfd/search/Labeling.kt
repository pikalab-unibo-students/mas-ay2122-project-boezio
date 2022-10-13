package clpfd.search

import clpfd.*
import clpCore.*
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.Model as ChocoModel
import org.chocosolver.solver.Solver as ChocoSolver
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector
import org.chocosolver.solver.search.strategy.strategy.IntStrategy
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.Variable
import it.unibo.tuprolog.core.List as LogicList

object Labeling : BinaryRelation<ExecutionContext>("labeling") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term, second: Term): Sequence<Solve.Response> {
        ensuringArgumentIsList(0)
        val keys: Set<Struct> = (first as LogicList).toList().filterIsInstance<Struct>().toSet()
        ensuringArgumentIsList(1)
        val logicVariables: List<Var> = (second as LogicList)
            .toSequence()
            .filterIsInstance<Var>()
            .distinct()
            .getOuterVariables(context.substitution)
            .toList()
        val chocoModel = chocoModel
        val configuration = LabelingConfiguration.fromTerms(keys)
        val chocoToLogic: Map<Variable, Var> = chocoModel.variablesMap(logicVariables, context.substitution)
        val solver = createChocoSolver(chocoModel, configuration, chocoToLogic, context.substitution)
        return if (configuration.problemType == ProblemType.SATISFY) {
            solver.solutions(chocoToLogic).map {
                replyWith(it)
            }
        } else {
            sequenceOf(replyWith(solver.solutions(chocoToLogic).last()))
        }
    }

    private fun createChocoSolver(
        model: ChocoModel,
        config: LabelingConfiguration,
        variables: Map<Variable, Var>,
        substitution: Substitution.Unifier
    ): ChocoSolver {
        val variableStrategy: VariableSelector<IntVar> = config.variableSelection.toVariableSelector(model, variables.keys)
        val valueStrategy: IntValueSelector = config.valueOrder.toValueSelector()

        if (config.branchingStrategy != BranchingStrategy.STEP) {
            throw NotImplementedError("Branching strategy not yet supported: ${config.branchingStrategy.name.lowercase()}")
        }

        require((config.objective != null) xor (config.problemType == ProblemType.SATISFY))

        if (config.problemType != ProblemType.SATISFY) {
            val parser = ExpressionParser(model, variables.flip(), substitution)
            val objectiveExpression = config.objective!!.accept(parser)
            model.setObjective(config.problemType.toChoco()!!, objectiveExpression.intVar())
        }

        model.solver.setSearch(
            IntStrategy(variables.keys.filterIsInstance<IntVar>().toTypedArray(), variableStrategy, valueStrategy)
        )

        return model.solver
    }
}