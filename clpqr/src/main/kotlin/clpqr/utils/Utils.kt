package clpqr.utils

import clpCore.flip
import clpCore.valueAsTerm
import clpqr.Precision
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
import clpqr.search.Configuration as Configuration
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solver
import org.chocosolver.solver.variables.Variable

internal fun Solve.Request<ExecutionContext>.createChocoSolver(
    chocoModel: Model,
    config: Configuration,
    variables: Map<Variable, Var>
): Solver {

    if(config.problemType != ProblemType.SATISFY){
        val parser = ExpressionParser(chocoModel, variables.flip())
        val objectiveExpression = config.objective!!.accept(parser)
        val precision = (context.flags[Precision] as Real).decimalValue.toDouble()
        chocoModel.setObjective(config.problemType.toChoco()!!, objectiveExpression.realVar(precision))
    }
    return chocoModel.solver
}

internal fun Solver.getVectorValue(varsMap: Map<Variable, Var>, vector: List<Var>): Sequence<List<Term>> = sequence {
    // filter solution variables
    val vectorMap = mutableMapOf<Variable, Var>()
    for (variable in vector) {
        for (entry in varsMap) {
            if (entry.value == variable) {
                vectorMap[entry.key] = entry.value
            }
        }
    }
    while (solve()) {
        // values of variables in vector
        yield(vectorMap.map { (k, _) -> k.valueAsTerm })
    }
}

internal fun Solver.calculateExpression(varsMap: Map<Variable, Var>, expression: Term): Sequence<Double> = sequence {
    val parser = ExpressionEvaluator(varsMap.flip())
    while (solve()) {
        yield(expression.accept(parser))
    }
}

private const val CONSTRAINTS = "constraints"
val Solve.Request<ExecutionContext>.constraints
    get() = if (CONSTRAINTS !in context.customData.durable) {
        mutableListOf<Term>()
    } else {
        context.customData.durable[CONSTRAINTS] as MutableList<Term>
    }

fun SideEffectsBuilder.setConstraints(constraints: MutableList<Term>) {
    setDurableData(CONSTRAINTS, constraints)
}

fun Map<Variable, Var>.filterNotConstantVar(): Map<Variable, Var> {
    return this.filter { (k,_) -> !k.name[0].isDigit() }
}