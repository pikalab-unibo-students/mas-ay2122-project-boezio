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

internal fun Solver.optimalSolution(
    varsMap: Map<Variable, Var>,
    expression: Term,
    optimum: Var,
    vector: List<Var>,
    vertex: Var
): Sequence<Substitution> = sequence {

    // filter solution variables
    val vectorMap = mutableMapOf<Variable, Var>()
    for(variable in vector){
        for(entry in varsMap){
            if(entry.value == variable){
                vectorMap[entry.key] = entry.value
            }
        }
    }
    val parser = ExpressionEvaluator(varsMap.flip())
    while(solve()){
        // values of variables in vector
        val vertexList = vectorMap.map { (k, _) -> k.valueAsTerm } as LogicList
        // optimum value
        val expressionValue = expression.accept(parser)
        yield(Substitution.of(mapOf(optimum to Real.of(expressionValue), vertex to vertexList)))
    }
}