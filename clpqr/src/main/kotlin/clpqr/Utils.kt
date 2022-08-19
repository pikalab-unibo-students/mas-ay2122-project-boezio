package clpqr

import clpCore.flip
import clpCore.valueAsTerm
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import clpqr.search.Configuration as Configuration
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solver
import org.chocosolver.solver.variables.Variable
import it.unibo.tuprolog.core.List as LogicList

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

internal fun Solver.getVectorValue(varsMap: Map<Variable, Var>, vector: List<Var>): List<Term> {
    while (solve()) { }
    // filter solution variables
    val vectorMap = mutableMapOf<Variable, Var>()
    for (variable in vector) {
        for (entry in varsMap) {
            if (entry.value == variable) {
                vectorMap[entry.key] = entry.value
            }
        }
    }
    // values of variables in vector
    return vectorMap.map { (k, _) -> k.valueAsTerm }
}

internal fun Solver.calculateExpression(varsMap: Map<Variable, Var>, expression: Term): Double {
    while (solve()) {
    }
    val parser = ExpressionEvaluator(varsMap.flip())
    return expression.accept(parser)
}
